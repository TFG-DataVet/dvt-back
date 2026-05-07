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
    
    private String clinicId;

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Owner lastname is required")
    private String ownerLastName;
    
    @NotNull(message = "Dni is required")
    private DocumentId ownerDni;
    
    private Phone ownerPhone;

    private Email ownerEmail;

    private Address ownerAddress;

    private String url;

    private boolean acceptTermsAndCond;
}
