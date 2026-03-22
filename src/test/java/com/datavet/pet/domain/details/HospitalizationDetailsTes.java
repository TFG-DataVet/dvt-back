package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordApplyActionException;
import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.hospitalization.ClinicalCondition;
import com.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.pet.domain.model.details.hospitalization.HospitalizationStatus;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.HospitalizationDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HospitalizationDetails Domain Model Tests")
class HospitalizationDetailsTes {

    // ================================================================
    // create() — happy path (always produces SCHEDULED)
    // ================================================================

    @Test
    @DisplayName("Should create HospitalizationDetails in SCHEDULED state with all valid fields")
    void create_shouldCreateHospitalizationInScheduledState() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertNotNull(details);
        assertEquals("Recuperación post-operatoria", details.getReason());
        assertEquals("Fractura de fémur", details.getDiagnosisAtAdmission());
        assertFalse(details.getIntensiveCare());
        assertEquals("Sala de Cirugía", details.getWard());
        assertEquals("Paciente estable, requiere monitoreo cada 4 horas", details.getNotes());
        assertEquals(HospitalizationStatus.SCHEDULED, details.getStatus());
        assertNull(details.getAdmissionDate(),  "SCHEDULED must have no admissionDate");
        assertNull(details.getDischargeDate(),  "SCHEDULED must have no dischargeDate");
        assertNull(details.getCondition(),      "SCHEDULED must have no clinicalCondition");
    }

    @Test
    @DisplayName("Should create HospitalizationDetails with intensiveCare=true")
    void create_shouldCreateHospitalizationWithIntensiveCare() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aScheduledHospitalizationWithIntensiveCare();

        assertTrue(details.getIntensiveCare());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.HOSPITALIZATION")
    void getType_shouldReturnHospitalizationType() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertEquals(MedicalRecordType.HOSPITALIZATION, details.getType());
    }

    // ================================================================
    // validate() — required fields
    // ================================================================

    @Test
    @DisplayName("Should throw when reason is null")
    void create_shouldFailWhenReasonIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithReason(null));

        assertTrue(ex.getMessage().contains("Hospitalization - reason"));
    }

    @Test
    @DisplayName("Should throw when reason is blank")
    void create_shouldFailWhenReasonIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithReason("   "));

        assertTrue(ex.getMessage().contains("Hospitalization - reason"));
    }

    @Test
    @DisplayName("Should throw when diagnosisAtAdmission is null")
    void create_shouldFailWhenDiagnosisAtAdmissionIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithDiagnosis(null));

        assertTrue(ex.getMessage().contains("Hospitalization - diagnosisAtAdmission"));
    }

    @Test
    @DisplayName("Should throw when diagnosisAtAdmission is blank")
    void create_shouldFailWhenDiagnosisAtAdmissionIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithDiagnosis("   "));

        assertTrue(ex.getMessage().contains("Hospitalization - diagnosisAtAdmission"));
    }

    @Test
    @DisplayName("Should throw when ward is null")
    void create_shouldFailWhenWardIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithWard(null));

        assertTrue(ex.getMessage().contains("Hospitalization - ward"));
    }

    @Test
    @DisplayName("Should throw when ward is blank")
    void create_shouldFailWhenWardIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithWard("   "));

        assertTrue(ex.getMessage().contains("Hospitalization - ward"));
    }

    @Test
    @DisplayName("Should throw when notes is null")
    void create_shouldFailWhenNotesIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithNotes(null));

        assertTrue(ex.getMessage().contains("Hospitalization - notes"));
    }

    @Test
    @DisplayName("Should throw when notes is blank")
    void create_shouldFailWhenNotesIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetailsTestDataBuilder.aHospitalizationWithNotes("   "));

        assertTrue(ex.getMessage().contains("Hospitalization - notes"));
    }

