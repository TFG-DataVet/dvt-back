package com.datavet.pet.domain.model.details;

import com.datavet.pet.domain.exception.MedicalRecordApplyActionException;
import com.datavet.pet.domain.exception.MedicalRecordStateException;
import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.surgery.*;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.SurgeryDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SurgeryDetails Domain Model Tests")
class SurgeryDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create SurgeryDetails in SCHEDULED state with all valid fields")
    void create_shouldCreateSurgeryInScheduledState() {
        SurgeryDetails details = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();

        assertNotNull(details);
        assertEquals("Osteosíntesis de fémur", details.getSurgeryName());
        assertEquals(SurgeryType.CORRECTIVE, details.getSurgeryType());
        assertFalse(details.getProcedures().isEmpty());
        assertEquals(AnesthesiaType.GENERAL, details.getAnesthesiaType());
        assertTrue(details.isHospitalizationRequired());
        assertTrue(details.getSurgeryDate().isAfter(LocalDateTime.now()));
        assertEquals(SurgeryStatus.SCHEDULED, details.getStatus());
        assertNull(details.getOutcome());
        assertNull(details.getCompletedAt());
        assertTrue(details.getPostOpMedications().isEmpty());
    }

    @Test
    @DisplayName("Should create SurgeryDetails without hospitalization required")
    void create_shouldCreateSurgeryWithoutHospitalization() {
        assertFalse(SurgeryDetailsTestDataBuilder
                .aScheduledSurgeryWithoutHospitalization().isHospitalizationRequired());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.SURGERY")
    void getType_shouldReturnSurgeryType() {
        assertEquals(MedicalRecordType.SURGERY,
                SurgeryDetailsTestDataBuilder.aValidScheduledSurgery().getType());
    }

    @Test
    @DisplayName("Should allow all SurgeryType variants")
    void create_shouldAllowAllSurgeryTypeVariants() {
        for (SurgeryType type : SurgeryType.values()) {
            assertEquals(type, SurgeryDetailsTestDataBuilder.aSurgeryWithType(type).getSurgeryType());
        }
    }

    @Test
    @DisplayName("Should allow all AnesthesiaType variants")
    void create_shouldAllowAllAnesthesiaTypeVariants() {
        for (AnesthesiaType anesthesia : AnesthesiaType.values()) {
            SurgeryDetails d = SurgeryDetails.create("Cirugía", SurgeryType.PREVENTIVE,
                    List.of(SurgeryDetailsTestDataBuilder.aValidSurgeryProcedure()),
                    anesthesia, false, LocalDateTime.now().plusDays(1));
            assertEquals(anesthesia, d.getAnesthesiaType());
        }
    }

    @Test
    @DisplayName("Procedures list should be immutable")
    void create_shouldStoreImmutableCopyOfProcedures() {
        SurgeryDetails details = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        assertThrows(UnsupportedOperationException.class,
                () -> details.getProcedures().add(
                        SurgeryDetailsTestDataBuilder.aValidSurgeryProcedure()));
    }

    // ================================================================
    // validate() — required fields
    // ================================================================

    @Test @DisplayName("Should throw when surgeryName is null")
    void create_shouldFailWhenSurgeryNameIsNull() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithName(null))
                .getMessage().contains("surgeryName"));
    }

    @Test @DisplayName("Should throw when surgeryName is blank")
    void create_shouldFailWhenSurgeryNameIsBlank() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithName("   "))
                .getMessage().contains("surgeryName"));
    }

    @Test @DisplayName("Should throw when surgeryType is null")
    void create_shouldFailWhenSurgeryTypeIsNull() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithType(null))
                .getMessage().contains("surgeryType"));
    }

    @Test @DisplayName("Should throw when procedures is null")
    void create_shouldFailWhenProceduresIsNull() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithProcedures(null))
                .getMessage().contains("procedures"));
    }

    @Test @DisplayName("Should throw when procedures is empty")
    void create_shouldFailWhenProceduresIsEmpty() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithProcedures(List.of()))
                .getMessage().contains("procedures"));
    }

    @Test @DisplayName("Should throw when surgeryDate is null in SCHEDULED state")
    void create_shouldFailWhenSurgeryDateIsNull() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithDate(null))
                .getMessage().contains("surgeryDate"));
    }

    @Test @DisplayName("Should throw when surgeryDate is in the past")
    void create_shouldFailWhenSurgeryDateIsInPast() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aSurgeryWithDate(
                        LocalDateTime.now().minusDays(1)))
                .getMessage().contains("surgeryDate"));
    }

    @Test @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetails.create(null, null, List.of(), null, false, null));
        String msg = ex.getMessage();
        assertTrue(msg.contains("surgeryName"));
        assertTrue(msg.contains("surgeryType"));
        assertTrue(msg.contains("procedures"));
        assertTrue(msg.contains("surgeryDate"));
    }

    // ================================================================
    // applyAction() — SCHEDULED
    // ================================================================

    @Test @DisplayName("SCHEDULED → ADMIT → ADMITTED")
    void applyAction_scheduledToAdmitted() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        StatusChangeResult r = d.applyAction(RecordAction.ADMIT);
        assertEquals(SurgeryStatus.SCHEDULED.toString(), r.getPreviousStatus());
        assertEquals(SurgeryStatus.ADMITTED.toString(),  r.getNewStatus());
        assertEquals(SurgeryStatus.ADMITTED,  d.getStatus());
    }

    @Test @DisplayName("SCHEDULED → CANCEL → CANCELLED, sets completedAt")
    void applyAction_scheduledToCancelled() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        d.applyAction(RecordAction.CANCEL);
        assertEquals(SurgeryStatus.CANCELLED, d.getStatus());
        assertNotNull(d.getCompletedAt());
    }

    @Test @DisplayName("SCHEDULED → invalid action → throws and rollback")
    void applyAction_scheduledInvalidRollback() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.COMPLETE));
        assertEquals(SurgeryStatus.SCHEDULED, d.getStatus());
        assertNull(d.getCompletedAt());
    }

    // ================================================================
    // applyAction() — ADMITTED
    // ================================================================

    @Test @DisplayName("ADMITTED → START → IN_PROGRESS")
    void applyAction_admittedToInProgress() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anAdmittedSurgery();
        StatusChangeResult r = d.applyAction(RecordAction.START);
        assertEquals(SurgeryStatus.ADMITTED.toString(),    r.getPreviousStatus());
        assertEquals(SurgeryStatus.IN_PROGRESS.toString(), r.getNewStatus());
    }

    @Test @DisplayName("ADMITTED → CANCEL → CANCELLED, sets completedAt")
    void applyAction_admittedToCancelled() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anAdmittedSurgery();
        d.applyAction(RecordAction.CANCEL);
        assertEquals(SurgeryStatus.CANCELLED, d.getStatus());
        assertNotNull(d.getCompletedAt());
    }

    @Test @DisplayName("ADMITTED → invalid action → throws and rollback")
    void applyAction_admittedInvalidRollback() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anAdmittedSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.COMPLETE));
        assertEquals(SurgeryStatus.ADMITTED, d.getStatus());
    }

    // ================================================================
    // applyAction() — IN_PROGRESS
    // ================================================================

    @Test @DisplayName("IN_PROGRESS → COMPLETE (outcome + medication) → COMPLETED")
    void applyAction_inProgressToCompleted() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.SUCCESSFUL);
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        StatusChangeResult r = d.applyAction(RecordAction.COMPLETE);
        assertEquals(SurgeryStatus.COMPLETED, r.getNewStatus());
        assertNotNull(d.getCompletedAt());
        assertEquals(SurgeryOutcome.SUCCESSFUL, d.getOutcome());
    }

    @Test @DisplayName("IN_PROGRESS → COMPLETE without outcome → throws and rollback")
    void applyAction_inProgressToCompletedWithoutOutcome() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        assertThrows(MedicalRecordApplyActionException.class, () -> d.applyAction(RecordAction.COMPLETE));
        assertEquals(SurgeryStatus.IN_PROGRESS, d.getStatus());
        assertNull(d.getCompletedAt());
    }

    @Test @DisplayName("IN_PROGRESS → COMPLETE without medication → throws and rollback")
    void applyAction_inProgressToCompletedWithoutMedication() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.SUCCESSFUL);
        assertThrows(MedicalRecordApplyActionException.class, () -> d.applyAction(RecordAction.COMPLETE));
        assertEquals(SurgeryStatus.IN_PROGRESS, d.getStatus());
        assertNull(d.getCompletedAt());
    }

    @Test @DisplayName("IN_PROGRESS → DECLARE_DECEASED (outcome + medication) → DECEASED")
    void applyAction_inProgressToDeceased() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.FAILED);
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        d.applyAction(RecordAction.DECLARE_DECEASED);
        assertEquals(SurgeryStatus.DECEASED, d.getStatus());
        assertNotNull(d.getCompletedAt());
    }

    @Test @DisplayName("IN_PROGRESS → invalid action → throws and rollback")
    void applyAction_inProgressInvalidRollback() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.ADMIT));
        assertEquals(SurgeryStatus.IN_PROGRESS, d.getStatus());
    }

    // ================================================================
    // Terminal states
    // ================================================================

    @Test @DisplayName("COMPLETED → any action → throws (terminal)")
    void applyAction_completedIsTerminal() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aCompletedSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.ADMIT));
        assertEquals(SurgeryStatus.COMPLETED, d.getStatus());
    }

    @Test @DisplayName("CANCELLED → any action → throws (terminal)")
    void applyAction_cancelledIsTerminal() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aCancelledSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.ADMIT));
        assertEquals(SurgeryStatus.CANCELLED, d.getStatus());
    }

    @Test @DisplayName("DECEASED → any action → throws (terminal)")
    void applyAction_deceasedIsTerminal() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aDeceasedSurgery();
        assertThrows(IllegalStateException.class, () -> d.applyAction(RecordAction.ADMIT));
        assertEquals(SurgeryStatus.DECEASED, d.getStatus());
    }

    // ================================================================
    // changedOutcome()
    // ================================================================

    @Test @DisplayName("Should set outcome when IN_PROGRESS")
    void changedOutcome_shouldSetWhenInProgress() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.SUCCESSFUL);
        assertEquals(SurgeryOutcome.SUCCESSFUL, d.getOutcome());
    }

    @Test @DisplayName("Should allow changing outcome between values while IN_PROGRESS")
    void changedOutcome_shouldAllowChangingValues() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.SUCCESSFUL_WITH_COMPLICATIONS);
        assertEquals(SurgeryOutcome.SUCCESSFUL_WITH_COMPLICATIONS, d.getOutcome());
        d.changedOutcome(SurgeryOutcome.FAILED);
        assertEquals(SurgeryOutcome.FAILED, d.getOutcome());
    }

    @Test @DisplayName("Should throw when setting outcome while SCHEDULED")
    void changedOutcome_shouldFailWhenScheduled() {
        assertThrows(MedicalRecordStateException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .changedOutcome(SurgeryOutcome.SUCCESSFUL));
    }

    @Test @DisplayName("Should throw when setting outcome while ADMITTED")
    void changedOutcome_shouldFailWhenAdmitted() {
        assertThrows(MedicalRecordStateException.class,
                () -> SurgeryDetailsTestDataBuilder.anAdmittedSurgery()
                        .changedOutcome(SurgeryOutcome.SUCCESSFUL));
    }

    @Test @DisplayName("Should throw when setting outcome while COMPLETED")
    void changedOutcome_shouldFailWhenCompleted() {
        assertThrows(MedicalRecordStateException.class,
                () -> SurgeryDetailsTestDataBuilder.aCompletedSurgery()
                        .changedOutcome(SurgeryOutcome.FAILED));
    }

    @Test @DisplayName("Should throw when outcome is null")
    void changedOutcome_shouldFailWhenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.anInProgressSurgery().changedOutcome(null));
    }

    // ================================================================
    // addPostOpMedication()
    // ================================================================

    @Test @DisplayName("Should add medication when IN_PROGRESS")
    void addPostOpMedication_shouldAddWhenInProgress() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication());
        assertEquals(1, d.getPostOpMedications().size());
    }

    @Test @DisplayName("Should accumulate multiple medications")
    void addPostOpMedication_shouldAccumulateMultiple() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.anInProgressSurgery();
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication());
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        assertEquals(2, d.getPostOpMedications().size());
    }

    @Test @DisplayName("Should throw when adding medication while SCHEDULED")
    void addPostOpMedication_shouldFailWhenScheduled() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication()));
    }

    @Test @DisplayName("Should throw when adding medication while ADMITTED")
    void addPostOpMedication_shouldFailWhenAdmitted() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.anAdmittedSurgery()
                        .addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication()));
    }

    @Test @DisplayName("Should throw when adding medication while COMPLETED")
    void addPostOpMedication_shouldFailWhenCompleted() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.aCompletedSurgery()
                        .addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication()));
    }

    @Test @DisplayName("Should throw when medication is null")
    void addPostOpMedication_shouldFailWhenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.anInProgressSurgery()
                        .addPostOpMedication(null));
    }

    // ================================================================
    // rescheduled()
    // ================================================================

    @Test @DisplayName("Should reschedule to a future date when SCHEDULED")
    void rescheduled_shouldUpdateDateWhenScheduled() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        LocalDateTime newDate = LocalDateTime.now().plusDays(10);
        d.rescheduled(newDate);
        assertEquals(newDate, d.getSurgeryDate());
    }

    @Test @DisplayName("Should throw when rescheduling while ADMITTED")
    void rescheduled_shouldFailWhenAdmitted() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.anAdmittedSurgery()
                        .rescheduled(LocalDateTime.now().plusDays(5)));
    }

    @Test @DisplayName("Should throw when rescheduling while IN_PROGRESS")
    void rescheduled_shouldFailWhenInProgress() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.anInProgressSurgery()
                        .rescheduled(LocalDateTime.now().plusDays(5)));
    }

    @Test @DisplayName("Should throw when new date is null")
    void rescheduled_shouldFailWhenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery().rescheduled(null));
    }

    @Test @DisplayName("Should throw when new date is in the past")
    void rescheduled_shouldFailWhenPast() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .rescheduled(LocalDateTime.now().minusDays(1)));
    }

    // ================================================================
    // SurgeryProcedure
    // ================================================================

    @Test @DisplayName("Should create SurgeryProcedure with valid data")
    void surgeryProcedure_shouldCreateWithValidData() {
        SurgeryProcedure p = SurgeryProcedure.create("Biopsia", "Extracción de tejido");
        assertEquals("Biopsia", p.getName());
        assertEquals("Extracción de tejido", p.getDescription());
    }

    @Test @DisplayName("Should allow null description in SurgeryProcedure")
    void surgeryProcedure_shouldAllowNullDescription() {
        assertNull(SurgeryProcedure.create("Biopsia", null).getDescription());
    }

    @Test @DisplayName("Should throw when SurgeryProcedure name is null")
    void surgeryProcedure_shouldFailWhenNameIsNull() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryProcedure.create(null, "desc"))
                .getMessage().contains("Surgery - procedure"));
    }

    @Test @DisplayName("Should throw when SurgeryProcedure name is blank")
    void surgeryProcedure_shouldFailWhenNameIsBlank() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryProcedure.create("   ", "desc"))
                .getMessage().contains("Surgery - procedure"));
    }

    @Test @DisplayName("Should throw when SurgeryProcedure description is blank")
    void surgeryProcedure_shouldFailWhenDescriptionIsBlank() {
        assertTrue(assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryProcedure.create("Biopsia", "   "))
                .getMessage().contains("Surgery - procedura"));
    }

    // ================================================================
    // SurgeryMedication
    // ================================================================

    @Test @DisplayName("Should create SurgeryMedication with all valid fields")
    void surgeryMedication_shouldCreateWithValidData() {
        SurgeryMedication m = SurgeryDetailsTestDataBuilder.aValidSurgeryMedication();
        assertEquals("Tramadol", m.getName());
        assertEquals("5mg/kg", m.getDosage());
        assertEquals("Cada 8h", m.getFrequency());
        assertEquals(5, m.getDurationInDays());
    }

    @Test @DisplayName("Should allow null notes in SurgeryMedication")
    void surgeryMedication_shouldAllowNullNotes() {
        assertNull(SurgeryDetailsTestDataBuilder.aSurgeryMedicationWithName("Amoxicilina").getNotes());
    }

    @Test @DisplayName("Should throw when SurgeryMedication name is null")
    void surgeryMedication_shouldFailWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create(null, "5mg/kg", "Cada 8h", 5, null));
    }

    @Test @DisplayName("Should throw when SurgeryMedication dosage is null")
    void surgeryMedication_shouldFailWhenDosageIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create("Tramadol", null, "Cada 8h", 5, null));
    }

    @Test @DisplayName("Should throw when SurgeryMedication frequency is null")
    void surgeryMedication_shouldFailWhenFrequencyIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create("Tramadol", "5mg/kg", null, 5, null));
    }

    @Test @DisplayName("Should throw when SurgeryMedication durationInDays is null")
    void surgeryMedication_shouldFailWhenDurationIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create("Tramadol", "5mg/kg", "Cada 8h", null, null));
    }

    @Test @DisplayName("Should throw when SurgeryMedication durationInDays is zero or negative")
    void surgeryMedication_shouldFailWhenDurationIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create("Tramadol", "5mg/kg", "Cada 8h", 0, null));
        assertThrows(IllegalArgumentException.class,
                () -> SurgeryMedication.create("Tramadol", "5mg/kg", "Cada 8h", -1, null));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test @DisplayName("canCorrect returns false when nothing changes")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        assertFalse(SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                .canCorrect(SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()));
    }

    @Test @DisplayName("canCorrect throws when previous is COMPLETED")
    void canCorrect_shouldThrowWhenPreviousIsCompleted() {
        assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .canCorrect(SurgeryDetailsTestDataBuilder.aCompletedSurgery()));
    }

    @Test @DisplayName("canCorrect throws when previous is CANCELLED")
    void canCorrect_shouldThrowWhenPreviousIsCancelled() {
        assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .canCorrect(SurgeryDetailsTestDataBuilder.aCancelledSurgery()));
    }

    @Test @DisplayName("canCorrect throws when previous is DECEASED")
    void canCorrect_shouldThrowWhenPreviousIsDeceased() {
        assertThrows(MedicalRecordValidationException.class,
                () -> SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                        .canCorrect(SurgeryDetailsTestDataBuilder.aDeceasedSurgery()));
    }

    @Test @DisplayName("canCorrect returns false when previous is wrong type")
    void canCorrect_shouldReturnFalseWhenWrongType() {
        assertFalse(SurgeryDetailsTestDataBuilder.aValidScheduledSurgery()
                .canCorrect(WeightDetails.create(10.0, WeightUnit.KG)));
    }

    // ================================================================
    // Full lifecycle
    // ================================================================

    @Test @DisplayName("Full lifecycle: SCHEDULED → ADMITTED → IN_PROGRESS → COMPLETED")
    void fullLifecycle_scheduledToCompleted() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();

        d.applyAction(RecordAction.ADMIT);
        assertEquals(SurgeryStatus.ADMITTED, d.getStatus());

        d.applyAction(RecordAction.START);
        assertEquals(SurgeryStatus.IN_PROGRESS, d.getStatus());

        d.changedOutcome(SurgeryOutcome.SUCCESSFUL_WITH_COMPLICATIONS);
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.aValidSurgeryMedication());
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        assertEquals(2, d.getPostOpMedications().size());

        d.applyAction(RecordAction.COMPLETE);
        assertEquals(SurgeryStatus.COMPLETED, d.getStatus());
        assertNotNull(d.getCompletedAt());
    }

    @Test @DisplayName("Full lifecycle: SCHEDULED → rescheduled → IN_PROGRESS → DECEASED")
    void fullLifecycle_withRescheduleThenDeceased() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        d.rescheduled(LocalDateTime.now().plusDays(7));

        d.applyAction(RecordAction.ADMIT);
        d.applyAction(RecordAction.START);
        d.changedOutcome(SurgeryOutcome.FAILED);
        d.addPostOpMedication(SurgeryDetailsTestDataBuilder.DEFAULT_POST_OP_MEDICATION);
        d.applyAction(RecordAction.DECLARE_DECEASED);

        assertEquals(SurgeryStatus.DECEASED, d.getStatus());
        assertNotNull(d.getCompletedAt());
        assertEquals(SurgeryOutcome.FAILED, d.getOutcome());
    }

    @Test @DisplayName("Full lifecycle: SCHEDULED → CANCELLED")
    void fullLifecycle_scheduledToCancelled() {
        SurgeryDetails d = SurgeryDetailsTestDataBuilder.aValidScheduledSurgery();
        d.applyAction(RecordAction.CANCEL);

        assertEquals(SurgeryStatus.CANCELLED, d.getStatus());
        assertNotNull(d.getCompletedAt());
        assertNull(d.getOutcome());
        assertTrue(d.getPostOpMedications().isEmpty());
    }
}