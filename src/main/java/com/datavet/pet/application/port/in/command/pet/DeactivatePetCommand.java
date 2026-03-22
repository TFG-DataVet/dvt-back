package com.datavet.pet.application.port.in.command.pet;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class DeactivatePetCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "El motivo de la desactivación es obligatorio")
    String reason;

}
