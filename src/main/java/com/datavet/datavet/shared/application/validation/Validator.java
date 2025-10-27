package com.datavet.datavet.shared.application.validation;

import com.datavet.datavet.shared.domain.validation.ValidationResult;

/**
 * Interface for validation utilities.
 * Provides a common contract for validating objects across domains.
 */
public interface Validator<T> {
    
    /**
     * Validates the given object and returns a validation result.
     */
    ValidationResult validate(T object);
}