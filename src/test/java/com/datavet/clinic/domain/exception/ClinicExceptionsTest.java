package com.datavet.clinic.domain.exception;

import com.datavet.shared.domain.validation.ValidationError;
import com.datavet.shared.domain.validation.ValidationResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Clinic Domain Exceptions Tests")
class ClinicExceptionsTest {

    // =========================================================================
    // ClinicNotFoundException
    // =========================================================================

    @Test
    @DisplayName("ClinicNotFoundException(entityType, id) should produce exact message")
    void clinicNotFoundException_WithEntityTypeAndId_ShouldProduceExactMessage() {
        ClinicNotFoundException exception = new ClinicNotFoundException("Clinic", "clinic-123");

        assertThat(exception.getMessage())
                .isEqualTo("Clinic not found with id: clinic-123");
    }

    @Test
    @DisplayName("ClinicNotFoundException(message) should preserve the message as-is")
    void clinicNotFoundException_WithRawMessage_ShouldPreserveMessage() {
        ClinicNotFoundException exception = new ClinicNotFoundException("Custom error message");

        assertThat(exception.getMessage()).isEqualTo("Custom error message");
    }

    @Test
    @DisplayName("ClinicNotFoundException should be a RuntimeException")
    void clinicNotFoundException_ShouldBeRuntimeException() {
        ClinicNotFoundException exception = new ClinicNotFoundException("Clinic", "clinic-123");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("ClinicNotFoundException(message, cause) should preserve both")
    void clinicNotFoundException_WithMessageAndCause_ShouldPreserveBoth() {
        Throwable cause = new IllegalStateException("DB timeout");
        ClinicNotFoundException exception = new ClinicNotFoundException("wrapped error", cause);

        assertThat(exception.getMessage()).isEqualTo("wrapped error");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    // =========================================================================
    // ClinicAlreadyExistsException
    // =========================================================================

    @Test
    @DisplayName("ClinicAlreadyExistsException(fieldName, fieldValue) should produce exact message")
    void clinicAlreadyExistsException_WithFieldNameAndValue_ShouldProduceExactMessage() {
        ClinicAlreadyExistsException exception =
                new ClinicAlreadyExistsException("email", "test@clinic.com");

        assertThat(exception.getMessage())
                .isEqualTo("Clinic already exists with email: test@clinic.com");
    }

    @Test
    @DisplayName("ClinicAlreadyExistsException(fieldName, fieldValue) works for legalNumber")
    void clinicAlreadyExistsException_WithLegalNumber_ShouldProduceExactMessage() {
        ClinicAlreadyExistsException exception =
                new ClinicAlreadyExistsException("legalNumber", "12345678A");

        assertThat(exception.getMessage())
                .isEqualTo("Clinic already exists with legalNumber: 12345678A");
    }

    @Test
    @DisplayName("ClinicAlreadyExistsException(message) should preserve the message as-is")
    void clinicAlreadyExistsException_WithRawMessage_ShouldPreserveMessage() {
        ClinicAlreadyExistsException exception =
                new ClinicAlreadyExistsException("Custom conflict message");

        assertThat(exception.getMessage()).isEqualTo("Custom conflict message");
    }

    @Test
    @DisplayName("ClinicAlreadyExistsException should be a RuntimeException")
    void clinicAlreadyExistsException_ShouldBeRuntimeException() {
        ClinicAlreadyExistsException exception =
                new ClinicAlreadyExistsException("email", "test@clinic.com");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("ClinicAlreadyExistsException(message, cause) should preserve both")
    void clinicAlreadyExistsException_WithMessageAndCause_ShouldPreserveBoth() {
        Throwable cause = new IllegalStateException("DB timeout");
        ClinicAlreadyExistsException exception =
                new ClinicAlreadyExistsException("wrapped error", cause);

        assertThat(exception.getMessage()).isEqualTo("wrapped error");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    // =========================================================================
    // ClinicValidationException
    // =========================================================================

    @Test
    @DisplayName("ClinicValidationException should expose the ValidationResult")
    void clinicValidationException_ShouldExposeValidationResult() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getValidationResult()).isSameAs(result);
    }

    @Test
    @DisplayName("ClinicValidationException message should start with 'Clinic validation failed:'")
    void clinicValidationException_MessageShouldStartWithPrefix() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getMessage()).startsWith("Clinic validation failed:");
    }

    @Test
    @DisplayName("ClinicValidationException message should include all field:message pairs")
    void clinicValidationException_MessageShouldIncludeAllErrors() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");
        result.addError("legalName", "El nombre fiscal no puede estar vacío");
        result.addError("email", "El email no puede ser nulo");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getMessage())
                .contains("clinicName: El nombre no puede estar vacío")
                .contains("legalName: El nombre fiscal no puede estar vacío")
                .contains("email: El email no puede ser nulo");
    }

    @Test
    @DisplayName("ClinicValidationException ValidationResult should contain all errors with correct fields")
    void clinicValidationException_ValidationResultShouldContainAllErrors() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");
        result.addError("email", "El email no puede ser nulo");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getValidationResult().getErrors())
                .hasSize(2)
                .extracting(ValidationError::getField)
                .containsExactly("clinicName", "email");
    }

    @Test
    @DisplayName("ClinicValidationException with single error should not include comma separator")
    void clinicValidationException_WithSingleError_ShouldNotHaveCommaSeparator() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getMessage())
                .contains("clinicName: El nombre no puede estar vacío")
                .doesNotContain(", ");
    }

    @Test
    @DisplayName("ClinicValidationException with multiple errors should separate them with commas")
    void clinicValidationException_WithMultipleErrors_ShouldSeparateWithCommas() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");
        result.addError("email", "El email no puede ser nulo");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception.getMessage()).contains(", ");
    }

    @Test
    @DisplayName("ClinicValidationException should be a RuntimeException")
    void clinicValidationException_ShouldBeRuntimeException() {
        ValidationResult result = new ValidationResult();
        result.addError("clinicName", "El nombre no puede estar vacío");

        ClinicValidationException exception = new ClinicValidationException(result);

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}