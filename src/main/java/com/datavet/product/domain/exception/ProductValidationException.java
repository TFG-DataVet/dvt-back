package com.datavet.product.domain.exception;

import com.datavet.shared.domain.exception.DomainException;
import com.datavet.shared.domain.validation.ValidationResult;

public class ProductValidationException extends DomainException {

    private final ValidationResult validationResult;

    public ProductValidationException(ValidationResult validationResult) {
        super("Product validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static String formatErrors(ValidationResult result) {
        return result.getErrors().stream()
                .map(e -> e.getField() + ": " + e.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}
