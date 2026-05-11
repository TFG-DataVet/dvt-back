package com.datavet.appointment.domain.exception;

import com.datavet.shared.domain.validation.ValidationResult;

public class AppointmentValidationException extends AppointmentDomainException {

    private final ValidationResult validationResult;

    public AppointmentValidationException(ValidationResult validationResult) {
        super("Appointment validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public AppointmentValidationException(String field, String message) {
        this(buildResult(field, message));
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static ValidationResult buildResult(String field, String message) {
        ValidationResult result = new ValidationResult();
        result.addError(field, message);
        return result;
    }

    private static String formatErrors(ValidationResult result) {
        return result.getErrors().stream()
                .map(e -> e.getField() + ": " + e.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}
