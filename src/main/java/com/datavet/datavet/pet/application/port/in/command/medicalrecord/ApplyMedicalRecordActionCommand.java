package com.datavet.datavet.pet.application.port.in.command.medicalrecord;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApplyMedicalRecordActionCommand {

    @NotBlank(message = "El ID del registro médico es obligatorio")
    String medicalRecordId;

    @NotNull(message = "La acción a aplicar es obligatoria")
    RecordAction action;

    @NotBlank(message = "El ID del veterinario que aplica la acción es obligatorio")
    String veterinarianId;

}
