package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.ConsultationDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationDetails Domain Model Tests")
class ConsultationDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create ConsultationDetails without follow-up (simplest valid case)")
    void create_shouldCreateConsultationWithoutFollowUp() {
        ConsultationDetails details = ConsultationDetailsTestDataBuilder.aValidConsultation();

        assertNotNull(details);
        assertEquals("Control anual de rutina", details.getReason());
        assertEquals(List.of("Letargo", "Pérdida de apetito"), details.getSymptoms());
        assertEquals("Sin hallazgos relevantes", details.getClinicalFindings());
        assertEquals("Animal sano", details.getDiagnosis());
        assertEquals("Ninguno por el momento", details.getTreatmentPlan());
        assertFalse(details.isFollowUpRequired());
        assertNull(details.getFollowUpDate());
    }

    @Test
    @DisplayName("Should create ConsultationDetails with follow-up required and future date")
    void create_shouldCreateConsultationWithFollowUp() {
        LocalDate followUpDate = LocalDate.now().plusDays(10);
        ConsultationDetails details = ConsultationDetailsTestDataBuilder
                .aValidConsultationWithFollowUp();

        assertNotNull(details);
        assertTrue(details.isFollowUpRequired());
        assertNotNull(details.getFollowUpDate());
        assertTrue(details.getFollowUpDate().isAfter(LocalDate.now()) ||
                details.getFollowUpDate().isEqual(LocalDate.now()));
    }

    @Test
    @DisplayName("Should return MedicalRecordType.CONSULTATION")
    void getType_shouldReturnConsultationType() {
        ConsultationDetails details = ConsultationDetailsTestDataBuilder.aValidConsultation();

        assertEquals(MedicalRecordType.CONSULTATION, details.getType());
    }

    @Test
    @DisplayName("Should allow clinicalFindings to be null (optional field)")
    void create_shouldAllowNullClinicalFindings() {
        ConsultationDetails details = ConsultationDetails.create(
                "Control", List.of("Fiebre"), null, "Diagnóstico", "Plan", false, null);

        assertNotNull(details);
        assertNull(details.getClinicalFindings());
    }

    @Test
    @DisplayName("Should allow diagnosis to be null (optional field)")
    void create_shouldAllowNullDiagnosis() {
        ConsultationDetails details = ConsultationDetails.create(
                "Control", List.of("Fiebre"), "Hallazgos", null, "Plan", false, null);

        assertNotNull(details);
        assertNull(details.getDiagnosis());
    }

    @Test
    @DisplayName("Should allow treatmentPlan to be null (optional field)")
    void create_shouldAllowNullTreatmentPlan() {
        ConsultationDetails details = ConsultationDetails.create(
                "Control", List.of("Fiebre"), "Hallazgos", "Diagnóstico", null, false, null);

        assertNotNull(details);
        assertNull(details.getTreatmentPlan());
    }

    @Test
    @DisplayName("Should allow followUpDate = today when followUpRequired=true")
    void create_shouldAllowFollowUpDateToday() {
        ConsultationDetails details = ConsultationDetailsTestDataBuilder
                .aConsultationWithFollowUp(true, LocalDate.now());

        assertNotNull(details);
        assertTrue(details.isFollowUpRequired());
        assertEquals(LocalDate.now(), details.getFollowUpDate());
    }

    // ================================================================
    // validate() — reason
    // ================================================================

    @Test
    @DisplayName("Should throw when reason is null")
    void create_shouldFailWhenReasonIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithReason(null));

        assertTrue(ex.getMessage().contains("Consultation - reason"));
    }

    @Test
    @DisplayName("Should throw when reason is blank")
    void create_shouldFailWhenReasonIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithReason("   "));

        assertTrue(ex.getMessage().contains("Consultation - reason"));
    }

    // ================================================================
    // validate() — symptoms
    // ================================================================

    @Test
    @DisplayName("Should throw when symptoms list is null")
    void create_shouldFailWhenSymptomsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithSymptoms(null));

        assertTrue(ex.getMessage().contains("Consultation - symptoms"));
    }

    @Test
    @DisplayName("Should throw when symptoms list is empty")
    void create_shouldFailWhenSymptomsIsEmpty() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithSymptoms(List.of()));

        assertTrue(ex.getMessage().contains("Consultation - symptoms"));
    }

    // ================================================================
    // validate() — followUp business rules (4 combinations)
    // ================================================================

    @Test
    @DisplayName("Should throw when followUpRequired=true but followUpDate=null")
    void create_shouldFailWhenFollowUpRequiredWithoutDate() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithFollowUp(true, null));

        assertTrue(ex.getMessage().contains("Consultation - RequiereSeguimiento sin fecha"));
    }

    @Test
    @DisplayName("Should throw when followUpRequired=false but followUpDate is set")
    void create_shouldFailWhenFollowUpNotRequiredButDateIsSet() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithFollowUp(
                        false, LocalDate.now().plusDays(5)));

        assertTrue(ex.getMessage().contains("Consultation - No requiereSeguimiento y tiene fecha"));
    }

    @Test
    @DisplayName("Should throw when followUpDate is in the past")
    void create_shouldFailWhenFollowUpDateIsInThePast() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetailsTestDataBuilder.aConsultationWithFollowUp(
                        true, LocalDate.now().minusDays(1)));

        assertTrue(ex.getMessage().contains("Consultation - FollowUpDate"));
    }

    @Test
    @DisplayName("Valid: followUpRequired=false and followUpDate=null should not throw")
    void create_shouldNotThrowWhenNoFollowUpAndNoDate() {
        assertDoesNotThrow(() ->
                ConsultationDetailsTestDataBuilder.aConsultationWithFollowUp(false, null));
    }

    @Test
    @DisplayName("Valid: followUpRequired=true and followUpDate in the future should not throw")
    void create_shouldNotThrowWhenFollowUpRequiredWithFutureDate() {
        assertDoesNotThrow(() ->
                ConsultationDetailsTestDataBuilder.aConsultationWithFollowUp(
                        true, LocalDate.now().plusDays(7)));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> ConsultationDetails.create(
                        null,        // reason null
                        List.of(),   // symptoms empty
                        null,
                        null,
                        null,
                        true,        // followUpRequired=true
                        null         // but no followUpDate → error
                ));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Consultation - reason"));
        assertTrue(msg.contains("Consultation - RequiereSeguimiento sin fecha"));
        assertTrue(msg.contains("Consultation - symptoms"));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return true when reason changes")
    void canCorrect_shouldReturnTrueWhenReasonChanges() {
        ConsultationDetails original = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails correction = ConsultationDetailsTestDataBuilder
                .aConsultationWithReason("Nueva razón de consulta");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when symptoms change")
    void canCorrect_shouldReturnTrueWhenSymptomsChange() {
        ConsultationDetails original = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails correction = ConsultationDetailsTestDataBuilder
                .aConsultationWithSymptoms(List.of("Vómito", "Diarrea"));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when clinicalFindings changes")
    void canCorrect_shouldReturnTrueWhenClinicalFindingsChange() {
        ConsultationDetails original = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails correction = ConsultationDetails.create(
                "Control anual de rutina",
                List.of("Letargo", "Pérdida de apetito"),
                "Hallazgo nuevo importante",   // changed
                "Animal sano",
                "Ninguno por el momento",
                false, null);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when diagnosis changes")
    void canCorrect_shouldReturnTrueWhenDiagnosisChanges() {
        ConsultationDetails original = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails correction = ConsultationDetails.create(
                "Control anual de rutina",
                List.of("Letargo", "Pérdida de apetito"),
                "Sin hallazgos relevantes",
                "Gastritis leve",              // changed
                "Ninguno por el momento",
                false, null);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when treatmentPlan changes")
    void canCorrect_shouldReturnTrueWhenTreatmentPlanChanges() {
        ConsultationDetails original = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails correction = ConsultationDetails.create(
                "Control anual de rutina",
                List.of("Letargo", "Pérdida de apetito"),
                "Sin hallazgos relevantes",
                "Animal sano",
                "Dieta blanda por 5 días",    // changed
                false, null);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when no relevant field differs")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        ConsultationDetails original  = ConsultationDetailsTestDataBuilder.aValidConsultation();
        ConsultationDetails identical = ConsultationDetailsTestDataBuilder.anIdenticalConsultation();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not a ConsultationDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        ConsultationDetails correction = ConsultationDetailsTestDataBuilder.aValidConsultation();
        WeightDetails wrongType = WeightDetails.create(10.0, WeightUnit.KG);

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));

        assertTrue(ex.getMessage().contains("Consultation - instanceOf"));
    }
}