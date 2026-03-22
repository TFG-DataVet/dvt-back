package com.datavet.datavet.pet.infrastructure.adapter.input.dto.medicalrecord;

import com.datavet.datavet.pet.application.port.in.command.medicalrecord.details.MedicalRecordDetailsRequest;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMedicalRecordRequest {

    @NotBlank(message = "El id de la mascota es obligatorio.")
    private String petId;

    @NotBlank(message = "El id de la clínica es obligatorio.")
    private String clinicId;

    @NotNull(message = "El tipo de registro médico es obligatorio.")
    private MedicalRecordType type;

    @NotBlank(message = "El id del veterinario es obligatorio.")
    private String veterinarianId;

    // Opcional – notas clínicas del veterinario
    private String notes;

    /**
     * Polimórfico gracias a @JsonTypeInfo / @JsonSubTypes declarado en la interfaz.
     * El cliente envía: { "detailsType": "VACCINE", "vaccineName": "...", ... }
     */
    @NotNull(message = "Los detalles del registro médico son obligatorios.")
    @Valid
    private MedicalRecordDetailsRequest details;
}