package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.validation.ValidationResult;

public class PetValidationException extends PetDomainException {

    private final ValidationResult validationResult;

    public PetValidationException(ValidationResult validationResult) {
        super("Pet validation failed: " + formatErrors(validationResult));
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
