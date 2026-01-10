package com.datavet.datavet.clinic.domain.model;

import com.datavet.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicDeletedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicUpdatedEvent;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.model.Document;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Clinic Domain Events Tests")
class ClinicDomainEventsTest {

    @Test
    @DisplayName("Should raise ClinicCreatedEvent when clinic is created")
    void shouldRaiseClinicCreatedEventWhenClinicIsCreated() {
        // Given
        String clinicId = "DataVet";
        String clinicName = "Test Clinic";
        String legalName = "Test Legal Name";
        String legalNumber = "123456789";
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        String logoUrl = "http://example.com/logo.png";
        String subscriptionStatus = "ACTIVE";

        // When
        Clinic clinic = Clinic.create(clinicId, clinicName, legalName, legalNumber,
                address, phone, email, logoUrl, subscriptionStatus);

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(ClinicCreatedEvent.class, event, "Should be ClinicCreatedEvent");
        
        ClinicCreatedEvent createdEvent = (ClinicCreatedEvent) event;
        assertEquals(clinicId, createdEvent.getClinicId(), "Event should have correct clinic ID");
        assertEquals(clinicName, createdEvent.getClinicName(), "Event should have correct clinic name");
        assertEquals(legalName, createdEvent.getLegalName(), "Event should have correct legal name");
        assertNotNull(createdEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should raise ClinicUpdatedEvent when clinic is updated")
    void shouldRaiseClinicUpdatedEventWhenClinicIsUpdated() {
        // Given
        Address originalAddress = new Address("123 Test Street", "Test City", "12345");
        Phone originalPhone = new Phone("+1234567890");
        Email originalEmail = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Original Clinic", "Original Legal Name", "123456789",
                originalAddress, originalPhone, originalEmail, "http://example.com/logo.png", "ACTIVE");
        
        // Clear the creation event
        clinic.clearDomainEvents();
        
        // When
        Address updatedAddress = new Address("456 Updated Street", "Updated City", "54321");
        Phone updatedPhone = new Phone("+0987654321");
        Email updatedEmail = new Email("updated@example.com");
        
        clinic.update("Updated Clinic", "Updated Legal Name", "987654321",
                updatedAddress, updatedPhone, updatedEmail, "http://example.com/updated-logo.png", "PREMIUM");

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(ClinicUpdatedEvent.class, event, "Should be ClinicUpdatedEvent");
        
        ClinicUpdatedEvent updatedEvent = (ClinicUpdatedEvent) event;
        assertEquals(1L, updatedEvent.getClinicId(), "Event should have correct clinic ID");
        assertEquals("Updated Clinic", updatedEvent.getClinicName(), "Event should have correct updated clinic name");
        assertNotNull(updatedEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should raise ClinicDeletedEvent when clinic is deleted")
    void shouldRaiseClinicDeletedEventWhenClinicIsDeleted() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");
        
        // Clear the creation event
        clinic.clearDomainEvents();
        
        // When
        clinic.delete();

        // Then
        List<DomainEvent> domainEvents = clinic.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(ClinicDeletedEvent.class, event, "Should be ClinicDeletedEvent");
        
        ClinicDeletedEvent deletedEvent = (ClinicDeletedEvent) event;
        assertEquals(1L, deletedEvent.getClinicId(), "Event should have correct clinic ID");
        assertEquals("Test Clinic", deletedEvent.getClinicName(), "Event should have correct clinic name");
        assertNotNull(deletedEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should implement Document interface correctly")
    void shouldImplementEntityInterfaceCorrectly() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");

        // When & Then
        assertEquals(1L, clinic.getId(), "getId() should return the clinic ID");
        assertEquals(1L, clinic.getClinicID(), "getClinicID() should return the same value as getId()");
        
        // Test that clinic is an instance of Document
        assertTrue(clinic instanceof Document,
                "Clinic should implement Document interface");
        
        // Test entity identity consistency
        Clinic sameClinic = Clinic.create("ClinicId", "Different Name", "Different Legal Name", "987654321",
                address, phone, email, "http://example.com/different-logo.png", "PREMIUM");
        assertEquals(clinic.getId(), sameClinic.getId(), "Clinics with same ID should have same identity");
        
        // Test different entity identity
        Clinic differentClinic = Clinic.create("ClinicId2", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");
        assertNotEquals(clinic.getId(), differentClinic.getId(), "Clinics with different IDs should have different identity");
    }

    @Test
    @DisplayName("Should use value objects correctly")
    void shouldUseValueObjectsCorrectly() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        // When
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");

        // Then
        assertNotNull(clinic.getAddress(), "Address should not be null");
        assertEquals("123 Test Street", clinic.getAddress().getStreet(), "Address street should be correct");
        assertEquals("Test City", clinic.getAddress().getCity(), "Address city should be correct");
        assertEquals("12345", clinic.getAddress().getPostalCode(), "Address postal code should be correct");
        
        assertNotNull(clinic.getPhone(), "Phone should not be null");
        assertEquals("+1234567890", clinic.getPhone().getValue(), "Phone value should be correct");
        
        assertNotNull(clinic.getEmail(), "Email should not be null");
        assertEquals("test@example.com", clinic.getEmail().getValue(), "Email value should be correct");
    }

    @Test
    @DisplayName("Should handle value object updates correctly")
    void shouldHandleValueObjectUpdatesCorrectly() {
        // Given
        Address originalAddress = new Address("123 Test Street", "Test City", "12345");
        Phone originalPhone = new Phone("+1234567890");
        Email originalEmail = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                originalAddress, originalPhone, originalEmail, "http://example.com/logo.png", "ACTIVE");
        
        // When
        Address updatedAddress = new Address("456 Updated Street", "Updated City", "54321");
        Phone updatedPhone = new Phone("+0987654321");
        Email updatedEmail = new Email("updated@example.com");
        
        clinic.update("Updated Clinic", "Updated Legal Name", "987654321",
                updatedAddress, updatedPhone, updatedEmail, "http://example.com/updated-logo.png", "PREMIUM");

        // Then
        assertEquals("456 Updated Street", clinic.getAddress().getStreet(), "Address should be updated");
        assertEquals("Updated City", clinic.getAddress().getCity(), "City should be updated");
        assertEquals("54321", clinic.getAddress().getPostalCode(), "Postal code should be updated");
        
        assertEquals("+0987654321", clinic.getPhone().getValue(), "Phone should be updated");
        assertEquals("updated@example.com", clinic.getEmail().getValue(), "Email should be updated");
        
        assertEquals("Updated Clinic", clinic.getClinicName(), "Clinic name should be updated");
        assertEquals("Updated Legal Name", clinic.getLegalName(), "Legal name should be updated");
        assertEquals("987654321", clinic.getLegalNumber(), "Legal number should be updated");
        assertEquals("PREMIUM", clinic.getSuscriptionStatus(), "Subscription status should be updated");
    }

    @Test
    @DisplayName("Should extend AggregateRoot correctly")
    void shouldExtendAggregateRootCorrectly() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        // When
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");

        // Then
        assertTrue(clinic instanceof com.datavet.datavet.shared.domain.model.AggregateRoot, 
                "Clinic should extend AggregateRoot");
        
        // Test domain events functionality
        assertNotNull(clinic.getDomainEvents(), "Should have domain events collection");
        assertFalse(clinic.getDomainEvents().isEmpty(), "Should have at least one domain event after creation");
        
        // Test that domain events are immutable
        List<DomainEvent> events = clinic.getDomainEvents();
        assertThrows(UnsupportedOperationException.class, () -> events.add(null), 
                "Domain events collection should be immutable");
        
        // Test clear domain events
        int initialEventCount = clinic.getDomainEvents().size();
        clinic.clearDomainEvents();
        assertTrue(clinic.getDomainEvents().isEmpty(), "Domain events should be cleared");
        
        // Test that new events can be added after clearing
        clinic.update("Updated Name", "Updated Legal Name", "987654321",
                address, phone, email, "http://example.com/updated-logo.png", "PREMIUM");
        assertFalse(clinic.getDomainEvents().isEmpty(), "Should have new domain events after update");
    }

    @Test
    @DisplayName("Should handle multiple domain events correctly")
    void shouldHandleMultipleDomainEventsCorrectly() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");
        
        // When - perform multiple operations without clearing events
        clinic.update("Updated Clinic", "Updated Legal Name", "987654321",
                address, phone, email, "http://example.com/updated-logo.png", "PREMIUM");
        clinic.delete();

        // Then
        List<DomainEvent> events = clinic.getDomainEvents();
        assertEquals(3, events.size(), "Should have three domain events: created, updated, deleted");
        
        // Verify event order and types
        assertInstanceOf(ClinicCreatedEvent.class, events.get(0), "First event should be ClinicCreatedEvent");
        assertInstanceOf(ClinicUpdatedEvent.class, events.get(1), "Second event should be ClinicUpdatedEvent");
        assertInstanceOf(ClinicDeletedEvent.class, events.get(2), "Third event should be ClinicDeletedEvent");
    }

