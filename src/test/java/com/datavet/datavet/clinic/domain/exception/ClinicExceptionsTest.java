package com.datavet.datavet.clinic.domain.exception;

import com.datavet.datavet.shared.domain.validation.ValidationError;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Clinic domain exceptions.
 * Validates that exceptions are created correctly with proper messages and data.
 */
class ClinicExceptionsTest {

    @Test
    void clinicNotFoundExceptionShouldContainClinicId() {
        // Given
        String clinicId = "hola";

        // When
        ClinicNotFoundException exception = new ClinicNotFoundException(clinicId);

        // Then
        assertThat(exception.getMessage())
                .isNotNull()
                .contains("Clinic")
                .contains("not found")
                .contains("id")
                .contains(clinicId.toString());
    }

    @Test
    void clinicAlreadyExistsExceptionShouldContainFieldInfo() {
        // Given
        String fieldName = "email";
        String fieldValue = "test@clinic.com";

        // When
        ClinicAlreadyExistsException exception = 
                new ClinicAlreadyExistsException(fieldName, fieldValue);

        // Then
        assertThat(exception.getMessage())
                .isNotNull()
                .contains("Clinic")
                .contains("already exists")
                .contains(fieldName)
                .contains(fieldValue);
    }

    @Test
    void clinicValidationExceptionShouldContainValidationResult() {
        // Given
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("clinicName", "Clinic name is required");
        validationResult.addError("email", "Email is invalid");

        // When
        ClinicValidationException exception = new ClinicValidationException(validationResult);

        // Then
        assertThat(exception.getValidationResult())
                .isNotNull()
                .isEqualTo(validationResult);
        assertThat(exception.getValidationResult().getErrors())
                .hasSize(2)
                .extracting(ValidationError::getField)
                .containsExactly("clinicName", "email");
    }

    @Test
    void clinicValidationExceptionShouldFormatErrorsCorrectly() {
        // Given
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("clinicName", "Clinic name is required");
        validationResult.addError("legalName", "Legal name exceeds maximum length");
        validationResult.addError("email", "Email format is invalid");

        // When
        ClinicValidationException exception = new ClinicValidationException(validationResult);

        // Then
        assertThat(exception.getMessage())
                .isNotNull()
                .contains("Clinic validation failed")
                .contains("clinicName: Clinic name is required")
                .contains("legalName: Legal name exceeds maximum length")
                .contains("email: Email format is invalid");
    }
}
