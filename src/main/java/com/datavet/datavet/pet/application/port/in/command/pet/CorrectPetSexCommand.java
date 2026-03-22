package com.datavet.datavet.pet.application.port.in.command.pet;

import com.datavet.datavet.pet.domain.model.Sex;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class CorrectPetSexCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "El nuevo sexo es obligatorio")
    Sex sex;

    @NotBlank(message = "El motivo de la corrección es obligatorio")
    String reason;

}
