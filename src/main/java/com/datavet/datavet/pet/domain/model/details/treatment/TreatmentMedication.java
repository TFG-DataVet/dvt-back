package com.datavet.datavet.pet.domain.model.details.treatment;

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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del medicamento no puede estar vacio o ser nulo.");
        }

        if (dosage == null || dosage.isBlank()) {
            throw new IllegalArgumentException("La cantidad de la dosis no puede estar vacia o ser nula ");
        }

        if (frequency == null || frequency.isBlank()) {
            throw new IllegalArgumentException("La frecuencia de la dosis no puede estar vacia o ser nula ");
        }

        if (durationInDays == null || durationInDays <= 0) {
            throw new IllegalArgumentException("La duración del tratamiento no puede ser nula o ser menor a un día.");
        }
    }

    public static TreatmentMedication create(
            String name,
            String dosage,
            String frequency,
            Integer durationInDays,
            String notes
    ) {
        TreatmentMedication treatmentMedication = TreatmentMedication.builder()
                .name(name)
                .dosage(dosage)
                .frequency(frequency)
                .durationInDays(durationInDays)
                .notes(notes)
                .build();

        treatmentMedication.validate();

        return treatmentMedication;
    }
}
