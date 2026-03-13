package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class DiagnosisDetailsRequest implements MedicalRecordDetailsRequest{
    private String diagnosisName;
    private String category;          // nombre del enum DiagnosisCategory
    private String description;
    private String severity;          // nombre del enum DiagnosisSeverity
    private LocalDate diagnosedAt;
    private boolean chronic;
    private boolean contagious;
    private List<String> symptoms;
    private List<String> recommendations;
    private boolean followUpRequired;
    private LocalDate followUpDate;
}
