package com.datavet.auth.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "El nombre de la clínica es obligatorio")
    @Size(max = 100)
    private String clinicName;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72)
    private String password;
}