package com.datavet.employee.infrastructure.adapter.input.dto;

import com.datavet.shared.domain.valueobject.DocumentId;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20)
    private DocumentId documentNumber;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50)
    private String city;

    @Size(max = 10)
    private String postalCode;

    @Size(max = 255)
    private String avatarUrl;

    @Size(max = 100)
    private String speciality;

    private String licenseNumber;

    @NotBlank(message = "El rol es obligatorio")
    private String role;
}