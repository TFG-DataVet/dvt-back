package com.datavet.pet.testutil.medicalrecord;

import com.datavet.pet.domain.model.details.vaccine.VaccineDetails;

import java.time.LocalDate;

/**
 * Test data builder for VaccineDetails.
 * Default values represent a valid, complete vaccine record.
 * Use field-specific helpers to test individual validations.
 */
public class VaccineDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String    DEFAULT_VACCINE_NAME    = "Antirrábica";
    private static final LocalDate DEFAULT_APPLICATION_DATE = LocalDate.of(2024, 3, 10);
    private static final LocalDate DEFAULT_NEXT_DOSE_DATE  = LocalDate.of(2025, 3, 10);
    private static final String    DEFAULT_BATCH_NUMBER    = "BATCH-2024-001";
    private static final String    DEFAULT_MANUFACTURER    = "Zoetis";

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Creates a valid VaccineDetails with all default values and a next dose date. */
    public static VaccineDetails aValidVaccineDetails() {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                DEFAULT_APPLICATION_DATE,
                DEFAULT_NEXT_DOSE_DATE,
                DEFAULT_BATCH_NUMBER,
                DEFAULT_MANUFACTURER
        );
    }

    /** Creates a valid VaccineDetails without a next dose date (optional field). */
    public static VaccineDetails aValidVaccineDetailsWithoutNextDose() {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                DEFAULT_APPLICATION_DATE,
                null,
                DEFAULT_BATCH_NUMBER,
                DEFAULT_MANUFACTURER
        );
    }

    /** Creates a valid VaccineDetails without a manufacturer (optional field). */
    public static VaccineDetails aValidVaccineDetailsWithoutManufacturer() {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                DEFAULT_APPLICATION_DATE,
                DEFAULT_NEXT_DOSE_DATE,
                DEFAULT_BATCH_NUMBER,
                null
        );
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static VaccineDetails aVaccineDetailsWithName(String vaccineName) {
        return VaccineDetails.create(
                vaccineName,
                DEFAULT_APPLICATION_DATE,
                DEFAULT_NEXT_DOSE_DATE,
                DEFAULT_BATCH_NUMBER,
                DEFAULT_MANUFACTURER
        );
    }

    public static VaccineDetails aVaccineDetailsWithApplicationDate(LocalDate applicationDate) {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                applicationDate,
                DEFAULT_NEXT_DOSE_DATE,
                DEFAULT_BATCH_NUMBER,
                DEFAULT_MANUFACTURER
        );
    }

    public static VaccineDetails aVaccineDetailsWithNextDoseDate(LocalDate nextDoseDate) {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                DEFAULT_APPLICATION_DATE,
                nextDoseDate,
                DEFAULT_BATCH_NUMBER,
                DEFAULT_MANUFACTURER
        );
    }

    public static VaccineDetails aVaccineDetailsWithBatchNumber(String batchNumber) {
        return VaccineDetails.create(
                DEFAULT_VACCINE_NAME,
                DEFAULT_APPLICATION_DATE,
                DEFAULT_NEXT_DOSE_DATE,
                batchNumber,
                DEFAULT_MANUFACTURER
        );
    }
}