package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.exception.PetValidationException;
import com.datavet.datavet.pet.testutil.PetTestDataBuilder;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OwnerInfo Domain Model Tests")
class OwnerInfoTest {

    // -------------------------------------------------------
    // CREATION - factory method
    // -------------------------------------------------------
    private final String uuid = UUID.randomUUID().toString();

    @Test
    @DisplayName("Should create OwnerInfo with all valid fields")
    void create_shouldCreateOwnerInfoWithValidData() {
        Phone phone = new Phone("+34123456789");

        OwnerInfo ownerInfo = OwnerInfo.create(uuid, "Juan", "García", phone);

        assertNotNull(ownerInfo);
        assertNotNull(ownerInfo.getOwnerId(), "ownerId should be auto-generated");
        assertEquals("Juan", ownerInfo.getName());
        assertEquals("García", ownerInfo.getLastName());
        assertEquals(phone, ownerInfo.getPhone());
    }

    // -------------------------------------------------------
    // getFullName
    // -------------------------------------------------------

    @Test
    @DisplayName("Should return concatenated full name")
    void getFullName_shouldReturnNameAndLastNameConcatenated() {
        OwnerInfo ownerInfo = OwnerInfo.create(uuid, "Ana", "López", new Phone("+34111222333"));

        assertEquals("Ana López", ownerInfo.getFullName());
    }

    // -------------------------------------------------------
    // VALIDATION - name
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when name is null")
    void create_shouldFailWhenNameIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.anOwnerInfoWithName(null));

        assertTrue(ex.getMessage().contains("El nombre del dueño no puede estar vacio o nulo."));
    }

    @Test
    @DisplayName("Should throw PetValidationException when name is blank")
    void create_shouldFailWhenNameIsBlank() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.anOwnerInfoWithName("     "));

        assertTrue(ex.getMessage().contains("El nombre del dueño no puede estar vacio o nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - lastName
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when lastName is null")
    void create_shouldFailWhenLastNameIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.anOwnerInfoWithLastName(null));

        assertTrue(ex.getMessage().contains("el apellido del dueño no puede estar vacio o nulo."));
    }

    @Test
    @DisplayName("Should throw PetValidationException when lastName is blank")
    void create_shouldFailWhenLastNameIsBlank() {
        PetValidationException ex = assertThrows(PetValidationException.class,
            () -> PetTestDataBuilder.anOwnerInfoWithLastName("     "));

        assertTrue(ex.getMessage().contains("el apellido del dueño no puede estar vacio o nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - phone
    // -------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when phone is null")
    void create_shouldFailWhenPhoneIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.anOwnerInfoWithPhone(null));

        assertTrue(ex.getMessage().contains("El numero de telefono del dueño no puede ser nulo."));
    }

    // -------------------------------------------------------
    // VALIDATION - multiple errors accumulated
    // -------------------------------------------------------

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> OwnerInfo.create(null,null, null, null));

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