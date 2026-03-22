package com.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class WeightDetailsRequest implements MedicalRecordDetailsRequest{
    private Double value;
    private String unit;
}
