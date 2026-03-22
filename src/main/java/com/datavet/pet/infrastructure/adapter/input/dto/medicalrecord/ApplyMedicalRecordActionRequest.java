package com.datavet.pet.infrastructure.adapter.input.dto.medicalrecord;

import com.datavet.pet.domain.model.action.RecordAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyMedicalRecordActionRequest {

    @NotNull(message = "La acción a aplicar es obligatoria.")
    private RecordAction action;

    @NotBlank(message = "El id del veterinario que aplica la acción es obligatorio.")
    private String veterinarianId;
}