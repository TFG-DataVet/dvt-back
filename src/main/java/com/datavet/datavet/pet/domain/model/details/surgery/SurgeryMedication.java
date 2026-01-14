package com.datavet.datavet.pet.domain.model.details.surgery;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurgeryMedication {

    private String name;
    private String dosage;
    private String frequency;
    private Integer durationInDays;
    private String notes;

    public void validate(){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del medicamento de la operación no puede ser nulo ni vacio.");
        }

        if (dosage == null || dosage.isBlank()) {
            throw new IllegalArgumentException("La dosis del tratamiento médico no puede ser nullo o estar vacio.");
        }

        if (frequency == null || frequency.isBlank()) {
            throw new IllegalArgumentException("La frecuencia de la medicación no puede ser nulla o estar vacia.");
        }

        if (durationInDays == null || durationInDays <= 0) {
            throw new IllegalArgumentException("La duración de los medicamentos no puede ser nulla o ser menor a un día.");
        }
    }

    public static SurgeryMedication create(
            String name,
            String dosage,
            String frequency,
            Integer durationInDays,
            String notes
    ){
        SurgeryMedication surgeryMedication = SurgeryMedication.builder()
                .name(name)
                .dosage(dosage)
                .frequency(frequency)
                .durationInDays(durationInDays)
                .notes(notes)
                .build();

        surgeryMedication.validate();

        return surgeryMedication;
    }
}
