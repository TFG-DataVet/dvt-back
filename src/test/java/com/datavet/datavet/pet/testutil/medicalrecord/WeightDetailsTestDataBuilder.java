package com.datavet.datavet.pet.testutil.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;

/**
 * Test data builder for WeightDetails.
 * Default values represent a valid weight record in KG.
 * Use field-specific helpers to test individual validations.
 */
public class WeightDetailsTestDataBuilder {

    // --- Defaults ---
    private static final Double     DEFAULT_VALUE = 12.5;
    private static final WeightUnit DEFAULT_UNIT  = WeightUnit.KG;

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Creates a valid WeightDetails with default values (12.5 KG). */
    public static WeightDetails aValidWeightDetails() {
        return WeightDetails.create(DEFAULT_VALUE, DEFAULT_UNIT);
    }

    /** Creates a valid WeightDetails using LB as unit. */
    public static WeightDetails aValidWeightDetailsInLb() {
        return WeightDetails.create(DEFAULT_VALUE, WeightUnit.LB);
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static WeightDetails aWeightDetailsWithValue(Double value) {
        return WeightDetails.create(value, DEFAULT_UNIT);
    }

    public static WeightDetails aWeightDetailsWithUnit(WeightUnit unit) {
        return WeightDetails.create(DEFAULT_VALUE, unit);
    }
}