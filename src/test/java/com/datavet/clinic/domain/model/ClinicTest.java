package com.datavet.clinic.domain.model;

import com.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.clinic.domain.event.ClinicDeactivatedEvent;
import com.datavet.clinic.domain.event.ClinicPendingCreatedEvent;
import com.datavet.clinic.domain.event.ClinicUpdatedEvent;
import com.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Clinic Domain Model Tests")
class ClinicTest {

    private Address address;
    private Phone phone;
    private Email email;
    private ClinicSchedule schedule;

    @BeforeEach
    void setUp() {
        address  = new Address("Calle Test 1", "Madrid", "28001");
        phone    = new Phone("+34912345678");
        email    = new Email("clinica@test.com");
        schedule = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra fines de semana");
    }

    // =========================================================================
    // create — estructura y campos
    // =========================================================================

    @Test
    @DisplayName("create: should set all fields correctly")
    void create_ShouldSetAllFields() {
        Clinic clinic = buildActiveClinic();

        assertThat(clinic.getClinicName()).isEqualTo("Clínica Test");
        assertThat(clinic.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(clinic.getLegalNumber()).isEqualTo("12345678A");
        assertThat(clinic.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(clinic.getAddress()).isEqualTo(address);
        assertThat(clinic.getPhone()).isEqualTo(phone);
        assertThat(clinic.getEmail()).isEqualTo(email);
        assertThat(clinic.getLogoUrl()).isEqualTo("https://example.com/logo.png");
        assertThat(clinic.getSchedule()).isEqualTo(schedule);
    }

    @Test
    @DisplayName("create: should generate a non-null UUID")
    void create_ShouldGenerateUUID() {
        Clinic clinic = buildActiveClinic();

        assertThat(clinic.getClinicID()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("create: should set status to ACTIVE")
    void create_ShouldSetActiveStatus() {
        Clinic clinic = buildActiveClinic();

        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.ACTIVE);
    }

    @Test
    @DisplayName("create: should set createdAt and updatedAt")
    void create_ShouldSetTimestamps() {
        LocalDateTime before = LocalDateTime.now();
        Clinic clinic = buildActiveClinic();
        LocalDateTime after = LocalDateTime.now();

        assertThat(clinic.getCreatedAt()).isNotNull();
        assertThat(clinic.getUpdatedAt()).isNotNull();
        assertThat(clinic.getCreatedAt()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
        assertThat(clinic.getUpdatedAt()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
    }

    @Test
    @DisplayName("create: should implement Document and AggregateRoot")
    void create_ShouldImplementInterfaces() {
        Clinic clinic = buildActiveClinic();

        assertThat(clinic).isInstanceOf(Document.class);
        assertThat(clinic).isInstanceOf(AggregateRoot.class);
        assertThat(clinic.getId()).isEqualTo(clinic.getClinicID());
    }

    @Test
    @DisplayName("create: should raise ClinicCreatedEvent")
    void create_ShouldRaiseClinicCreatedEvent() {
        Clinic clinic = buildActiveClinic();

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ClinicCreatedEvent.class);

        ClinicCreatedEvent event = (ClinicCreatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo(clinic.getClinicID());
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(event.getOccurredOn()).isNotNull();
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when clinicName is blank")
    void create_WhenClinicNameIsBlank_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Nombre");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when legalName is blank")
    void create_WhenLegalNameIsBlank_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Nombre legal");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when legalNumber is blank")
    void create_WhenLegalNumberIsBlank_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Numero legal");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when legalType is null")
    void create_WhenLegalTypeIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                null, address, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Tipo de persona");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when address is null")
    void create_WhenAddressIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, null, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Dirección");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when phone is null")
    void create_WhenPhoneIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, null, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Telefono");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when email is null")
    void create_WhenEmailIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, null,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Email");
    }

    @Test
    @DisplayName("create: should throw ClinicValidationException when schedule is null")
    void create_WhenScheduleIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Horario");
    }

    // =========================================================================
    // createPending — estructura y campos
    // =========================================================================

    @Test
    @DisplayName("createPending: should set only basic fields")
    void createPending_ShouldSetOnlyBasicFields() {
        Clinic clinic = buildPendingClinic();

        assertThat(clinic.getClinicName()).isEqualTo("Clínica Test");
        assertThat(clinic.getEmail()).isEqualTo(email);
        assertThat(clinic.getPhone()).isEqualTo(phone);
        assertThat(clinic.getLegalName()).isNull();
        assertThat(clinic.getLegalNumber()).isNull();
        assertThat(clinic.getLegalType()).isNull();
        assertThat(clinic.getAddress()).isNull();
        assertThat(clinic.getSchedule()).isNull();
    }

    @Test
    @DisplayName("createPending: should set status to PENDING_SETUP")
    void createPending_ShouldSetPendingStatus() {
        Clinic clinic = buildPendingClinic();

        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.PENDING_SETUP);
    }

    @Test
    @DisplayName("createPending: should generate a non-null UUID")
    void createPending_ShouldGenerateUUID() {
        Clinic clinic = buildPendingClinic();

        assertThat(clinic.getClinicID()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("createPending: should set createdAt and leave updatedAt null")
    void createPending_ShouldSetCreatedAtAndNullUpdatedAt() {
        Clinic clinic = buildPendingClinic();

        assertThat(clinic.getCreatedAt()).isNotNull();
        assertThat(clinic.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("createPending: should raise ClinicPendingCreatedEvent")
    void createPending_ShouldRaisePendingCreatedEvent() {
        Clinic clinic = buildPendingClinic();

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ClinicPendingCreatedEvent.class);

        ClinicPendingCreatedEvent event = (ClinicPendingCreatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo(clinic.getClinicID());
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getOccurredOn()).isNotNull();
    }

    @Test
    @DisplayName("createPending: should throw ClinicValidationException when clinicName is blank")
    void createPending_WhenClinicNameIsBlank_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.createPending("", email, phone))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Nombre");
    }

    @Test
    @DisplayName("createPending: should throw ClinicValidationException when email is null")
    void createPending_WhenEmailIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.createPending("Clínica Test", null, phone))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Email");
    }

    @Test
    @DisplayName("createPending: should throw ClinicValidationException when phone is null")
    void createPending_WhenPhoneIsNull_ShouldThrow() {
        assertThatThrownBy(() -> Clinic.createPending("Clínica Test", email, null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("Telefono");
    }

    // =========================================================================
    // completeSetup
    // =========================================================================

    @Test
    @DisplayName("completeSetup: should transition status from PENDING_SETUP to ACTIVE")
    void completeSetup_ShouldTransitionToActive() {
        Clinic clinic = buildPendingClinic();
        clinic.clearDomainEvents();

        clinic.completeSetup("Clínica Test S.L.", "12345678A", LegalType.AUTONOMO,
                address, phone, email, "https://example.com/logo.png", schedule);

        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.ACTIVE);
    }

    @Test
    @DisplayName("completeSetup: should fill all legal fields")
    void completeSetup_ShouldFillAllFields() {
        Clinic clinic = buildPendingClinic();

        clinic.completeSetup("Clínica Test S.L.", "12345678A", LegalType.AUTONOMO,
                address, phone, email, "https://example.com/logo.png", schedule);

        assertThat(clinic.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(clinic.getLegalNumber()).isEqualTo("12345678A");
        assertThat(clinic.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(clinic.getAddress()).isEqualTo(address);
        assertThat(clinic.getSchedule()).isEqualTo(schedule);
        assertThat(clinic.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("completeSetup: should raise ClinicCreatedEvent")
    void completeSetup_ShouldRaiseClinicCreatedEvent() {
        Clinic clinic = buildPendingClinic();
        clinic.clearDomainEvents();

        clinic.completeSetup("Clínica Test S.L.", "12345678A", LegalType.AUTONOMO,
                address, phone, email, "https://example.com/logo.png", schedule);

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ClinicCreatedEvent.class);
    }

    @Test
    @DisplayName("completeSetup: should throw ClinicValidationException when clinic is not PENDING_SETUP")
    void completeSetup_WhenNotPending_ShouldThrow() {
        Clinic active = buildActiveClinic();

        assertThatThrownBy(() -> active.completeSetup(
                "Clínica Test S.L.", "12345678A", LegalType.AUTONOMO,
                address, phone, email, "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("PENDING_SETUP");
    }

    // =========================================================================
    // update
    // =========================================================================

    @Test
    @DisplayName("update: should update all fields correctly")
    void update_ShouldUpdateAllFields() {
        Clinic clinic = buildActiveClinic();
        Email newEmail = new Email("nueva@clinica.com");
        Phone newPhone = new Phone("+34911111111");
        Address newAddress = new Address("Calle Nueva 2", "Barcelona", "08001");

        clinic.update("Clínica Nueva", "Clínica Nueva S.L.", "87654321B",
                LegalType.SOCIEDAD_LIMITADA, newAddress, newPhone, newEmail,
                "https://example.com/logo-nuevo.png", schedule);

        assertThat(clinic.getClinicName()).isEqualTo("Clínica Nueva");
        assertThat(clinic.getLegalName()).isEqualTo("Clínica Nueva S.L.");
        assertThat(clinic.getLegalNumber()).isEqualTo("87654321B");
        assertThat(clinic.getLegalType()).isEqualTo(LegalType.SOCIEDAD_LIMITADA);
        assertThat(clinic.getAddress()).isEqualTo(newAddress);
        assertThat(clinic.getPhone()).isEqualTo(newPhone);
        assertThat(clinic.getEmail()).isEqualTo(newEmail);
        assertThat(clinic.getLogoUrl()).isEqualTo("https://example.com/logo-nuevo.png");
    }

    @Test
    @DisplayName("update: should update updatedAt but not createdAt")
    void update_ShouldUpdateUpdatedAtButNotCreatedAt() {
        Clinic clinic = buildActiveClinic();
        LocalDateTime originalCreatedAt = clinic.getCreatedAt();

        clinic.update("Clínica Nueva", "Clínica Nueva S.L.", "87654321B",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);

        assertThat(clinic.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(clinic.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("update: should raise ClinicUpdatedEvent")
    void update_ShouldRaiseClinicUpdatedEvent() {
        Clinic clinic = buildActiveClinic();
        clinic.clearDomainEvents();

        clinic.update("Clínica Nueva", "Clínica Nueva S.L.", "87654321B",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ClinicUpdatedEvent.class);

        ClinicUpdatedEvent event = (ClinicUpdatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo(clinic.getClinicID());
        assertThat(event.getClinicName()).isEqualTo("Clínica Nueva");
    }

    @Test
    @DisplayName("update: should throw ClinicValidationException when clinic is DEACTIVATED")
    void update_WhenClinicIsDeactivated_ShouldThrow() {
        Clinic clinic = buildActiveClinic();
        clinic.deactivate("Cierre temporal");

        assertThatThrownBy(() -> clinic.update(
                "Clínica Nueva", "Clínica Nueva S.L.", "87654321B",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("desactivada");
    }

    // =========================================================================
    // deactivate
    // =========================================================================

    @Test
    @DisplayName("deactivate: should set status to DEACTIVATED")
    void deactivate_ShouldSetDeactivatedStatus() {
        Clinic clinic = buildActiveClinic();
        clinic.deactivate("Cierre temporal");

        assertThat(clinic.getStatus()).isEqualTo(ClinicStatus.DEACTIVATED);
    }

    @Test
    @DisplayName("deactivate: should update updatedAt")
    void deactivate_ShouldUpdateUpdatedAt() {
        Clinic clinic = buildActiveClinic();
        clinic.deactivate("Cierre temporal");

        assertThat(clinic.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("deactivate: should raise ClinicDeactivatedEvent with reason")
    void deactivate_ShouldRaiseDeactivatedEventWithReason() {
        Clinic clinic = buildActiveClinic();
        clinic.clearDomainEvents();

        clinic.deactivate("Cierre temporal");

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ClinicDeactivatedEvent.class);

        ClinicDeactivatedEvent event = (ClinicDeactivatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo(clinic.getClinicID());
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getReason()).isEqualTo("Cierre temporal");
    }

    @Test
    @DisplayName("deactivate: should throw ClinicValidationException when already deactivated")
    void deactivate_WhenAlreadyDeactivated_ShouldThrow() {
        Clinic clinic = buildActiveClinic();
        clinic.deactivate("Cierre temporal");

        assertThatThrownBy(() -> clinic.deactivate("Segunda vez"))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("desactivada");
    }

    // =========================================================================
    // Domain events — ciclo de vida
    // =========================================================================

    @Test
    @DisplayName("getDomainEvents: should return immutable list")
    void getDomainEvents_ShouldReturnImmutableList() {
        Clinic clinic = buildActiveClinic();

        assertThatThrownBy(() -> clinic.getDomainEvents().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("clearDomainEvents: should empty the events list")
    void clearDomainEvents_ShouldEmptyEvents() {
        Clinic clinic = buildActiveClinic();
        assertThat(clinic.getDomainEvents()).isNotEmpty();

        clinic.clearDomainEvents();

        assertThat(clinic.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should accumulate multiple events without clearing")
    void shouldAccumulateMultipleEvents() {
        Clinic clinic = buildActiveClinic();

        clinic.update("Clínica Nueva", "Clínica Nueva S.L.", "87654321B",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);

        clinic.deactivate("Cierre temporal");

        List<DomainEvent> events = clinic.getDomainEvents();
        assertThat(events).hasSize(3);
        assertThat(events.get(0)).isInstanceOf(ClinicCreatedEvent.class);
        assertThat(events.get(1)).isInstanceOf(ClinicUpdatedEvent.class);
        assertThat(events.get(2)).isInstanceOf(ClinicDeactivatedEvent.class);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Clinic buildActiveClinic() {
        return Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);
    }

    private Clinic buildPendingClinic() {
        return Clinic.createPending("Clínica Test", email, phone);
    }
}