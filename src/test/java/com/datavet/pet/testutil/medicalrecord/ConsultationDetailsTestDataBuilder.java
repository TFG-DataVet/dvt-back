package com.datavet.pet.testutil.medicalrecord;

import com.datavet.pet.domain.model.details.consultation.ConsultationDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * Test data builder for ConsultationDetails.
 *
 * Default: followUpRequired=false / followUpDate=null
 * because that is the simplest valid combination.
 *
 * Use the dedicated helpers to work with the followUp branch.
 */
public class ConsultationDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String       DEFAULT_REASON           = "Control anual de rutina";
    private static final List<String> DEFAULT_SYMPTOMS         = List.of("Letargo", "Pérdida de apetito");
    private static final String       DEFAULT_CLINICAL_FINDINGS = "Sin hallazgos relevantes";
    private static final String       DEFAULT_DIAGNOSIS        = "Animal sano";
    private static final String       DEFAULT_TREATMENT_PLAN   = "Ninguno por el momento";
    private static final boolean      DEFAULT_FOLLOW_UP        = false;
    private static final LocalDate    DEFAULT_FOLLOW_UP_DATE   = null;

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Valid consultation without follow-up (simplest valid case). */
    public static ConsultationDetails aValidConsultation() {
        return ConsultationDetails.create(
                DEFAULT_REASON,
                DEFAULT_SYMPTOMS,
                DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS,
                DEFAULT_TREATMENT_PLAN,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }

    /** Valid consultation WITH a follow-up date set. */
    public static ConsultationDetails aValidConsultationWithFollowUp() {
        return ConsultationDetails.create(
                DEFAULT_REASON,
                DEFAULT_SYMPTOMS,
                DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS,
                DEFAULT_TREATMENT_PLAN,
                true,
                LocalDate.now().plusDays(10)
        );
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static ConsultationDetails aConsultationWithReason(String reason) {
        return ConsultationDetails.create(reason, DEFAULT_SYMPTOMS, DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS, DEFAULT_TREATMENT_PLAN, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static ConsultationDetails aConsultationWithSymptoms(List<String> symptoms) {
        return ConsultationDetails.create(DEFAULT_REASON, symptoms, DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS, DEFAULT_TREATMENT_PLAN, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static ConsultationDetails aConsultationWithFollowUp(boolean followUpRequired,
                                                                LocalDate followUpDate) {
        return ConsultationDetails.create(DEFAULT_REASON, DEFAULT_SYMPTOMS, DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS, DEFAULT_TREATMENT_PLAN, followUpRequired, followUpDate);
    }

    /**
     * Returns a ConsultationDetails identical to aValidConsultation() — useful for canCorrect() tests
     * where we need two independent instances with the same values.
     */
    public static ConsultationDetails anIdenticalConsultation() {
        return ConsultationDetails.create(
                DEFAULT_REASON,
                DEFAULT_SYMPTOMS,
                DEFAULT_CLINICAL_FINDINGS,
                DEFAULT_DIAGNOSIS,
                DEFAULT_TREATMENT_PLAN,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }
}