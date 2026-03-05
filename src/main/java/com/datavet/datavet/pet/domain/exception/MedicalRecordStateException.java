package com.datavet.datavet.pet.domain.exception;

import com.datavet.datavet.shared.domain.validation.ValidationResult;

public class MedicalRecordStateException extends MedicalRecordDomainException {

    private final ValidationResult validationResult;

    public MedicalRecordStateException(ValidationResult validationResult) {
        super("Medical record status failed" + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public MedicalRecordStateException(String domain, String cause){
        this(buildResult(domain, cause));
    }

    public static ValidationResult buildResult(String field, String message){
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
