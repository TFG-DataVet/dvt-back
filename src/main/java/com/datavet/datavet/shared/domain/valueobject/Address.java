package com.datavet.datavet.shared.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object representing a physical address.
 * Ensures address validation and immutability.
 */
@Getter
@EqualsAndHashCode
public class Address {
    
    private final String street;
    private final String city;
    private final String postalCode;
    
    public Address(String street, String city, String postalCode) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        
        this.street = street.trim();
        this.city = city.trim();
        this.postalCode = postalCode != null ? postalCode.trim() : null;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(street);
        sb.append(", ").append(city);
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getFullAddress();
    }
}