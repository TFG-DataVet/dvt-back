package com.datavet.pet.application.port.in.command.pet;

import com.datavet.pet.domain.model.Sex;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;

@Getter
@Value
@Builder
public class CreatePetCommand {

    @NotBlank(message = "El ID de la clínica es obligatorio")
    String clinicId;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    String name;

    @NotBlank(message = "La especie es obligatoria")
    String species;

    @NotBlank(message = "La raza es obligatoria")
    String breed;

    @NotBlank(message = "El sexo es obligatorio")
    Sex sex;

    LocalDate dateOfBirth;

    String chipNumber;

    String avatarUrl;

    @NotBlank(message = "El ID del dueño es obligatorio")
    String ownerId;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    String ownerName;

    @NotBlank(message = "Ela apellido del dueño es obligatorio")
    String ownerLastName;

    @NotBlank(message = "El teléfono del dueño es obligatorio")
    Phone ownerPhone;
}
