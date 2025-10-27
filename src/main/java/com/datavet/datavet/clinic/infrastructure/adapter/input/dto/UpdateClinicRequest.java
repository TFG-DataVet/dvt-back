package com.datavet.datavet.clinic.infrastructure.adapter.input.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class UpdateClinicRequest {
    @NotBlank(message = "Clinic name is required")
    @Size(max = 100, message = "Clinic name must not exceed 100 characters")
    private String clinicName;
    
    @NotBlank(message = "Legal name is required")
    @Size(max = 150, message = "Legal name must not exceed 150 characters")
    private String legalName;
    
    @NotBlank(message = "Legal number is required")
    @Size(max = 50, message = "Legal number must not exceed 50 characters")
    private String legalNumber;
    
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String codePostal;
    
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$", message = "Phone number format is invalid")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logoUrl;
    
    @Size(max = 50, message = "Subscription status must not exceed 50 characters")
    private String suscriptionStatus;
}