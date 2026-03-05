package com.datavet.datavet.pet.domain.model.details.surgery;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurgeryProcedure {

    private String name;
    private String description;

    public void validate(){
        ValidationResult result = new ValidationResult();

        if (name == null || name.isBlank()) {
            result.addError("Surgery - procedure", "El nombre del procedimiento clinico no puede estar vacío.");
        }

        if (description != null && description.isBlank()) {
            result.addError("Surgery - procedura", "La descripción de la procedimiento no puede ser nula.");
        }

        if (result.hasErrors()) {
            throw new MedicalRecordValidationException(result);
        }
    }

    public static SurgeryProcedure create(String name, String description){
        SurgeryProcedure surgeryProcedure = new SurgeryProcedure(
                name,
                description);

        surgeryProcedure.validate();

        return surgeryProcedure;
    }
}
