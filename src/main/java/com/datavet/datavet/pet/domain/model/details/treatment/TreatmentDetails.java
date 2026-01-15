package com.datavet.datavet.pet.domain.model.details.treatment;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreatmentDetails implements MedicalRecordDetails {

    private String treatmentName;

    private LocalDate startDate;

    private String instructions;

    private LocalDate endDate;

    private List<TreatmentMedication> medications;

    private boolean followUpRequired;

    private LocalDate followUpDate;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.TREATMENT;
    }

    @Override
    public void validate() {
        if (treatmentName == null || treatmentName.isBlank()) {
            throw new IllegalArgumentException("El nombre del tratamiento no puede ser nulo.");
        }

        if (startDate == null || startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de comienzo del tratamiento no puede ser mayor a hoy.");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("LA fecha de culminación del tratamiento no puede ser mayor a la fecha de comienzo");
        }

        if (instructions == null || instructions.isBlank()) {
            throw new IllegalArgumentException("La instrucciones del tratamiento no pueden estar vacias.");
        }

        if (!followUpRequired && followUpDate != null) {
            throw new IllegalArgumentException("No debe existir fecha de seguimiento si no se requiere seguimiento.");
        }

        if (followUpRequired && followUpDate == null) {
            throw new IllegalArgumentException("Se requiere una fecha de seguimiento si el paciente necesaria seguimiento.");
        }

        if (followUpRequired && endDate != null && followUpDate.isBefore(endDate)) {
            throw new IllegalArgumentException(
                    "La fecha de seguimiento no puede ser anterior al fin del tratamiento."
            );
        }

        if (medications != null) {
            for (TreatmentMedication medication : medications) {
                medication.validate();
            }
        }
    }

    public static TreatmentDetails create(
            String treatmentName,
            LocalDate startDate,
            String instructions,
            LocalDate endDate,
            List<TreatmentMedication> medications,
            boolean followUpRequired,
            LocalDate followUpDate
    ) {
        TreatmentDetails treatmentDetails = TreatmentDetails.builder()
                .treatmentName(treatmentName)
                .startDate(startDate)
                .instructions(instructions)
                .endDate(endDate)
                .medications(medications)
                .followUpRequired(followUpRequired)
                .followUpDate(followUpDate)
                .build();

        treatmentDetails.validate();
        return treatmentDetails;
    }
}
