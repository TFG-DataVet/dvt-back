package com.datavet.datavet.pet.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CorrectBirthDateRequest {

    @NotNull(message = "La nueva fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada.")
    private LocalDate newBirthDate;

    @NotBlank(message = "El motivo de corrección es obligatorio.")
    private String reason;
}