package com.datavet.clinic.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePendingClinicRequest {

    @NotBlank(message = "El nombre de la clínica es obligatorio")
    @Size(max = 100)
    private String clinicName;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$")
    private String phone;
}