package com.datavet.datavet.clinic.application.validation;

import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.testutil.ClinicTestDataBuilder;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UpdateClinicCommandValidator Tests")
class UpdateClinicCommandValidatorTest {

    private UpdateClinicCommandValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UpdateClinicCommandValidator();
    }

    @Test
    @DisplayName("Should pass validation with valid command")
    void shouldPassValidationWithValidCommand() {
        // Given
        UpdateClinicCommand command = ClinicTestDataBuilder.aValidUpdateCommand("ClinicId");

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Validation should pass for valid command");
        assertFalse(result.hasErrors(), "Should have no errors");
        assertEquals(0, result.getErrors().size(), "Error list should be empty");
    }

    // ========== Clinic ID Validation Tests ==========

    @Test
    @DisplayName("Should fail when clinic ID is null")
    void shouldFailWhenClinicIdIsNull() {
        // Given
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(null)
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for null clinic ID");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicId") &&
                        error.getMessage().equals("Clinic ID is required")),
                "Should have error for clinic ID");
    }

    @Test
    @DisplayName("Should fail when clinic ID is negative")
    void shouldFailWhenClinicIdIsEmpty() {
        // Given
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for negative clinic ID");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicId") &&
                        error.getMessage().equals("Clinic ID must be positive")),
                "Should have error for clinic ID");
    }

    @Test
    @DisplayName("Should fail when clinic ID is zero")
    void shouldFailWhenClinicIdIsZero() {
        // Given
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for zero clinic ID");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicId") &&
                        error.getMessage().equals("Clinic ID must be positive")),
                "Should have error for clinic ID");
    }

    // ========== Clinic Name Validation Tests ==========

    @Test
    @DisplayName("Should fail when clinic name is null")
    void shouldFailWhenClinicNameIsNull() {
        // Given
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName(null)
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName(longName)
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName(null)
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName(longLegalName)
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber(null)
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber(longLegalNumber)
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(null)
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(null)
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(null)
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl(longLogoUrl)
                .suscriptionStatus("ACTIVE")
                .build();

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
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl(null)
                .suscriptionStatus("ACTIVE")
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Validation should pass with null logo URL");
        assertFalse(result.hasErrors(), "Should have no errors");
    }

    // ========== Subscription Status Validation Tests ==========

    @Test
    @DisplayName("Should fail when subscription status exceeds max length")
    void shouldFailWhenSubscriptionStatusExceedsMaxLength() {
        // Given
        String longStatus = "a".repeat(51); // Exceeds 50 character limit
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus(longStatus)
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail for subscription status exceeding max length");
        assertTrue(result.hasErrors(), "Should have errors");
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("suscriptionStatus") &&
                        error.getMessage().equals("Subscription status must not exceed 50 characters")),
                "Should have error for subscription status max length");
    }

    @Test
    @DisplayName("Should accept null subscription status")
    void shouldAcceptNullSubscriptionStatus() {
        // Given
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId("ClinicId")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .address(ClinicTestDataBuilder.aValidAddress())
                .phone(ClinicTestDataBuilder.aValidPhone())
                .email(ClinicTestDataBuilder.aValidEmail())
                .logoUrl("https://example.com/logo.png")
                .suscriptionStatus(null)
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Validation should pass with null subscription status");
        assertFalse(result.hasErrors(), "Should have no errors");
    }

    // ========== Multiple Errors Test ==========

    @Test
    @DisplayName("Should collect multiple validation errors")
    void shouldCollectMultipleValidationErrors() {
        // Given - Command with multiple invalid fields
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(null)  // Invalid clinic ID
                .clinicName(null)  // Invalid clinic name
                .legalName("")  // Invalid legal name
                .legalNumber("a".repeat(51))  // Invalid legal number (too long)
                .address(null)  // Invalid address
                .phone(null)  // Invalid phone
                .email(null)  // Invalid email
                .logoUrl("https://example.com/" + "a".repeat(256))  // Invalid logo URL (too long)
                .suscriptionStatus("a".repeat(51))  // Invalid subscription status (too long)
                .build();

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Validation should fail with multiple errors");
        assertTrue(result.hasErrors(), "Should have errors");
        assertEquals(9, result.getErrors().size(), "Should have 9 validation errors");

        // Verify all expected errors are present
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("clinicId")),
                "Should have error for clinic ID");
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
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.getField().equals("suscriptionStatus")),
                "Should have error for subscription status");
    }
}
