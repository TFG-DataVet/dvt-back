package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class HospitalizationDetailsRequest implements MedicalRecordDetailsRequest{
    private String reason;
    private String diagnosisAtAdmission;
    private Boolean intensiveCare;
    private String ward;
    private String notes;
}
