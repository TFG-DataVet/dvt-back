package com.datavet.datavet.clinic.domain.exception;

import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.exception.DomainException;

/**
 * Exception thrown when clinic validation fails.
 * Contains validation errors from the shared validation framework.
 */
public class ClinicValidationException extends DomainException {
    
    private final ValidationResult validationResult;
    
    public ClinicValidationException(ValidationResult validationResult) {
        super("Clinic validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }
    
    public ValidationResult getValidationResult() {
        return validationResult;
    }
    
    private static String formatErrors(ValidationResult result) {
        return result.getErrors().stream()
                .map(error -> error.getField() + ": " + error.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}