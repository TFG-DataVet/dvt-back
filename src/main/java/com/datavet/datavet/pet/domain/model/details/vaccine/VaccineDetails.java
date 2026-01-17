package com.datavet.datavet.pet.domain.model.details.vaccine;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccineDetails implements MedicalRecordDetails {

    private String vaccineName;

    private LocalDate applicationDate;

    private LocalDate nextDoseDate;

    private String batchNumber;

    private String manufacturer;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.VACCINE;
    }

    @Override
    public void validate() {
        if (vaccineName == null || vaccineName.isBlank()) {
            throw new IllegalArgumentException("Vaccine name cannot be null");
        }

        if (applicationDate == null || applicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("ApplicationDate cannot be null or cannot have a future date");
        }

        if (nextDoseDate != null && nextDoseDate.isBefore(applicationDate)) {
            throw new IllegalArgumentException("Next dose date cannot be before application date");
        }

        if (batchNumber == null || batchNumber.isBlank()) {
            throw new IllegalArgumentException("BatchNumber cannot be null");
        }
    }

    public static VaccineDetails create(String vaccineName,
                                 LocalDate applicationDate,
                                 LocalDate nextDoseDate,
                                 String batchNumber,
                                 String manufacturer) {

        VaccineDetails vaccineDetails = VaccineDetails.builder()
                .vaccineName(vaccineName)
                .applicationDate(applicationDate)
                .nextDoseDate(nextDoseDate)
                .batchNumber(batchNumber)
                .manufacturer(manufacturer)
                .build();

        vaccineDetails.validate();

        return vaccineDetails;
    }
}
