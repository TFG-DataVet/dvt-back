package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ConsultationDetailsRequest implements  MedicalRecordDetailsRequest{
    private String reason;
    private List<String> symptoms;
    private String clinicalFindings;
    private String diagnosis;
    private String treatmentPlan;
    private boolean followUpRequired;
    private LocalDate followUpDate;
}
