package com.datavet.datavet.pet.application.port.in.command.pet;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CorrectPetBreedCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "La nueva raza no puede estar vacía")
    String newBreed;

    @NotBlank(message = "El motivo de la corrección es obligatorio")
    String reason;
}
