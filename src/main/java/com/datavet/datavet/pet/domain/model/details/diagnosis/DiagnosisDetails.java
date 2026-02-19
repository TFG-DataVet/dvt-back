package com.datavet.datavet.pet.domain.model.details.diagnosis;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiagnosisDetails implements MedicalRecordDetails {

    private String diagnosisName;
    private DiagnosisCategory category;
    private String description;
    private DiagnosisSeverity severity;
    private LocalDate diagnosedAt;
    private boolean chronic;
    private boolean contagious;
    private List<String> symptoms;
    private List<String> recommendations;
    private boolean followUpRequired;
    private LocalDate followUpDate;

    public static DiagnosisDetails create(
            String diagnosisName,
            DiagnosisCategory category,
            String description,
            DiagnosisSeverity severity,
            LocalDate diagnosedAt,
            boolean chronic,
            boolean contagious,
            List<String> symptoms,
            List<String> recommendations,
            boolean followUpRequired,
            LocalDate followUpDate
    ){
        try {
            DiagnosisDetails diagnosisDetails = new DiagnosisDetails(
                    diagnosisName,
                    category,
                    description,
                    severity,
                    diagnosedAt,
                    chronic,
                    contagious,
                    symptoms,
                    recommendations,
                    followUpRequired,
                    followUpDate);

            diagnosisDetails.validate();

            return diagnosisDetails;
        } catch (RuntimeException e) {
            throw e;
        }
    }


    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.DIAGNOSIS;
    }

    @Override
    public void validate() {
        if (diagnosisName == null || diagnosisName.isBlank()) {
            throw new IllegalArgumentException("El nombre del diagnostico no puede ser nulo o vacio.");
        }

        if (category == null) {
            throw new IllegalArgumentException("La categoria del diagnostico no puede ser nulo");
        }

        if (severity == null) {
            throw new IllegalArgumentException("La severidad del diagnostico no puede ser nula");
        }

        if (diagnosedAt == null || diagnosedAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del dianostico no puede ser nula, ni puede tener una fecha futura.");
        }

        if (!followUpRequired && followUpDate != null) {
            throw new IllegalArgumentException("El seguimiento no esta activado y ha seleccionado una fecha de seguimiento");
        }

        if (followUpRequired) {

            if (followUpDate == null) {
                throw new IllegalArgumentException("Ha seleccionado que el paciente requiere seguimiento, pero no ha seleccionado una fecha de seguimiento");
            }

            if (followUpDate.isBefore(diagnosedAt)) {
                throw new IllegalArgumentException("La fecha del seguimiento no puede ser anterior a la fecha del diagnostico");
            }

        }

        if (symptoms != null) {
            for (String symptom : symptoms) {
                if (symptom == null || symptom.isBlank()) {
                    throw new IllegalArgumentException("El sintoma no puede ser nulo ni puede estar vacio.");
                }
            }
        }

        if (recommendations != null) {
            for (String recommendation : recommendations) {
                if (recommendation == null || recommendation.isBlank()) {
                    throw new IllegalArgumentException("La recomendación no puede ser nula o estar vacia.");
                }
            }
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof DiagnosisDetails)) {
            throw new IllegalArgumentException(
                    "No es posible corregir un registro con un tipo de detalle diferente."
            );
        }

        DiagnosisDetails prev = (DiagnosisDetails) previous;

        boolean diagnosisNameChanged = !Objects.equals(this.diagnosisName, prev.diagnosisName);
        boolean categoryChanged = !Objects.equals(this.category, prev.category);
        boolean descriptionChanged = !Objects.equals(this.description, prev.description);
        boolean severityChanged = !Objects.equals(this.severity, prev.severity);
        boolean diagnosedAtChanged = !Objects.equals(this.diagnosedAt, prev.diagnosedAt);
        boolean chronicChanged = this.chronic != prev.chronic;
        boolean contagiousChanged = this.contagious != prev.contagious;
        boolean symptomsChanged = !Objects.equals(this.symptoms, prev.symptoms);
        boolean recommendationsChanged = !Objects.equals(this.recommendations, prev.recommendations);
        boolean followUpRequiredChanged = this.followUpRequired != prev.followUpRequired;

        return diagnosisNameChanged ||
                categoryChanged ||
                descriptionChanged ||
                severityChanged ||
                diagnosedAtChanged ||
                chronicChanged ||
                contagiousChanged ||
                symptomsChanged ||
                recommendationsChanged ||
                followUpRequiredChanged;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        throw new IllegalArgumentException("No se puede aplicar una acción de cambio de estado en un regristro que no tiene estados.");
    }


}
