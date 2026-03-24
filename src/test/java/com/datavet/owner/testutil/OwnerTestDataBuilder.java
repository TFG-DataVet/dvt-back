package com.datavet.owner.testutil;

import com.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.owner.domain.model.Owner;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;

import java.util.UUID;

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
    public static Owner aValidOwner() {
        return Owner.create(
                UUID.randomUUID().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                aValidDocument(),
                aValidPhone(),
                aValidEmail(),
                aValidAddress(),
                "esto es una web",
                true);
    }

    /**
     * Creates a valid Owner using the factory method with specific owner and clinic IDs.
     */
    public static Owner buildValidOwnerWithId(String clinicId) {

        return Owner.create(
                clinicId,
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                aValidDocument(),
                aValidPhone(),
                aValidEmail(),
                aValidAddress(),
                "esto es una web",
                true);
    }

    /**
     * Creates a valid Owner using the factory method with a specific email.
     */
    public static Owner buildValidOwnerWithEmail(String email) {
        return Owner.create(
                UUID.randomUUID().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                aValidDocument(),
                aValidPhone(),
                new Email(email),
                aValidAddress(),
                "esto es una web",
                true);
    }

    /**
     * Creates a valid Owner using the factory method with a specific DNI.
     */
    public static Owner buildValidOwnerWithDni(String type, String dni) {
        return Owner.create(
                UUID.randomUUID().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                DocumentId.of(type, dni),
                aValidPhone(),
                aValidEmail(),
                aValidAddress(),
                "esto es un email",
                true);
    }

    /**
     * Creates a valid Owner using the factory method with a specific phone.
     */
    public static Owner buildValidOwnerWithPhone(String phone) {
        return Owner.create(
                UUID.randomUUID().toString(),
                DEFAULT_OWNER_NAME,
                DEFAULT_OWNER_LAST_NAME,
                aValidDocument(),
                new Phone(phone),
                aValidEmail(),
                aValidAddress(),
                "esto es una web",
                true);
    }

    /**
     * Creates a valid CreateOwnerCommand with default test data.
     */
    public static CreateOwnerCommand aValidCreateCommand() {
        return  CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();
    }

    /**
     * Creates a CreateOwnerCommand with a specific email.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithEmail(String email) {
        return CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(new Email(email))
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();
    }

    /**
     * Creates a CreateOwnerCommand with a specific DNI.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithDni(String type, String dni) {
        return CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(DocumentId.of(type, dni))
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();
    }

    /**
     * Creates a CreateOwnerCommand with a specific phone.
     * Useful for testing uniqueness constraints.
     */
    public static CreateOwnerCommand aCreateCommandWithPhone(String phone) {
        return CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(new Phone(phone))
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();
    }

    /**
     * Creates a CreateOwnerCommand with a specific owner name.
     */
    public static CreateOwnerCommand aCreateCommandWithOwnerName(String ownerName) {
        return CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(ownerName)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();
    }

    /**
     * Creates a valid UpdateOwnerCommand with default test data.
     * @param ownerId The ID of the owner to update (should be a valid ObjectId string)
     */
    public static UpdateOwnerCommand aValidUpdateCommand(String ownerId) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .build();
    }

    /**
     * Creates an UpdateOwnerCommand with a specific email.
     * Useful for testing uniqueness constraints during updates.
     */
    public static UpdateOwnerCommand anUpdateCommandWithEmail(String ownerId, String email) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(new Email(email))
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .build();
    }

    /**
     * Creates an UpdateOwnerCommand with a specific DNI.
     * Useful for testing uniqueness constraints during updates.
     */
    public static UpdateOwnerCommand anUpdateCommandWithDni(String ownerId, String type,String dni) {
        return UpdateOwnerCommand.builder()
                .ownerID(ownerId)
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(DocumentId.of(type, dni))
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .build();
    }

    /**
     * Creates a valid Address with default test data.
     */
    public static Address aValidAddress() {
        return new Address(DEFAULT_STREET, DEFAULT_CITY, DEFAULT_POSTAL_CODE);
    }

    public static DocumentId aValidDocument() {
        return DocumentId.of("DNI", "23402587H");
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
