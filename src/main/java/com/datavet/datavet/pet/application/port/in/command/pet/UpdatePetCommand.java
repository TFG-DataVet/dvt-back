package com.datavet.datavet.pet.application.port.in.command.pet;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class UpdatePetCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "El nuevo nombre no puede ser vacío")
    String name;

    @NotBlank(message = "La nueva URL del avatar no puede estar vacía")
    String avatarUrl;
}
