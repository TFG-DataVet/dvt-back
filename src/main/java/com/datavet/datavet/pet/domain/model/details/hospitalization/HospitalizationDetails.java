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
    private HospitalizationStatus status;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.HOSPITALIZATION;
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof HospitalizationDetails )) return false;

        HospitalizationDetails prev = (HospitalizationDetails) previous;

        boolean reasonChanged = !this.reason.equals(prev.reason);
        boolean diagnosisAtAdmissionChanged = !this.diagnosisAtAdmission.equals(prev.diagnosisAtAdmission);
        boolean notesChanged = !this.notes.equals(prev.notes);
        boolean wardChanges = !this.ward.equals(prev.ward);
        boolean intensiveCareChanged = !this.intensiveCare.equals(prev.intensiveCare);

        return reasonChanged || diagnosisAtAdmissionChanged || notesChanged || wardChanges || intensiveCareChanged;
    }

    @Override
    public void validate() {
        if (admissionDate == null || admissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de admisión no puede ser nula o tener una fecha futura a hoy.");
        }

        if (dischargeDate != null && dischargeDate.isBefore(admissionDate)) {
            throw new IllegalArgumentException("La fecha de salida no puede ser anterior a la fecha de ingreso");
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

        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("La nota hospitalaria no puede ser nula o estar vacia.");
        }

        if (status == null) {
            throw new IllegalArgumentException("El estado de la hospitalización no debe estar vacio.");
        }
    }

    public static HospitalizationDetails create(
            LocalDateTime admissionDate,
            LocalDateTime dischargeDate,
            String reason,
            String diagnosisAtAdmission,
            Boolean intensiveCare,
            String ward,
            String notes,
            HospitalizationStatus status
    ) {
        HospitalizationDetails hospitalizationDetails = HospitalizationDetails.builder()
                .admissionDate(admissionDate)
                .dischargeDate(dischargeDate)
                .reason(reason)
                .diagnosisAtAdmission(diagnosisAtAdmission)
                .intensiveCare(intensiveCare)
                .ward(ward)
                .notes(notes)
                .status(status)
                .build();

        hospitalizationDetails.validate();

        return hospitalizationDetails;
    }

    public void markAsCompleted(){
        if (status != HospitalizationStatus.IN_TREATMENT){
            throw new IllegalArgumentException("Solo una hospitalización EN TRATAMIENTO puede ser dada de alta");
        }

        this.status = HospitalizationStatus.DISCHARGED;
        dischargeDate = LocalDateTime.now();
    }

    public void markAsInTreatment(){
        if (this.status != HospitalizationStatus.ADMITTED) {
            throw new IllegalArgumentException("Solo una hospitalización admitida puede ser pasada como en tratamiento");
        }

        this.status = HospitalizationStatus.IN_TREATMENT;
    }
}
