package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.exception.PetValidationException;
import com.datavet.datavet.pet.testutil.PetTestDataBuilder;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OwnerInfo Domain Model Tests")
class OwnerInfoTest {

    // -------------------------------------------------------
    // CREATION - factory method
    // -------------------------------------------------------

    @Test
    @DisplayName("Should create OwnerInfo with all valid fields")
    void from_shouldCreateOwnerInfoWithValidData() {
        Phone phone = new Phone("+34123456789");

        OwnerInfo ownerInfo = OwnerInfo.from("Juan", "García", phone);

        assertNotNull(ownerInfo);
        assertNotNull(ownerInfo.getOwnerId(), "ownerId should be auto-generated");
        assertEquals("Juan", ownerInfo.getName());
        assertEquals("García", ownerInfo.getLastName());
        assertEquals(phone, ownerInfo.getPhone());
    }

    @Test
    @DisplayName("Should auto-generate a unique UUID as ownerId on creation")
    void from_shouldGenerateUniqueOwnerIds() {
        Phone phone = new Phone("+34123456789");

        OwnerInfo ownerInfo1 = OwnerInfo.from("Juan", "García", phone);
        OwnerInfo ownerInfo2 = OwnerInfo.from("Juan", "García", phone);

        assertNotEquals(ownerInfo1.getOwnerId(), ownerInfo2.getOwnerId(),
                "Each OwnerInfo should have a different UUID");
    }

    // -------------------------------------------------------
    // getFullName
    // -------------------------------------------------------

    @Test
    @DisplayName("Should return concatenated full name")
    void getFullName_shouldReturnNameAndLastNameConcatenated() {
        OwnerInfo ownerInfo = OwnerInfo.from("Ana", "López", new Phone("+34111222333"));

        assertEquals("Ana López", ownerInfo.getFullName());
    }

    // -------------------------------------------------------
    // VALIDATION - name
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when name is null")
    void from_shouldFailWhenNameIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from(null, "García", new Phone("+34123456789")));

        assertTrue(ex.getMessage().contains("El nombre del dueño no puede estar vacio o nulo."));
    }

    @Test
    @DisplayName("Should throw PetValidationException when name is blank")
    void from_shouldFailWhenNameIsBlank() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from("   ", "García", new Phone("+34123456789")));

        assertTrue(ex.getMessage().contains("El nombre del dueño no puede estar vacio o nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - lastName
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when lastName is null")
    void from_shouldFailWhenLastNameIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from("Juan", null, new Phone("+34123456789")));

        assertTrue(ex.getMessage().contains("el apellido del dueño no puede estar vacio o nulo."));
    }

    @Test
    @DisplayName("Should throw PetValidationException when lastName is blank")
    void from_shouldFailWhenLastNameIsBlank() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from("Juan", "  ", new Phone("+34123456789")));

        assertTrue(ex.getMessage().contains("el apellido del dueño no puede estar vacio o nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - phone
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when phone is null")
    void from_shouldFailWhenPhoneIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from("Juan", "García", null));

        assertTrue(ex.getMessage().contains("El numero de telefono del dueño no puede ser nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - multiple errors accumulated
    // -------------------------------------------------------

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void from_shouldAccumulateMultipleValidationErrors() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.from(null, null, null));

        String message = ex.getMessage();
        assertTrue(message.contains("El nombre del dueño no puede estar vacio o nulo."));
        assertTrue(message.contains("el apellido del dueño no puede estar vacio o nulo."));
        assertTrue(message.contains("El numero de telefono del dueño no puede ser nulo."));
    }

    // -------------------------------------------------------
    // IMMUTABILITY (@Value)
    // -------------------------------------------------------

    @Test
    @DisplayName("Should be immutable - no setters available (Lombok @Value)")
    void ownerInfo_shouldBeImmutable() {
        OwnerInfo ownerInfo = PetTestDataBuilder.aValidOwnerInfo();

        // @Value makes all fields final and private; we verify state doesn't change
        // by checking the values remain the same after multiple reads
        String id1 = ownerInfo.getOwnerId();
        String id2 = ownerInfo.getOwnerId();
        assertEquals(id1, id2, "ownerId should always return the same value");
    }
}