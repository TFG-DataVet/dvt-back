package com.datavet.datavet.shared.domain.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a validation error with field and message information.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    
    private String field;
    private String message;
    
    @Override
    public String toString() {
        return field + ": " + message;
    }
}