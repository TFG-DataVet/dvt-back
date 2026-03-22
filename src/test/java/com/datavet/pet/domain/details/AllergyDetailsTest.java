package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.pet.domain.model.details.allergy.AllergySeverity;
import com.datavet.pet.domain.model.details.allergy.AllergyType;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.AllergyDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AllergyDetails Domain Model Tests")
class AllergyDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create AllergyDetails with all valid fields (MILD)")
    void create_shouldCreateAllergyDetailsWithValidData() {
        AllergyDetails details = AllergyDetailsTestDataBuilder.aValidAllergyDetails();

        assertNotNull(details);
        assertEquals("Pollo", details.getAllergenName());
        assertEquals(AllergyType.FOOD, details.getAllergyType());
        assertEquals(AllergySeverity.MILD, details.getSeverity());
        assertEquals(List.of("Urticaria", "Picazón"), details.getReactions());
        assertFalse(details.isLifeThreatening());
        assertEquals(LocalDate.of(2023, 6, 15), details.getIdentifiedAt());
        assertEquals("Alergia leve al pollo detectada en consulta.", details.getNotes());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.ALLERGY")
    void getType_shouldReturnAllergyType() {
        AllergyDetails details = AllergyDetailsTestDataBuilder.aValidAllergyDetails();

        assertEquals(MedicalRecordType.ALLERGY, details.getType());
    }

    @Test
    @DisplayName("Should create AllergyDetails with ANAPHYLAXIS severity and lifeThreatening=true")
    void create_shouldCreateAnaphylaxisAllergyDetailsWithLifeThreatening() {
        AllergyDetails details = AllergyDetailsTestDataBuilder.anAnaphylaxisAllergyDetails();

        assertEquals(AllergySeverity.ANAPHYLAXIS, details.getSeverity());
        assertTrue(details.isLifeThreatening());
    }

    @Test
    @DisplayName("Should allow all AllergyType variants")
    void create_shouldAllowAllAllergyTypeVariants() {
        for (AllergyType type : AllergyType.values()) {
            AllergyDetails details = AllergyDetailsTestDataBuilder.anAllergyDetailsWithType(type);
            assertEquals(type, details.getAllergyType());
        }
    }

    @Test
    @DisplayName("Should allow all non-ANAPHYLAXIS severities with lifeThreatening=false")
    void create_shouldAllowNonAnaphylaxisSeveritiesWithoutLifeThreatening() {
        for (AllergySeverity severity : AllergySeverity.values()) {
            if (severity == AllergySeverity.ANAPHYLAXIS) continue;

            AllergyDetails details = AllergyDetailsTestDataBuilder
                    .anAllergyDetailsWithSeverity(severity, false);

            assertEquals(severity, details.getSeverity());
            assertFalse(details.isLifeThreatening());
        }
    }

    @Test
    @DisplayName("Should allow notes to be null (optional field)")
    void create_shouldAllowNullNotes() {
        AllergyDetails details = AllergyDetails.create(
                "Pollo", AllergyType.FOOD, AllergySeverity.MILD,
                List.of("Urticaria"), false,
                LocalDate.of(2023, 6, 15), null);

        assertNotNull(details);
        assertNull(details.getNotes());
    }

    // ================================================================
    // validate() — allergenName
    // ================================================================

    @Test
    @DisplayName("Should throw when allergenName is null")
    void create_shouldFailWhenAllergenNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithAllergen(null));

        assertTrue(ex.getMessage().contains("allergen - name"));
    }

    @Test
    @DisplayName("Should throw when allergenName is blank")
    void create_shouldFailWhenAllergenNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithAllergen("   "));

        assertTrue(ex.getMessage().contains("allergen - name"));
    }

    // ================================================================
    // validate() — type
    // ================================================================

    @Test
    @DisplayName("Should throw when allergyType is null")
    void create_shouldFailWhenAllergyTypeIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetails.create("Pollo", null, AllergySeverity.MILD,
                        List.of("Urticaria"), false,
                        LocalDate.of(2023, 6, 15), null));

        assertTrue(ex.getMessage().contains("allergen - type"));
    }

    // ================================================================
    // validate() — severity
    // ================================================================

    @Test
    @DisplayName("Should throw when severity is null")
    void create_shouldFailWhenSeverityIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetails.create("Pollo", AllergyType.FOOD, null,
                        List.of("Urticaria"), false,
                        LocalDate.of(2023, 6, 15), null));

        assertTrue(ex.getMessage().contains("allergen - severity"));
    }

    @Test
    @DisplayName("Should throw when severity is ANAPHYLAXIS but lifeThreatening=false")
    void create_shouldFailWhenAnaphylaxisWithoutLifeThreatening() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithSeverity(
                        AllergySeverity.ANAPHYLAXIS, false));

        assertTrue(ex.getMessage().contains("allergen - severity anaphylaxis"));
    }

    // ================================================================
    // validate() — identifiedAt
    // ================================================================

    @Test
    @DisplayName("Should throw when identifiedAt is null")
    void create_shouldFailWhenIdentifiedAtIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithIdentifiedAt(null));

        assertTrue(ex.getMessage().contains("allergen - identifiedAt"));
    }

    @Test
    @DisplayName("Should throw when identifiedAt is in the future")
    void create_shouldFailWhenIdentifiedAtIsInFuture() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithIdentifiedAt(
                        LocalDate.now().plusDays(1)));

        assertTrue(ex.getMessage().contains("allergen - identifiedAt"));
    }

    @Test
    @DisplayName("Should allow identifiedAt = today")
    void create_shouldAllowIdentifiedAtToday() {
        AllergyDetails details = AllergyDetailsTestDataBuilder
                .anAllergyDetailsWithIdentifiedAt(LocalDate.now());

        assertNotNull(details);
        assertEquals(LocalDate.now(), details.getIdentifiedAt());
    }

    // ================================================================
    // validate() — reactions
    // ================================================================

    @Test
    @DisplayName("Should throw when reactions list is null")
    void create_shouldFailWhenReactionsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithReactions(null));

        assertTrue(ex.getMessage().contains("allergen - reactions"));
    }

    @Test
    @DisplayName("Should throw when reactions list is empty")
    void create_shouldFailWhenReactionsIsEmpty() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithReactions(List.of()));

        assertTrue(ex.getMessage().contains("allergen - reactions"));
    }

    @Test
    @DisplayName("Should throw when any reaction in the list is blank")
    void create_shouldFailWhenAnyReactionIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithReactions(
                        List.of("Urticaria", "  ")));

        assertTrue(ex.getMessage().contains("allergen - reaction"));
    }

    @Test
    @DisplayName("Should throw when any reaction in the list is null")
    void create_shouldFailWhenAnyReactionIsNull() {
        List<String> reactionsWithNull = new java.util.ArrayList<>();
        reactionsWithNull.add("Urticaria");
        reactionsWithNull.add(null);

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetailsTestDataBuilder.anAllergyDetailsWithReactions(reactionsWithNull));

        assertTrue(ex.getMessage().contains("allergen - reaction"));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> AllergyDetails.create(
                        null,       // allergenName null
                        null,       // type null
                        null,       // severity null
                        List.of(),  // reactions empty
                        false,
                        null,       // identifiedAt null
                        null));

        String msg = ex.getMessage();
        assertTrue(msg.contains("allergen - name"));
        assertTrue(msg.contains("allergen - type"));
        assertTrue(msg.contains("allergen - severity"));
        assertTrue(msg.contains("allergen - reactions"));
        assertTrue(msg.contains("allergen - identifiedAt"));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return true when at least one field differs")
    void canCorrect_shouldReturnTrueWhenAllergenChanges() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        AllergyDetails correction = AllergyDetailsTestDataBuilder
                .anAllergyDetailsWithAllergen("Atún");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when severity changes")
    void canCorrect_shouldReturnTrueWhenSeverityChanges() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        AllergyDetails correction = AllergyDetailsTestDataBuilder
                .anAllergyDetailsWithSeverity(AllergySeverity.MODERATE, false);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when reactions list changes")
    void canCorrect_shouldReturnTrueWhenReactionsChange() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        AllergyDetails correction = AllergyDetailsTestDataBuilder
                .anAllergyDetailsWithReactions(List.of("Vómito"));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when lifeThreatening changes")
    void canCorrect_shouldReturnTrueWhenLifeThreateningChanges() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails(); // false
        AllergyDetails correction = AllergyDetailsTestDataBuilder
                .anAnaphylaxisAllergyDetails(); // true (+ severity=ANAPHYLAXIS)

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when identifiedAt changes")
    void canCorrect_shouldReturnTrueWhenIdentifiedAtChanges() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        AllergyDetails correction = AllergyDetailsTestDataBuilder
                .anAllergyDetailsWithIdentifiedAt(LocalDate.of(2022, 1, 1));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when no relevant field differs")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        AllergyDetails original = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        AllergyDetails identical = AllergyDetailsTestDataBuilder.aValidAllergyDetails();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not an AllergyDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        AllergyDetails correction = AllergyDetailsTestDataBuilder.aValidAllergyDetails();
        WeightDetails wrongType = WeightDetails.create(10.0, WeightUnit.KG);

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));

        assertTrue(ex.getMessage().contains("Allergen - instanceOf"));
    }
}