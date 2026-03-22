package com.datavet.datavet.pet.infrastructure.adapter.input.dto.medicalrecord;

import com.datavet.datavet.pet.application.port.in.command.medicalrecord.details.MedicalRecordDetailsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorrectMedicalRecordRequest {

    @NotBlank(message = "El id del veterinario que realiza la corrección es obligatorio.")
    private String veterinarianId;

    @NotBlank(message = "El motivo de la corrección es obligatorio.")
    private String reason;

    /**
     * Nuevos detalles corregidos. Debe ser del mismo tipo que el registro original.
     * Polimórfico gracias a @JsonTypeInfo declarado en MedicalRecordDetailsRequest.
     */
    @NotNull(message = "Los detalles corregidos son obligatorios.")
    @Valid
    private MedicalRecordDetailsRequest details;
}