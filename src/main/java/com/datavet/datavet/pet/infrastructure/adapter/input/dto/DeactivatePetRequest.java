package com.datavet.datavet.pet.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeactivatePetRequest {

    @NotBlank(message = "El motivo de desactivación es obligatorio.")
    private String reason;
}
