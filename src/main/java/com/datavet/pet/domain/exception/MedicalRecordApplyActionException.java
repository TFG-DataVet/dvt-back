package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.validation.ValidationResult;

public class MedicalRecordApplyActionException extends MedicalRecordDomainException {

    private final ValidationResult validationResult;

    public MedicalRecordApplyActionException(ValidationResult validationResult) {
        super("Medical record apply action failed" + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public MedicalRecordApplyActionException(String field, String message) {
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

    public static String formatErrors(ValidationResult result) {
        return result.getErrors().stream()
                .map(error -> error.getField() + ": " + error.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}
