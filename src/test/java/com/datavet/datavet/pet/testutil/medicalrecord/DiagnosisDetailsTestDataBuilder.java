package com.datavet.datavet.pet.testutil.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisCategory;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisSeverity;

import java.time.LocalDate;
import java.util.List;

/**
 * Test data builder for DiagnosisDetails.
 *
 * Default: followUpRequired=false / followUpDate=null — simplest valid combination.
 * Use dedicated helpers to exercise the followUp branch and the chronic/contagious flags.
 */
public class DiagnosisDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String            DEFAULT_NAME          = "Gastritis aguda";
    private static final DiagnosisCategory DEFAULT_CATEGORY      = DiagnosisCategory.DIGESTIVE;
    private static final String            DEFAULT_DESCRIPTION   = "Inflamación del revestimiento del estómago";
    private static final DiagnosisSeverity DEFAULT_SEVERITY      = DiagnosisSeverity.MILD;
    private static final LocalDate         DEFAULT_DIAGNOSED_AT  = LocalDate.now();
    private static final boolean           DEFAULT_CHRONIC       = false;
    private static final boolean           DEFAULT_CONTAGIOUS    = false;
    private static final List<String>      DEFAULT_SYMPTOMS      = List.of("Vómito", "Inapetencia");
    private static final List<String>      DEFAULT_RECOMMENDATIONS = List.of("Dieta blanda 5 días");
    private static final boolean           DEFAULT_FOLLOW_UP     = false;
    private static final LocalDate         DEFAULT_FOLLOW_UP_DATE = null;

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Valid diagnosis without follow-up (simplest valid case). */
    public static DiagnosisDetails aValidDiagnosis() {
        return DiagnosisDetails.create(
                DEFAULT_NAME,
                DEFAULT_CATEGORY,
                DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY,
                DEFAULT_DIAGNOSED_AT,
                DEFAULT_CHRONIC,
                DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS,
                DEFAULT_RECOMMENDATIONS,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }

    /** Valid diagnosis WITH follow-up required and a future date. */
    public static DiagnosisDetails aValidDiagnosisWithFollowUp() {
        return DiagnosisDetails.create(
                DEFAULT_NAME,
                DEFAULT_CATEGORY,
                DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY,
                DEFAULT_DIAGNOSED_AT,
                DEFAULT_CHRONIC,
                DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS,
                DEFAULT_RECOMMENDATIONS,
                true,
                LocalDate.now().plusDays(14)
        );
    }

    /**
     * Returns a DiagnosisDetails identical to aValidDiagnosis() —
     * useful for canCorrect() tests where two independent instances with the same values are needed.
     */
    public static DiagnosisDetails anIdenticalDiagnosis() {
        return DiagnosisDetails.create(
                DEFAULT_NAME,
                DEFAULT_CATEGORY,
                DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY,
                DEFAULT_DIAGNOSED_AT,
                DEFAULT_CHRONIC,
                DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS,
                DEFAULT_RECOMMENDATIONS,
                DEFAULT_FOLLOW_UP,
                DEFAULT_FOLLOW_UP_DATE
        );
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static DiagnosisDetails aDiagnosisWithName(String name) {
        return DiagnosisDetails.create(name, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithCategory(DiagnosisCategory category) {
        return DiagnosisDetails.create(DEFAULT_NAME, category, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithSeverity(DiagnosisSeverity severity) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                severity, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithDiagnosedAt(LocalDate diagnosedAt) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, diagnosedAt, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithSymptoms(List<String> symptoms) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                symptoms, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithRecommendations(List<String> recommendations) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, recommendations, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }

    public static DiagnosisDetails aDiagnosisWithFollowUp(boolean followUpRequired,
                                                          LocalDate followUpDate) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, DEFAULT_CHRONIC, DEFAULT_CONTAGIOUS,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, followUpRequired, followUpDate);
    }

    public static DiagnosisDetails aDiagnosisWithFlags(boolean chronic, boolean contagious) {
        return DiagnosisDetails.create(DEFAULT_NAME, DEFAULT_CATEGORY, DEFAULT_DESCRIPTION,
                DEFAULT_SEVERITY, DEFAULT_DIAGNOSED_AT, chronic, contagious,
                DEFAULT_SYMPTOMS, DEFAULT_RECOMMENDATIONS, DEFAULT_FOLLOW_UP, DEFAULT_FOLLOW_UP_DATE);
    }
}