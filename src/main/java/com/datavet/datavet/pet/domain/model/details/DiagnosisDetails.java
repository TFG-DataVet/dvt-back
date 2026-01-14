package com.datavet.datavet.pet.domain.model.details;

import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisCategory;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisSeverity;
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
public class DiagnosisDetails implements MedicalRecordDetails{

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

        if (recommendations == null) {
            for (String recommendation : recommendations) {
                if (recommendation == null || recommendation.isBlank()) {
                    throw new IllegalArgumentException("La recomendación no puede ser nula o estar vacia.");
                }
            }
        }
    }

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

        DiagnosisDetails diagnosisDetails = DiagnosisDetails.builder()
                .diagnosisName(diagnosisName)
                .category(category)
                .description(description)
                .severity(severity)
                .diagnosedAt(diagnosedAt)
                .chronic(chronic)
                .contagious(contagious)
                .symptoms(symptoms)
                .recommendations(recommendations)
                .followUpRequired(followUpRequired)
                .followUpDate(followUpDate)
                .build();

        diagnosisDetails.validate();

        return diagnosisDetails;
    }

}
