package com.datavet.pet.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePetRequest {

    @NotBlank(message = "El nuevo nombre de la mascota es obligatorio.")
    private String name;

    @NotBlank(message = "La nueva URL de avatar es obligatoria.")
    private String avatarUrl;
}