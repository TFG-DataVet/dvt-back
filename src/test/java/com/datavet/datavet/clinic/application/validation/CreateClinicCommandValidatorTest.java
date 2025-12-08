package com.datavet.datavet.clinic.application.validation;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.testutil.ClinicTestDataBuilder;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateClinicCommandValidator Tests")
class CreateClinicCommandValidatorTest {

    private CreateClinicCommandValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateClinicCommandValidator();
    }

    @Test
    @DisplayName("Should pass validation with valid command")
    void shouldPassValidationWithValidCommand() {
        // Given
        CreateClinicCommand command = ClinicTestDataBuilder.aValidCreateCommand();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Validation should pass for valid command");
        assertFalse(result.hasErrors(), "Should have no errors");
        assertEquals(0, result.getErrors().size(), "Error list should be empty");
    }

    // ========== Clinic Name Validation Tests ==========

    @Test
    @DisplayName("Should fail when clinic name is null")
    void shouldFailWhenClinicNameIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                null,
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null clinic name");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicName") &&
                        error.getMessage().equals("Clinic name is required")),
                "Should have error for clinic name");
    }

    @Test
    @DisplayName("Should fail when clinic name is empty")
    void shouldFailWhenClinicNameIsEmpty() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "",
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for empty clinic name");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicName") &&
                        error.getMessage().equals("Clinic name is required")),
                "Should have error for clinic name");
    }

    @Test
    @DisplayName("Should fail when clinic name exceeds max length")
    void shouldFailWhenClinicNameExceedsMaxLength() {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        CreateClinicCommand command = new CreateClinicCommand(
                longName,
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for clinic name exceeding max length");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicName") &&
                        error.getMessage().equals("Clinic name must not exceed 100 characters")),
                "Should have error for clinic name max length");
    }

    // ========== Legal Name Validation Tests ==========

    @Test
    @DisplayName("Should fail when legal name is null")
    void shouldFailWhenLegalNameIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                null,
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null legal name");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalName") &&
                        error.getMessage().equals("Legal name is required")),
                "Should have error for legal name");
    }

    @Test
    @DisplayName("Should fail when legal name is empty")
    void shouldFailWhenLegalNameIsEmpty() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for empty legal name");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalName") &&
                        error.getMessage().equals("Legal name is required")),
                "Should have error for legal name");
    }

    @Test
    @DisplayName("Should fail when legal name exceeds max length")
    void shouldFailWhenLegalNameExceedsMaxLength() {
        // Given
        String longLegalName = "a".repeat(151); // Exceeds 150 character limit
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                longLegalName,
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for legal name exceeding max length");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalName") &&
                        error.getMessage().equals("Legal name must not exceed 150 characters")),
                "Should have error for legal name max length");
    }

    // ========== Legal Number Validation Tests ==========

    @Test
    @DisplayName("Should fail when legal number is null")
    void shouldFailWhenLegalNumberIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                null,
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null legal number");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalNumber") &&
                        error.getMessage().equals("Legal number is required")),
                "Should have error for legal number");
    }

    @Test
    @DisplayName("Should fail when legal number is empty")
    void shouldFailWhenLegalNumberIsEmpty() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for empty legal number");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalNumber") &&
                        error.getMessage().equals("Legal number is required")),
                "Should have error for legal number");
    }

    @Test
    @DisplayName("Should fail when legal number exceeds max length")
    void shouldFailWhenLegalNumberExceedsMaxLength() {
        // Given
        String longLegalNumber = "a".repeat(51); // Exceeds 50 character limit
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                longLegalNumber,
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for legal number exceeding max length");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalNumber") &&
                        error.getMessage().equals("Legal number must not exceed 50 characters")),
                "Should have error for legal number max length");
    }

    // ========== Value Objects Validation Tests ==========

    @Test
    @DisplayName("Should fail when address is null")
    void shouldFailWhenAddressIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "12345678",
                null,
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null address");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("address") &&
                        error.getMessage().equals("Address is required")),
                "Should have error for address");
    }

    @Test
    @DisplayName("Should fail when phone is null")
    void shouldFailWhenPhoneIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                null,
                ClinicTestDataBuilder.aValidEmail(),
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null phone");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("phone") &&
                        error.getMessage().equals("Phone is required")),
                "Should have error for phone");
    }

    @Test
    @DisplayName("Should fail when email is null")
    void shouldFailWhenEmailIsNull() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                null,
                "https://example.com/logo.png"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null email");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("email") &&
                        error.getMessage().equals("Email is required")),
                "Should have error for email");
    }

    // ========== Logo URL Validation Tests ==========

    @Test
    @DisplayName("Should fail when logo URL exceeds max length")
    void shouldFailWhenLogoUrlExceedsMaxLength() {
        // Given
        String longLogoUrl = "https://example.com/" + "a".repeat(256); // Exceeds 255 character limit
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                longLogoUrl
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for logo URL exceeding max length");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("logoUrl") &&
                        error.getMessage().equals("Logo URL must not exceed 255 characters")),
                "Should have error for logo URL max length");
    }

    @Test
    @DisplayName("Should accept null logo URL")
    void shouldAcceptNullLogoUrl() {
        // Given
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "12345678",
                ClinicTestDataBuilder.aValidAddress(),
                ClinicTestDataBuilder.aValidPhone(),
                ClinicTestDataBuilder.aValidEmail(),
                null
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Validation should pass with null logo URL");
        assertFalse(result.hasErrors(), "Should have no errors");
    }

    // ========== Multiple Errors Test ==========

    @Test
    @DisplayName("Should collect multiple validation errors")
    void shouldCollectMultipleValidationErrors() {
        // Given - Command with multiple invalid fields
        CreateClinicCommand command = new CreateClinicCommand(
                null,  // Invalid clinic name
                "",    // Invalid legal name
                "a".repeat(51),  // Invalid legal number (too long)
                null,  // Invalid address
                null,  // Invalid phone
                null,  // Invalid email
                "https://example.com/" + "a".repeat(256)  // Invalid logo URL (too long)
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail with multiple errors");
        assertTrue(result.hasErrors(), "Should have errors");
        assertEquals(7, result.getErrors().size(), "Should have 7 validation errors");

        // Verify all expected errors are present
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicName")),
                "Should have error for clinic name");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalName")),
                "Should have error for legal name");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("legalNumber")),
                "Should have error for legal number");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("address")),
                "Should have error for address");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("phone")),
                "Should have error for phone");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("email")),
                "Should have error for email");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("logoUrl")),
                "Should have error for logo URL");
    }
}
