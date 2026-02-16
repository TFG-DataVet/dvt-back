package com.datavet.datavet.pet.domain.model.details.consultation;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsultationDetails implements MedicalRecordDetails {

    private String reason;
    private List<String> symptoms;
    private String clinicalFindings;
    private String diagnosis;
    private String treatmentPlan;
    private boolean followUpRequired;
    private LocalDate followUpDate;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.CONSULTATION;
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof ConsultationDetails)) {
            throw new IllegalArgumentException(
                    "No es posible corregir un registro con un tipo de detalle diferente."
            );
        }

        ConsultationDetails prev = (ConsultationDetails) previous;

        boolean reasonChanged = !Objects.equals(this.reason, prev.reason);
        boolean symptomsChanged = !Objects.equals(this.symptoms, prev.symptoms);
        boolean clinicalFindingsChanged = !Objects.equals(this.clinicalFindings, prev.clinicalFindings);
        boolean diagnosisChanged = !Objects.equals(this.diagnosis, prev.diagnosis);
        boolean treatmentPlanChanged = !Objects.equals(this.treatmentPlan, prev.treatmentPlan);

        return reasonChanged ||
                symptomsChanged ||
                clinicalFindingsChanged ||
                diagnosisChanged ||
                treatmentPlanChanged;
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
    }

    public static ConsultationDetails create(String reason,
                                             List<String> symptoms,
                                             String clinicalFindings,
                                             String diagnosis,
                                             String treatmentPlan,
                                             boolean followUpRequired,
                                             LocalDate followUpDate){
        ConsultationDetails consultationDetails = new ConsultationDetails(
                reason, symptoms, clinicalFindings, diagnosis, treatmentPlan, followUpRequired, followUpDate);

        consultationDetails.validate();

        return consultationDetails;
    }

}
