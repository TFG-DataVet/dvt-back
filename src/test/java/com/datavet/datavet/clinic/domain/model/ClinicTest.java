package com.datavet.datavet.clinic.domain.model;

import com.datavet.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicDeletedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicUpdatedEvent;
import com.datavet.datavet.clinic.testutil.ClinicTestDataBuilder;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Clinic domain model.
 * Tests focus on factory methods, field updates, and timestamp management.
 */
class ClinicTest {

    @Test
    void shouldCreateClinicWithValidData() {
        // Given
        String clinicId = "hola";
        String clinicName = "Test Clinic";
        String legalName = "Test Clinic Legal Name";
        String legalNumber = "12345678";
        Address address = ClinicTestDataBuilder.aValidAddress();
        Phone phone = ClinicTestDataBuilder.aValidPhone();
        Email email = ClinicTestDataBuilder.aValidEmail();
        String logoUrl = "https://example.com/logo.png";
        String subscriptionStatus = "ACTIVE";

        // When
        Clinic clinic = Clinic.create(
                clinicId,
                clinicName,
                legalName,
                legalNumber,
                address,
                phone,
                email,
                logoUrl,
                subscriptionStatus
        );

        // Then
        assertThat(clinic).isNotNull();
        assertThat(clinic.getClinicID()).isEqualTo(clinicId);
        assertThat(clinic.getClinicName()).isEqualTo(clinicName);
        assertThat(clinic.getLegalName()).isEqualTo(legalName);
        assertThat(clinic.getLegalNumber()).isEqualTo(legalNumber);
        assertThat(clinic.getAddress()).isEqualTo(address);
        assertThat(clinic.getPhone()).isEqualTo(phone);
        assertThat(clinic.getEmail()).isEqualTo(email);
        assertThat(clinic.getLogoUrl()).isEqualTo(logoUrl);
        assertThat(clinic.getSuscriptionStatus()).isEqualTo(subscriptionStatus);
    }

    @Test
    void shouldGenerateCreatedAtAndUpdatedAtOnCreation() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();

        // Then
        LocalDateTime afterCreation = LocalDateTime.now();
        
        assertThat(clinic.getCreatedAt()).isNotNull();
        assertThat(clinic.getUpdatedAt()).isNotNull();
        assertThat(clinic.getCreatedAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(clinic.getCreatedAt()).isBeforeOrEqualTo(afterCreation);
        assertThat(clinic.getUpdatedAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(clinic.getUpdatedAt()).isBeforeOrEqualTo(afterCreation);
    }

    @Test
    void shouldUpdateClinicFieldsCorrectly() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        
        String newClinicName = "Updated Clinic Name";
        String newLegalName = "Updated Legal Name";
        String newLegalNumber = "87654321";
        Address newAddress = new Address("456 Oak St", "Shelbyville", "54321");
        Phone newPhone = new Phone("+0987654321");
        Email newEmail = new Email("updated@clinic.com");
        String newLogoUrl = "https://example.com/new-logo.png";
        String newSubscriptionStatus = "INACTIVE";

        // When
        clinic.update(
                newClinicName,
                newLegalName,
                newLegalNumber,
                newAddress,
                newPhone,
                newEmail,
                newLogoUrl,
                newSubscriptionStatus
        );

        // Then
        assertThat(clinic.getClinicName()).isEqualTo(newClinicName);
        assertThat(clinic.getLegalName()).isEqualTo(newLegalName);
        assertThat(clinic.getLegalNumber()).isEqualTo(newLegalNumber);
        assertThat(clinic.getAddress()).isEqualTo(newAddress);
        assertThat(clinic.getPhone()).isEqualTo(newPhone);
        assertThat(clinic.getEmail()).isEqualTo(newEmail);
        assertThat(clinic.getLogoUrl()).isEqualTo(newLogoUrl);
        assertThat(clinic.getSuscriptionStatus()).isEqualTo(newSubscriptionStatus);
    }

    @Test
    void shouldUpdateUpdatedAtOnUpdate() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        LocalDateTime originalUpdatedAt = clinic.getUpdatedAt();
        
        // Wait a small amount to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        clinic.update(
                "Updated Name",
                clinic.getLegalName(),
                clinic.getLegalNumber(),
                clinic.getAddress(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getLogoUrl(),
                clinic.getSuscriptionStatus()
        );

