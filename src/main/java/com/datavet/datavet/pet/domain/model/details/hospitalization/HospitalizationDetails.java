package com.datavet.datavet.pet.domain.model.details.hospitalization;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HospitalizationDetails implements MedicalRecordDetails {

    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;
    private String reason;
    private String diagnosisAtAdmission;
    private Boolean intensiveCare;
    private String ward;
    private String notes;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.HOSPITALIZATION;
    }

    @Override
    public void validate() {
        if (admissionDate == null || admissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de admisión no puede ser nula o tener una fecha futura a hoy.");
        }

        if (dischargeDate != null && dischargeDate.isBefore(admissionDate)) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de ingreso");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("La razón de la hospitalización no puede ser nula, ni estar vacia.");
        }

        if (diagnosisAtAdmission == null || diagnosisAtAdmission.isBlank()) {
            throw new IllegalArgumentException("El diagnostico de la hospitalización no puede ser nula o vacia.");
        }

        if (ward == null || ward.isBlank()) {
            throw new IllegalArgumentException("El area de hospitalización no puede ser nula o estar vacia.");
        }
    }

    public static HospitalizationDetails create(
            LocalDateTime admissionDate,
            LocalDateTime dischargeDate,
            String reason,
            String diagnosisAtAdmission,
            Boolean intensiveCare,
            String ward,
            String notes
    ) {
        HospitalizationDetails hospitalizationDetails = HospitalizationDetails.builder()
                .admissionDate(admissionDate)
                .dischargeDate(dischargeDate)
                .reason(reason)
                .diagnosisAtAdmission(diagnosisAtAdmission)
                .intensiveCare(intensiveCare)
                .ward(ward)
                .notes(notes)
                .build();

        hospitalizationDetails.validate();

        return hospitalizationDetails;
    }

}
