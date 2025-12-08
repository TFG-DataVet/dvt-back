package com.datavet.datavet.clinic.testutil;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;

/**
 * Test data builder for creating valid Clinic test data.
 * Provides helper methods to generate valid clinics, commands, and value objects for testing.
 */
public class ClinicTestDataBuilder {

    // Default valid test data
    private static final String DEFAULT_CLINIC_NAME = "Test Clinic";
    private static final String DEFAULT_LEGAL_NAME = "Test Clinic Legal Name";
    private static final String DEFAULT_LEGAL_NUMBER = "12345678";
    private static final String DEFAULT_STREET = "123 Main St";
    private static final String DEFAULT_CITY = "Springfield";
    private static final String DEFAULT_POSTAL_CODE = "12345";
    private static final String DEFAULT_PHONE = "+1234567890";
    private static final String DEFAULT_EMAIL = "test@clinic.com";
    private static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";
    private static final String DEFAULT_SUBSCRIPTION_STATUS = "ACTIVE";

    /**
     * Creates a valid Clinic builder with default test data.
     * Use this as a starting point and customize fields as needed.
     */
    public static Clinic.ClinicBuilder aValidClinic() {
        return Clinic.builder()
                .clinicID("hola")
                .clinicName(DEFAULT_CLINIC_NAME)
                .legalName(DEFAULT_LEGAL_NAME)
                .legalNumber(DEFAULT_LEGAL_NUMBER)
                .address(aValidAddress())
                .phone(aValidPhone())
                .email(aValidEmail())
                .logoUrl(DEFAULT_LOGO_URL)
                .suscriptionStatus(DEFAULT_SUBSCRIPTION_STATUS);
    }

    /**
     * Creates a valid CreateClinicCommand with default test data.
     */
    public static CreateClinicCommand aValidCreateCommand() {
        return new CreateClinicCommand(
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                aValidEmail(),
                DEFAULT_LOGO_URL
        );
    }

    /**
     * Creates a valid UpdateClinicCommand with default test data.
     * @param clinicId The ID of the clinic to update
     */
    public static UpdateClinicCommand aValidUpdateCommand(String clinicId) {
        return UpdateClinicCommand.builder()
                .clinicId(clinicId)
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(new Address("456 Oak St", "Shelbyville", "54321"))
                .phone(new Phone("+0987654321"))
                .email(new Email("updated@clinic.com"))
                .logoUrl("https://example.com/new-logo.png")
                .suscriptionStatus(DEFAULT_SUBSCRIPTION_STATUS)
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
     * Creates a CreateClinicCommand with a specific email.
     * Useful for testing uniqueness constraints.
     */
    public static CreateClinicCommand aCreateCommandWithEmail(String email) {
        return new CreateClinicCommand(
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                new Email(email),
                DEFAULT_LOGO_URL
        );
    }

    /**
     * Creates a CreateClinicCommand with a specific legal number.
     * Useful for testing uniqueness constraints.
     */
    public static CreateClinicCommand aCreateCommandWithLegalNumber(String legalNumber) {
        return new CreateClinicCommand(
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                legalNumber,
                aValidAddress(),
                aValidPhone(),
                aValidEmail(),
                DEFAULT_LOGO_URL
        );
    }

    /**
     * Creates a CreateClinicCommand with a specific clinic name.
     */
    public static CreateClinicCommand aCreateCommandWithClinicName(String clinicName) {
        return new CreateClinicCommand(
                clinicName,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                aValidEmail(),
                DEFAULT_LOGO_URL
        );
    }

    /**
     * Creates an UpdateClinicCommand with a specific email.
     * Useful for testing uniqueness constraints during updates.
     */
    public static UpdateClinicCommand anUpdateCommandWithEmail(String clinicId, String email) {
        return UpdateClinicCommand.builder()
                .clinicId(clinicId)
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(aValidAddress())
                .phone(aValidPhone())
                .email(new Email(email))
                .logoUrl("https://example.com/new-logo.png")
                .suscriptionStatus(DEFAULT_SUBSCRIPTION_STATUS)
                .build();
    }

    /**
     * Creates a Clinic using the factory method with default test data.
     */
    public static Clinic aValidCreatedClinic() {
        return Clinic.create(
                "hola",
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                aValidEmail(),
                DEFAULT_LOGO_URL,
                DEFAULT_SUBSCRIPTION_STATUS
        );
    }

    /**
     * Creates a Clinic using the factory method with a specific ID.
     */
    public static Clinic aValidCreatedClinicWithId(String clinicId) {
        return Clinic.create(
                clinicId,
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                aValidEmail(),
                DEFAULT_LOGO_URL,
                DEFAULT_SUBSCRIPTION_STATUS
        );
    }

    /**
     * Creates a Clinic using the factory method with a specific email.
     */
    public static Clinic aValidCreatedClinicWithEmail(String clinicId, String email) {
        return Clinic.create(
                clinicId,
                DEFAULT_CLINIC_NAME,
                DEFAULT_LEGAL_NAME,
                DEFAULT_LEGAL_NUMBER,
                aValidAddress(),
                aValidPhone(),
                new Email(email),
                DEFAULT_LOGO_URL,
                DEFAULT_SUBSCRIPTION_STATUS
        );
    }
}
