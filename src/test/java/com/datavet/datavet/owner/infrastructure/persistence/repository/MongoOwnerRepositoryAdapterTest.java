package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerDocument;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MongoOwnerRepositoryAdapter.
 * Validates basic CRUD operations and custom query methods for the Owner repository using MongoDB.
 * Requirements: 8.1, 8.2, 8.3, 8.4, 8.5
 */
@DataMongoTest
@ActiveProfiles("test")
class MongoOwnerRepositoryAdapterTest {

    @Autowired
    private MongoOwnerRepositoryAdapter repository;

    private OwnerDocument testOwner;
    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        // Clear MongoDB collections before each test
        repository.deleteAll();

        testAddress = new Address("123 Test St", "Test City", "12345");
        testEmail = new Email("test@owner.com");
        testPhone = new Phone("+1234567890");

        testOwner = OwnerDocument.builder()
                .id(new ObjectId().toString())
                .clinicId(new ObjectId().toString())
                .firstName("Juan")
                .lastName("Pérez")
                .dni("12345678A")
                .phone(testPhone)
                .email(testEmail)
                .address(testAddress)
                .build();
    }

    @Test
    @DisplayName("Should save owner and generate ID automatically")
    void shouldSaveOwnerAndGenerateId() {
        // Given: a new owner document without ID
        OwnerDocument newOwner = OwnerDocument.builder()
                .clinicId(new ObjectId().toString())
                .firstName("Maria")
                .lastName("Garcia")
                .dni("87654321B")
                .phone(new Phone("+0987654321"))
                .email(new Email("maria@owner.com"))
                .address(new Address("456 Oak St", "Another City", "54321"))
                .build();

        // When: saving the owner
        OwnerDocument savedOwner = repository.save(newOwner);

        // Then: owner should have an auto-generated ID
        assertNotNull(savedOwner, "Saved owner should not be null");
        assertNotNull(savedOwner.getId(), "Owner ID should be generated");
        assertTrue(ObjectId.isValid(savedOwner.getId()), "Generated ID should be a valid ObjectId");

        // Verify all fields are persisted correctly
        assertEquals("Maria", savedOwner.getFirstName());
        assertEquals("Garcia", savedOwner.getLastName());
        assertEquals("87654321B", savedOwner.getDni());
        assertNotNull(savedOwner.getAddress());
        assertNotNull(savedOwner.getPhone());
        assertNotNull(savedOwner.getEmail());
    }

    @Test
    @DisplayName("Should save owner with pre-assigned ID")
    void shouldSaveOwnerWithPreAssignedId() {
        // Given: an owner with a pre-assigned ObjectId
        String preAssignedId = new ObjectId().toString();
        testOwner.setId(preAssignedId);

        // When: saving the owner
        OwnerDocument savedOwner = repository.save(testOwner);

        // Then: owner should retain the pre-assigned ID
        assertNotNull(savedOwner, "Saved owner should not be null");
        assertEquals(preAssignedId, savedOwner.getId(), "Owner should retain pre-assigned ID");
        assertEquals("Juan", savedOwner.getFirstName());
        assertEquals("Pérez", savedOwner.getLastName());
    }

    @Test
    @DisplayName("Should find owner by ID")
    void shouldFindOwnerById() {
        // Given: a saved owner
        OwnerDocument savedOwner = repository.save(testOwner);
        String ownerId = savedOwner.getId();

        // When: finding owner by ID
        Optional<OwnerDocument> foundOwner = repository.findById(ownerId);

        // Then: owner should be found with all data intact
        assertTrue(foundOwner.isPresent(), "Owner should be found");

        OwnerDocument owner = foundOwner.get();
        assertEquals(ownerId, owner.getId());
        assertEquals("Juan", owner.getFirstName());
        assertEquals("Pérez", owner.getLastName());
        assertEquals("12345678A", owner.getDni());

        // Verify value objects are correctly retrieved
        assertNotNull(owner.getAddress());
        assertEquals("123 Test St", owner.getAddress().getStreet());
        assertEquals("Test City", owner.getAddress().getCity());
        assertEquals("12345", owner.getAddress().getPostalCode());

        assertNotNull(owner.getEmail());
        assertEquals("test@owner.com", owner.getEmail().getValue());

        assertNotNull(owner.getPhone());
        assertEquals("+1234567890", owner.getPhone().getValue());
    }

    @Test
    @DisplayName("Should return empty when owner not found")
    void shouldReturnEmptyWhenOwnerNotFound() {
        // Given: a non-existent owner ID
        String nonExistentId = new ObjectId().toString();

        // When: finding owner by non-existent ID
        Optional<OwnerDocument> foundOwner = repository.findById(nonExistentId);

        // Then: should return empty Optional
        assertFalse(foundOwner.isPresent(), "Should return empty Optional for non-existent ID");
        assertTrue(foundOwner.isEmpty(), "Optional should be empty");
    }

    @Test
    @DisplayName("Should find all owners")
    void shouldFindAllOwners() {
        // Given: multiple saved owners
        OwnerDocument owner1 = repository.save(testOwner);

        OwnerDocument owner2 = OwnerDocument.builder()
                .clinicId(new ObjectId().toString())
                .firstName("Maria")
                .lastName("Lopez")
                .dni("87654321B")
                .phone(new Phone("+0987654321"))
                .email(new Email("maria@owner.com"))
                .address(new Address("456 Oak St", "Another City", "54321"))
                .build();
        OwnerDocument savedOwner2 = repository.save(owner2);

        OwnerDocument owner3 = OwnerDocument.builder()
                .clinicId(new ObjectId().toString())
                .firstName("Carlos")
                .lastName("Rodriguez")
                .dni("11111111C")
                .phone(new Phone("+1111111111"))
                .email(new Email("carlos@owner.com"))
                .address(new Address("789 Pine St", "Third City", "99999"))
                .build();
        OwnerDocument savedOwner3 = repository.save(owner3);

        // When: finding all owners
        List<OwnerDocument> allOwners = repository.findAll();

        // Then: should return all saved owners
        assertNotNull(allOwners, "Owners list should not be null");
        assertEquals(3, allOwners.size(), "Should have exactly 3 owners");

        // Verify all owners are present
        assertTrue(allOwners.stream().anyMatch(o -> "Juan".equals(o.getFirstName())),
                "Should contain first owner");
        assertTrue(allOwners.stream().anyMatch(o -> "Maria".equals(o.getFirstName())),
                "Should contain second owner");
        assertTrue(allOwners.stream().anyMatch(o -> "Carlos".equals(o.getFirstName())),
                "Should contain third owner");
    }

    @Test
    @DisplayName("Should delete owner by ID")
    void shouldDeleteOwnerById() {
        // Given: a saved owner
        OwnerDocument savedOwner = repository.save(testOwner);
        String ownerId = savedOwner.getId();

        // Verify owner exists before deletion
        assertTrue(repository.existsById(ownerId), "Owner should exist before deletion");
        Optional<OwnerDocument> ownerBeforeDelete = repository.findById(ownerId);
        assertTrue(ownerBeforeDelete.isPresent(), "Owner should be found before deletion");

        // When: deleting the owner
        repository.deleteById(ownerId);

        // Then: owner should be completely removed
        assertFalse(repository.existsById(ownerId), "Owner should not exist after deletion");

        Optional<OwnerDocument> ownerAfterDelete = repository.findById(ownerId);
        assertFalse(ownerAfterDelete.isPresent(), "Owner should not be found after deletion");
        assertTrue(ownerAfterDelete.isEmpty(), "Optional should be empty after deletion");
    }

    @Test
    @DisplayName("Should persist and retrieve Address correctly")
    void shouldPersistAndRetrieveAddressCorrectly() {
        // Given: an owner with a specific Address value object
        Address originalAddress = new Address("742 Evergreen Terrace", "Springfield", "58008");
        testOwner.setAddress(originalAddress);

        // When: saving and retrieving the owner
        OwnerDocument savedOwner = repository.save(testOwner);
        Optional<OwnerDocument> retrievedOwner = repository.findById(savedOwner.getId());

        // Then: Address should be persisted and retrieved without data loss
        assertTrue(retrievedOwner.isPresent(), "Owner should be found");

        Address retrievedAddress = retrievedOwner.get().getAddress();
        assertNotNull(retrievedAddress, "Address should not be null");
        assertEquals(originalAddress.getStreet(), retrievedAddress.getStreet(),
                "Street should match original");
        assertEquals(originalAddress.getCity(), retrievedAddress.getCity(),
                "City should match original");
        assertEquals(originalAddress.getPostalCode(), retrievedAddress.getPostalCode(),
                "Postal code should match original");

        // Verify Address value object equality
        assertEquals(originalAddress, retrievedAddress,
                "Retrieved Address should equal original Address");
    }

    @Test
    @DisplayName("Should persist and retrieve Email correctly")
    void shouldPersistAndRetrieveEmailCorrectly() {
        // Given: an owner with a specific Email value object
        Email originalEmail = new Email("unique.owner@example.com");
        testOwner.setEmail(originalEmail);

        // When: saving and retrieving the owner
        OwnerDocument savedOwner = repository.save(testOwner);
        Optional<OwnerDocument> retrievedOwner = repository.findById(savedOwner.getId());

        // Then: Email should be persisted and retrieved without data loss
        assertTrue(retrievedOwner.isPresent(), "Owner should be found");

        Email retrievedEmail = retrievedOwner.get().getEmail();
        assertNotNull(retrievedEmail, "Email should not be null");
        assertEquals(originalEmail.getValue(), retrievedEmail.getValue(),
                "Email value should match original");

        // Verify Email value object equality
        assertEquals(originalEmail, retrievedEmail,
                "Retrieved Email should equal original Email");
    }

    @Test
    @DisplayName("Should persist and retrieve Phone correctly")
    void shouldPersistAndRetrievePhoneCorrectly() {
        // Given: an owner with a specific Phone value object
        Phone originalPhone = new Phone("+15551234567");
        testOwner.setPhone(originalPhone);

        // When: saving and retrieving the owner
        OwnerDocument savedOwner = repository.save(testOwner);
        Optional<OwnerDocument> retrievedOwner = repository.findById(savedOwner.getId());

        // Then: Phone should be persisted and retrieved without data loss
        assertTrue(retrievedOwner.isPresent(), "Owner should be found");

        Phone retrievedPhone = retrievedOwner.get().getPhone();
        assertNotNull(retrievedPhone, "Phone should not be null");
        assertEquals(originalPhone.getValue(), retrievedPhone.getValue(),
                "Phone value should match original");

        // Verify Phone value object equality
        assertEquals(originalPhone, retrievedPhone,
                "Retrieved Phone should equal original Phone");
    }

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {
        // Given: a saved owner with a specific email
        Email existingEmail = new Email("existing@owner.com");
        testOwner.setEmail(existingEmail);
        repository.save(testOwner);

        // When: checking if the email exists
        boolean exists = repository.existsByEmail(existingEmail);

        // Then: should return true
        assertTrue(exists, "Should return true when email exists in database");
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Given: a saved owner with a different email
        repository.save(testOwner);

        // When: checking if a non-existent email exists
        Email nonExistentEmail = new Email("nonexistent@owner.com");
        boolean exists = repository.existsByEmail(nonExistentEmail);

        // Then: should return false
        assertFalse(exists, "Should return false when email does not exist in database");
    }

    @Test
    @DisplayName("Should return true when DNI exists")
    void shouldReturnTrueWhenDniExists() {
        // Given: a saved owner with a specific DNI
        String existingDni = "12345678A";
        testOwner.setDni(existingDni);
        repository.save(testOwner);

        // When: checking if the DNI exists
        boolean exists = repository.existsByDni(existingDni);

        // Then: should return true
        assertTrue(exists, "Should return true when DNI exists in database");
    }

    @Test
    @DisplayName("Should return false when DNI does not exist")
    void shouldReturnFalseWhenDniDoesNotExist() {
        // Given: a saved owner with a different DNI
        repository.save(testOwner);

        // When: checking if a non-existent DNI exists
        String nonExistentDni = "99999999Z";
        boolean exists = repository.existsByDni(nonExistentDni);

        // Then: should return false
        assertFalse(exists, "Should return false when DNI does not exist in database");
    }

    @Test
    @DisplayName("Should return true when phone exists")
    void shouldReturnTrueWhenPhoneExists() {
        // Given: a saved owner with a specific phone
        Phone existingPhone = new Phone("+1234567890");
        testOwner.setPhone(existingPhone);
        repository.save(testOwner);

        // When: checking if the phone exists
        boolean exists = repository.existsByPhone(existingPhone);

        // Then: should return true
        assertTrue(exists, "Should return true when phone exists in database");
    }

    @Test
    @DisplayName("Should return false when phone does not exist")
    void shouldReturnFalseWhenPhoneDoesNotExist() {
        // Given: a saved owner with a different phone
        repository.save(testOwner);

        // When: checking if a non-existent phone exists
        Phone nonExistentPhone = new Phone("+9999999999");
        boolean exists = repository.existsByPhone(nonExistentPhone);

        // Then: should return false
        assertFalse(exists, "Should return false when phone does not exist in database");
    }

    @Test
    @DisplayName("Should return true when email and DNI exist together")
    void shouldReturnTrueWhenEmailAndDniExistTogether() {
        // Given: a saved owner with specific email and DNI
        Email existingEmail = new Email("test@owner.com");
        String existingDni = "12345678A";
        testOwner.setEmail(existingEmail);
        testOwner.setDni(existingDni);
        repository.save(testOwner);

        // When: checking if both email and DNI exist
        boolean exists = repository.existsByEmailAndDni(existingEmail, existingDni);

        // Then: should return true
        assertTrue(exists, "Should return true when both email and DNI exist in database");
    }

    @Test
    @DisplayName("Should return false when email and DNI do not exist together")
    void shouldReturnFalseWhenEmailAndDniDoNotExistTogether() {
        // Given: a saved owner
        repository.save(testOwner);

        // When: checking with different email and DNI combination
        Email differentEmail = new Email("different@owner.com");
        String differentDni = "99999999Z";
        boolean exists = repository.existsByEmailAndDni(differentEmail, differentDni);

        // Then: should return false
        assertFalse(exists, "Should return false when email and DNI combination does not exist");
    }

    @Test
    @DisplayName("Should exclude current owner when checking DNI uniqueness")
    void shouldExcludeCurrentOwnerWhenCheckingDniUniqueness() {
        // Given: a saved owner with a specific DNI
        String ownerDni = "12345678A";
        testOwner.setDni(ownerDni);
        OwnerDocument savedOwner = repository.save(testOwner);
        String ownerId = savedOwner.getId();

        // When: checking if the same DNI exists excluding the current owner
        boolean existsForOtherOwner = repository.existsByDniAndIdNot(ownerDni, ownerId);

        // Then: should return false (DNI belongs to current owner, not another one)
        assertFalse(existsForOtherOwner,
                "Should return false when DNI belongs to the current owner being checked");

        // Given: another owner with a different DNI
        OwnerDocument anotherOwner = OwnerDocument.builder()
                .clinicId(new ObjectId().toString())
                .firstName("Maria")
                .lastName("Lopez")
                .dni("87654321B")
                .phone(new Phone("+0987654321"))
                .email(new Email("maria@owner.com"))
                .address(new Address("456 Oak St", "Another City", "54321"))
                .build();
        OwnerDocument savedAnotherOwner = repository.save(anotherOwner);

        // When: checking if the first owner's DNI exists excluding the first owner
        boolean existsForFirstOwner = repository.existsByDniAndIdNot(ownerDni, ownerId);

        // Then: should still return false (no other owner has this DNI)
        assertFalse(existsForFirstOwner,
                "Should return false when no other owner has the DNI");

        // When: checking if the first owner's DNI exists excluding the second owner
        boolean existsForSecondOwner = repository.existsByDniAndIdNot(
                ownerDni, savedAnotherOwner.getId());

        // Then: should return true (another owner has this DNI)
        assertTrue(existsForSecondOwner,
                "Should return true when another owner has the DNI");
    }

    @Test
    @DisplayName("Should update all fields correctly")
    void shouldUpdateAllFieldsCorrectly() {
        // Given: a saved owner
        OwnerDocument savedOwner = repository.save(testOwner);
        String ownerId = savedOwner.getId();

        // When: updating all fields
        OwnerDocument ownerToUpdate = repository.findById(ownerId).orElseThrow();
        ownerToUpdate.setFirstName("Updated Name");
        ownerToUpdate.setLastName("Updated LastName");
        ownerToUpdate.setDni("87654321B");
        ownerToUpdate.setAddress(new Address("999 Updated St", "Updated City", "99999"));
        ownerToUpdate.setPhone(new Phone("+9999999999"));
        ownerToUpdate.setEmail(new Email("updated@owner.com"));

        OwnerDocument updatedOwner = repository.save(ownerToUpdate);

        // Then: all fields should be updated correctly
        OwnerDocument retrievedOwner = repository.findById(ownerId).orElseThrow();

        assertEquals("Updated Name", retrievedOwner.getFirstName(),
                "First name should be updated");
        assertEquals("Updated LastName", retrievedOwner.getLastName(),
                "Last name should be updated");
        assertEquals("87654321B", retrievedOwner.getDni(),
                "DNI should be updated");

        // Verify value objects were updated
        assertNotNull(retrievedOwner.getAddress(), "Address should not be null");
        assertEquals("999 Updated St", retrievedOwner.getAddress().getStreet());
        assertEquals("Updated City", retrievedOwner.getAddress().getCity());

        assertNotNull(retrievedOwner.getPhone(), "Phone should not be null");
        assertEquals("+9999999999", retrievedOwner.getPhone().getValue());

        assertNotNull(retrievedOwner.getEmail(), "Email should not be null");
        assertEquals("updated@owner.com", retrievedOwner.getEmail().getValue());
    }

    @Test
    @DisplayName("Should maintain ID after update")
    void shouldMaintainIdAfterUpdate() {
        // Given: a saved owner with a generated ID
        OwnerDocument savedOwner = repository.save(testOwner);
        String originalId = savedOwner.getId();
        assertNotNull(originalId, "Original ID should not be null");

        // When: updating the owner multiple times
        OwnerDocument ownerToUpdate = repository.findById(originalId).orElseThrow();
        ownerToUpdate.setFirstName("First Update");
        repository.save(ownerToUpdate);

        OwnerDocument ownerToUpdate2 = repository.findById(originalId).orElseThrow();
        ownerToUpdate2.setFirstName("Second Update");
        ownerToUpdate2.setLastName("Updated LastName");
        ownerToUpdate2.setAddress(new Address("New Street", "New City", "00000"));
        repository.save(ownerToUpdate2);

        OwnerDocument ownerToUpdate3 = repository.findById(originalId).orElseThrow();
        ownerToUpdate3.setEmail(new Email("final@owner.com"));
        ownerToUpdate3.setPhone(new Phone("+1111111111"));
        repository.save(ownerToUpdate3);

        // Then: ID should remain unchanged after all updates
        OwnerDocument finalOwner = repository.findById(originalId).orElseThrow();

        assertEquals(originalId, finalOwner.getId(),
                "Owner ID should remain unchanged after updates");
        assertNotNull(finalOwner.getId(),
                "Owner ID should not be null after updates");

        // Verify the updates were applied
        assertEquals("Second Update", finalOwner.getFirstName(),
                "First name should reflect last update");
        assertEquals("Updated LastName", finalOwner.getLastName(),
                "Last name should reflect update");
        assertEquals("final@owner.com", finalOwner.getEmail().getValue(),
                "Email should reflect last update");
        assertEquals("+1111111111", finalOwner.getPhone().getValue(),
                "Phone should reflect last update");
        assertEquals("New City", finalOwner.getAddress().getCity(),
                "Address should reflect update");
    }

    @Test
    @DisplayName("Should handle String IDs correctly")
    void shouldHandleStringIdsCorrectly() {
        // Given: an owner with a String ID (ObjectId format)
        String stringId = new ObjectId().toString();
        testOwner.setId(stringId);

        // When: saving and retrieving the owner
        OwnerDocument savedOwner = repository.save(testOwner);

        // Then: ID should be stored and retrieved as String
        assertNotNull(savedOwner.getId(), "ID should not be null");
        assertEquals(stringId, savedOwner.getId(), "ID should match the assigned String ID");
        assertTrue(ObjectId.isValid(savedOwner.getId()), "ID should be a valid ObjectId string");

        // Verify retrieval by String ID works
        Optional<OwnerDocument> retrievedOwner = repository.findById(stringId);
        assertTrue(retrievedOwner.isPresent(), "Owner should be found by String ID");
        assertEquals(stringId, retrievedOwner.get().getId(), "Retrieved ID should match");
    }

    @Test
    @DisplayName("Should handle clinic ID as String")
    void shouldHandleClinicIdAsString() {
        // Given: an owner with a String clinic ID
        String clinicId = new ObjectId().toString();
        testOwner.setClinicId(clinicId);

        // When: saving and retrieving the owner
        OwnerDocument savedOwner = repository.save(testOwner);
        Optional<OwnerDocument> retrievedOwner = repository.findById(savedOwner.getId());

        // Then: clinic ID should be stored and retrieved as String
        assertTrue(retrievedOwner.isPresent(), "Owner should be found");
        assertNotNull(retrievedOwner.get().getClinicId(), "Clinic ID should not be null");
        assertEquals(clinicId, retrievedOwner.get().getClinicId(),
                "Clinic ID should match the assigned String ID");
        assertTrue(ObjectId.isValid(retrievedOwner.get().getClinicId()),
                "Clinic ID should be a valid ObjectId string");
    }
}
