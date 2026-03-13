package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class SurgeryDetailsRequest implements MedicalRecordDetailsRequest{
    private String surgeryName;
    private String surgeryType;
    private List<SurgeryProcedureRequest> procedures;
    private String anesthesiaType;
    private boolean hospitalizationRequired;
    private LocalDateTime surgeryDate;
}
