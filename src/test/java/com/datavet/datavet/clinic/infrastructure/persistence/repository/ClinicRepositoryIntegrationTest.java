package com.datavet.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MongoClinicRepositoryAdapter MongoDB repository implementation.
 * Verifies that the repository properly works with MongoDB.
 * Requirements: 5.3
 */
@DataMongoTest
@ActiveProfiles("test")
class ClinicRepositoryIntegrationTest {

    @Autowired
    private MongoClinicRepositoryAdapter repository;

    private ClinicDocument testClinic;
    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        
        testAddress = new Address("123 Test St", "Test City", "12345");
        testEmail = new Email("test@clinic.com");
        testPhone = new Phone("+1234567890");
        
        testClinic = ClinicDocument.builder()
                .name("Test Clinic")
                .legalName("Test Clinic Legal")
                .legalNumber("TEST123")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .logoUrl("http://test.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Repository should save and retrieve clinic document with value objects")
    void repository_ShouldSaveAndRetrieveClinicWithValueObjects() {
        // Save the clinic
        ClinicDocument savedClinic = repository.save(testClinic);
        
        assertNotNull(savedClinic, "Saved clinic should not be null");
        assertNotNull(savedClinic.getId(), "Saved clinic should have an ID");
        
        // Retrieve the clinic
        Optional<ClinicDocument> retrievedClinic = repository.findById(savedClinic.getId());
        
        assertTrue(retrievedClinic.isPresent(), "Retrieved clinic should be present");
        
        ClinicDocument clinic = retrievedClinic.get();
        assertEquals("Test Clinic", clinic.getName());
        assertEquals("Test Clinic Legal", clinic.getLegalName());
        assertEquals("TEST123", clinic.getLegalNumber());
        
        // Verify value objects are properly persisted and retrieved
        assertNotNull(clinic.getAddress(), "Address should not be null");
        assertEquals("123 Test St", clinic.getAddress().getStreet());
        assertEquals("Test City", clinic.getAddress().getCity());
        assertEquals("12345", clinic.getAddress().getPostalCode());
        
        assertNotNull(clinic.getEmail(), "Email should not be null");
        assertEquals("test@clinic.com", clinic.getEmail().getValue());
        
        assertNotNull(clinic.getPhone(), "Phone should not be null");
        assertEquals("+1234567890", clinic.getPhone().getValue());
    }

    @Test
    @DisplayName("Repository should handle findAll operation")
    void repository_ShouldHandleFindAllOperation() {
        // Save multiple clinics
        repository.save(testClinic);
        
        ClinicDocument clinic2 = ClinicDocument.builder()
                .name("Second Clinic")
                .legalName("Second Legal")
                .legalNumber("SECOND123")
                .address(new Address("456 Second St", "Second City", "67890"))
                .email(new Email("second@clinic.com"))
                .phone(new Phone("+0987654321"))
                .suscriptionStatus("INACTIVE")
                .build();
        
        repository.save(clinic2);
        
        // Retrieve all clinics
        List<ClinicDocument> allClinics = repository.findAll();
        
        assertNotNull(allClinics, "All clinics list should not be null");
        assertEquals(2, allClinics.size(), "Should have exactly 2 clinics");
        
        // Verify both clinics are in the list
        boolean hasFirstClinic = allClinics.stream()
                .anyMatch(c -> "Test Clinic".equals(c.getName()));
        boolean hasSecondClinic = allClinics.stream()
                .anyMatch(c -> "Second Clinic".equals(c.getName()));
        
        assertTrue(hasFirstClinic, "Should contain first clinic");
        assertTrue(hasSecondClinic, "Should contain second clinic");
    }

    @Test
    @DisplayName("Repository should handle existsById operation")
    void repository_ShouldHandleExistsByIdOperation() {
        // Save clinic
        ClinicDocument savedClinic = repository.save(testClinic);
        
        // Test exists
        boolean exists = repository.existsById(savedClinic.getId());
        assertTrue(exists, "Clinic should exist");
        
        // Test non-existent ID
        boolean notExists = repository.existsById("non-existent-id");
        assertFalse(notExists, "Non-existent clinic should not exist");
    }

