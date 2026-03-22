package com.datavet.datavet.pet.infrastructure.adapter.input.dto;

import com.datavet.datavet.pet.domain.model.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorrectSexRequest {

    @NotNull(message = "El nuevo sexo es obligatorio.")
    private Sex sex;

    @NotBlank(message = "El motivo de corrección es obligatorio.")
    private String reason;
}