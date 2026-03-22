package com.datavet.pet.application.port.in.command.medicalrecord.details;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class VaccineDetailsRequest implements  MedicalRecordDetailsRequest{
    private String vaccineName;
    private LocalDate applicationDate;
    private LocalDate nextDoseDate;
    private String batchNumber;
    private String manufacturer;
}
