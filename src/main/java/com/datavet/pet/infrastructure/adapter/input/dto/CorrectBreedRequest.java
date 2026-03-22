package com.datavet.pet.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorrectBreedRequest {

    @NotBlank(message = "La nueva raza es obligatoria.")
    private String newBreed;

    @NotBlank(message = "El motivo de corrección es obligatorio.")
    private String reason;
}