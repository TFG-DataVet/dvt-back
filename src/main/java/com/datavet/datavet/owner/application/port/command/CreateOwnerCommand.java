package com.datavet.datavet.owner.application.port.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class CreateOwnerCommand {
    
    @NotBlank
    @Size(max = 50, message = "Owner name is required")
    private String ownerName;
    
    @NotBlank
    @Size(max = 50, message = "Owner lastname is required")
    private String ownerLastName;
    
    @NotBlank
    @Size(max = 10, message = "Owner dni is required")
    private String ownerDni;
    
    @NotBlank
    @Size(max= 9, message = "Owner phone is required")
    private Phone ownerPhone;
    
    @NotBlank
    @Size(max = 70, message = "Owner email is required")
    private Email ownerEmail;
    
    @NotBlank
    @Size(max = 30, message = "Owner address is required")
    private String ownerPassword;
    
    @NotBlank
    @Size(max = 50, message = "Owner password is required")
    private Address ownerAddress;
}
