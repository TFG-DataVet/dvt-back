package com.datavet.datavet.pet.application.port.in.command.pet;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;

@Getter
@Value
@Builder
public class CorrectPetBirthDateCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotNull(message = "La nueva fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    LocalDate newBirthDate;

    @NotBlank(message = "El motivo de la corrección es obligatorio")
    String reason;

}
