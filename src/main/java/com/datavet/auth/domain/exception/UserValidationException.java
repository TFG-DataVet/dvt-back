package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.DomainException;
import com.datavet.shared.domain.validation.ValidationResult;

public class UserValidationException extends DomainException {

    private final ValidationResult validationResult;

    public UserValidationException(ValidationResult validationResult) {
        super("User validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public UserValidationException(String field, String message) {
        this(buildResult(field, message));
    }

    private static ValidationResult buildResult(String field, String message) {
        ValidationResult result = new ValidationResult();
        result.addError(field, message);
        return result;
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
