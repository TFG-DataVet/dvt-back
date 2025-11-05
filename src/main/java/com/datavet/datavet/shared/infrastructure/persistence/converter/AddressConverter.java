package com.datavet.datavet.shared.infrastructure.persistence.converter;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA converter for Address value object.
 * Converts Address to JSON string for database storage.
 */
@Converter(autoApply = true)
@Slf4j
public class AddressConverter implements AttributeConverter<Address, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Address address) {
        if (address == null) {
            return null;
        }
        
        try {
            // Create a simple JSON representation
            return String.format("{\"street\":\"%s\",\"city\":\"%s\",\"postalCode\":\"%s\"}", 
                address.getStreet(), 
                address.getCity(), 
                address.getPostalCode() != null ? address.getPostalCode() : "");
        } catch (Exception e) {
            log.error("Error converting Address to database column", e);
            throw new RuntimeException("Error converting Address to database column", e);
        }
    }
    
    @Override
    public Address convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Parse the JSON and create Address
            if (dbData.startsWith("{")) {
                // Handle JSON format
                var node = objectMapper.readTree(dbData);
                String street = node.get("street").asText();
                String city = node.get("city").asText();
                String postalCode = node.has("postalCode") && !node.get("postalCode").asText().isEmpty() 
                    ? node.get("postalCode").asText() : null;
                return new Address(street, city, postalCode);
            } else {
                // Handle legacy format - assume it's just the street
                return new Address(dbData, "Unknown", null);
            }
        } catch (Exception e) {
            log.error("Error converting database column to Address", e);
            throw new RuntimeException("Error converting database column to Address", e);
        }
    }
}