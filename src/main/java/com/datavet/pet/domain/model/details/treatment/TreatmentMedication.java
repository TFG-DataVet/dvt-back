package com.datavet.pet.domain.model.details.treatment;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreatmentMedication {

    private String name;
    private String dosage;
    private String frequency;
    private Integer durationInDays;
    private String notes;

    public void validate() {
        ValidationResult result = new ValidationResult();

        if (name == null || name.isBlank()) result.addError("TreatmentMedication - name", "El nombre del medicamento no puede estar vacio o ser nulo.");
        if (dosage == null || dosage.isBlank()) result.addError("TreatmentMedication - dosage", "La cantidad de la dosis no puede estar vacia o ser nula ");
        if (frequency == null || frequency.isBlank()) result.addError("TreatmentMedication - frequency", "La frecuencia de la dosis no puede estar vacia o ser nula ");
        if (durationInDays == null || durationInDays <= 0) result.addError("TreatmentMedication - durationInDays", "La duración del tratamiento no puede ser nula o ser menor a un día.");

        if (result.hasErrors()) throw new MedicalRecordValidationException(result);
    }

    public static TreatmentMedication create(
            String name,
            String dosage,
            String frequency,
            Integer durationInDays,
            String notes
    ) {
        try {
            TreatmentMedication treatmentMedication = TreatmentMedication.builder()
                    .name(name)
                    .dosage(dosage)
                    .frequency(frequency)
                    .durationInDays(durationInDays)
                    .notes(notes)
                    .build();

            treatmentMedication.validate();

            return treatmentMedication;
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