    @Test
    @DisplayName("Should validate value object constraints")
    void shouldValidateValueObjectConstraints() {
        // Test Address validation
        assertThrows(IllegalArgumentException.class, () -> 
                new Address(null, "Test City", "12345"), 
                "Address should reject null street");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Address("", "Test City", "12345"), 
                "Address should reject empty street");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Address("123 Test Street", null, "12345"), 
                "Address should reject null city");
        
        // Test Email validation
        assertThrows(IllegalArgumentException.class, () -> 
                new Email(null), 
                "Email should reject null value");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Email(""), 
                "Email should reject empty value");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Email("invalid-email"), 
                "Email should reject invalid format");
        
        // Test Phone validation
        assertThrows(IllegalArgumentException.class, () -> 
                new Phone(null), 
                "Phone should reject null value");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Phone(""), 
                "Phone should reject empty value");
        
        assertThrows(IllegalArgumentException.class, () -> 
                new Phone("invalid-phone"), 
                "Phone should reject invalid format");
    }

    @Test
    @DisplayName("Should handle value object immutability correctly")
    void shouldHandleValueObjectImmutabilityCorrectly() {
        // Given
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        
        Clinic clinic = Clinic.create("ClinicId", "Test Clinic", "Test Legal Name", "123456789",
                address, phone, email, "http://example.com/logo.png", "ACTIVE");

        // When - get value objects
        Address clinicAddress = clinic.getAddress();
        Phone clinicPhone = clinic.getPhone();
        Email clinicEmail = clinic.getEmail();

        // Then - verify they are the same instances (immutable)
        assertSame(address, clinicAddress, "Address should be the same instance");
        assertSame(phone, clinicPhone, "Phone should be the same instance");
        assertSame(email, clinicEmail, "Email should be the same instance");
        
        // Verify value object methods work correctly
        assertEquals("123 Test Street, Test City 12345", address.getFullAddress(), 
                "Address should format full address correctly");
        assertEquals("test@example.com", email.toString(), 
                "Email should return value in toString");
        assertEquals("+1234567890", phone.toString(), 
                "Phone should return value in toString");
    }
}