package com.datavet.pet.infrastructure.adapter.input.dto;

import com.datavet.pet.domain.model.Sex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreatePetRequest {

    @NotBlank(message = "El id de la clínica es obligatorio.")
    private String clinicId;

    @NotBlank(message = "El nombre de la mascota es obligatorio.")
    private String name;

    @NotBlank(message = "La especie es obligatoria.")
    private String species;

    @NotBlank(message = "La raza es obligatoria.")
    private String breed;

    @NotNull(message = "El sexo es obligatorio.")
    private Sex sex;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada.")
    private LocalDate dateOfBirth;

    // Opcional – chip puede ser null
    private String chipNumber;

    // Opcional – foto de perfil
    private String avatarUrl;

    @NotNull(message = "Los datos del dueño son obligatorios.")
    @Valid
    private OwnerRequest owner;

    // ── Inner DTO del dueño ─────────────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class OwnerRequest {

        @NotBlank(message = "El id del dueño es obligatorio.")
        private String ownerId;

        @NotBlank(message = "El nombre del dueño es obligatorio.")
        private String ownerName;

        @NotBlank(message = "El apellido del dueño es obligatorio.")
        private String ownerLastName;

        @NotBlank(message = "El teléfono del dueño es obligatorio.")
        private String ownerPhone;
    }
}