package com.datavet.datavet.shared.infrastructure.persistence.converter;

import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for shared converters with multiple domain entities.
 * Verifies that converters work correctly from shared location with both Clinic and Owner domains.
 * Requirements: 2.4, 2.5
 */
@DataJpaTest
@ActiveProfiles("test")
class SharedConverterIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testAddress = new Address("123 Shared St", "Shared City", "12345");
        testEmail = new Email("shared@test.com");
        testPhone = new Phone("+1234567890");
    }

    @Test
    @DisplayName("Shared converters should work with ClinicEntity persistence")
    void sharedConverters_ShouldWorkWithClinicEntityPersistence() {
        // Create clinic entity with value objects
        ClinicEntity clinic = ClinicEntity.builder()
                .clinicName("Test Clinic")
                .legalName("Test Clinic Legal")
                .legalNumber("CLINIC123")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .logoUrl("http://test.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Persist the entity
        ClinicEntity savedClinic = entityManager.persistAndFlush(clinic);
        entityManager.clear();

        // Retrieve and verify value objects are properly converted
        ClinicEntity retrievedClinic = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        
        assertNotNull(retrievedClinic, "Retrieved clinic should not be null");
        
        // Verify Address conversion
        assertNotNull(retrievedClinic.getAddress(), "Address should not be null");
        assertEquals("123 Shared St", retrievedClinic.getAddress().getStreet());
        assertEquals("Shared City", retrievedClinic.getAddress().getCity());
        assertEquals("12345", retrievedClinic.getAddress().getPostalCode());
        
        // Verify Email conversion
        assertNotNull(retrievedClinic.getEmail(), "Email should not be null");
        assertEquals("shared@test.com", retrievedClinic.getEmail().getValue());
        
        // Verify Phone conversion
        assertNotNull(retrievedClinic.getPhone(), "Phone should not be null");
        assertEquals("+1234567890", retrievedClinic.getPhone().getValue());
    }

    @Test
    @DisplayName("Shared converters should work with OwnerEntity persistence")
    void sharedConverters_ShouldWorkWithOwnerEntityPersistence() {
        // Create owner entity with value objects
        OwnerEntity owner = OwnerEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .dni("12345678")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .build();

        // Persist the entity
        OwnerEntity savedOwner = entityManager.persistAndFlush(owner);
        entityManager.clear();

        // Retrieve and verify value objects are properly converted
        OwnerEntity retrievedOwner = entityManager.find(OwnerEntity.class, savedOwner.getOwnerId());
        
        assertNotNull(retrievedOwner, "Retrieved owner should not be null");
        
        // Verify Address conversion
        assertNotNull(retrievedOwner.getAddress(), "Address should not be null");
        assertEquals("123 Shared St", retrievedOwner.getAddress().getStreet());
        assertEquals("Shared City", retrievedOwner.getAddress().getCity());
        assertEquals("12345", retrievedOwner.getAddress().getPostalCode());
        
        // Verify Email conversion
        assertNotNull(retrievedOwner.getEmail(), "Email should not be null");
        assertEquals("shared@test.com", retrievedOwner.getEmail().getValue());
        
        // Verify Phone conversion
        assertNotNull(retrievedOwner.getPhone(), "Phone should not be null");
        assertEquals("+1234567890", retrievedOwner.getPhone().getValue());
    }

    @Test
    @DisplayName("Shared converters should work with both domains simultaneously")
    void sharedConverters_ShouldWorkWithBothDomainsSimultaneously() {
        // Create different value objects for each domain
        Address clinicAddress = new Address("456 Clinic Ave", "Clinic City", "54321");
        Email clinicEmail = new Email("clinic@test.com");
        Phone clinicPhone = new Phone("+0987654321");
        
        Address ownerAddress = new Address("789 Owner Blvd", "Owner City", "98765");
        Email ownerEmail = new Email("owner@test.com");
        Phone ownerPhone = new Phone("+1122334455");

        // Create and persist clinic entity
        ClinicEntity clinic = ClinicEntity.builder()
                .clinicName("Multi Domain Clinic")
                .legalName("Multi Domain Clinic Legal")
                .legalNumber("MULTI123")
                .address(clinicAddress)
                .email(clinicEmail)
                .phone(clinicPhone)
                .logoUrl("http://multi.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Create and persist owner entity
        OwnerEntity owner = OwnerEntity.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dni("87654321")
                .address(ownerAddress)
                .email(ownerEmail)
                .phone(ownerPhone)
                .build();

        ClinicEntity savedClinic = entityManager.persistAndFlush(clinic);
        OwnerEntity savedOwner = entityManager.persistAndFlush(owner);
        entityManager.clear();

        // Retrieve both entities
        ClinicEntity retrievedClinic = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        OwnerEntity retrievedOwner = entityManager.find(OwnerEntity.class, savedOwner.getOwnerId());

        // Verify clinic value objects
        assertNotNull(retrievedClinic, "Retrieved clinic should not be null");
        assertEquals("456 Clinic Ave", retrievedClinic.getAddress().getStreet());
        assertEquals("clinic@test.com", retrievedClinic.getEmail().getValue());
        assertEquals("+0987654321", retrievedClinic.getPhone().getValue());

        // Verify owner value objects
        assertNotNull(retrievedOwner, "Retrieved owner should not be null");
        assertEquals("789 Owner Blvd", retrievedOwner.getAddress().getStreet());
        assertEquals("owner@test.com", retrievedOwner.getEmail().getValue());
        assertEquals("+1122334455", retrievedOwner.getPhone().getValue());
    }

    @Test
    @DisplayName("Shared converters should handle null values consistently across domains")
    void sharedConverters_ShouldHandleNullValuesConsistentlyAcrossDomains() {
        // Create clinic with null phone (optional field)
        ClinicEntity clinic = ClinicEntity.builder()
                .clinicName("Null Phone Clinic")
                .legalName("Null Phone Clinic Legal")
                .legalNumber("NULL123")
                .address(testAddress)
                .email(testEmail)
                .phone(null) // Null phone
                .logoUrl("http://null.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        ClinicEntity savedClinic = entityManager.persistAndFlush(clinic);
        entityManager.clear();

        // Retrieve and verify null handling
        ClinicEntity retrievedClinic = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        
        assertNotNull(retrievedClinic, "Retrieved clinic should not be null");
        assertNull(retrievedClinic.getPhone(), "Phone should remain null");
        
        // Verify other converters still work
        assertNotNull(retrievedClinic.getAddress(), "Address should not be null");
        assertNotNull(retrievedClinic.getEmail(), "Email should not be null");
    }

    @Test
    @DisplayName("Shared converters should handle complex Address objects across domains")
    void sharedConverters_ShouldHandleComplexAddressObjectsAcrossDomains() {
        // Create address with all fields
        Address complexAddress = new Address("123 Complex Street, Apt 4B", "Complex City", "12345-6789");
        
        // Test with clinic
        ClinicEntity clinic = ClinicEntity.builder()
                .clinicName("Complex Address Clinic")
                .legalName("Complex Address Clinic Legal")
                .legalNumber("COMPLEX123")
                .address(complexAddress)
                .email(new Email("complex@clinic.com"))
                .phone(new Phone("+1234567890"))
                .suscriptionStatus("ACTIVE")
                .build();

        // Test with owner
        OwnerEntity owner = OwnerEntity.builder()
                .firstName("Complex")
                .lastName("Owner")
                .dni("12345678")
                .address(complexAddress)
                .email(new Email("complex@owner.com"))
                .phone(new Phone("+0987654321"))
                .build();

        ClinicEntity savedClinic = entityManager.persistAndFlush(clinic);
        OwnerEntity savedOwner = entityManager.persistAndFlush(owner);
        entityManager.clear();

        // Retrieve and verify complex address handling
        ClinicEntity retrievedClinic = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        OwnerEntity retrievedOwner = entityManager.find(OwnerEntity.class, savedOwner.getOwnerId());

        // Verify clinic address
        assertNotNull(retrievedClinic.getAddress(), "Clinic address should not be null");
        assertEquals("123 Complex Street, Apt 4B", retrievedClinic.getAddress().getStreet());
        assertEquals("Complex City", retrievedClinic.getAddress().getCity());
        assertEquals("12345-6789", retrievedClinic.getAddress().getPostalCode());

        // Verify owner address
        assertNotNull(retrievedOwner.getAddress(), "Owner address should not be null");
        assertEquals("123 Complex Street, Apt 4B", retrievedOwner.getAddress().getStreet());
        assertEquals("Complex City", retrievedOwner.getAddress().getCity());
        assertEquals("12345-6789", retrievedOwner.getAddress().getPostalCode());
    }

    @Test
    @DisplayName("Shared converters should maintain data integrity during updates")
    void sharedConverters_ShouldMaintainDataIntegrityDuringUpdates() {
        // Create and persist initial entities
        ClinicEntity clinic = ClinicEntity.builder()
                .clinicName("Update Test Clinic")
                .legalName("Update Test Clinic Legal")
                .legalNumber("UPDATE123")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .suscriptionStatus("ACTIVE")
                .build();

        OwnerEntity owner = OwnerEntity.builder()
                .firstName("Update")
                .lastName("Test")
                .dni("12345678")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .build();

        ClinicEntity savedClinic = entityManager.persistAndFlush(clinic);
        OwnerEntity savedOwner = entityManager.persistAndFlush(owner);
        entityManager.clear();

        // Update value objects
        Address newAddress = new Address("Updated Street", "Updated City", "99999");
        Email newEmail = new Email("updated@test.com");
        Phone newPhone = new Phone("+9999999999");

        // Retrieve and update clinic
        ClinicEntity clinicToUpdate = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        clinicToUpdate.setAddress(newAddress);
        clinicToUpdate.setEmail(newEmail);
        clinicToUpdate.setPhone(newPhone);
        entityManager.persistAndFlush(clinicToUpdate);

        // Retrieve and update owner
        OwnerEntity ownerToUpdate = entityManager.find(OwnerEntity.class, savedOwner.getOwnerId());
        ownerToUpdate.setAddress(newAddress);
        ownerToUpdate.setEmail(newEmail);
        ownerToUpdate.setPhone(newPhone);
        entityManager.persistAndFlush(ownerToUpdate);
        entityManager.clear();

        // Verify updates
        ClinicEntity updatedClinic = entityManager.find(ClinicEntity.class, savedClinic.getClinicId());
        OwnerEntity updatedOwner = entityManager.find(OwnerEntity.class, savedOwner.getOwnerId());

        // Verify clinic updates
        assertEquals("Updated Street", updatedClinic.getAddress().getStreet());
        assertEquals("updated@test.com", updatedClinic.getEmail().getValue());
        assertEquals("+9999999999", updatedClinic.getPhone().getValue());

        // Verify owner updates
        assertEquals("Updated Street", updatedOwner.getAddress().getStreet());
        assertEquals("updated@test.com", updatedOwner.getEmail().getValue());
        assertEquals("+9999999999", updatedOwner.getPhone().getValue());
    }
}