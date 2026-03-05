package com.datavet.datavet.pet.domain.model.details.diagnosis;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
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
        } catch (MedicalRecordValidationException e) {
            throw e;
        }
    }

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.DIAGNOSIS;
    }

    @Override
    public void validate() {
        ValidationResult result = new ValidationResult();

        if (diagnosisName == null || diagnosisName.isBlank()) {
            result.addError("Diagnosis - name", "El nombre del diagnostico no puede ser nulo o vacio.");
        }

        if (category == null) {
            result.addError("Diagnosis - category", "La categoria del diagnostico no puede ser nulo.");
        }

        if (severity == null) {
            result.addError("Diagnosis - severity", "La severidad del diagnostico no puede ser nula.");
        }

        if (Objects.isNull(diagnosedAt)) {
            result.addError("Diagnosis - DiagnosedAt ", "La fecha del dianostico no puede ser nula.");
        } else if (diagnosedAt.isAfter(LocalDate.now())) {
            result.addError("Diagnosis - DiagnosedAt ", "La fecha del dianostico no puede tener una fecha futura a hoy.");
        }

        if (!followUpRequired && followUpDate != null) {
            result.addError("Diagnosis - followUpRequired", "El seguimiento no esta activado y ha seleccionado una fecha de seguimiento.");
        }

        if (followUpRequired) {
            if (followUpDate == null) {
                result.addError("Diagnosis - followUpRequired FollowUpDate", "Ha seleccionado que el paciente requiere seguimiento, pero no ha seleccionado una fecha de seguimiento.");
            }

            if(followUpDate == null){
                result.addError("Diagnosis - followUpDate", "La fecha del seguimiento no puede estar vacia o ser nula.");
            } else {
                if (followUpDate.isBefore(diagnosedAt)) {
                    result.addError("Diagnosis - followUpRequired - followUpRequiered", "La fecha del seguimiento no puede ser anterior a la fecha del diagnostico.");
                }
            }

        }

        if (symptoms == null || symptoms.isEmpty()) {
            result.addError("Diagnosis - symptoms", "El sintoma no puede ser nulo ni puede estar vacio.");
        }

        if (recommendations == null || recommendations.isEmpty()) {
            result.addError("Diagnosis - recommendations", "La recomendación no puede ser nula o estar vacia.");
        }

        if (result.hasErrors()) {
            throw new MedicalRecordValidationException(result);
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof DiagnosisDetails)) {
            throw new MedicalRecordValidationException("Diagnosis - instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");
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
}
