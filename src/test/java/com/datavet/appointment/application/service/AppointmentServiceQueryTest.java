package com.datavet.appointment.application.service;

import com.datavet.appointment.application.port.in.command.CancelAppointmentCommand;
import com.datavet.appointment.application.port.in.command.UpdateAppointmentStatusCommand;
import com.datavet.appointment.application.port.out.AppointmentEmailPort;
import com.datavet.appointment.application.port.out.AppointmentRepositoryPort;
import com.datavet.appointment.domain.exception.AppointmentNotFoundException;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.model.PetSnapshot;
import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService - updateStatus / cancel / getById Tests")
class AppointmentServiceQueryTest {

    private AppointmentService appointmentService;

    @Mock private AppointmentRepositoryPort appointmentRepositoryPort;
    @Mock private AppointmentEmailPort      appointmentEmailPort;
    @Mock private DomainEventPublisher      domainEventPublisher;

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepositoryPort, appointmentEmailPort, domainEventPublisher);
    }

    // =========================================================================
    // updateStatus
    // =========================================================================

    @Test
    @DisplayName("updateStatus: should advance status and save")
    void updateStatus_ShouldAdvanceAndSave() {
        Appointment existing = buildReservedAppointment("appt-1", "clinic-1");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.updateStatus(UpdateAppointmentStatusCommand.builder()
                .appointmentId("appt-1")
                .clinicId("clinic-1")
                .newStatus(AppointmentStatus.CLIENTE_LLEGADO)
                .build());

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CLIENTE_LLEGADO);
        verify(appointmentRepositoryPort).save(any(Appointment.class));
    }

    @Test
    @DisplayName("updateStatus: should assign medicalEmployee when provided")
    void updateStatus_WithMedicalEmployee_ShouldAssign() {
        Appointment existing = buildReservedAppointment("appt-1", "clinic-1");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.updateStatus(UpdateAppointmentStatusCommand.builder()
                .appointmentId("appt-1")
                .clinicId("clinic-1")
                .newStatus(AppointmentStatus.CLIENTE_LLEGADO)
                .medicalEmployeeId("vet-99")
                .build());

        assertThat(result.getMedicalEmployeeId()).isEqualTo("vet-99");
    }

    @Test
    @DisplayName("updateStatus: should throw AppointmentNotFoundException when not found")
    void updateStatus_WhenNotFound_ShouldThrow() {
        when(appointmentRepositoryPort.findById("appt-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.updateStatus(UpdateAppointmentStatusCommand.builder()
                .appointmentId("appt-x")
                .clinicId("clinic-1")
                .newStatus(AppointmentStatus.CLIENTE_LLEGADO)
                .build()))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("updateStatus: should throw AccessDeniedException when appointment belongs to different clinic")
    void updateStatus_WhenDifferentClinic_ShouldThrowAccessDenied() {
        Appointment existing = buildReservedAppointment("appt-1", "other-clinic");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> appointmentService.updateStatus(UpdateAppointmentStatusCommand.builder()
                .appointmentId("appt-1")
                .clinicId("clinic-1")
                .newStatus(AppointmentStatus.CLIENTE_LLEGADO)
                .build()))
                .isInstanceOf(AccessDeniedException.class);
    }

    // =========================================================================
    // cancel
    // =========================================================================

    @Test
    @DisplayName("cancel: should cancel and save appointment")
    void cancel_ShouldCancelAndSave() {
        Appointment existing = buildReservedAppointment("appt-1", "clinic-1");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.cancel(CancelAppointmentCommand.builder()
                .appointmentId("appt-1")
                .clinicId("clinic-1")
                .reason("El cliente canceló")
                .build());

        assertThat(existing.getStatus()).isEqualTo(AppointmentStatus.CANCELADA);
        verify(appointmentRepositoryPort).save(existing);
    }

    @Test
    @DisplayName("cancel: should throw when appointment not found")
    void cancel_WhenNotFound_ShouldThrow() {
        when(appointmentRepositoryPort.findById("appt-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancel(CancelAppointmentCommand.builder()
                .appointmentId("appt-x")
                .clinicId("clinic-1")
                .reason("motivo")
                .build()))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("cancel: should throw AccessDeniedException when different clinic")
    void cancel_WhenDifferentClinic_ShouldThrowAccessDenied() {
        Appointment existing = buildReservedAppointment("appt-1", "other-clinic");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> appointmentService.cancel(CancelAppointmentCommand.builder()
                .appointmentId("appt-1")
                .clinicId("clinic-1")
                .reason("motivo")
                .build()))
                .isInstanceOf(AccessDeniedException.class);
    }

    // =========================================================================
    // getById
    // =========================================================================

    @Test
    @DisplayName("getById: should return appointment when found and same clinic")
    void getById_ShouldReturnAppointment() {
        Appointment existing = buildReservedAppointment("appt-1", "clinic-1");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));

        Appointment result = appointmentService.getById("appt-1", "clinic-1");

        assertThat(result.getId()).isEqualTo("appt-1");
    }

    @Test
    @DisplayName("getById: should throw when not found")
    void getById_WhenNotFound_ShouldThrow() {
        when(appointmentRepositoryPort.findById("appt-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getById("appt-x", "clinic-1"))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("getById: should throw AccessDeniedException when different clinic")
    void getById_WhenDifferentClinic_ShouldThrowAccessDenied() {
        Appointment existing = buildReservedAppointment("appt-1", "other-clinic");
        when(appointmentRepositoryPort.findById("appt-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> appointmentService.getById("appt-1", "clinic-1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    // =========================================================================
    // getByClinic
    // =========================================================================

    @Test
    @DisplayName("getByClinic: should delegate to repository with all filters")
    void getByClinic_ShouldDelegateToRepository() {
        Appointment existing = buildReservedAppointment("appt-1", "clinic-1");
        when(appointmentRepositoryPort.findByClinicIdWithFilters(
                eq("clinic-1"), any(), any(), any(), any()))
                .thenReturn(List.of(existing));

        List<Appointment> result = appointmentService.getByClinic(
                "clinic-1", null, null, null, null);

        assertThat(result).hasSize(1);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Appointment buildReservedAppointment(String id, String clinicId) {
        return Appointment.reconstitute(
                id, clinicId, false, AppointmentType.RUTINA,
                AppointmentStatus.RESERVADA, FUTURE,
                "owner-1", "Ana García", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, "Revisión anual", List.of(),
                AppointmentSource.PANEL, LocalDateTime.now(), null
        );
    }
}
