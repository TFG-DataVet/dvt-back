package com.datavet.pet.testutil.medicalrecord;

import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.hospitalization.ClinicalCondition;
import com.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;

/**
 * Test data builder for HospitalizationDetails.
 *
 * HospitalizationDetails.create() always produces a SCHEDULED record
 * (admissionDate=null, dischargeDate=null, condition=null) — that is
 * the only valid starting state exposed by the factory method.
 *
 * All other states (ADMITTED, IN_PROGRESS, COMPLETED, CANCELLED, DECEASED)
 * are reached by calling applyAction() on an existing instance, so the
 * builder only provides SCHEDULED helpers plus utilities to drive the
 * state machine forward for tests that need a specific intermediate state.
 */
public class HospitalizationDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String  DEFAULT_REASON              = "Recuperación post-operatoria";
    private static final String  DEFAULT_DIAGNOSIS_AT_ADMISSION = "Fractura de fémur";
    private static final Boolean DEFAULT_INTENSIVE_CARE      = false;
    private static final String  DEFAULT_WARD                = "Sala de Cirugía";
    private static final String  DEFAULT_NOTES               = "Paciente estable, requiere monitoreo cada 4 horas";

    // ----------------------------------------------------------------
    // Happy path — SCHEDULED (only valid factory state)
    // ----------------------------------------------------------------

    /** Creates a valid HospitalizationDetails in SCHEDULED state (simplest valid case). */
    public static HospitalizationDetails aValidScheduledHospitalization() {
        return HospitalizationDetails.create(
                DEFAULT_REASON,
                DEFAULT_DIAGNOSIS_AT_ADMISSION,
                DEFAULT_INTENSIVE_CARE,
                DEFAULT_WARD,
                DEFAULT_NOTES
        );
    }

    /** Creates a valid HospitalizationDetails in SCHEDULED state with intensive care. */
    public static HospitalizationDetails aScheduledHospitalizationWithIntensiveCare() {
        return HospitalizationDetails.create(
                DEFAULT_REASON,
                DEFAULT_DIAGNOSIS_AT_ADMISSION,
                true,
                DEFAULT_WARD,
                DEFAULT_NOTES
        );
    }

    // ----------------------------------------------------------------
    // State-machine helpers — drive to specific states via applyAction()
    // ----------------------------------------------------------------

    /**
     * Returns a HospitalizationDetails driven to ADMITTED state.
     * SCHEDULED → ADMIT → ADMITTED
     */
    public static HospitalizationDetails anAdmittedHospitalization() {
        HospitalizationDetails details = aValidScheduledHospitalization();
        details.applyAction(RecordAction.ADMIT);
        return details;
    }

    /**
     * Returns a HospitalizationDetails driven to IN_PROGRESS state.
     * SCHEDULED → ADMIT → START → IN_PROGRESS
     */
    public static HospitalizationDetails anInProgressHospitalization() {
        HospitalizationDetails details = anAdmittedHospitalization();
        details.applyAction(RecordAction.START);
        return details;
    }

    /**
     * Returns a HospitalizationDetails driven to COMPLETED state.
     * SCHEDULED → ADMIT → START → COMPLETE → COMPLETED
     * Note: COMPLETED requires a ClinicalCondition — set it before completing.
     */
    public static HospitalizationDetails aCompletedHospitalization() {
        HospitalizationDetails details = anInProgressHospitalization();
        details.changeClinicalCondition(ClinicalCondition.STABLE);
        details.applyAction(RecordAction.COMPLETE);
        return details;
    }

    /**
     * Returns a HospitalizationDetails driven to CANCELLED state.
     * SCHEDULED → CANCEL → CANCELLED
     */
    public static HospitalizationDetails aCancelledHospitalization() {
        HospitalizationDetails details = aValidScheduledHospitalization();
        details.applyAction(RecordAction.CANCEL);
        return details;
    }

    /**
     * Returns a HospitalizationDetails driven to DECEASED state.
     * SCHEDULED → ADMIT → START → DECLARE_DECEASED → DECEASED
     */
    public static HospitalizationDetails aDeceasedHospitalization() {
        HospitalizationDetails details = anInProgressHospitalization();
        details.changeClinicalCondition(ClinicalCondition.CRITICAL);
        details.applyAction(RecordAction.DECLARE_DECEASED);
        return details;
    }

    // ----------------------------------------------------------------
    // Field-specific variants (all SCHEDULED)
    // ----------------------------------------------------------------

    public static HospitalizationDetails aHospitalizationWithReason(String reason) {
        return HospitalizationDetails.create(reason, DEFAULT_DIAGNOSIS_AT_ADMISSION,
                DEFAULT_INTENSIVE_CARE, DEFAULT_WARD, DEFAULT_NOTES);
    }

    public static HospitalizationDetails aHospitalizationWithDiagnosis(String diagnosis) {
        return HospitalizationDetails.create(DEFAULT_REASON, diagnosis,
                DEFAULT_INTENSIVE_CARE, DEFAULT_WARD, DEFAULT_NOTES);
    }

    public static HospitalizationDetails aHospitalizationWithWard(String ward) {
        return HospitalizationDetails.create(DEFAULT_REASON, DEFAULT_DIAGNOSIS_AT_ADMISSION,
                DEFAULT_INTENSIVE_CARE, ward, DEFAULT_NOTES);
    }

    public static HospitalizationDetails aHospitalizationWithNotes(String notes) {
        return HospitalizationDetails.create(DEFAULT_REASON, DEFAULT_DIAGNOSIS_AT_ADMISSION,
                DEFAULT_INTENSIVE_CARE, DEFAULT_WARD, notes);
    }
}