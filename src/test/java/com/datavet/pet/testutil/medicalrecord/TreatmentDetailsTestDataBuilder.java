package com.datavet.pet.testutil.medicalrecord;

import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.pet.domain.model.details.treatment.TreatmentMedication;

import java.time.LocalDate;
import java.util.List;

/**
 * Test data builder for TreatmentDetails.
 *
 * TreatmentDetails.create() always produces a PLANNED record.
 * All other states are reached by driving the state machine forward.
 *
 * State machine:
 *   PLANNED → ACTIVATE → ACTIVE
 *   ACTIVE  → SUSPEND  → SUSPENDED
 *   ACTIVE  → FINISH   → FINISHED
 *   SUSPENDED → ACTIVATE → ACTIVE
 *
 * Default: followUpRequired=false / followUpDate=null — simplest valid combination.
 */
public class TreatmentDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String        DEFAULT_NAME          = "Tratamiento antibiótico";
    private static final LocalDate     DEFAULT_START_DATE    = LocalDate.now().minusDays(1);
    private static final String        DEFAULT_INSTRUCTIONS  = "Administrar con comida, cada 12 horas";
    private static final LocalDate     DEFAULT_END_DATE      = LocalDate.now().plusDays(14);
    private static final boolean       DEFAULT_FOLLOW_UP     = false;
    private static final LocalDate     DEFAULT_FOLLOW_UP_DATE = null;

    private static final List<TreatmentMedication> DEFAULT_MEDICATIONS = List.of(
            TreatmentMedication.create("Amoxicilina", "250mg", "Cada 12h", 14, "Con comida")
    );

    // ----------------------------------------------------------------
    // Happy path — PLANNED (only valid factory state)
    // ----------------------------------------------------------------

    /** Creates a valid TreatmentDetails in PLANNED state without follow-up (simplest valid case). */
    public static TreatmentDetails aValidPlannedTreatment() {
        return TreatmentDetails.create(
                DEFAULT_NAME,
                DEFAULT_START_DATE,
                DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE,
                DEFAULT_MEDICATIONS,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }

    /** Creates a valid TreatmentDetails in PLANNED state WITH follow-up. */
    public static TreatmentDetails aValidPlannedTreatmentWithFollowUp() {
        return TreatmentDetails.create(
                DEFAULT_NAME,
                DEFAULT_START_DATE,
                DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE,
                DEFAULT_MEDICATIONS,
                true,
                DEFAULT_END_DATE.plusDays(7)
        );
    }

    /**
     * Returns a TreatmentDetails identical to aValidPlannedTreatment() —
     * useful for canCorrect() tests.
     */
    public static TreatmentDetails anIdenticalPlannedTreatment() {
        return TreatmentDetails.create(
                DEFAULT_NAME,
                DEFAULT_START_DATE,
                DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE,
                DEFAULT_MEDICATIONS,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }

    // ----------------------------------------------------------------
    // State-machine helpers
    // ----------------------------------------------------------------

    /**
     * PLANNED → ACTIVATE → ACTIVE
     */
    public static TreatmentDetails anActiveTreatment() {
        TreatmentDetails details = aValidPlannedTreatment();
        details.applyAction(RecordAction.ACTIVATE);
        return details;
    }

    /**
     * PLANNED → ACTIVATE → SUSPEND → SUSPENDED
     */
    public static TreatmentDetails aSuspendedTreatment() {
        TreatmentDetails details = anActiveTreatment();
        details.applyAction(RecordAction.SUSPEND);
        return details;
    }

    /**
     * PLANNED → ACTIVATE → FINISH → FINISHED
     */
    public static TreatmentDetails aFinishedTreatment() {
        TreatmentDetails details = anActiveTreatment();
        details.applyAction(RecordAction.FINISH);
        return details;
    }

    // ----------------------------------------------------------------
    // Field-specific variants (all PLANNED)
    // ----------------------------------------------------------------

    public static TreatmentDetails aTreatmentWithName(String name) {
        return TreatmentDetails.create(name, DEFAULT_START_DATE, DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE, DEFAULT_MEDICATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static TreatmentDetails aTreatmentWithStartDate(LocalDate startDate) {
        return TreatmentDetails.create(DEFAULT_NAME, startDate, DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE, DEFAULT_MEDICATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static TreatmentDetails aTreatmentWithInstructions(String instructions) {
        return TreatmentDetails.create(DEFAULT_NAME, DEFAULT_START_DATE, instructions,
                DEFAULT_END_DATE, DEFAULT_MEDICATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static TreatmentDetails aTreatmentWithEndDate(LocalDate endDate) {
        return TreatmentDetails.create(DEFAULT_NAME, DEFAULT_START_DATE, DEFAULT_INSTRUCTIONS,
                endDate, DEFAULT_MEDICATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static TreatmentDetails aTreatmentWithMedications(List<TreatmentMedication> medications) {
        return TreatmentDetails.create(DEFAULT_NAME, DEFAULT_START_DATE, DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE, medications, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static TreatmentDetails aTreatmentWithFollowUp(boolean followUpRequired,
                                                          LocalDate followUpDate) {
        return TreatmentDetails.create(DEFAULT_NAME, DEFAULT_START_DATE, DEFAULT_INSTRUCTIONS,
                DEFAULT_END_DATE, DEFAULT_MEDICATIONS, followUpRequired, followUpDate);
    }

    // ----------------------------------------------------------------
    // TreatmentMedication helpers
    // ----------------------------------------------------------------

    public static TreatmentMedication aValidTreatmentMedication() {
        return TreatmentMedication.create("Metronidazol", "500mg", "Cada 8h", 7, "Con comida");
    }

    public static TreatmentMedication aTreatmentMedicationWithName(String name) {
        return TreatmentMedication.create(name, "500mg", "Cada 8h", 7, null);
    }
}