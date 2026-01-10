package com.datavet.datavet.clinic.application.port.in.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Value;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

@Value
@Builder
public class UpdateClinicCommand {
    @NotNull(message = "Clinic ID is required")
    String clinicId;
    
    @NotBlank(message = "Clinic name is required")
    @Size(max = 100, message = "Clinic name must not exceed 100 characters")
    String clinicName;
    
    @NotBlank(message = "Legal name is required")
    @Size(max = 150, message = "Legal name must not exceed 150 characters")
    String legalName;
    
    @NotBlank(message = "Legal number is required")
    @Size(max = 50, message = "Legal number must not exceed 50 characters")
    String legalNumber;
    
    @NotNull(message = "Address is required")
    @Valid
    Address address;
    
    @NotNull(message = "Phone is required")
    @Valid
    Phone phone;
    
    @NotNull(message = "Email is required")
    @Valid
    Email email;
    
    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    String logoUrl;
    
    @Size(max = 50, message = "Subscription status must not exceed 50 characters")
    String suscriptionStatus;
}