package com.datavet.datavet.pet.domain.model.details.allergy;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
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
public class AllergyDetails implements MedicalRecordDetails {

    private String allergen;
    private AllergyType type;
    private AllergySeverity severity;
    private List<String> reactions;
    private boolean lifeThreatening;
    private LocalDate identifiedAt;
    private String notes;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.ALLERGY;
    }

    @Override
    public void validate() {
        if (allergen == null || allergen.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sustancio o agente de la alergia no puede ser nulo.");
        }

        if (type == null) {
            throw new IllegalArgumentException("El tipo de alergía no puede ser nulo.");
        }

        if (severity == null) {
            throw new IllegalArgumentException("El nivel de gravedad de la reacción no puede ser nulo.");
        }

        if ((severity == AllergySeverity.ANAPHYLAXIS) && !lifeThreatening ) {
            throw new IllegalArgumentException("Si la gravedad de la reacción tiene riesgos de vida inmediato, debe " +
                    "de indicar que la condición alergica tiene peligro de riesgo de vida");
        }

        if (identifiedAt == null || identifiedAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la identificación no puede ser nula ni tener una" +
                    " fecha futura a hoy.");
        }

        if (reactions == null || reactions.isEmpty()) {
            throw new IllegalArgumentException("La alergia debe tener al menos una reacción registrada.");
        }

        for (String reaction : reactions) {
            if (reaction == null || reaction.isBlank()) {
                throw new IllegalArgumentException("Las reacciones no pueden ser nulas o estar vacías.");
            }
        }

    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        return false;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        return MedicalRecordDetails.super.applyAction(action);
    }

    public static AllergyDetails create(
            String allergen,
            AllergyType type,
            AllergySeverity severity,
            List<String> reactions,
            boolean lifeThreatening,
            LocalDate identifiedAt,
            String notes
    ) {
        AllergyDetails allergyDetails = AllergyDetails
                .builder()
                .allergen(allergen)
                .type(type)
                .severity(severity)
                .reactions(reactions)
                .lifeThreatening(lifeThreatening)
                .identifiedAt(identifiedAt)
                .notes(notes)
                .build();

        allergyDetails.validate();

        return allergyDetails;
    }
}
