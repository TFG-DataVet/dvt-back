package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.shared.domain.exception.DomainException;
import com.datavet.datavet.shared.domain.validation.ValidationResult;

public class OwnerValidationException extends DomainException {

    private final ValidationResult validationResult;

    public OwnerValidationException(ValidationResult validationResult) {
        super("Owner validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public static String formatErrors(ValidationResult result) {
        return result.getErrors().stream()
                .map(error -> error.getField() + ": " + error.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}
