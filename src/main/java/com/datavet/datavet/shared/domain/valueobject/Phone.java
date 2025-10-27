package com.datavet.datavet.shared.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Value object representing a phone number.
 * Ensures phone format validation and immutability.
 */
@Getter
@EqualsAndHashCode
public class Phone {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{7,15}$"
    );
    
    private final String value;
    
    public Phone(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        
        String trimmedValue = value.trim();
        if (!PHONE_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid phone format: " + trimmedValue);
        }
        
        this.value = trimmedValue;
    }
    
    @Override
    public String toString() {
        return value;
    }
}