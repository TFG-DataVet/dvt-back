package com.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class AllergyDetailsRequest implements MedicalRecordDetailsRequest{
    private String allergen;
    private String type;              // nombre del enum AllergyType
    private String severity;          // nombre del enum AllergySeverity
    private List<String> reactions;
    private boolean lifeThreatening;
    private LocalDate identifiedAt;
    private String notes;
}
