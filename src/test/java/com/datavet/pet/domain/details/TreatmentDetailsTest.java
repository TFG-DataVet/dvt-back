package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.pet.domain.model.details.treatment.TreatmentMedication;
import com.datavet.pet.domain.model.details.treatment.TreatmentStatus;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.TreatmentDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TreatmentDetails Domain Model Tests")
class TreatmentDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create TreatmentDetails in PLANNED state with all valid fields")
    void create_shouldCreateTreatmentInPlannedState() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        assertNotNull(details);
        assertEquals("Tratamiento antibiótico", details.getTreatmentName());
        assertEquals(LocalDate.now().minusDays(1), details.getStartDate());
        assertEquals("Administrar con comida, cada 12 horas", details.getInstructions());
        assertEquals(LocalDate.now().plusDays(14), details.getEstimatedEndDate());
        assertFalse(details.getMedications().isEmpty());
        assertEquals(TreatmentStatus.PLANNED, details.getStatus());
        assertFalse(details.isFollowUpRequired());
        assertNull(details.getFollowUpDate());
        assertNull(details.getCompletedAt(), "completedAt must be null on creation");
    }

    @Test
    @DisplayName("Should create TreatmentDetails WITH follow-up required and valid date")
    void create_shouldCreateTreatmentWithFollowUp() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatmentWithFollowUp();

        assertTrue(details.isFollowUpRequired());
        assertNotNull(details.getFollowUpDate());
        assertTrue(details.getFollowUpDate().isAfter(details.getEstimatedEndDate()));
    }

    @Test
    @DisplayName("Should return MedicalRecordType.TREATMENT")
    void getType_shouldReturnTreatmentType() {
        assertEquals(MedicalRecordType.TREATMENT,
                TreatmentDetailsTestDataBuilder.aValidPlannedTreatment().getType());
    }

    @Test
    @DisplayName("Should throw when estimatedEndDate is null ")
    void create_shouldAllowNullEstimatedEndDate() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithEndDate(null));

        assertTrue(ex.getMessage().contains("Treatment - estimatedEndDate"));
    }

    @Test
    @DisplayName("Should allow estimatedEndDate equal to startDate (same-day treatment)")
    void create_shouldAllowEstimatedEndDateEqualToStartDate() {
        assertDoesNotThrow(() ->
                TreatmentDetailsTestDataBuilder.aTreatmentWithEndDate(LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("Should allow multiple medications")
    void create_shouldAllowMultipleMedications() {
        List<TreatmentMedication> meds = List.of(
                TreatmentDetailsTestDataBuilder.aValidTreatmentMedication(),
                TreatmentDetailsTestDataBuilder.aTreatmentMedicationWithName("Ibuprofeno")
        );
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aTreatmentWithMedications(meds);

        assertEquals(2, details.getMedications().size());
    }

    // ================================================================
    // validate() — treatmentName
    // ================================================================

    @Test
    @DisplayName("Should throw when treatmentName is null")
    void create_shouldFailWhenTreatmentNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithName(null));

        assertTrue(ex.getMessage().contains("Treatment - name"));
    }

    @Test
    @DisplayName("Should throw when treatmentName is blank")
    void create_shouldFailWhenTreatmentNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithName("   "));

        assertTrue(ex.getMessage().contains("Treatment - name"));
    }

    // ================================================================
    // validate() — startDate
    // ================================================================

    @Test
    @DisplayName("Should throw when startDate is null")
    void create_shouldFailWhenStartDateIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithStartDate(null));

        assertTrue(ex.getMessage().contains("Treatment - startDate"));
    }

    @Test
    @DisplayName("Should throw when startDate is in the future")
    void create_shouldFailWhenStartDateIsInFuture() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithStartDate(
                        LocalDate.now().plusDays(1)));

        assertTrue(ex.getMessage().contains("Treatment - startDate"));
    }

    @Test
    @DisplayName("Should allow startDate = today")
    void create_shouldAllowStartDateToday() {
        assertDoesNotThrow(() ->
                TreatmentDetailsTestDataBuilder.aTreatmentWithStartDate(LocalDate.now()));
    }

    // ================================================================
    // validate() — estimatedEndDate
    // ================================================================

    @Test
    @DisplayName("Should throw when estimatedEndDate is before startDate")
    void create_shouldFailWhenEstimatedEndDateIsBeforeStartDate() {
        // startDate = yesterday, endDate = 2 days ago → invalid
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetails.create(
                        "Tratamiento", LocalDate.now().minusDays(1),
                        "Instrucciones",
                        LocalDate.now().minusDays(2),  // before startDate
                        List.of(TreatmentDetailsTestDataBuilder.aValidTreatmentMedication()),
                        false, null));

        assertTrue(ex.getMessage().contains("Treatment - estimatedEndDate"));
    }

    // ================================================================
    // validate() — instructions
    // ================================================================

    @Test
    @DisplayName("Should throw when instructions is null")
    void create_shouldFailWhenInstructionsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithInstructions(null));

        assertTrue(ex.getMessage().contains("Treatment - instructions"));
    }

    @Test
    @DisplayName("Should throw when instructions is blank")
    void create_shouldFailWhenInstructionsIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithInstructions("   "));

        assertTrue(ex.getMessage().contains("Treatment - instructions"));
    }

    // ================================================================
    // validate() — medications
    // ================================================================

    @Test
    @DisplayName("Should throw when medications list is empty")
    void create_shouldFailWhenMedicationsIsEmpty() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithMedications(List.of()));

        assertTrue(ex.getMessage().contains("Treatment - medication.isEmpty"));
    }

    // ================================================================
    // validate() — followUp business rules (4 combinations)
    // ================================================================

    @Test
    @DisplayName("Should throw when followUpRequired=true but followUpDate=null")
    void create_shouldFailWhenFollowUpRequiredWithoutDate() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithFollowUp(true, null));

        assertTrue(ex.getMessage().contains("Treatment - followUpRequired & followUpDate"));
    }

    @Test
    @DisplayName("Should throw when followUpRequired=false but followUpDate is set")
    void create_shouldFailWhenNoFollowUpButDateIsSet() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetailsTestDataBuilder.aTreatmentWithFollowUp(
                        false, LocalDate.now().plusDays(20)));

        assertTrue(ex.getMessage().contains("Treatment - followUpRequired & followUpDate"));
    }

    @Test
    @DisplayName("Should throw when followUpDate is before estimatedEndDate")
    void create_shouldFailWhenFollowUpDateIsBeforeEstimatedEndDate() {
        // estimatedEndDate = now+14, followUpDate = now+5 → before endDate → invalid
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetails.create(
                        "Tratamiento", LocalDate.now().minusDays(1),
                        "Instrucciones",
                        LocalDate.now().plusDays(14),
                        List.of(TreatmentDetailsTestDataBuilder.aValidTreatmentMedication()),
                        true,
                        LocalDate.now().plusDays(5)  // before estimatedEndDate
                ));

        assertTrue(ex.getMessage().contains("Treatment - followUpRequired & followUpDate.isBefore"));
    }

    @Test
    @DisplayName("Valid: followUpRequired=false and followUpDate=null should not throw")
    void create_shouldNotThrowWhenNoFollowUpAndNoDate() {
        assertDoesNotThrow(() ->
                TreatmentDetailsTestDataBuilder.aTreatmentWithFollowUp(false, null));
    }

    @Test
    @DisplayName("Valid: followUpRequired=true with followUpDate after estimatedEndDate should not throw")
    void create_shouldNotThrowWhenFollowUpAfterEndDate() {
        assertDoesNotThrow(() ->
                TreatmentDetailsTestDataBuilder.aTreatmentWithFollowUp(
                        true, LocalDate.now().plusDays(20)));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentDetails.create(
                        null,               // name null
                        null,               // startDate null
                        null,               // instructions null
                        null,
                        List.of(),          // medications empty
                        true,               // followUpRequired=true
                        null                // but no followUpDate
                ));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Treatment - name"));
        assertTrue(msg.contains("Treatment - startDate"));
        assertTrue(msg.contains("Treatment - instructions"));
        assertTrue(msg.contains("Treatment - medication.isEmpty"));
        assertTrue(msg.contains("Treatment - followUpRequired & followUpDate"));
    }

    // ================================================================
    // applyAction() — state machine: PLANNED
    // ================================================================

    @Test
    @DisplayName("PLANNED → ACTIVATE → should transition to ACTIVE")
    void applyAction_plannedToActive() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        StatusChangeResult result = details.applyAction(RecordAction.ACTIVATE);

        assertEquals(TreatmentStatus.PLANNED.toString(), result.getPreviousStatus());
        assertEquals(TreatmentStatus.ACTIVE.toString(),  result.getNewStatus());
        assertEquals(TreatmentStatus.ACTIVE,  details.getStatus());
        assertNull(details.getCompletedAt(), "completedAt must remain null on ACTIVE");
    }

    @Test
    @DisplayName("PLANNED → invalid action → should throw and keep PLANNED status (rollback)")
    void applyAction_plannedWithInvalidActionShouldRollback() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        assertThrows(IllegalStateException.class,
                () -> details.applyAction(RecordAction.FINISH));

        assertEquals(TreatmentStatus.PLANNED, details.getStatus());
        assertNull(details.getCompletedAt());
    }

    // ================================================================
    // applyAction() — state machine: ACTIVE
    // ================================================================

    @Test
    @DisplayName("ACTIVE → SUSPEND → should transition to SUSPENDED")
    void applyAction_activeToSuspended() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.anActiveTreatment();

        StatusChangeResult result = details.applyAction(RecordAction.SUSPEND);

        assertEquals(TreatmentStatus.ACTIVE.toString(),    result.getPreviousStatus());
        assertEquals(TreatmentStatus.SUSPENDED.toString(), result.getNewStatus());
        assertEquals(TreatmentStatus.SUSPENDED, details.getStatus());
        assertNull(details.getCompletedAt());
    }

    @Test
    @DisplayName("ACTIVE → FINISH → should transition to FINISHED and set completedAt")
    void applyAction_activeToFinished() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.anActiveTreatment();

        StatusChangeResult result = details.applyAction(RecordAction.FINISH);

        assertEquals(TreatmentStatus.ACTIVE.toString(),   result.getPreviousStatus());
        assertEquals(TreatmentStatus.FINISHED.toString(), result.getNewStatus());
        assertEquals(TreatmentStatus.FINISHED, details.getStatus());
        assertNotNull(details.getCompletedAt(), "completedAt must be set after FINISH");
        assertEquals(LocalDate.now(), details.getCompletedAt());
    }

    @Test
    @DisplayName("ACTIVE → invalid action → should throw and keep ACTIVE status (rollback)")
    void applyAction_activeWithInvalidActionShouldRollback() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.anActiveTreatment();

        assertThrows(IllegalStateException.class,
                () -> details.applyAction(RecordAction.ACTIVATE));

        assertEquals(TreatmentStatus.ACTIVE, details.getStatus());
        assertNull(details.getCompletedAt());
    }

    // ================================================================
    // applyAction() — state machine: SUSPENDED
    // ================================================================

    @Test
    @DisplayName("SUSPENDED → ACTIVATE → should transition back to ACTIVE")
    void applyAction_suspendedToActive() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aSuspendedTreatment();

        StatusChangeResult result = details.applyAction(RecordAction.ACTIVATE);

        assertEquals(TreatmentStatus.SUSPENDED.toString(), result.getPreviousStatus());
        assertEquals(TreatmentStatus.ACTIVE.toString(),    result.getNewStatus());
        assertEquals(TreatmentStatus.ACTIVE,    details.getStatus());
    }

    @Test
    @DisplayName("SUSPENDED → invalid action → should throw and keep SUSPENDED status (rollback)")
    void applyAction_suspendedWithInvalidActionShouldRollback() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aSuspendedTreatment();

        assertThrows(IllegalStateException.class,
                () -> details.applyAction(RecordAction.FINISH));

        assertEquals(TreatmentStatus.SUSPENDED, details.getStatus());
    }

    // ================================================================
    // applyAction() — terminal state: FINISHED
    // ================================================================

    @Test
    @DisplayName("FINISHED → any action → should throw (terminal state)")
    void applyAction_finishedIsTerminal() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aFinishedTreatment();

        assertThrows(IllegalStateException.class,
                () -> details.applyAction(RecordAction.ACTIVATE));

        assertEquals(TreatmentStatus.FINISHED, details.getStatus());
    }

    @Test
    @DisplayName("FINISHED should preserve completedAt after failed action attempt")
    void applyAction_finishedKeepsCompletedAt() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aFinishedTreatment();
        LocalDate completedAt = details.getCompletedAt();

        assertThrows(IllegalStateException.class,
                () -> details.applyAction(RecordAction.SUSPEND));

        assertEquals(completedAt, details.getCompletedAt());
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return false when no relevant field differs")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        TreatmentDetails original  = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails identical = TreatmentDetailsTestDataBuilder.anIdenticalPlannedTreatment();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when instructions change")
    void canCorrect_shouldReturnTrueWhenInstructionsChange() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithInstructions("Nuevas instrucciones de administración");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when medications change")
    void canCorrect_shouldReturnTrueWhenMedicationsChange() {
        TreatmentDetails original = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder.aTreatmentWithMedications(
                List.of(TreatmentDetailsTestDataBuilder.aValidTreatmentMedication()));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when followUpRequired changes")
    void canCorrect_shouldReturnTrueWhenFollowUpRequiredChanges() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithFollowUp(true, LocalDate.now().plusDays(20));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when followUpDate changes")
    void canCorrect_shouldReturnTrueWhenFollowUpDateChanges() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatmentWithFollowUp();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithFollowUp(true, LocalDate.now().plusDays(30));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when estimatedEndDate changes")
    void canCorrect_shouldReturnTrueWhenEstimatedEndDateChanges() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithEndDate(LocalDate.now().plusDays(21));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when immutable fields differ (treatmentName changed)")
    void canCorrect_shouldReturnFalseWhenImmutableFieldChanges() {
        // treatmentName is an immutable identity field — changing it makes canCorrect return false
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithName("Nombre completamente diferente");

        assertFalse(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when startDate differs (immutable identity field)")
    void canCorrect_shouldReturnFalseWhenStartDateChanges() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder
                .aTreatmentWithStartDate(LocalDate.now().minusDays(5));

        assertFalse(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous is FINISHED (terminal — cannot be corrected)")
    void canCorrect_shouldThrowWhenPreviousIsFinished() {
        TreatmentDetails original   = TreatmentDetailsTestDataBuilder.aFinishedTreatment();
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not a TreatmentDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        TreatmentDetails correction = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();
        WeightDetails wrongType     = WeightDetails.create(10.0, WeightUnit.KG);

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));
    }

    // ================================================================
    // TreatmentMedication — create() validation
    // ================================================================

    @Test
    @DisplayName("Should create TreatmentMedication with all valid fields")
    void treatmentMedication_shouldCreateWithValidData() {
        TreatmentMedication medication = TreatmentDetailsTestDataBuilder.aValidTreatmentMedication();

        assertNotNull(medication);
        assertEquals("Metronidazol", medication.getName());
        assertEquals("500mg", medication.getDosage());
        assertEquals("Cada 8h", medication.getFrequency());
        assertEquals(7, medication.getDurationInDays());
        assertEquals("Con comida", medication.getNotes());
    }

    @Test
    @DisplayName("Should allow notes to be null in TreatmentMedication (optional)")
    void treatmentMedication_shouldAllowNullNotes() {
        TreatmentMedication medication = TreatmentDetailsTestDataBuilder
                .aTreatmentMedicationWithName("Ibuprofeno");

        assertNotNull(medication);
        assertNull(medication.getNotes());
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication name is null")
    void treatmentMedication_shouldFailWhenNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create(null, "500mg", "Cada 8h", 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - name"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication name is blank")
    void treatmentMedication_shouldFailWhenNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("   ", "500mg", "Cada 8h", 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - name"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication dosage is null")
    void treatmentMedication_shouldFailWhenDosageIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", null, "Cada 8h", 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - dosage"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication dosage is blank")
    void treatmentMedication_shouldFailWhenDosageIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "   ", "Cada 8h", 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - dosage"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication frequency is null")
    void treatmentMedication_shouldFailWhenFrequencyIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "500mg", null, 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - frequency"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication frequency is blank")
    void treatmentMedication_shouldFailWhenFrequencyIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "500mg", "   ", 7, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - frequency"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication durationInDays is null")
    void treatmentMedication_shouldFailWhenDurationIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "500mg", "Cada 8h", null, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - durationInDays"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication durationInDays is zero")
    void treatmentMedication_shouldFailWhenDurationIsZero() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "500mg", "Cada 8h", 0, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - durationInDays"));
    }

    @Test
    @DisplayName("Should throw when TreatmentMedication durationInDays is negative")
    void treatmentMedication_shouldFailWhenDurationIsNegative() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create("Ibuprofeno", "500mg", "Cada 8h", -1, null));

        assertTrue(ex.getMessage().contains("TreatmentMedication - durationInDays"));
    }

    @Test
    @DisplayName("Should accumulate multiple TreatmentMedication validation errors")
    void treatmentMedication_shouldAccumulateErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> TreatmentMedication.create(null, null, null, null, null));

        String msg = ex.getMessage();
        assertTrue(msg.contains("TreatmentMedication - name"));
        assertTrue(msg.contains("TreatmentMedication - dosage"));
        assertTrue(msg.contains("TreatmentMedication - frequency"));
        assertTrue(msg.contains("TreatmentMedication - durationInDays"));
    }

    // ================================================================
    // Full lifecycle
    // ================================================================

    @Test
    @DisplayName("Full lifecycle: PLANNED → ACTIVE → SUSPENDED → ACTIVE → FINISHED")
    void fullLifecycle_withSuspensionAndReactivation() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        assertEquals(TreatmentStatus.PLANNED, details.getStatus());
        assertNull(details.getCompletedAt());

        details.applyAction(RecordAction.ACTIVATE);
        assertEquals(TreatmentStatus.ACTIVE, details.getStatus());

        details.applyAction(RecordAction.SUSPEND);
        assertEquals(TreatmentStatus.SUSPENDED, details.getStatus());

        details.applyAction(RecordAction.ACTIVATE);
        assertEquals(TreatmentStatus.ACTIVE, details.getStatus());

        details.applyAction(RecordAction.FINISH);
        assertEquals(TreatmentStatus.FINISHED, details.getStatus());
        assertNotNull(details.getCompletedAt());
        assertEquals(LocalDate.now(), details.getCompletedAt());
    }

    @Test
    @DisplayName("Full lifecycle: PLANNED → ACTIVE → FINISHED (direct, no suspension)")
    void fullLifecycle_directToFinished() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aValidPlannedTreatment();

        details.applyAction(RecordAction.ACTIVATE);
        assertEquals(TreatmentStatus.ACTIVE, details.getStatus());

        details.applyAction(RecordAction.FINISH);
        assertEquals(TreatmentStatus.FINISHED, details.getStatus());
        assertNotNull(details.getCompletedAt());
    }

    @Test
    @DisplayName("FINISHED must not allow any further state changes")
    void fullLifecycle_finishedBlocksAllTransitions() {
        TreatmentDetails details = TreatmentDetailsTestDataBuilder.aFinishedTreatment();
        LocalDate completedAt = details.getCompletedAt();

        for (RecordAction action : new RecordAction[]{
                RecordAction.ACTIVATE, RecordAction.SUSPEND, RecordAction.FINISH}) {
            assertThrows(IllegalStateException.class, () -> details.applyAction(action));
        }

        // State and completedAt must remain unchanged after all failed attempts
        assertEquals(TreatmentStatus.FINISHED, details.getStatus());
        assertEquals(completedAt, details.getCompletedAt());
    }
}