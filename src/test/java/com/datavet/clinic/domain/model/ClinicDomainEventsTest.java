package com.datavet.clinic.domain.model;

import com.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.clinic.domain.event.ClinicDeactivatedEvent;
import com.datavet.clinic.domain.event.ClinicPendingCreatedEvent;
import com.datavet.clinic.domain.event.ClinicUpdatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Clinic Domain Events Tests")
class ClinicDomainEventsTest {

    // =========================================================================
    // ClinicCreatedEvent
    // =========================================================================

    @Test
    @DisplayName("ClinicCreatedEvent.of() should set all fields correctly")
    void clinicCreatedEvent_ShouldSetAllFields() {
        ClinicCreatedEvent event = ClinicCreatedEvent.of("clinic-1", "Clínica Test", "Clínica Test S.L.");

        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(event.getOccurredOn()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("ClinicCreatedEvent.occurredOn() should match getOccurredOn()")
    void clinicCreatedEvent_OccurredOnShouldBeConsistent() {
        ClinicCreatedEvent event = ClinicCreatedEvent.of("clinic-1", "Clínica Test", "Clínica Test S.L.");

        assertThat(event.occurredOn()).isEqualTo(event.getOccurredOn());
    }

    @Test
    @DisplayName("ClinicCreatedEvent should implement DomainEvent")
    void clinicCreatedEvent_ShouldImplementDomainEvent() {
        ClinicCreatedEvent event = ClinicCreatedEvent.of("clinic-1", "Clínica Test", "Clínica Test S.L.");

        assertThat(event).isInstanceOf(com.datavet.shared.domain.event.DomainEvent.class);
    }

    // =========================================================================
    // ClinicPendingCreatedEvent
    // =========================================================================

    @Test
    @DisplayName("ClinicPendingCreatedEvent.of() should set all fields correctly")
    void clinicPendingCreatedEvent_ShouldSetAllFields() {
        ClinicPendingCreatedEvent event = ClinicPendingCreatedEvent.of("clinic-1", "Clínica Test");

        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getOccurredOn()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("ClinicPendingCreatedEvent.occurredOn() should match getOccurredOn()")
    void clinicPendingCreatedEvent_OccurredOnShouldBeConsistent() {
        ClinicPendingCreatedEvent event = ClinicPendingCreatedEvent.of("clinic-1", "Clínica Test");

        assertThat(event.occurredOn()).isEqualTo(event.getOccurredOn());
    }

    @Test
    @DisplayName("ClinicPendingCreatedEvent should implement DomainEvent")
    void clinicPendingCreatedEvent_ShouldImplementDomainEvent() {
        ClinicPendingCreatedEvent event = ClinicPendingCreatedEvent.of("clinic-1", "Clínica Test");

        assertThat(event).isInstanceOf(com.datavet.shared.domain.event.DomainEvent.class);
    }

    // =========================================================================
    // ClinicUpdatedEvent
    // =========================================================================

    @Test
    @DisplayName("ClinicUpdatedEvent.of() should set all fields correctly")
    void clinicUpdatedEvent_ShouldSetAllFields() {
        ClinicUpdatedEvent event = ClinicUpdatedEvent.of("clinic-1", "Clínica Actualizada");

        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getClinicName()).isEqualTo("Clínica Actualizada");
        assertThat(event.getOccurredOn()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("ClinicUpdatedEvent.occurredOn() should match getOccurredOn()")
    void clinicUpdatedEvent_OccurredOnShouldBeConsistent() {
        ClinicUpdatedEvent event = ClinicUpdatedEvent.of("clinic-1", "Clínica Actualizada");

        assertThat(event.occurredOn()).isEqualTo(event.getOccurredOn());
    }

    @Test
    @DisplayName("ClinicUpdatedEvent should implement DomainEvent")
    void clinicUpdatedEvent_ShouldImplementDomainEvent() {
        ClinicUpdatedEvent event = ClinicUpdatedEvent.of("clinic-1", "Clínica Actualizada");

        assertThat(event).isInstanceOf(com.datavet.shared.domain.event.DomainEvent.class);
    }

    // =========================================================================
    // ClinicDeactivatedEvent
    // =========================================================================

    @Test
    @DisplayName("ClinicDeactivatedEvent.of() should set all fields correctly")
    void clinicDeactivatedEvent_ShouldSetAllFields() {
        ClinicDeactivatedEvent event = ClinicDeactivatedEvent.of("clinic-1", "Clínica Test", "Cierre temporal");

        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getClinicName()).isEqualTo("Clínica Test");
        assertThat(event.getReason()).isEqualTo("Cierre temporal");
        assertThat(event.getOccurredOn()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("ClinicDeactivatedEvent.occurredOn() should match getOccurredOn()")
    void clinicDeactivatedEvent_OccurredOnShouldBeConsistent() {
        ClinicDeactivatedEvent event = ClinicDeactivatedEvent.of("clinic-1", "Clínica Test", "Cierre temporal");

        assertThat(event.occurredOn()).isEqualTo(event.getOccurredOn());
    }

    @Test
    @DisplayName("ClinicDeactivatedEvent should implement DomainEvent")
    void clinicDeactivatedEvent_ShouldImplementDomainEvent() {
        ClinicDeactivatedEvent event = ClinicDeactivatedEvent.of("clinic-1", "Clínica Test", "Cierre temporal");

        assertThat(event).isInstanceOf(com.datavet.shared.domain.event.DomainEvent.class);
    }
}