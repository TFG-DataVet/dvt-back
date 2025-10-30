package com.datavet.datavet.owner.application.port.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

@Value
@Builder
public class UpdateOwnerCommand {
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
    private String ownerPhone;

    @NotBlank
    @Size(max = 70, message = "Owner email is required")
    private String ownerEmail;

    @NotBlank
    @Size(max = 30, message = "Owner address is required")
    private String ownerPassword;

    @NotBlank
    @Size(max = 50, message = "Owner password is required")
    private Address ownerAddress;
}