        // Then
        assertThat(clinic.getUpdatedAt()).isNotNull();
        assertThat(clinic.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldNotModifyCreatedAtOnUpdate() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        LocalDateTime originalCreatedAt = clinic.getCreatedAt();

        // When
        clinic.update(
                "Updated Name",
                clinic.getLegalName(),
                clinic.getLegalNumber(),
                clinic.getAddress(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getLogoUrl(),
                clinic.getSuscriptionStatus()
        );

        // Then
        assertThat(clinic.getCreatedAt()).isNotNull();
        assertThat(clinic.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    void shouldGenerateClinicCreatedEventOnCreation() {
        // Given
        String clinicId = "hola";
        String clinicName = "Test Clinic";
        String legalName = "Test Clinic Legal Name";
        String legalNumber = "12345678";
        Address address = ClinicTestDataBuilder.aValidAddress();
        Phone phone = ClinicTestDataBuilder.aValidPhone();
        Email email = ClinicTestDataBuilder.aValidEmail();
        String logoUrl = "https://example.com/logo.png";
        String subscriptionStatus = "ACTIVE";

        // When
        Clinic clinic = Clinic.create(
                clinicId,
                clinicName,
                legalName,
                legalNumber,
                address,
                phone,
                email,
                logoUrl,
                subscriptionStatus
        );

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertThat(domainEvents).isNotEmpty();
        assertThat(domainEvents).hasSize(1);
        
        DomainEvent event = domainEvents.get(0);
        assertThat(event).isInstanceOf(ClinicCreatedEvent.class);
        
        ClinicCreatedEvent createdEvent = (ClinicCreatedEvent) event;
        assertThat(createdEvent.getClinicId()).isEqualTo(clinicId);
        assertThat(createdEvent.getClinicName()).isEqualTo(clinicName);
        assertThat(createdEvent.getLegalName()).isEqualTo(legalName);
        assertThat(createdEvent.getOccurredOn()).isNotNull();
    }

    @Test
    void shouldGenerateClinicUpdatedEventOnUpdate() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        clinic.clearDomainEvents(); // Clear creation event
        
        String updatedClinicName = "Updated Clinic Name";
        String updatedLegalName = "Updated Legal Name";
        String updatedLegalNumber = "87654321";
        Address updatedAddress = new Address("456 Oak St", "Shelbyville", "54321");
        Phone updatedPhone = new Phone("+0987654321");
        Email updatedEmail = new Email("updated@clinic.com");
        String updatedLogoUrl = "https://example.com/new-logo.png";
        String updatedSubscriptionStatus = "PREMIUM";

        // When
        clinic.update(
                updatedClinicName,
                updatedLegalName,
                updatedLegalNumber,
                updatedAddress,
                updatedPhone,
                updatedEmail,
                updatedLogoUrl,
                updatedSubscriptionStatus
        );

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertThat(domainEvents).isNotEmpty();
        assertThat(domainEvents).hasSize(1);
        
        DomainEvent event = domainEvents.get(0);
        assertThat(event).isInstanceOf(ClinicUpdatedEvent.class);
        
        ClinicUpdatedEvent updatedEvent = (ClinicUpdatedEvent) event;
        assertThat(updatedEvent.getClinicId()).isEqualTo(clinic.getClinicID());
        assertThat(updatedEvent.getClinicName()).isEqualTo(updatedClinicName);
        assertThat(updatedEvent.getOccurredOn()).isNotNull();
    }

    @Test
    void shouldGenerateClinicDeletedEventOnDelete() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        String clinicId = clinic.getClinicID();
        String clinicName = clinic.getClinicName();
        clinic.clearDomainEvents(); // Clear creation event

        // When
        clinic.delete();

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertThat(domainEvents).isNotEmpty();
        assertThat(domainEvents).hasSize(1);
        
        DomainEvent event = domainEvents.get(0);
        assertThat(event).isInstanceOf(ClinicDeletedEvent.class);
        
        ClinicDeletedEvent deletedEvent = (ClinicDeletedEvent) event;
        assertThat(deletedEvent.getClinicId()).isEqualTo(clinicId);
        assertThat(deletedEvent.getClinicName()).isEqualTo(clinicName);
        assertThat(deletedEvent.getOccurredOn()).isNotNull();
    }

    @Test
    void shouldClearDomainEventsAfterRetrieval() {
        // Given
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();
        
        // Verify events exist
        assertThat(clinic.getDomainEvents()).isNotEmpty();
        int initialEventCount = clinic.getDomainEvents().size();
        assertThat(initialEventCount).isGreaterThan(0);

        // When
        clinic.clearDomainEvents();

        // Then
        assertThat(clinic.getDomainEvents()).isEmpty();
        
        // Verify new events can be added after clearing
        clinic.update(
                "Updated Name",
                clinic.getLegalName(),
                clinic.getLegalNumber(),
                clinic.getAddress(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getLogoUrl(),
                clinic.getSuscriptionStatus()
        );
        
        assertThat(clinic.getDomainEvents()).isNotEmpty();
        assertThat(clinic.getDomainEvents()).hasSize(1);
        assertThat(clinic.getDomainEvents().get(0)).isInstanceOf(ClinicUpdatedEvent.class);
    }
}