/*    @Test
    @DisplayName("Should throw when status is null")
    void create_shouldFailWhenStatusIsNull() {
        // The only way to reach status=null is via the builder directly
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> {
                    HospitalizationDetails details = HospitalizationDetails.create(
                            "razón",
                            "diagnóstico",
                            false
                            , "Sala A",
                            "notas",
                            null);
                    details.validate();
                });

        assertTrue(ex.getMessage().contains("Hospitalization - status"));
    }
*/
    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> HospitalizationDetails.create(
                        null,   // reason null
                        null,   // diagnosisAtAdmission null
                        false,
                        null,   // ward null
                        null    // notes null
                ));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Hospitalization - reason"));
        assertTrue(msg.contains("Hospitalization - diagnosisAtAdmission"));
        assertTrue(msg.contains("Hospitalization - ward"));
        assertTrue(msg.contains("Hospitalization - notes"));
    }

    // ================================================================
    // applyAction() — state machine: SCHEDULED
    // ================================================================

    // ESTO VAYA POR QUE NO TIENE UN ATRIBUTO GetPreviousStatus
    @Test
    @DisplayName("SCHEDULED → ADMIT → should transition to ADMITTED and set admissionDate")
    void applyAction_scheduledToAdmitted() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        StatusChangeResult result = details.applyAction(RecordAction.ADMIT);

        assertEquals(HospitalizationStatus.SCHEDULED.toString(), result.getPreviousStatus());
        assertEquals(HospitalizationStatus.ADMITTED.toString(),  result.getNewStatus());
        assertEquals(HospitalizationStatus.ADMITTED,  details.getStatus());
        assertNotNull(details.getAdmissionDate(), "admissionDate must be set after ADMIT");
        assertNull(details.getDischargeDate());
    }

    // ESTO VAYA POR QUE NO TIENE UN ATRIBUTO GetPreviousStatus
    @Test
    @DisplayName("SCHEDULED → CANCEL → should transition to CANCELLED and set dischargeDate")
    void applyAction_scheduledToCancelled() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        StatusChangeResult result = details.applyAction(RecordAction.CANCEL);

        assertEquals(HospitalizationStatus.SCHEDULED.toString(),  result.getPreviousStatus());
        assertEquals(HospitalizationStatus.CANCELLED.toString(),   result.getNewStatus());
        assertEquals(HospitalizationStatus.CANCELLED,   details.getStatus());
        assertNotNull(details.getDischargeDate(), "dischargeDate must be set after CANCEL");
        assertNull(details.getAdmissionDate(),    "admissionDate must remain null after CANCEL");
    }

    @Test
    @DisplayName("SCHEDULED → invalid action → should throw and keep original status")
    void applyAction_scheduledWithInvalidActionShouldThrowAndRollback() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.COMPLETE));

        // Status must not have changed (rollback)
        assertEquals(HospitalizationStatus.SCHEDULED, details.getStatus());
    }

    // ================================================================
    // applyAction() — state machine: ADMITTED
    // ================================================================

    @Test
    @DisplayName("ADMITTED → START → should transition to IN_PROGRESS")
    void applyAction_admittedToInProgress() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anAdmittedHospitalization();

        StatusChangeResult result = details.applyAction(RecordAction.START);

        assertEquals(HospitalizationStatus.ADMITTED.toString(),    result.getPreviousStatus());
        assertEquals(HospitalizationStatus.IN_PROGRESS.toString(), result.getNewStatus());
        assertEquals(HospitalizationStatus.IN_PROGRESS, details.getStatus());
    }

    @Test
    @DisplayName("ADMITTED → invalid action → should throw and keep ADMITTED status")
    void applyAction_admittedWithInvalidActionShouldThrowAndRollback() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anAdmittedHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.COMPLETE));

        assertEquals(HospitalizationStatus.ADMITTED, details.getStatus());
    }

    // ================================================================
    // applyAction() — state machine: IN_PROGRESS
    // ================================================================

    @Test
    @DisplayName("IN_PROGRESS → COMPLETE → should transition to COMPLETED and set dischargeDate")
    void applyAction_inProgressToCompleted() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anInProgressHospitalization();
        details.changeClinicalCondition(ClinicalCondition.STABLE);

        StatusChangeResult result = details.applyAction(RecordAction.COMPLETE);

        assertEquals(HospitalizationStatus.IN_PROGRESS.toString(), result.getPreviousStatus());
        assertEquals(HospitalizationStatus.COMPLETED.toString(),   result.getNewStatus());
        assertEquals(HospitalizationStatus.COMPLETED,   details.getStatus());
        assertNotNull(details.getDischargeDate(), "dischargeDate must be set after COMPLETE");
    }

    @Test
    @DisplayName("IN_PROGRESS → DECLARE_DECEASED → should transition to DECEASED and set dischargeDate")
    void applyAction_inProgressToDeceased() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anInProgressHospitalization();
        details.changeClinicalCondition(ClinicalCondition.CRITICAL);

        StatusChangeResult result = details.applyAction(RecordAction.DECLARE_DECEASED);

        assertEquals(HospitalizationStatus.IN_PROGRESS.toString(), result.getPreviousStatus());
        assertEquals(HospitalizationStatus.DECEASED.toString(),    result.getNewStatus());
        assertEquals(HospitalizationStatus.DECEASED,    details.getStatus());
        assertNotNull(details.getDischargeDate(), "dischargeDate must be set after DECLARE_DECEASED");
    }

    @Test
    @DisplayName("IN_PROGRESS → invalid action → should throw and keep IN_PROGRESS status")
    void applyAction_inProgressWithInvalidActionShouldThrowAndRollback() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anInProgressHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.ADMIT));

        assertEquals(HospitalizationStatus.IN_PROGRESS, details.getStatus());
    }

    // ================================================================
    // applyAction() — terminal states: COMPLETED, CANCELLED, DECEASED
    // ================================================================

    @Test
    @DisplayName("COMPLETED → any action → should throw (terminal state)")
    void applyAction_completedIsTerminal() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aCompletedHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.ADMIT));

        assertEquals(HospitalizationStatus.COMPLETED, details.getStatus());
    }

    @Test
    @DisplayName("CANCELLED → any action → should throw (terminal state)")
    void applyAction_cancelledIsTerminal() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aCancelledHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.ADMIT));

        assertEquals(HospitalizationStatus.CANCELLED, details.getStatus());
    }

    @Test
    @DisplayName("DECEASED → any action → should throw (terminal state)")
    void applyAction_deceasedIsTerminal() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aDeceasedHospitalization();

        assertThrows(MedicalRecordApplyActionException.class,
                () -> details.applyAction(RecordAction.ADMIT));

        assertEquals(HospitalizationStatus.DECEASED, details.getStatus());
    }

    // ================================================================
    // changeClinicalCondition()
    // ================================================================

    @Test
    @DisplayName("Should change clinical condition when IN_PROGRESS")
    void changeClinicalCondition_shouldUpdateConditionWhenInProgress() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anInProgressHospitalization();

        details.changeClinicalCondition(ClinicalCondition.STABLE);

        assertEquals(ClinicalCondition.STABLE, details.getCondition());
    }

    @Test
    @DisplayName("Should allow changing condition from one value to another when IN_PROGRESS")
    void changeClinicalCondition_shouldAllowChangingBetweenValues() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anInProgressHospitalization();

        details.changeClinicalCondition(ClinicalCondition.UNDER_OBSERVATION);
        assertEquals(ClinicalCondition.UNDER_OBSERVATION, details.getCondition());

        details.changeClinicalCondition(ClinicalCondition.CRITICAL);
        assertEquals(ClinicalCondition.CRITICAL, details.getCondition());
    }

    @Test
    @DisplayName("Should throw when changing condition while SCHEDULED")
    void changeClinicalCondition_shouldFailWhenScheduled() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertThrows(IllegalArgumentException.class,
                () -> details.changeClinicalCondition(ClinicalCondition.STABLE));
    }

    @Test
    @DisplayName("Should throw when changing condition while ADMITTED")
    void changeClinicalCondition_shouldFailWhenAdmitted() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .anAdmittedHospitalization();

        assertThrows(IllegalArgumentException.class,
                () -> details.changeClinicalCondition(ClinicalCondition.STABLE));
    }

    @Test
    @DisplayName("Should throw when changing condition while COMPLETED (terminal)")
    void changeClinicalCondition_shouldFailWhenCompleted() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aCompletedHospitalization();

        assertThrows(IllegalArgumentException.class,
                () -> details.changeClinicalCondition(ClinicalCondition.STABLE));
    }

    @Test
    @DisplayName("Should throw when changing condition while CANCELLED (terminal)")
    void changeClinicalCondition_shouldFailWhenCancelled() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aCancelledHospitalization();

        assertThrows(IllegalArgumentException.class,
                () -> details.changeClinicalCondition(ClinicalCondition.STABLE));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return true when reason changes on a SCHEDULED record")
    void canCorrect_shouldReturnTrueWhenReasonChanges() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aHospitalizationWithReason("Nueva razón de hospitalización");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when diagnosisAtAdmission changes")
    void canCorrect_shouldReturnTrueWhenDiagnosisChanges() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aHospitalizationWithDiagnosis("Nuevo diagnóstico");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when ward changes")
    void canCorrect_shouldReturnTrueWhenWardChanges() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aHospitalizationWithWard("Sala de Urgencias");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when notes change")
    void canCorrect_shouldReturnTrueWhenNotesChange() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aHospitalizationWithNotes("Nuevas indicaciones médicas");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when intensiveCare flag changes")
    void canCorrect_shouldReturnTrueWhenIntensiveCareChanges() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();            // intensiveCare=false
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aScheduledHospitalizationWithIntensiveCare(); // intensiveCare=true

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when nothing changes")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        HospitalizationDetails original  = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        HospitalizationDetails identical = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous is COMPLETED (terminal — cannot be corrected)")
    void canCorrect_shouldThrowWhenPreviousIsCompleted() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aCompletedHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous is CANCELLED (terminal — cannot be corrected)")
    void canCorrect_shouldThrowWhenPreviousIsCancelled() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aCancelledHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous is DECEASED (terminal — cannot be corrected)")
    void canCorrect_shouldThrowWhenPreviousIsDeceased() {
        HospitalizationDetails original   = HospitalizationDetailsTestDataBuilder
                .aDeceasedHospitalization();
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not a HospitalizationDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        HospitalizationDetails correction = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();
        WeightDetails wrongType = WeightDetails.create(10.0, WeightUnit.KG);

        assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));
    }

    // ================================================================
    // Full lifecycle — integration of the complete happy path
    // ================================================================

    @Test
    @DisplayName("Full lifecycle: SCHEDULED → ADMITTED → IN_PROGRESS → COMPLETED")
    void fullLifecycle_scheduledToCompleted() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        assertEquals(HospitalizationStatus.SCHEDULED, details.getStatus());

        details.applyAction(RecordAction.ADMIT);
        assertEquals(HospitalizationStatus.ADMITTED, details.getStatus());
        assertNotNull(details.getAdmissionDate());

        details.applyAction(RecordAction.START);
        assertEquals(HospitalizationStatus.IN_PROGRESS, details.getStatus());

        details.changeClinicalCondition(ClinicalCondition.UNDER_OBSERVATION);
        assertEquals(ClinicalCondition.UNDER_OBSERVATION, details.getCondition());

        details.changeClinicalCondition(ClinicalCondition.STABLE);
        details.applyAction(RecordAction.COMPLETE);
        assertEquals(HospitalizationStatus.COMPLETED, details.getStatus());
        assertNotNull(details.getDischargeDate());
        assertEquals(ClinicalCondition.STABLE, details.getCondition());
    }

    @Test
    @DisplayName("Full lifecycle: SCHEDULED → ADMITTED → IN_PROGRESS → DECEASED")
    void fullLifecycle_scheduledToDeceased() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        details.applyAction(RecordAction.ADMIT);
        details.applyAction(RecordAction.START);
        details.changeClinicalCondition(ClinicalCondition.CRITICAL);
        details.applyAction(RecordAction.DECLARE_DECEASED);

        assertEquals(HospitalizationStatus.DECEASED, details.getStatus());
        assertNotNull(details.getAdmissionDate());
        assertNotNull(details.getDischargeDate());
        assertEquals(ClinicalCondition.CRITICAL, details.getCondition());
    }

    @Test
    @DisplayName("Full lifecycle: SCHEDULED → CANCELLED")
    void fullLifecycle_scheduledToCancelled() {
        HospitalizationDetails details = HospitalizationDetailsTestDataBuilder
                .aValidScheduledHospitalization();

        details.applyAction(RecordAction.CANCEL);

        assertEquals(HospitalizationStatus.CANCELLED, details.getStatus());
        assertNull(details.getAdmissionDate(), "CANCELLED must not have admissionDate");
        assertNotNull(details.getDischargeDate());
        assertNull(details.getCondition(), "CANCELLED must not have clinicalCondition");
    }
}