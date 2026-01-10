package com.datavet.datavet.owner.testutil;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;

/**
 * Test data builder for creating valid Owner test data.
 * Provides helper methods to generate valid owners, commands, and value objects for testing.
 * Uses MongoDB ObjectId strings for IDs.
 */
public class OwnerTestDataBuilder {

    // Default valid test data
    private static final String DEFAULT_OWNER_NAME = "Juan";
    private static final String DEFAULT_OWNER_LAST_NAME = "Pérez";
    private static final String DEFAULT_DNI = "12345678A";
    private static final String DEFAULT_STREET = "Calle Mayor 123";
    private static final String DEFAULT_CITY = "Madrid";
    private static final String DEFAULT_POSTAL_CODE = "28001";
    private static final String DEFAULT_PHONE = "+34612345678";
    private static final String DEFAULT_EMAIL = "juan.perez@example.com";

    /**
     * Creates a valid Owner builder with default test data.
     * Uses new ObjectId strings for ownerID and clinicID.
     * Use this as a starting point and customize fields as needed.
     */
    public static Owner.OwnerBuilder aValidOwner() {
        return Owner.builder()
                .id(new ObjectId().toString())
                .clinicId(new ObjectId().toString())
                .name(DEFAULT_OWNER_NAME)
                .lastName(DEFAULT_OWNER_LAST_NAME)
                .documentNumber(DEFAULT_DNI)
                .phone(aValidPhone())
                .email(aValidEmail())
                .address(aValidAddress());
    }

    /**
     * Creates a valid Owner using the factory method with default test data.
     * Generates new ObjectId strings for IDs.
     */
    public static Owner buildValidOwner() {
        return Owner.create(
                new ObjectId().toString(),
                new ObjectId().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid Owner using the factory method with a specific owner ID.
     * Generates a new ObjectId string for clinicID.
     */
    public static Owner buildValidOwnerWithId(String ownerId) {
        return Owner.create(
                ownerId,
                new ObjectId().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid Owner using the factory method with specific owner and clinic IDs.
     */
    public static Owner buildValidOwnerWithIds(String ownerId, String clinicId) {
        return Owner.create(
                ownerId,
                clinicId,
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid Owner using the factory method with a specific email.
     */
    public static Owner buildValidOwnerWithEmail(String ownerId, String email) {
        return Owner.create(
                ownerId,
                new ObjectId().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                new Email(email),
                aValidAddress()
        );
    }

    /**
     * Creates a valid Owner using the factory method with a specific DNI.
     */
    public static Owner buildValidOwnerWithDni(String ownerId, String dni) {
        return Owner.create(
                ownerId,
                new ObjectId().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                dni,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid Owner using the factory method with a specific phone.
     */
    public static Owner buildValidOwnerWithPhone(String ownerId, String phone) {
        return Owner.create(
                ownerId,
                new ObjectId().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                new Phone(phone),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid CreateOwnerCommand with default test data.
     */
    public static CreateOwnerCommand aValidCreateCommand() {
        return new CreateOwnerCommand(
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a CreateOwnerCommand with a specific email.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithEmail(String email) {
        return new CreateOwnerCommand(
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                new Email(email),
                aValidAddress()
        );
    }

    /**
     * Creates a CreateOwnerCommand with a specific DNI.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithDni(String dni) {
        return new CreateOwnerCommand(
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                dni,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a CreateOwnerCommand with a specific phone.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithPhone(String phone) {
        return new CreateOwnerCommand(
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                new Phone(phone),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a CreateOwnerCommand with a specific owner name.
     */
    public static CreateOwnerCommand aCreateCommandWithOwnerName(String ownerName) {
        return new CreateOwnerCommand(
                ownerName,
                DEFAULT_OWNER_LAST_NAME,
                DEFAULT_DNI,
                aValidPhone(),
                aValidEmail(),
                aValidAddress()
        );
    }

    /**
     * Creates a valid UpdateOwnerCommand with default test data.
     * @param ownerId The ID of the owner to update (should be a valid ObjectId string)
     */
    public static UpdateOwnerCommand aValidUpdateCommand(String ownerId) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName("Updated Name")
                .ownerLastName("Updated LastName")
                .ownerDni("87654321B")
                .ownerPhone("+34698765432")
                .ownerEmail("updated@example.com")
                .ownerPassword("password123")
                .ownerAddress(new Address("Calle Nueva 456", "Barcelona", "08001"))
                .build();
    }

    /**
     * Creates an UpdateOwnerCommand with a specific email.
     * Useful for testing uniqueness constraints during updates.
     */
    public static UpdateOwnerCommand anUpdateCommandWithEmail(String ownerId, String email) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName("Updated Name")
                .ownerLastName("Updated LastName")
                .ownerDni("87654321B")
                .ownerPhone("+34698765432")
                .ownerEmail(email)
                .ownerPassword("password123")
                .ownerAddress(aValidAddress())
                .build();
    }

    /**
     * Creates an UpdateOwnerCommand with a specific DNI.
     * Useful for testing uniqueness constraints during updates.
     */
    public static UpdateOwnerCommand anUpdateCommandWithDni(String ownerId, String dni) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName("Updated Name")
                .ownerLastName("Updated LastName")
                .ownerDni(dni)
                .ownerPhone("+34698765432")
                .ownerEmail("updated@example.com")
                .ownerPassword("password123")
                .ownerAddress(aValidAddress())
                .build();
    }

    /**
     * Creates a valid Address with default test data.
     */
    public static Address aValidAddress() {
        return new Address(DEFAULT_STREET, DEFAULT_CITY, DEFAULT_POSTAL_CODE);
    }

    /**
     * Creates a valid Phone with default test data.
     */
    public static Phone aValidPhone() {
        return new Phone(DEFAULT_PHONE);
    }

    /**
     * Creates a valid Email with default test data.
     */
    public static Email aValidEmail() {
        return new Email(DEFAULT_EMAIL);
    }

    /**
     * Generates a new valid ObjectId string.
     * Useful for creating unique IDs in tests.
     */
    public static String generateObjectId() {
        return new ObjectId().toString();
    }
}
