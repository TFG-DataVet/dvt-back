package com.datavet.appointment.application.service;

import com.datavet.appointment.application.port.in.command.CreateAppointmentCommand;
import com.datavet.appointment.application.port.out.AppointmentEmailPort;
import com.datavet.appointment.application.port.out.AppointmentRepositoryPort;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.model.PetSnapshot;
import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService - create Tests")
class AppointmentServiceCreateTest {

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
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should create and save a regular appointment")
    void create_RegularAppointment_ShouldSave() {
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.create(buildRegularCommand());

        assertThat(result).isNotNull();
        assertThat(result.getClinicId()).isEqualTo("clinic-1");
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.RESERVADA);
        verify(appointmentRepositoryPort).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should publish domain events after creating appointment")
    void create_ShouldPublishDomainEvents() {
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.create(buildRegularCommand());

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should send confirmation email for regular appointment with email")
    void create_WhenRegularWithEmail_ShouldSendEmail() {
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.create(buildRegularCommand());

        verify(appointmentEmailPort).sendAppointmentCreatedEmail(
                eq("ana@test.com"), any(), any(Appointment.class));
    }

    @Test
    @DisplayName("Should NOT send email for emergency appointment")
    void create_WhenEmergency_ShouldNotSendEmail() {
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.create(buildEmergencyCommand());

        verify(appointmentEmailPort, never()).sendAppointmentCreatedEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void create_ShouldClearEventsAfterPublishing() {
        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        when(appointmentRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        appointmentService.create(buildRegularCommand());

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should continue even if email sending fails")
    void create_WhenEmailFails_ShouldNotPropagate() {
        when(appointmentRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));
        doThrow(new RuntimeException("SMTP error"))
                .when(appointmentEmailPort).sendAppointmentCreatedEmail(any(), any(), any());

        Appointment result = appointmentService.create(buildRegularCommand());

        assertThat(result).isNotNull();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private CreateAppointmentCommand buildRegularCommand() {
        return CreateAppointmentCommand.builder()
                .clinicId("clinic-1")
                .emergency(false)
                .type(AppointmentType.RUTINA)
                .scheduledAt(FUTURE)
                .ownerId("owner-1")
                .ownerName("Ana García")
                .ownerEmail("ana@test.com")
                .ownerPhone("+34600000001")
                .petId("pet-1")
                .petName("Max")
                .petSpecies("Perro")
                .creationEmployeeId("emp-1")
                .medicalEmployeeId(null)
                .notes("Revisión anual")
                .productIds(List.of())
                .source(AppointmentSource.PANEL)
                .build();
    }

    private CreateAppointmentCommand buildEmergencyCommand() {
        return CreateAppointmentCommand.builder()
                .clinicId("clinic-1")
                .emergency(true)
                .type(AppointmentType.EMERGENCIA)
                .scheduledAt(FUTURE)
                .creationEmployeeId("emp-1")
                .source(AppointmentSource.PANEL)
                .build();
    }
}
