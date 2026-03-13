package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentMedicationRequest {
    private String name;
    private String dosage;
    private String frequency;
    private Integer durationInDays;
    private String notes;
}
