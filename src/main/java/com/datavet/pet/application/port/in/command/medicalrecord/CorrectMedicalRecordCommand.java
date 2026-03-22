package com.datavet.pet.application.port.in.command.medicalrecord;

import com.datavet.pet.application.port.in.command.medicalrecord.details.MedicalRecordDetailsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class CorrectMedicalRecordCommand {

    @NotBlank(message = "El ID del registro original es obligatorio")
    String originalRecordId;

    @NotBlank(message = "El ID del veterinario que realiza la corrección es obligatorio")
    String veterinarianId;

    @NotBlank(message = "El motivo de la corrección es obligatorio")
    String reason;

    @NotNull(message = "Los detalles corregidos son obligatorios")
    @Valid
    MedicalRecordDetailsRequest detailsRequest;
}
