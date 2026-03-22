package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.details.diagnosis.DiagnosisCategory;
import com.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.pet.domain.model.details.diagnosis.DiagnosisSeverity;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.DiagnosisDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiagnosisDetails Domain Model Tests")
class DiagnosisDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create DiagnosisDetails with all valid fields (no follow-up)")
    void create_shouldCreateDiagnosisWithValidData() {
        DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();

        assertNotNull(details);
        assertEquals("Gastritis aguda", details.getDiagnosisName());
        assertEquals(DiagnosisCategory.DIGESTIVE, details.getCategory());
        assertEquals("Inflamación del revestimiento del estómago", details.getDescription());
        assertEquals(DiagnosisSeverity.MILD, details.getSeverity());
        assertEquals(LocalDate.now(), details.getDiagnosedAt());
        assertFalse(details.isChronic());
        assertFalse(details.isContagious());
        assertEquals(List.of("Vómito", "Inapetencia"), details.getSymptoms());
        assertEquals(List.of("Dieta blanda 5 días"), details.getRecommendations());
        assertFalse(details.isFollowUpRequired());
        assertNull(details.getFollowUpDate());
    }

    @Test
    @DisplayName("Should create DiagnosisDetails with follow-up required and a future date")
    void create_shouldCreateDiagnosisWithFollowUp() {
        DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aValidDiagnosisWithFollowUp();

        assertTrue(details.isFollowUpRequired());
        assertNotNull(details.getFollowUpDate());
        assertTrue(details.getFollowUpDate().isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Should return MedicalRecordType.DIAGNOSIS")
    void getType_shouldReturnDiagnosisType() {
        DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();

        assertEquals(MedicalRecordType.DIAGNOSIS, details.getType());
    }

    @Test
    @DisplayName("Should allow all DiagnosisCategory variants")
    void create_shouldAllowAllCategoryVariants() {
        for (DiagnosisCategory category : DiagnosisCategory.values()) {
            DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aDiagnosisWithCategory(category);
            assertEquals(category, details.getCategory());
        }
    }

    @Test
    @DisplayName("Should allow all DiagnosisSeverity variants")
    void create_shouldAllowAllSeverityVariants() {
        for (DiagnosisSeverity severity : DiagnosisSeverity.values()) {
            DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aDiagnosisWithSeverity(severity);
            assertEquals(severity, details.getSeverity());
        }
    }

    @Test
    @DisplayName("Should allow chronic=true and contagious=true independently")
    void create_shouldAllowChronicAndContagiousFlags() {
        DiagnosisDetails chronicOnly = DiagnosisDetailsTestDataBuilder.aDiagnosisWithFlags(true, false);
        assertTrue(chronicOnly.isChronic());
        assertFalse(chronicOnly.isContagious());

        DiagnosisDetails contagiousOnly = DiagnosisDetailsTestDataBuilder.aDiagnosisWithFlags(false, true);
        assertFalse(contagiousOnly.isChronic());
        assertTrue(contagiousOnly.isContagious());

        DiagnosisDetails both = DiagnosisDetailsTestDataBuilder.aDiagnosisWithFlags(true, true);
        assertTrue(both.isChronic());
        assertTrue(both.isContagious());
    }

    @Test
    @DisplayName("Should allow description to be null (optional field)")
    void create_shouldAllowNullDescription() {
        DiagnosisDetails details = DiagnosisDetails.create(
                "Gastritis", DiagnosisCategory.DIGESTIVE, null,
                DiagnosisSeverity.MILD, LocalDate.now(), false, false,
                List.of("Vómito"), List.of("Dieta blanda"), false, null);

        assertNotNull(details);
        assertNull(details.getDescription());
    }

    @Test
    @DisplayName("Should allow recommendations to contain multiple entries")
    void create_shouldAllowMultipleRecommendations() {
        List<String> recs = List.of("Dieta blanda", "Reposo", "Control en 7 días");
        DiagnosisDetails details = DiagnosisDetailsTestDataBuilder.aDiagnosisWithRecommendations(recs);

        assertEquals(3, details.getRecommendations().size());
    }

    @Test
    @DisplayName("Should allow followUpDate equal to diagnosedAt (same day follow-up)")
    void create_shouldAllowFollowUpDateEqualToDiagnosedAt() {
        // diagnosedAt=today, followUpDate=today → today is NOT before today → valid
        assertDoesNotThrow(() ->
                DiagnosisDetailsTestDataBuilder.aDiagnosisWithFollowUp(true, LocalDate.now()));
    }

    // ================================================================
    // validate() — diagnosisName
    // ================================================================

    @Test
    @DisplayName("Should throw when diagnosisName is null")
    void create_shouldFailWhenDiagnosisNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithName(null));

        assertTrue(ex.getMessage().contains("Diagnosis - name"));
    }

    @Test
    @DisplayName("Should throw when diagnosisName is blank")
    void create_shouldFailWhenDiagnosisNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithName("   "));

        assertTrue(ex.getMessage().contains("Diagnosis - name"));
    }

    // ================================================================
    // validate() — category
    // ================================================================

    @Test
    @DisplayName("Should throw when category is null")
    void create_shouldFailWhenCategoryIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithCategory(null));

        assertTrue(ex.getMessage().contains("Diagnosis - category"));
    }

    // ================================================================
    // validate() — severity
    // ================================================================

    @Test
    @DisplayName("Should throw when severity is null")
    void create_shouldFailWhenSeverityIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithSeverity(null));

        assertTrue(ex.getMessage().contains("Diagnosis - severity"));
    }

    // ================================================================
    // validate() — diagnosedAt
    // ================================================================

    @Test
    @DisplayName("Should throw when diagnosedAt is null")
    void create_shouldFailWhenDiagnosedAtIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithDiagnosedAt(null));

        assertTrue(ex.getMessage().contains("Diagnosis - DiagnosedAt"));
    }

    @Test
    @DisplayName("Should throw when diagnosedAt is in the future")
    void create_shouldFailWhenDiagnosedAtIsInFuture() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithDiagnosedAt(
                        LocalDate.now().plusDays(1)));

        assertTrue(ex.getMessage().contains("Diagnosis - DiagnosedAt"));
    }

    @Test
    @DisplayName("Should allow diagnosedAt = today")
    void create_shouldAllowDiagnosedAtToday() {
        assertDoesNotThrow(() ->
                DiagnosisDetailsTestDataBuilder.aDiagnosisWithDiagnosedAt(LocalDate.now()));
    }

    // ================================================================
    // validate() — symptoms
    // ================================================================

    @Test
    @DisplayName("Should throw when symptoms list is null")
    void create_shouldFailWhenSymptomsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithSymptoms(null));

        assertTrue(ex.getMessage().contains("Diagnosis - symptoms"));
    }

    @Test
    @DisplayName("Should throw when symptoms list is empty")
    void create_shouldFailWhenSymptomsIsEmpty() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithSymptoms(List.of()));

        assertTrue(ex.getMessage().contains("Diagnosis - symptoms"));
    }

    // ================================================================
    // validate() — recommendations
    // ================================================================

    @Test
    @DisplayName("Should throw when recommendations list is null")
    void create_shouldFailWhenRecommendationsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithRecommendations(null));

        assertTrue(ex.getMessage().contains("Diagnosis - recommendations"));
    }

    @Test
    @DisplayName("Should throw when recommendations list is empty")
    void create_shouldFailWhenRecommendationsIsEmpty() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithRecommendations(List.of()));

        assertTrue(ex.getMessage().contains("Diagnosis - recommendations"));
    }

    // ================================================================
    // validate() — followUp business rules (4 combinations)
    // ================================================================

    @Test
    @DisplayName("Should throw when followUpRequired=true but followUpDate=null")
    void create_shouldFailWhenFollowUpRequiredWithoutDate() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithFollowUp(true, null));

        assertTrue(ex.getMessage().contains("Diagnosis - followUpRequired FollowUpDate"));
    }

    @Test
    @DisplayName("Should throw when followUpRequired=false but followUpDate is set")
    void create_shouldFailWhenNoFollowUpButDateIsSet() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetailsTestDataBuilder.aDiagnosisWithFollowUp(
                        false, LocalDate.now().plusDays(5)));

        assertTrue(ex.getMessage().contains("Diagnosis - followUpRequired"));
    }

    @Test
    @DisplayName("Should throw when followUpDate is before diagnosedAt")
    void create_shouldFailWhenFollowUpDateIsBeforeDiagnosedAt() {
        // diagnosedAt = today, followUpDate = yesterday → invalid
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetails.create(
                        "Gastritis aguda", DiagnosisCategory.DIGESTIVE,
                        "Inflamación del revestimiento del estómago",
                        DiagnosisSeverity.MILD,
                        LocalDate.now(),
                        false, false,
                        List.of("Vómito"), List.of("Dieta blanda"),
                        true,
                        LocalDate.now().minusDays(1)  // before diagnosedAt
                ));

        assertTrue(ex.getMessage().contains("Diagnosis - followUpRequired - followUpRequiered"));
    }

    @Test
    @DisplayName("Valid: followUpRequired=false and followUpDate=null should not throw")
    void create_shouldNotThrowWhenNoFollowUpAndNoDate() {
        assertDoesNotThrow(() ->
                DiagnosisDetailsTestDataBuilder.aDiagnosisWithFollowUp(false, null));
    }

    @Test
    @DisplayName("Valid: followUpRequired=true and followUpDate after diagnosedAt should not throw")
    void create_shouldNotThrowWhenFollowUpRequiredWithValidDate() {
        assertDoesNotThrow(() ->
                DiagnosisDetailsTestDataBuilder.aDiagnosisWithFollowUp(
                        true, LocalDate.now().plusDays(7)));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DiagnosisDetails.create(
                        null,                        // name null
                        null,                        // category null
                        null,
                        null,                        // severity null
                        null,                        // diagnosedAt null
                        false, false,
                        List.of(),                   // symptoms empty
                        List.of(),                   // recommendations empty
                        true,                        // followUpRequired=true
                        null                         // but no followUpDate
                ));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Diagnosis - name"));
        assertTrue(msg.contains("Diagnosis - category"));
        assertTrue(msg.contains("Diagnosis - severity"));
        assertTrue(msg.contains("Diagnosis - DiagnosedAt"));
        assertTrue(msg.contains("Diagnosis - symptoms"));
        assertTrue(msg.contains("Diagnosis - recommendations"));
        assertTrue(msg.contains("Diagnosis - followUpRequired FollowUpDate"));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return false when no relevant field differs")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        DiagnosisDetails original  = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails identical = DiagnosisDetailsTestDataBuilder.anIdenticalDiagnosis();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when diagnosisName changes")
    void canCorrect_shouldReturnTrueWhenNameChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder.aDiagnosisWithName("Pancreatitis");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when category changes")
    void canCorrect_shouldReturnTrueWhenCategoryChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithCategory(DiagnosisCategory.INFECTIOUS);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when description changes")
    void canCorrect_shouldReturnTrueWhenDescriptionChanges() {
        DiagnosisDetails original = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetails.create(
                "Gastritis aguda", DiagnosisCategory.DIGESTIVE,
                "Descripción actualizada",   // changed
                DiagnosisSeverity.MILD, LocalDate.now(), false, false,
                List.of("Vómito", "Inapetencia"), List.of("Dieta blanda 5 días"),
                false, null);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when severity changes")
    void canCorrect_shouldReturnTrueWhenSeverityChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithSeverity(DiagnosisSeverity.SEVERE);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when diagnosedAt changes")
    void canCorrect_shouldReturnTrueWhenDiagnosedAtChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithDiagnosedAt(LocalDate.now().minusDays(5));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when chronic flag changes")
    void canCorrect_shouldReturnTrueWhenChronicChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis(); // chronic=false
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder.aDiagnosisWithFlags(true, false);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when contagious flag changes")
    void canCorrect_shouldReturnTrueWhenContagiousChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis(); // contagious=false
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder.aDiagnosisWithFlags(false, true);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when symptoms change")
    void canCorrect_shouldReturnTrueWhenSymptomsChange() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithSymptoms(List.of("Diarrea", "Dolor abdominal"));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when recommendations change")
    void canCorrect_shouldReturnTrueWhenRecommendationsChange() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithRecommendations(List.of("Antibiótico 7 días", "Control semanal"));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when followUpRequired changes")
    void canCorrect_shouldReturnTrueWhenFollowUpRequiredChanges() {
        DiagnosisDetails original   = DiagnosisDetailsTestDataBuilder.aValidDiagnosis(); // followUp=false
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder
                .aDiagnosisWithFollowUp(true, LocalDate.now().plusDays(10));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not a DiagnosisDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        DiagnosisDetails correction = DiagnosisDetailsTestDataBuilder.aValidDiagnosis();
        WeightDetails wrongType = WeightDetails.create(10.0, WeightUnit.KG);

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));

        assertTrue(ex.getMessage().contains("Diagnosis - instanceOf"));
    }
}