package com.datavet.owner.testutil;

import com.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.owner.domain.model.Owner;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OwnerTestDataBuilder to ensure it generates valid test data.
 */
class OwnerTestDataBuilderTest {

    @Test
    void buildValidOwner_shouldCreateOwnerWithObjectIdStrings() {
        // When
        Owner owner = OwnerTestDataBuilder.aValidOwner();

        // Then
        assertNotNull(owner);
        assertNotNull(owner.getId());
        assertNotNull(owner.getClinicId());
        assertTrue(ObjectId.isValid(owner.getId()), "Owner ID should be a valid ObjectId string");
        assertTrue(ObjectId.isValid(owner.getClinicId()), "Clinic ID should be a valid ObjectId string");
        assertEquals("Juan", owner.getName());
        assertEquals("Pérez", owner.getLastName());
        assertEquals("12345678A", owner.getDocumentNumber());
    }

    @Test
    void buildValidOwnerWithIds_shouldCreateOwnerWithSpecificIds() {
        // Given
        String clinicId = UUID.randomUUID().toString();

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithId(clinicId);

        // Then
        assertNotNull(owner);
        assertEquals(clinicId, owner.getClinicId());
    }

    @Test
    void buildValidOwnerWithEmail_shouldCreateOwnerWithSpecificEmail() {
        // Given
        String email = "custom@example.com";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithEmail(email);

        // Then
        assertNotNull(owner);
        assertEquals(email, owner.getEmail().getValue());
    }

    @Test
    void buildValidOwnerWithDni_shouldCreateOwnerWithSpecificDni() {
        // Given
        String type = "DNI";
        String dni = "98765432Z";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithDni(type, dni);

        // Then
        assertNotNull(owner);
        assertEquals(dni, owner.getDocumentNumber());
    }

    @Test
    void buildValidOwnerWithPhone_shouldCreateOwnerWithSpecificPhone() {
        // Given
        String phone = "+34699999999";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithPhone(phone);

        // Then
        assertNotNull(owner);
        assertEquals(phone, owner.getPhone().getValue());
    }

    @Test
    void aValidCreateCommand_shouldCreateValidCommand() {
        // When
        CreateOwnerCommand command = OwnerTestDataBuilder.aValidCreateCommand();

        // Then
        assertNotNull(command);
        assertEquals("Juan", command.getOwnerName());
        assertEquals("Pérez", command.getOwnerLastName());
        assertEquals("12345678A", command.getOwnerDni());
        assertNotNull(command.getOwnerPhone());
        assertNotNull(command.getOwnerEmail());
        assertNotNull(command.getOwnerAddress());
    }

    @Test
    void aCreateCommandWithEmail_shouldCreateCommandWithSpecificEmail() {
        // Given
        String email = "specific@example.com";

        // When
        CreateOwnerCommand command = OwnerTestDataBuilder.aCreateCommandWithEmail(email);

        // Then
        assertNotNull(command);
        assertEquals(email, command.getOwnerEmail().getValue());
    }

    @Test
    void aCreateCommandWithDni_shouldCreateCommandWithSpecificDni() {
        // Given
        String type = "DNI";
        String dni = "23402587H";

        // When
        CreateOwnerCommand command = OwnerTestDataBuilder.aCreateCommandWithDni(type, dni);

        // Then
        assertNotNull(command);
        assertEquals(dni, command.getOwnerDni());
    }

    @Test
    void aCreateCommandWithPhone_shouldCreateCommandWithSpecificPhone() {
        // Given
        String phone = "+34611111111";

        // When
        CreateOwnerCommand command = OwnerTestDataBuilder.aCreateCommandWithPhone(phone);

        // Then
        assertNotNull(command);
        assertEquals(phone, command.getOwnerPhone().getValue());
    }

    @Test
    void aValidUpdateCommand_shouldCreateValidCommandWithObjectId() {
        // Given
        String ownerId = new ObjectId().toString();

        // When
        UpdateOwnerCommand command = OwnerTestDataBuilder.aValidUpdateCommand(ownerId);

        // Then
        assertNotNull(command);
        assertEquals(ownerId, command.getOwnerID());
        assertTrue(ObjectId.isValid(command.getOwnerID()));
        assertEquals("Updated Name", command.getOwnerName());
        assertEquals("Updated LastName", command.getOwnerLastName());
    }

    @Test
    void anUpdateCommandWithEmail_shouldCreateCommandWithSpecificEmail() {
        // Given
        String ownerId = UUID.randomUUID().toString();
        String email = "newemail@example.com";

        // When
        UpdateOwnerCommand command = OwnerTestDataBuilder.anUpdateCommandWithEmail(ownerId, email);

        // Then
        assertNotNull(command);
        assertEquals(ownerId, command.getOwnerID());
        assertEquals(email, command.getOwnerEmail());
    }

    @Test
    void anUpdateCommandWithDni_shouldCreateCommandWithSpecificDni() {
        // Given
        String ownerId = UUID.randomUUID().toString();
        String type = "DNI";
        String dni = "23402587H";

        // When
        UpdateOwnerCommand command = OwnerTestDataBuilder.anUpdateCommandWithDni(ownerId, type, dni);

        // Then
        assertNotNull(command);
        assertEquals(ownerId, command.getOwnerID());
        assertEquals(dni, command.getOwnerDni());
    }

    @Test
    void aValidAddress_shouldCreateValidAddress() {
        // When
        Address address = OwnerTestDataBuilder.aValidAddress();

        // Then
        assertNotNull(address);
        assertEquals("Calle Mayor 123", address.getStreet());
        assertEquals("Madrid", address.getCity());
        assertEquals("28001", address.getPostalCode());
    }

    @Test
    void aValidPhone_shouldCreateValidPhone() {
        // When
        Phone phone = OwnerTestDataBuilder.aValidPhone();

        // Then
        assertNotNull(phone);
        assertEquals("+34612345678", phone.getValue());
    }

    @Test
    void aValidEmail_shouldCreateValidEmail() {
        // When
        Email email = OwnerTestDataBuilder.aValidEmail();

        // Then
        assertNotNull(email);
        assertEquals("juan.perez@example.com", email.getValue());
    }

    @Test
    void generateObjectId_shouldGenerateValidObjectIdString() {
        // When
        String objectId = OwnerTestDataBuilder.generateObjectId();

        // Then
        assertNotNull(objectId);
        assertTrue(ObjectId.isValid(objectId));
    }

    @Test
    void generateObjectId_shouldGenerateUniqueIds() {
        // When
        String id1 = OwnerTestDataBuilder.generateObjectId();
        String id2 = OwnerTestDataBuilder.generateObjectId();

        // Then
        assertNotEquals(id1, id2, "Generated ObjectIds should be unique");
    }
}
