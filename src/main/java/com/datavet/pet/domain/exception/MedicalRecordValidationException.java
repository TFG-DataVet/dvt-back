package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.validation.ValidationResult;

public class MedicalRecordValidationException extends MedicalRecordDomainException {

    private final ValidationResult validationResult;

    public MedicalRecordValidationException(ValidationResult validationResult) {
        super("Medical record validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public MedicalRecordValidationException(String domain, String cause){
        this(buildResult(domain, cause));
    }

    private static ValidationResult buildResult(String field, String message) {
        ValidationResult result = new ValidationResult();
        result.addError(field, message);
        return result;
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
