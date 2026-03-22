package com.datavet.pet.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOwnerInfoRequest {

    @NotBlank(message = "El id del nuevo dueño es obligatorio.")
    private String ownerId;

    @NotBlank(message = "El nombre del dueño es obligatorio.")
    private String ownerName;

    @NotBlank(message = "El apellido del dueño es obligatorio.")
    private String ownerLastName;

    @NotBlank(message = "El teléfono del dueño es obligatorio.")
    private String ownerPhone;
}