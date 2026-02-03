package com.datavet.datavet.pet.domain.model.details.consultation;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsultationDetails implements MedicalRecordDetails {

    private String reason;
    private List<String> symptoms;
    private String clinicalFindings;
    private String diagnosis;
    private String treatmentPlan;
    private ConsultationStatus status;
    private boolean followUpRequired;
    private LocalDate followUpDate;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.CONSULTATION;
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof ConsultationDetails)) return false;
        ConsultationDetails prev = (ConsultationDetails) previous;

        boolean reasonChanged = !this.reason.equals(prev.reason);
        boolean diagnosisChanged = !this.diagnosis.equals(prev.diagnosis);
        boolean treatmentChanged = !this.treatmentPlan.equals(prev.treatmentPlan);

        return reasonChanged || diagnosisChanged || treatmentChanged;
    }

    @Override
    public void validate(){
        if (reason == null || reason.isBlank()){
            throw new IllegalArgumentException("La razón de la consulta no puede estar vacía.");
        }

        if ( followUpRequired && followUpDate == null ) {
            throw new IllegalArgumentException("Si el paciente requiere seguimiento, debe de escoger una fecha para la proxima consulta.");
        }

        if (followUpDate != null && followUpDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha selecciona no puede ser anterior al día de hoy.");
        }

        if (symptoms != null) {
            for (String symptom : symptoms) {
                if (symptom == null || symptom.isBlank()) {
                    throw new IllegalArgumentException("Los síntomas no pueden estar vacíos.");
                }
            }
        }

        if (status == null){
            throw new IllegalArgumentException("El estado de la consulta no puede estar vacio.");
        }
    }

    public static ConsultationDetails create(String reason,
                                             List<String> symptoms,
                                             String clinicalFindings,
                                             String diagnosis,
                                             String treatmentPlan,
                                             boolean followUpRequired,
                                             LocalDate followUpDate){
        ConsultationDetails consultationDetails = ConsultationDetails.builder()
                .reason(reason)
                .symptoms(symptoms)
                .clinicalFindings(clinicalFindings)
                .diagnosis(diagnosis)
                .treatmentPlan(treatmentPlan)
                .status(ConsultationStatus.SCHEDULED)
                .followUpRequired(followUpRequired)
                .followUpDate(followUpDate)
                .build();

        consultationDetails.validate();

        return consultationDetails;
    }

    public void markAsCompleted(){
        if (status != ConsultationStatus.SCHEDULED){
            throw new IllegalArgumentException("Solo una consulta programada puede completarse");
        }

        this.status = ConsultationStatus.COMPLETED;
    }

    public void markAsNoShow(){
        if (status != ConsultationStatus.SCHEDULED){
            throw new IllegalArgumentException("Solo una consulta programada puede ocultarse");
        }

        this.status = ConsultationStatus.NO_SHOW;
    }
}
