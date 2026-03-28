package com.datavet.employee.infrastructure.adapter.input.dto;

import com.datavet.shared.domain.valueobject.DocumentId;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateEmployeeRequest {


    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20)
    private String documentNumber;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Size(max = 100)
    private String email;

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

    // Obligatorio solo si role == CLINIC_VETERINARIAN
    // La validación la hace el dominio
    private String licenseNumber;

    @NotNull(message = "La fecha de contratación es obligatoria")
    private LocalDate hireDate;

    @NotBlank(message = "El rol es obligatorio")
    private String role;
}