    @Test
    @DisplayName("Repository should handle deleteById operation")
    void repository_ShouldHandleDeleteByIdOperation() {
        // Save clinic
        ClinicDocument savedClinic = repository.save(testClinic);
        String clinicId = savedClinic.getId();
        
        // Verify it exists
        assertTrue(repository.existsById(clinicId), "Clinic should exist before deletion");
        
        // Delete clinic
        repository.deleteById(clinicId);
        
        // Verify it's deleted
        assertFalse(repository.existsById(clinicId), "Clinic should not exist after deletion");
        
        Optional<ClinicDocument> deletedClinic = repository.findById(clinicId);
        assertFalse(deletedClinic.isPresent(), "Deleted clinic should not be found");
    }

    @Test
    @DisplayName("Repository should handle domain-specific queries with Email value object")
    void repository_ShouldHandleDomainSpecificQueriesWithEmail() {
        // Save clinic
        repository.save(testClinic);
        
        // Test existsByEmail with Email value object
        boolean existsByEmail = repository.existsByEmail(testEmail);
        assertTrue(existsByEmail, "Should find clinic by email value object");
        
        // Test with different email
        Email differentEmail = new Email("different@clinic.com");
        boolean notExistsByEmail = repository.existsByEmail(differentEmail);
        assertFalse(notExistsByEmail, "Should not find clinic with different email");
    }

    @Test
    @DisplayName("Repository should handle domain-specific queries with legal number")
    void repository_ShouldHandleDomainSpecificQueriesWithLegalNumber() {
        // Save clinic
        repository.save(testClinic);
        
        // Test existsByLegalNumber
        boolean existsByLegalNumber = repository.existsByLegalNumber("TEST123");
        assertTrue(existsByLegalNumber, "Should find clinic by legal number");
        
        // Test with different legal number
        boolean notExistsByLegalNumber = repository.existsByLegalNumber("DIFFERENT123");
        assertFalse(notExistsByLegalNumber, "Should not find clinic with different legal number");
    }

    @Test
    @DisplayName("Repository should handle exclusion queries for updates")
    void repository_ShouldHandleExclusionQueriesForUpdates() {
        // Save clinic
        ClinicDocument savedClinic = repository.save(testClinic);
        String clinicId = savedClinic.getId();
        
        // Test existsByEmailAndIdNot - should return false for same clinic
        boolean existsByEmailExcludingSelf = repository.existsByEmailAndIdNot(testEmail, clinicId);
        assertFalse(existsByEmailExcludingSelf, 
                "Should not find email conflict when excluding the same clinic");
        
        // Test existsByLegalNumberAndIdNot - should return false for same clinic
        boolean existsByLegalNumberExcludingSelf = repository.existsByLegalNumberAndIdNot("TEST123", clinicId);
        assertFalse(existsByLegalNumberExcludingSelf, 
                "Should not find legal number conflict when excluding the same clinic");
        
        // Save another clinic with different email and legal number
        ClinicDocument anotherClinic = ClinicDocument.builder()
                .name("Another Clinic")
                .legalName("Another Legal")
                .legalNumber("ANOTHER123")
                .address(new Address("789 Another St", "Another City", "99999"))
                .email(new Email("another@clinic.com"))
                .phone(new Phone("+1111111111"))
                .suscriptionStatus("ACTIVE")
                .build();
        
        repository.save(anotherClinic);
        
        // Now test exclusion with the other clinic's email - should return true
        boolean existsByOtherEmailExcludingSelf = repository.existsByEmailAndIdNot(
                new Email("another@clinic.com"), clinicId);
        assertTrue(existsByOtherEmailExcludingSelf, 
                "Should find email conflict when checking other clinic's email");
    }

    @Test
    @DisplayName("Repository should properly handle MongoDB audit fields")
    void repository_ShouldHandleMongoAuditFields() {
        // Save clinic
        ClinicDocument savedClinic = repository.save(testClinic);
        
        // Retrieve and verify audit fields
        Optional<ClinicDocument> retrievedClinic = repository.findById(savedClinic.getId());
        assertTrue(retrievedClinic.isPresent(), "Clinic should be found");
        
        ClinicDocument clinic = retrievedClinic.get();
        assertNotNull(clinic.getCreatedAt(), "CreatedAt should be set");
        assertNotNull(clinic.getUpdatedAt(), "UpdatedAt should be set");
        
        // Update the clinic
        clinic.setName("Updated Clinic Name");
        ClinicDocument updatedClinic = repository.save(clinic);
        
        assertNotNull(updatedClinic.getUpdatedAt(), "UpdatedAt should be updated");
    }
}
