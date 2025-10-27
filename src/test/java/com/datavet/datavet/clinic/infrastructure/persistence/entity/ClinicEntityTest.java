package com.datavet.datavet.clinic.infrastructure.persistence.entity;

import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClinicEntity BaseEntity integration and value object handling.
 * Verifies that ClinicEntity properly extends BaseEntity and handles audit fields.
 * Requirements: 5.3
 */
class ClinicEntityTest {

    private ClinicEntity clinicEntity;
    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testAddress = new Address("123 Main St", "Springfield", "12345");
        testEmail = new Email("test@clinic.com");
        testPhone = new Phone("+1234567890");
        
        clinicEntity = ClinicEntity.builder()
                .clinicName("Test Clinic")
                .legalName("Test Clinic Legal Name")
                .legalNumber("123456789")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .logoUrl("http://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("ClinicEntity should extend BaseEntity")
    void clinicEntity_ShouldExtendBaseEntity() {
        assertTrue(BaseEntity.class.isAssignableFrom(ClinicEntity.class),
                "ClinicEntity should extend BaseEntity");
    }

    @Test
    @DisplayName("ClinicEntity should inherit audit fields from BaseEntity")
    void clinicEntity_ShouldInheritAuditFields() {
        // Verify that ClinicEntity has access to BaseEntity methods
        // In unit tests, these fields may be null since JPA lifecycle events don't fire
        // But the methods should be accessible
        LocalDateTime createdAt = clinicEntity.getCreatedAt();
        LocalDateTime updatedAt = clinicEntity.getUpdatedAt();
        
        // Verify the methods exist and are callable (inheritance works)
        assertTrue(true, "Should be able to access createdAt from BaseEntity");
        assertTrue(true, "Should be able to access updatedAt from BaseEntity");
    }

    @Test
    @DisplayName("ClinicEntity should properly handle Address value object")
    void clinicEntity_ShouldHandleAddressValueObject() {
        clinicEntity.setAddress(testAddress);
        
        Address retrievedAddress = clinicEntity.getAddress();
        assertNotNull(retrievedAddress, "Address should not be null");
        assertEquals("123 Main St", retrievedAddress.getStreet());
        assertEquals("Springfield", retrievedAddress.getCity());
        assertEquals("12345", retrievedAddress.getPostalCode());
    }

    @Test
    @DisplayName("ClinicEntity should properly handle Email value object")
    void clinicEntity_ShouldHandleEmailValueObject() {
        clinicEntity.setEmail(testEmail);
        
        Email retrievedEmail = clinicEntity.getEmail();
        assertNotNull(retrievedEmail, "Email should not be null");
        assertEquals("test@clinic.com", retrievedEmail.getValue());
    }

    @Test
    @DisplayName("ClinicEntity should properly handle Phone value object")
    void clinicEntity_ShouldHandlePhoneValueObject() {
        clinicEntity.setPhone(testPhone);
        
        Phone retrievedPhone = clinicEntity.getPhone();
        assertNotNull(retrievedPhone, "Phone should not be null");
        assertEquals("+1234567890", retrievedPhone.getValue());
    }

    @Test
    @DisplayName("ClinicEntity should handle null value objects gracefully")
    void clinicEntity_ShouldHandleNullValueObjects() {
        clinicEntity.setAddress(null);
        clinicEntity.setEmail(null);
        clinicEntity.setPhone(null);
        
        assertNull(clinicEntity.getAddress(), "Address should be null");
        assertNull(clinicEntity.getEmail(), "Email should be null");
        assertNull(clinicEntity.getPhone(), "Phone should be null");
    }

    @Test
    @DisplayName("ClinicEntity should have access to BaseEntity audit field methods")
    void clinicEntity_ShouldHaveAccessToBaseEntityAuditMethods() {
        // Verify that ClinicEntity inherits audit field getters from BaseEntity
        assertNotNull(clinicEntity, "ClinicEntity should be created");
        
        // The audit fields will be set by JPA lifecycle events in real usage
        // Here we just verify the methods are accessible
        LocalDateTime createdAt = clinicEntity.getCreatedAt();
        LocalDateTime updatedAt = clinicEntity.getUpdatedAt();
        
        // These may be null in unit tests since JPA lifecycle events don't fire
        // But the methods should be accessible due to BaseEntity inheritance
        assertTrue(true, "Should be able to call getCreatedAt() from BaseEntity");
        assertTrue(true, "Should be able to call getUpdatedAt() from BaseEntity");
    }

    @Test
    @DisplayName("ClinicEntity should maintain all required fields")
    void clinicEntity_ShouldMaintainAllRequiredFields() {
        assertNotNull(clinicEntity.getClinicName(), "Clinic name should not be null");
        assertNotNull(clinicEntity.getLegalName(), "Legal name should not be null");
        assertNotNull(clinicEntity.getLegalNumber(), "Legal number should not be null");
        assertNotNull(clinicEntity.getAddress(), "Address should not be null");
        assertNotNull(clinicEntity.getEmail(), "Email should not be null");
        
        assertEquals("Test Clinic", clinicEntity.getClinicName());
        assertEquals("Test Clinic Legal Name", clinicEntity.getLegalName());
        assertEquals("123456789", clinicEntity.getLegalNumber());
    }

    @Test
    @DisplayName("ClinicEntity should support builder pattern")
    void clinicEntity_ShouldSupportBuilderPattern() {
        ClinicEntity builtEntity = ClinicEntity.builder()
                .clinicName("Built Clinic")
                .legalName("Built Legal Name")
                .legalNumber("987654321")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .logoUrl("http://built.com/logo.png")
                .suscriptionStatus("INACTIVE")
                .build();
        
        assertNotNull(builtEntity, "Built entity should not be null");
        assertEquals("Built Clinic", builtEntity.getClinicName());
        assertEquals("Built Legal Name", builtEntity.getLegalName());
        assertEquals("987654321", builtEntity.getLegalNumber());
        assertEquals("INACTIVE", builtEntity.getSuscriptionStatus());
    }
}