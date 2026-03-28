package com.datavet.owner.application.port.in.command;

import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateOwnerCommand {

    private String ownerID;

    @NotBlank
    @Size(max = 50, message = "Owner name is required")
    private String ownerName;

    @NotBlank
    @Size(max = 50, message = "Owner lastname is required")
    private String ownerLastName;

    @NotNull(message = "Dni is required")
    private DocumentId ownerDni;

    @NotBlank
    @Size(max= 15, message = "Owner phone is required")
    private Phone ownerPhone;

    @NotBlank
    @Size(max = 70, message = "Owner email is required")
    private Email ownerEmail;

    @NotBlank
    @Size(max = 50, message = "Owner password is required")
    private Address ownerAddress;

    private String url;
}
