package com.datavet.datavet.pet.testutil;

import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;

/**
 * Test data builder for MedicalRecord.
 * Uses WeightDetails as the default detail (simplest valid detail)
 * to keep the builder focused on MedicalRecord itself.
 * Each Detail will have its own dedicated builder.
 */
public class MedicalRecordTestDataBuilder {

    // --- Defaults ---
    private static final String DEFAULT_PET_ID         = "pet-id-123";
    private static final String DEFAULT_CLINIC_ID      = "clinic-id-456";
    private static final String DEFAULT_VETERINARIAN_ID = "vet-id-789";
    private static final String DEFAULT_NOTES          = "Registro de control de peso rutinario.";

    // Default detail: WeightDetails is the simplest valid detail (only 2 fields)
    private static final WeightDetails DEFAULT_DETAILS =
            WeightDetails.create(12.5, WeightUnit.KG);

    // ----------------------------------------------------------------
    // MedicalRecord factory helpers
    // ----------------------------------------------------------------

    /** Creates a valid MedicalRecord with default data (WEIGHT type). */
    public static MedicalRecord aValidMedicalRecord() {
        return MedicalRecord.create(
                DEFAULT_PET_ID,
                DEFAULT_CLINIC_ID,
                MedicalRecordType.WEIGHT,
                DEFAULT_VETERINARIAN_ID,
                DEFAULT_NOTES,
                DEFAULT_DETAILS
        );
    }

    /** Creates a valid MedicalRecord with a specific petId. */
    public static MedicalRecord aMedicalRecordWithPetId(String petId) {
        return MedicalRecord.create(
                petId,
                DEFAULT_CLINIC_ID,
                MedicalRecordType.WEIGHT,
                DEFAULT_VETERINARIAN_ID,
                DEFAULT_NOTES,
                DEFAULT_DETAILS
        );
    }

    /** Creates a valid MedicalRecord with a specific clinicId. */
    public static MedicalRecord aMedicalRecordWithClinicId(String clinicId) {
        return MedicalRecord.create(
                DEFAULT_PET_ID,
                clinicId,
                MedicalRecordType.WEIGHT,
                DEFAULT_VETERINARIAN_ID,
                DEFAULT_NOTES,
                DEFAULT_DETAILS
        );
    }

    /** Creates a valid MedicalRecord with a specific veterinarianId. */
    public static MedicalRecord aMedicalRecordWithVeterinarianId(String veterinarianId) {
        return MedicalRecord.create(
                DEFAULT_PET_ID,
                DEFAULT_CLINIC_ID,
                MedicalRecordType.WEIGHT,
                veterinarianId,
                DEFAULT_NOTES,
                DEFAULT_DETAILS
        );
    }

    /** Creates a valid MedicalRecord with the given details (and matching type). */
    public static MedicalRecord aMedicalRecordWithDetails(MedicalRecordType type,
                                                          MedicalRecordDetails details) {
        return MedicalRecord.create(
                DEFAULT_PET_ID,
                DEFAULT_CLINIC_ID,
                type,
                DEFAULT_VETERINARIAN_ID,
                DEFAULT_NOTES,
                details
        );
    }

    // ----------------------------------------------------------------
    // WeightDetails shortcut (default detail used by this builder)
    // ----------------------------------------------------------------

    /** Creates the default valid WeightDetails. */
    public static WeightDetails aValidWeightDetails() {
        return DEFAULT_DETAILS;
    }

    /** Creates a WeightDetails with a custom value and unit. */
    public static WeightDetails aWeightDetailsWith(Double value, WeightUnit unit) {
        return WeightDetails.create(value, unit);
    }
}