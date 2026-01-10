package com.datavet.datavet.owner.testutil;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OwnerTestDataBuilder to ensure it generates valid test data.
 */
class OwnerTestDataBuilderTest {

    @Test
    void buildValidOwner_shouldCreateOwnerWithObjectIdStrings() {
        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwner();

        // Then
        assertNotNull(owner);
        assertNotNull(owner.getOwnerID());
        assertNotNull(owner.getClinicID());
        assertTrue(ObjectId.isValid(owner.getOwnerID()), "Owner ID should be a valid ObjectId string");
        assertTrue(ObjectId.isValid(owner.getClinicID()), "Clinic ID should be a valid ObjectId string");
        assertEquals("Juan", owner.getOwnerName());
        assertEquals("Pérez", owner.getOwnerLastName());
        assertEquals("12345678A", owner.getOwnerDni());
    }

    @Test
    void buildValidOwnerWithId_shouldCreateOwnerWithSpecificId() {
        // Given
        String specificId = new ObjectId().toString();

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithId(specificId);

        // Then
        assertNotNull(owner);
        assertEquals(specificId, owner.getOwnerID());
        assertTrue(ObjectId.isValid(owner.getClinicID()));
    }

    @Test
    void buildValidOwnerWithIds_shouldCreateOwnerWithSpecificIds() {
        // Given
        String ownerId = new ObjectId().toString();
        String clinicId = new ObjectId().toString();

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithIds(ownerId, clinicId);

        // Then
        assertNotNull(owner);
        assertEquals(ownerId, owner.getOwnerID());
        assertEquals(clinicId, owner.getClinicID());
    }

    @Test
    void buildValidOwnerWithEmail_shouldCreateOwnerWithSpecificEmail() {
        // Given
        String specificId = new ObjectId().toString();
        String email = "custom@example.com";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithEmail(specificId, email);

        // Then
        assertNotNull(owner);
        assertEquals(email, owner.getOwnerEmail().getValue());
    }

    @Test
    void buildValidOwnerWithDni_shouldCreateOwnerWithSpecificDni() {
        // Given
        String specificId = new ObjectId().toString();
        String dni = "98765432Z";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithDni(specificId, dni);

        // Then
        assertNotNull(owner);
        assertEquals(dni, owner.getOwnerDni());
    }

    @Test
    void buildValidOwnerWithPhone_shouldCreateOwnerWithSpecificPhone() {
        // Given
        String specificId = new ObjectId().toString();
        String phone = "+34699999999";

        // When
        Owner owner = OwnerTestDataBuilder.buildValidOwnerWithPhone(specificId, phone);

        // Then
        assertNotNull(owner);
        assertEquals(phone, owner.getOwnerPhone().getValue());
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
        String dni = "11111111A";

        // When
        CreateOwnerCommand command = OwnerTestDataBuilder.aCreateCommandWithDni(dni);

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
        String ownerId = new ObjectId().toString();
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
        String ownerId = new ObjectId().toString();
        String dni = "22222222B";

        // When
        UpdateOwnerCommand command = OwnerTestDataBuilder.anUpdateCommandWithDni(ownerId, dni);

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
