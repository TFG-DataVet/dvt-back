package com.datavet.clinic.infrastructure.adapter.input.dto;

import com.datavet.clinic.domain.model.LegalType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CompleteClinicSetupRequest {

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 150)
    private String legalName;

    @NotBlank(message = "El número legal es obligatorio")
    @Size(max = 50)
    private String legalNumber;

    @NotNull(message = "El tipo de persona jurídica es obligatorio")
    private LegalType legalType;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50)
    private String city;

    @Size(max = 10)
    private String codePostal;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String logoUrl;

    @NotBlank(message = "Los días de apertura son obligatorios")
    private String scheduleOpenDays;

    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime scheduleOpenTime;

    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime scheduleCloseTime;

    private String scheduleNotes;
}