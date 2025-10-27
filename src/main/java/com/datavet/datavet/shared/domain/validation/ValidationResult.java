package com.datavet.datavet.shared.domain.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation.
 * Contains validation errors and provides methods to check validation status.
 */
@Getter
public class ValidationResult {
    
    private final List<ValidationError> errors;
    
    public ValidationResult() {
        this.errors = new ArrayList<>();
    }
    
    public ValidationResult(List<ValidationError> errors) {
        this.errors = new ArrayList<>(errors);
    }
    
    public void addError(String field, String message) {
        this.errors.add(new ValidationError(field, message));
    }
    
    public void addError(ValidationError error) {
        this.errors.add(error);
    }
    
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}