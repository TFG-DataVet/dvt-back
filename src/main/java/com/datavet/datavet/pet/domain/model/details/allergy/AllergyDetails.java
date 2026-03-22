package com.datavet.datavet.pet.domain.model.details.allergy;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("allergy")
@Document
public class AllergyDetails implements MedicalRecordDetails {

    private String allergenName;
    private AllergyType allergyType;
    private AllergySeverity severity;
    private List<String> reactions;
    private boolean lifeThreatening;
    private LocalDate identifiedAt;
    private String notes;

    public MedicalRecordType getType() {
        return MedicalRecordType.ALLERGY;
    }

    @Override
    public void validate() {
        ValidationResult result = new ValidationResult();

        if (allergenName == null || allergenName.isBlank()) {
            result.addError("allergen - name","El nombre de la sustancio o agente de la alergia no puede ser nulo.");
        }

        if (allergyType == null) {
            result.addError("allergen - type","El tipo de alergía no puede ser nulo.");
        }

        if (severity == null) {
            result.addError("allergen - severity","El nivel de gravedad de la reacción no puede ser nulo.");
        }

        if ((severity == AllergySeverity.ANAPHYLAXIS) && !lifeThreatening ) {
            result.addError("allergen - severity anaphylaxis","Si la gravedad de la reacción tiene riesgos de vida inmediato, debe de indicar que la condición alergica tiene peligro de riesgo de vida");
        }

        if (identifiedAt == null || identifiedAt.isAfter(LocalDate.now())) {
            result.addError("allergen - identifiedAt","La fecha de la identificación no puede ser nula ni tener una fecha futura a hoy.");
        }

        if (reactions == null || reactions.isEmpty()) {
            result.addError("allergen - reactions","La alergia debe tener al menos una reacción registrada.");
        } else {
            for (String reaction : reactions) {
                if (reaction == null || reaction.isBlank()) {
                    result.addError("allergen - reaction","Las reacciones no pueden ser nulas o estar vacías.");
                }
            }
        }

        if (result.hasErrors()) {
            throw new MedicalRecordValidationException(result);
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof AllergyDetails)){
            throw new MedicalRecordValidationException("Allergen - instanceOf","No es posible corregir un registro con un tipo de detalle diferente.");
        }

        AllergyDetails prev = (AllergyDetails) previous;

        boolean allergenChanged = !Objects.equals(this.allergenName, prev.allergenName);
        boolean typeChanged = !Objects.equals(this.allergyType, prev.allergyType);
        boolean severityChanged = !Objects.equals(this.severity, prev.severity);
        boolean reactionsChanged = !Objects.equals(this.reactions, prev.reactions);
        boolean lifeThreateningChanged = this.lifeThreatening != prev.lifeThreatening;
        boolean identifierDateChanged = !Objects.equals(this.identifiedAt,prev.identifiedAt);

        return allergenChanged ||
                typeChanged ||
                severityChanged ||
                reactionsChanged ||
                lifeThreateningChanged ||
                identifierDateChanged;
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
        try {
            AllergyDetails allergyDetails = new AllergyDetails(
                    allergen,
                    type,
                    severity,
                    reactions,
                    lifeThreatening,
                    identifiedAt,
                    notes);

            allergyDetails.validate();

            return allergyDetails;
        } catch (MedicalRecordValidationException e) {
            throw e;
        }
    }
}
