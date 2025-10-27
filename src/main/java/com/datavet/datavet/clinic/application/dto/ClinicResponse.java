package com.datavet.datavet.clinic.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@AllArgsConstructor
public class ClinicResponse {

    private Long clinicId;
    private String clinicName;
    private String legalName;
    private String legalNumber;
    
    // Address value object fields
    @JsonProperty("address")
    private AddressDto address;
    
    // Phone value object field
    private String phone;
    
    // Email value object field
    private String email;
    
    private String logoUrl;
    private String suscriptionStatus;
    
    @Getter
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String postalCode;
        
        @JsonProperty("fullAddress")
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder(street);
            sb.append(", ").append(city);
            if (postalCode != null && !postalCode.isEmpty()) {
                sb.append(" ").append(postalCode);
            }
            return sb.toString();
        }
    }
}