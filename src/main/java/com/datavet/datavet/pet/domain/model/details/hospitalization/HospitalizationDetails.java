package com.datavet.datavet.pet.domain.model.details.hospitalization;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private ClinicalCondition condition;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.HOSPITALIZATION;
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof HospitalizationDetails )) return false;

        if (this.status == HospitalizationStatus.COMPLETED ||
            this.status == HospitalizationStatus.CANCELLED ||
            this.status == HospitalizationStatus.DECEASED) {
            return false;
        }

        HospitalizationDetails prev = (HospitalizationDetails) previous;

        boolean reasonChanged =  !Objects.equals(this.reason, prev.reason);
        boolean diagnosisAtAdmissionChanged = !this.diagnosisAtAdmission.equals(prev.diagnosisAtAdmission);
        boolean notesChanged = !this.notes.equals(prev.notes);
        boolean wardChanges = !this.ward.equals(prev.ward);
        boolean intensiveCareChanged = !this.intensiveCare.equals(prev.intensiveCare);

        return reasonChanged || diagnosisAtAdmissionChanged || notesChanged || wardChanges || intensiveCareChanged;
    }

    @Override
    public void validate() {

        if (status == null) {
            throw new IllegalArgumentException("El estado de la hospitalización no debe estar vacio.");
        }

        if (this.status == HospitalizationStatus.SCHEDULED){
            if (admissionDate != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puedo tener una fecha de admición");
            }
            if (dischargeDate != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puedo tener una fecha de finalización");
            }

            if (condition != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puede tener una condición clinica");
            }
        }

        if (this.status == HospitalizationStatus.ADMITTED) {
            if (admissionDate == null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " debe de tener una fecha de admición");
            }

            if (dischargeDate != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no debe de tener una fecha de culminación");
            }

            if (condition != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puede tener una condición clinica");
            }
        }

        if (this.status == HospitalizationStatus.IN_PROGRESS) {
            if (admissionDate == null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " debe de tener una fecha de admición");
            }

            if (dischargeDate != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + "no puedo tener una fecha de finalización");
            }
        }

        if (this.status == HospitalizationStatus.COMPLETED ||
            this.status == HospitalizationStatus.DECEASED) {
            if (admissionDate == null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " debe de tener una fecha de admición");
            }

            if (dischargeDate == null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + "no puedo tener una fecha de finalización");
            }

            if (condition != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puede tener una condición clinica");
            }
        }

        if (this.status == HospitalizationStatus.CANCELLED) {
            if (admissionDate != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no debe de tener una fecha de admición");
            }

            if (dischargeDate == null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " debe de tener una fecha de finalización");
            }

            if (condition != null) {
                throw new IllegalArgumentException("Una hospitalización en estado de " + status + " no puede tener una condición clinica");
            }
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
    }

    public static HospitalizationDetails create(
            String reason,
            String diagnosisAtAdmission,
            Boolean intensiveCare,
            String ward,
            String notes
    ) {
        HospitalizationDetails hospitalizationDetails = HospitalizationDetails.builder()
                .reason(reason)
                .diagnosisAtAdmission(diagnosisAtAdmission)
                .intensiveCare(intensiveCare)
                .ward(ward)
                .notes(notes)
                .status(HospitalizationStatus.SCHEDULED)
                .build();
        hospitalizationDetails.validate();

        return hospitalizationDetails;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action){
        var previuos = this.status;
        var next = this.status.next(action);

        this.status = next;

        if (next == HospitalizationStatus.ADMITTED) {
            admissionDate = LocalDateTime.now();
        }

        if (next == HospitalizationStatus.COMPLETED ||
            next == HospitalizationStatus.DECEASED ||
            next == HospitalizationStatus.CANCELLED) {
            dischargeDate = LocalDateTime.now();
        }

        this.validate();

        return StatusChangeResult.of(previuos, next);
    }

    public void changeClinicalCondition(ClinicalCondition condition){
        if (this.status != HospitalizationStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Solo se puede modificar la condición clínica cuando la hospitalización está en progreso.");
        }

        this.condition = condition;
        this.validate();
    }
}
