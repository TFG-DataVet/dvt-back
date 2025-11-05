package com.datavet.datavet.shared.infrastructure.persistence.converter;

import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for Phone value object.
 * Converts Phone to String for database storage.
 */
@Converter(autoApply = true)
public class PhoneConverter implements AttributeConverter<Phone, String> {
    
    @Override
    public String convertToDatabaseColumn(Phone phone) {
        return phone != null ? phone.getValue() : null;
    }
    
    @Override
    public Phone convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.trim().isEmpty() ? new Phone(dbData) : null;
    }
}