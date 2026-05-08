package com.datavet.appointment.domain.model;

import com.datavet.appointment.domain.event.AppointmentCancelledEvent;
import com.datavet.appointment.domain.event.AppointmentCreatedEvent;
import com.datavet.appointment.domain.event.AppointmentStatusChangedEvent;
import com.datavet.appointment.domain.exception.AppointmentStateException;
import com.datavet.appointment.domain.exception.AppointmentValidationException;
import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Appointment Domain Model Tests")
class AppointmentTest {

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(1);

    // =========================================================================
    // create — estructura y campos
    // =========================================================================

    @Test
    @DisplayName("create: should set all fields correctly for a regular appointment")
    void create_RegularAppointment_ShouldSetAllFields() {
        Appointment appt = buildRegularAppointment();

        assertThat(appt.getClinicId()).isEqualTo("clinic-1");
        assertThat(appt.isEmergency()).isFalse();
        assertThat(appt.getType()).isEqualTo(AppointmentType.RUTINA);
        assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.RESERVADA);
        assertThat(appt.getScheduledAt()).isEqualTo(FUTURE);
        assertThat(appt.getOwnerId()).isEqualTo("owner-1");
        assertThat(appt.getPet()).isNotNull();
        assertThat(appt.getCreationEmployeeId()).isEqualTo("emp-1");
        assertThat(appt.getSource()).isEqualTo(AppointmentSource.PANEL);
    }

    @Test
    @DisplayName("create: should generate a non-null UUID")
    void create_ShouldGenerateUUID() {
        Appointment appt = buildRegularAppointment();
        assertThat(appt.getId()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("create: should set createdAt and leave updatedAt null")
    void create_ShouldSetCreatedAtAndNullUpdatedAt() {
        Appointment appt = buildRegularAppointment();
        assertThat(appt.getCreatedAt()).isNotNull();
        assertThat(appt.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("create: should raise AppointmentCreatedEvent")
    void create_ShouldRaiseCreatedEvent() {
        Appointment appt = buildRegularAppointment();

        List<DomainEvent> events = appt.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(AppointmentCreatedEvent.class);

        AppointmentCreatedEvent event = (AppointmentCreatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getType()).isEqualTo(AppointmentType.RUTINA);
        assertThat(event.isEmergency()).isFalse();
    }

    @Test
    @DisplayName("create: emergency appointment should not require ownerId nor pet")
    void create_EmergencyAppointment_ShouldNotRequireOwnerOrPet() {
        Appointment appt = Appointment.create(
                "clinic-1", true, AppointmentType.EMERGENCIA, FUTURE,
                null, null, null, null, null,
                "emp-1", null, null, null, AppointmentSource.PANEL);

        assertThat(appt.isEmergency()).isTrue();
        assertThat(appt.getOwnerId()).isNull();
        assertThat(appt.getPet()).isNull();
    }

    @Test
    @DisplayName("create: should default source to PANEL when null")
    void create_WhenSourceNull_ShouldDefaultToPanel() {
        Appointment appt = Appointment.create(
                "clinic-1", true, AppointmentType.EMERGENCIA, FUTURE,
                null, null, null, null, null,
                "emp-1", null, null, null, null);

        assertThat(appt.getSource()).isEqualTo(AppointmentSource.PANEL);
    }

    // =========================================================================
    // create — validaciones
    // =========================================================================

    @Test
    @DisplayName("create: should throw when clinicId is blank")
    void create_WhenClinicIdBlank_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "", false, AppointmentType.RUTINA, FUTURE,
                "owner-1", "Ana", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("clinicId");
    }

    @Test
    @DisplayName("create: should throw when type is null")
    void create_WhenTypeNull_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "clinic-1", false, null, FUTURE,
                "owner-1", "Ana", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("type");
    }

    @Test
    @DisplayName("create: should throw when scheduledAt is null")
    void create_WhenScheduledAtNull_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "clinic-1", false, AppointmentType.RUTINA, null,
                "owner-1", "Ana", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("scheduledAt");
    }

    @Test
    @DisplayName("create: should throw when creationEmployeeId is blank")
    void create_WhenCreationEmployeeIdBlank_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "clinic-1", false, AppointmentType.RUTINA, FUTURE,
                "owner-1", "Ana", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("creationEmployeeId");
    }

    @Test
    @DisplayName("create: non-emergency without ownerId should throw")
    void create_NonEmergencyWithoutOwner_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "clinic-1", false, AppointmentType.RUTINA, FUTURE,
                null, null, null, null,
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("ownerId");
    }

    @Test
    @DisplayName("create: non-emergency without pet should throw")
    void create_NonEmergencyWithoutPet_ShouldThrow() {
        assertThatThrownBy(() -> Appointment.create(
                "clinic-1", false, AppointmentType.RUTINA, FUTURE,
                "owner-1", "Ana", "ana@test.com", "+34600000001",
                null, "emp-1", null, null, null, AppointmentSource.PANEL))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("pet");
    }

    // =========================================================================
    // advanceStatus
    // =========================================================================

    @Test
    @DisplayName("advanceStatus: RESERVADA → CLIENTE_LLEGADO should succeed")
    void advanceStatus_ReservadaToClienteLlegado_ShouldSucceed() {
        Appointment appt = buildRegularAppointment();
        appt.clearDomainEvents();

        appt.advanceStatus(AppointmentStatus.CLIENTE_LLEGADO);

        assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.CLIENTE_LLEGADO);
        assertThat(appt.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("advanceStatus: should raise AppointmentStatusChangedEvent")
    void advanceStatus_ShouldRaiseStatusChangedEvent() {
        Appointment appt = buildRegularAppointment();
        appt.clearDomainEvents();

        appt.advanceStatus(AppointmentStatus.CLIENTE_LLEGADO);

        assertThat(appt.getDomainEvents()).hasSize(1);
        assertThat(appt.getDomainEvents().get(0)).isInstanceOf(AppointmentStatusChangedEvent.class);
    }

    @Test
    @DisplayName("advanceStatus: RESERVADA → EN_CONSULTA (invalid) should throw")
    void advanceStatus_InvalidTransition_ShouldThrow() {
        Appointment appt = buildRegularAppointment();

        assertThatThrownBy(() -> appt.advanceStatus(AppointmentStatus.EN_CONSULTA))
                .isInstanceOf(AppointmentStateException.class);
    }

    @Test
    @DisplayName("advanceStatus: EN_CONSULTA → FINALIZADA without medicalEmployee should throw")
    void advanceStatus_ToFinalizadaWithoutMedicalEmployee_ShouldThrow() {
        Appointment appt = advanceToEnConsulta();

        assertThatThrownBy(() -> appt.advanceStatus(AppointmentStatus.FINALIZADA))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("medicalEmployeeId");
    }

    @Test
    @DisplayName("advanceStatus: EN_CONSULTA → FINALIZADA with medicalEmployee assigned should succeed")
    void advanceStatus_ToFinalizadaWithMedicalEmployee_ShouldSucceed() {
        Appointment appt = advanceToEnConsulta();
        appt.assignMedicalEmployee("vet-1");
        appt.clearDomainEvents();

        appt.advanceStatus(AppointmentStatus.FINALIZADA);

        assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.FINALIZADA);
    }

    // =========================================================================
    // cancel
    // =========================================================================

    @Test
    @DisplayName("cancel: should set status to CANCELADA")
    void cancel_ShouldSetCanceladaStatus() {
        Appointment appt = buildRegularAppointment();
        appt.clearDomainEvents();

        appt.cancel("Paciente no puede asistir");

        assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.CANCELADA);
        assertThat(appt.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("cancel: should raise AppointmentCancelledEvent and AppointmentStatusChangedEvent")
    void cancel_ShouldRaiseTwoEvents() {
        Appointment appt = buildRegularAppointment();
        appt.clearDomainEvents();

        appt.cancel("Paciente no puede asistir");

        assertThat(appt.getDomainEvents()).hasSize(2);
        assertThat(appt.getDomainEvents().get(0)).isInstanceOf(AppointmentCancelledEvent.class);
        assertThat(appt.getDomainEvents().get(1)).isInstanceOf(AppointmentStatusChangedEvent.class);
    }

    @Test
    @DisplayName("cancel: should include reason in CancelledEvent")
    void cancel_ShouldIncludeReasonInEvent() {
        Appointment appt = buildRegularAppointment();
        appt.clearDomainEvents();

        appt.cancel("Paciente no puede asistir");

        AppointmentCancelledEvent event = (AppointmentCancelledEvent) appt.getDomainEvents().get(0);
        assertThat(event.getReason()).isEqualTo("Paciente no puede asistir");
    }

    @Test
    @DisplayName("cancel: should throw when appointment is not RESERVADA")
    void cancel_WhenNotReservada_ShouldThrow() {
        Appointment appt = buildRegularAppointment();
        appt.advanceStatus(AppointmentStatus.CLIENTE_LLEGADO);

        assertThatThrownBy(() -> appt.cancel("ya no puede"))
                .isInstanceOf(AppointmentStateException.class)
                .hasMessageContaining("RESERVADA");
    }

    // =========================================================================
    // assignMedicalEmployee
    // =========================================================================

    @Test
    @DisplayName("assignMedicalEmployee: should assign the employee id")
    void assignMedicalEmployee_ShouldAssign() {
        Appointment appt = buildRegularAppointment();
        appt.assignMedicalEmployee("vet-99");
        assertThat(appt.getMedicalEmployeeId()).isEqualTo("vet-99");
    }

    @Test
    @DisplayName("assignMedicalEmployee: should throw when employeeId is blank")
    void assignMedicalEmployee_WhenBlank_ShouldThrow() {
        Appointment appt = buildRegularAppointment();
        assertThatThrownBy(() -> appt.assignMedicalEmployee(""))
                .isInstanceOf(AppointmentValidationException.class)
                .hasMessageContaining("medicalEmployeeId");
    }

    // =========================================================================
    // Domain events lifecycle
    // =========================================================================

    @Test
    @DisplayName("getDomainEvents: should return immutable list")
    void getDomainEvents_ShouldReturnImmutableList() {
        Appointment appt = buildRegularAppointment();
        assertThatThrownBy(() -> appt.getDomainEvents().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("clearDomainEvents: should empty the events list")
    void clearDomainEvents_ShouldEmptyEvents() {
        Appointment appt = buildRegularAppointment();
        assertThat(appt.getDomainEvents()).isNotEmpty();

        appt.clearDomainEvents();

        assertThat(appt.getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Appointment buildRegularAppointment() {
        return Appointment.create(
                "clinic-1", false, AppointmentType.RUTINA, FUTURE,
                "owner-1", "Ana García", "ana@test.com", "+34600000001",
                PetSnapshot.of("pet-1", "Max", "Perro"),
                "emp-1", null, "Revisión anual", List.of(), AppointmentSource.PANEL);
    }

    private Appointment advanceToEnConsulta() {
        Appointment appt = buildRegularAppointment();
        appt.advanceStatus(AppointmentStatus.CLIENTE_LLEGADO);
        appt.advanceStatus(AppointmentStatus.PROXIMO_A_ATENDER);
        appt.advanceStatus(AppointmentStatus.EN_CONSULTA);
        return appt;
    }
}
