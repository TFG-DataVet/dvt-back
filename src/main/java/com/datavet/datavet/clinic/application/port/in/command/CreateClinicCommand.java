package com.datavet.datavet.clinic.application.port.in.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

@Value
@RequiredArgsConstructor
public class CreateClinicCommand {
    @NotBlank(message = "Clinic name is required")
    @Size(max = 100, message = "Clinic name must not exceed 100 characters")
    private String clinicName;
    
    @NotBlank(message = "Legal name is required")
    @Size(max = 150, message = "Legal name must not exceed 150 characters")
    private String legalName;
    
    @NotBlank(message = "Legal number is required")
    @Size(max = 50, message = "Legal number must not exceed 50 characters")
    private String legalNumber;
    
    @NotNull(message = "Address is required")
    @Valid
    private Address address;
    
    @NotNull(message = "Phone is required")
    @Valid
    private Phone phone;
    
    @NotNull(message = "Email is required")
    @Valid
    private Email email;
    
    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logoUrl;
}