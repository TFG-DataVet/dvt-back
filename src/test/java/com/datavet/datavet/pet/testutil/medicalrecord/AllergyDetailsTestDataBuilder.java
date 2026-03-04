package com.datavet.datavet.pet.testutil.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergySeverity;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyType;

import java.time.LocalDate;
import java.util.List;

/**
 * Test data builder for AllergyDetails.
 * Default severity is MILD (lifeThreatening=false) to keep the happy path simple.
 * Use dedicated helpers to test the ANAPHYLAXIS / lifeThreatening branch.
 */
public class AllergyDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String        DEFAULT_ALLERGEN     = "Pollo";
    private static final AllergyType   DEFAULT_TYPE         = AllergyType.FOOD;
    private static final AllergySeverity DEFAULT_SEVERITY   = AllergySeverity.MILD;
    private static final List<String>  DEFAULT_REACTIONS    = List.of("Urticaria", "Picazón");
    private static final boolean       DEFAULT_LIFE_THREATENING = false;
    private static final LocalDate     DEFAULT_IDENTIFIED_AT = LocalDate.of(2023, 6, 15);
    private static final String        DEFAULT_NOTES        = "Alergia leve al pollo detectada en consulta.";

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Creates a valid AllergyDetails with all default values (MILD, no lifeThreatening). */
    public static AllergyDetails aValidAllergyDetails() {
        return AllergyDetails.create(
                DEFAULT_ALLERGEN,
                DEFAULT_TYPE,
                DEFAULT_SEVERITY,
                DEFAULT_REACTIONS,
                DEFAULT_LIFE_THREATENING,
                DEFAULT_IDENTIFIED_AT,
                DEFAULT_NOTES
        );
    }

    /** Creates a valid AllergyDetails with ANAPHYLAXIS severity and lifeThreatening=true. */
    public static AllergyDetails anAnaphylaxisAllergyDetails() {
        return AllergyDetails.create(
                DEFAULT_ALLERGEN,
                DEFAULT_TYPE,
                AllergySeverity.ANAPHYLAXIS,
                DEFAULT_REACTIONS,
                true,
                DEFAULT_IDENTIFIED_AT,
                DEFAULT_NOTES
        );
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static AllergyDetails anAllergyDetailsWithAllergen(String allergen) {
        return AllergyDetails.create(allergen, DEFAULT_TYPE, DEFAULT_SEVERITY,
                DEFAULT_REACTIONS, DEFAULT_LIFE_THREATENING, DEFAULT_IDENTIFIED_AT, DEFAULT_NOTES);
    }

    public static AllergyDetails anAllergyDetailsWithType(AllergyType type) {
        return AllergyDetails.create(DEFAULT_ALLERGEN, type, DEFAULT_SEVERITY,
                DEFAULT_REACTIONS, DEFAULT_LIFE_THREATENING, DEFAULT_IDENTIFIED_AT, DEFAULT_NOTES);
    }

    public static AllergyDetails anAllergyDetailsWithSeverity(AllergySeverity severity, boolean lifeThreatening) {
        return AllergyDetails.create(DEFAULT_ALLERGEN, DEFAULT_TYPE, severity,
                DEFAULT_REACTIONS, lifeThreatening, DEFAULT_IDENTIFIED_AT, DEFAULT_NOTES);
    }

    public static AllergyDetails anAllergyDetailsWithReactions(List<String> reactions) {
        return AllergyDetails.create(DEFAULT_ALLERGEN, DEFAULT_TYPE, DEFAULT_SEVERITY,
                reactions, DEFAULT_LIFE_THREATENING, DEFAULT_IDENTIFIED_AT, DEFAULT_NOTES);
    }

    public static AllergyDetails anAllergyDetailsWithIdentifiedAt(LocalDate identifiedAt) {
        return AllergyDetails.create(DEFAULT_ALLERGEN, DEFAULT_TYPE, DEFAULT_SEVERITY,
                DEFAULT_REACTIONS, DEFAULT_LIFE_THREATENING, identifiedAt, DEFAULT_NOTES);
    }
}