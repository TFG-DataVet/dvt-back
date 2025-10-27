package com.datavet.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.clinic.infrastructure.persistence.repository.JpaClinicRepositoryAdapter;
import com.datavet.datavet.shared.application.port.Repository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JpaClinicRepositoryAdapter shared repository implementation.
 * Verifies that the repository properly implements the shared Repository interface.
 * Requirements: 5.3
 */
@DataJpaTest
@ActiveProfiles("test")
class ClinicRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaClinicRepositoryAdapter repository;

    private ClinicEntity testClinic;
    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testAddress = new Address("123 Test St", "Test City", "12345");
        testEmail = new Email("test@clinic.com");
        testPhone = new Phone("+1234567890");
        
        testClinic = ClinicEntity.builder()
                .clinicName("Test Clinic")
                .legalName("Test Clinic Legal")
                .legalNumber("TEST123")
                .address(testAddress)
                .email(testEmail)
                .phone(testPhone)
                .logoUrl("http://test.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Repository should implement shared Repository interface")
    void repository_ShouldImplementSharedRepositoryInterface() {
        assertTrue(Repository.class.isAssignableFrom(JpaClinicRepositoryAdapter.class),
                "JpaClinicRepositoryAdapter should implement Repository interface");
    }

    @Test
    @DisplayName("Repository should save and retrieve clinic entity with value objects")
    void repository_ShouldSaveAndRetrieveClinicWithValueObjects() {
        // Save the clinic
        ClinicEntity savedClinic = repository.save(testClinic);
        
        assertNotNull(savedClinic, "Saved clinic should not be null");
        assertNotNull(savedClinic.getClinicId(), "Saved clinic should have an ID");
        
        // Flush to ensure database persistence
        entityManager.flush();
        entityManager.clear();
        
        // Retrieve the clinic
        Optional<ClinicEntity> retrievedClinic = repository.findById(savedClinic.getClinicId());
        
        assertTrue(retrievedClinic.isPresent(), "Retrieved clinic should be present");
        
        ClinicEntity clinic = retrievedClinic.get();
        assertEquals("Test Clinic", clinic.getClinicName());
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
        ClinicEntity clinic1 = repository.save(testClinic);
        
        ClinicEntity clinic2 = ClinicEntity.builder()
                .clinicName("Second Clinic")
                .legalName("Second Legal")
                .legalNumber("SECOND123")
                .address(new Address("456 Second St", "Second City", "67890"))
                .email(new Email("second@clinic.com"))
                .phone(new Phone("+0987654321"))
                .suscriptionStatus("INACTIVE")
                .build();
        
        ClinicEntity savedClinic2 = repository.save(clinic2);
        
        entityManager.flush();
        
        // Retrieve all clinics
        List<ClinicEntity> allClinics = repository.findAll();
        
        assertNotNull(allClinics, "All clinics list should not be null");
        assertTrue(allClinics.size() >= 2, "Should have at least 2 clinics");
        
        // Verify both clinics are in the list
        boolean hasFirstClinic = allClinics.stream()
                .anyMatch(c -> "Test Clinic".equals(c.getClinicName()));
        boolean hasSecondClinic = allClinics.stream()
                .anyMatch(c -> "Second Clinic".equals(c.getClinicName()));
        
        assertTrue(hasFirstClinic, "Should contain first clinic");
        assertTrue(hasSecondClinic, "Should contain second clinic");
    }

    @Test
    @DisplayName("Repository should handle existsById operation")
    void repository_ShouldHandleExistsByIdOperation() {
        // Save clinic
        ClinicEntity savedClinic = repository.save(testClinic);
        entityManager.flush();
        
        // Test exists
        boolean exists = repository.existsById(savedClinic.getClinicId());
        assertTrue(exists, "Clinic should exist");
        
        // Test non-existent ID
        boolean notExists = repository.existsById(999L);
        assertFalse(notExists, "Non-existent clinic should not exist");
    }

    @Test
    @DisplayName("Repository should handle deleteById operation")
    void repository_ShouldHandleDeleteByIdOperation() {
        // Save clinic
        ClinicEntity savedClinic = repository.save(testClinic);
        entityManager.flush();
        
        Long clinicId = savedClinic.getClinicId();
        
        // Verify it exists
        assertTrue(repository.existsById(clinicId), "Clinic should exist before deletion");
        
        // Delete clinic
        repository.deleteById(clinicId);
        entityManager.flush();
        
        // Verify it's deleted
        assertFalse(repository.existsById(clinicId), "Clinic should not exist after deletion");
        
        Optional<ClinicEntity> deletedClinic = repository.findById(clinicId);
        assertFalse(deletedClinic.isPresent(), "Deleted clinic should not be found");
    }

    @Test
    @DisplayName("Repository should handle domain-specific queries with Email value object")
    void repository_ShouldHandleDomainSpecificQueriesWithEmail() {
        // Save clinic
        repository.save(testClinic);
        entityManager.flush();
        
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
        entityManager.flush();
        
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
        ClinicEntity savedClinic = repository.save(testClinic);
        entityManager.flush();
        
        Long clinicId = savedClinic.getClinicId();
        
        // Test existsByEmailAndClinicIdNot - should return false for same clinic
        boolean existsByEmailExcludingSelf = repository.existsByEmailAndClinicIdNot(testEmail, clinicId);
        assertFalse(existsByEmailExcludingSelf, 
                "Should not find email conflict when excluding the same clinic");
        
        // Test existsByLegalNumberAndClinicIdNot - should return false for same clinic
        boolean existsByLegalNumberExcludingSelf = repository.existsByLegalNumberAndClinicIdNot("TEST123", clinicId);
        assertFalse(existsByLegalNumberExcludingSelf, 
                "Should not find legal number conflict when excluding the same clinic");
        
        // Save another clinic with different email and legal number
        ClinicEntity anotherClinic = ClinicEntity.builder()
                .clinicName("Another Clinic")
                .legalName("Another Legal")
                .legalNumber("ANOTHER123")
                .address(new Address("789 Another St", "Another City", "99999"))
                .email(new Email("another@clinic.com"))
                .phone(new Phone("+1111111111"))
                .suscriptionStatus("ACTIVE")
                .build();
        
        ClinicEntity savedAnotherClinic = repository.save(anotherClinic);
        entityManager.flush();
        
        // Now test exclusion with the other clinic's email - should return true
        boolean existsByOtherEmailExcludingSelf = repository.existsByEmailAndClinicIdNot(
                new Email("another@clinic.com"), clinicId);
        assertTrue(existsByOtherEmailExcludingSelf, 
                "Should find email conflict when checking other clinic's email");
    }

    @Test
    @DisplayName("Repository should properly handle BaseEntity audit fields")
    void repository_ShouldHandleBaseEntityAuditFields() {
        // Save clinic
        ClinicEntity savedClinic = repository.save(testClinic);
        entityManager.flush();
        entityManager.clear();
        
        // Retrieve and verify audit fields
        Optional<ClinicEntity> retrievedClinic = repository.findById(savedClinic.getClinicId());
        assertTrue(retrievedClinic.isPresent(), "Clinic should be found");
        
        ClinicEntity clinic = retrievedClinic.get();
        assertNotNull(clinic.getCreatedAt(), "CreatedAt should be set");
        assertNotNull(clinic.getUpdatedAt(), "UpdatedAt should be set");
        
        // Update the clinic
        clinic.setClinicName("Updated Clinic Name");
        ClinicEntity updatedClinic = repository.save(clinic);
        entityManager.flush();
        
        assertNotNull(updatedClinic.getUpdatedAt(), "UpdatedAt should be updated");
    }
}