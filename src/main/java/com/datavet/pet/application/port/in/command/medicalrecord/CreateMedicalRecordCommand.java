package com.datavet.pet.application.port.in.command.medicalrecord;

import com.datavet.pet.application.port.in.command.medicalrecord.details.MedicalRecordDetailsRequest;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class CreateMedicalRecordCommand {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    String petId;

    @NotBlank(message = "El ID de la clinica es obligatorio")
    String clinicId;

    @NotNull(message = "El tipo de registro médico es obligatorio")
    MedicalRecordType type;

    @NotBlank(message = "El ID del veterinario es obligatorio")
    String veterinarianId;

    String notes;

    @NotNull(message = "El detalle del registro médico es obligatorio")
    @Valid
    MedicalRecordDetailsRequest detailsRequest;
}
