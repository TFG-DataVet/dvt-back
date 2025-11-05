package com.datavet.datavet.shared.infrastructure.persistence.converter;

import com.datavet.datavet.shared.domain.valueobject.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for Email value object.
 * Converts Email to String for database storage.
 */
@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {
    
    @Override
    public String convertToDatabaseColumn(Email email) {
        return email != null ? email.getValue() : null;
    }
    
    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.trim().isEmpty() ? new Email(dbData) : null;
    }
}