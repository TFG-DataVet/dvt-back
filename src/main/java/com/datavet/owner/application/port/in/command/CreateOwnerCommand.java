package com.datavet.owner.application.port.in.command;

import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOwnerCommand {
    
    @NotBlank(message = "Clinic id is required")
    @Size(max = 50, message = "Clinic id can't be mayor a 50  ")
    private String clinidId;

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Owner lastname is required")
    private String ownerLastName;
    
    @NotNull(message = "Dni is required")
    private DocumentId ownerDni;
    
    @NotBlank(message = "Phone is required")
    @Size(max= 9, message = "Owner phone is required")
    private Phone ownerPhone;
    
    @NotBlank(message = "Email is required")
    @Size(max = 70, message = "Owner email is required")
    private Email ownerEmail;

    @NotBlank
    @Size(max = 50, message = "Owner password is required")
    private Address ownerAddress;

    private String url;

    private boolean acceptTermsAndCond;
}
