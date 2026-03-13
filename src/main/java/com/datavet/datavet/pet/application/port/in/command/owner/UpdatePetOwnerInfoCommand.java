package com.datavet.datavet.pet.application.port.in.command.owner;

import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdatePetOwnerInfoCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "El ID del dueño es obligatorio")
    String ownerId;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    String ownerName;

    @NotBlank(message = "El apellido del dueño es obligatorio")
    String ownerLastName;

    @NotBlank(message = "El teléfono del dueño es oblogatorio")
    Phone ownerPhone;

}
