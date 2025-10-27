package com.datavet.datavet.shared.domain.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a validation error with field and message information.
 */
@Getter
@AllArgsConstructor
public class ValidationError {
    
    private final String field;
    private final String message;
    
    @Override
    public String toString() {
        return field + ": " + message;
    }
}