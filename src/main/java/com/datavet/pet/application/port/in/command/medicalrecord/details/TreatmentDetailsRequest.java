package com.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class TreatmentDetailsRequest implements MedicalRecordDetailsRequest{
    private String treatmentName;
    private LocalDate startDate;
    private String instructions;
    private LocalDate estimatedEndDate;
    private List<TreatmentMedicationRequest> medications;
    private boolean followUpRequired;
    private LocalDate followUpDate;
}
