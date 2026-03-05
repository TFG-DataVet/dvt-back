package com.datavet.datavet.pet.domain.details;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyType;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergySeverity;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.testutil.medicalrecord.WeightDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeightDetails Domain Model Tests")
class WeightDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create WeightDetails with valid value and KG unit")
    void create_shouldCreateWeightDetailsWithValidData() {
        WeightDetails details = WeightDetailsTestDataBuilder.aValidWeightDetails();

        assertNotNull(details);
        assertEquals(12.5, details.getValue());
        assertEquals(WeightUnit.KG, details.getUnit());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.WEIGHT")
    void getType_shouldReturnWeightType() {
        WeightDetails details = WeightDetailsTestDataBuilder.aValidWeightDetails();

        assertEquals(MedicalRecordType.WEIGHT, details.getType());
    }

    @Test
    @DisplayName("Should allow all WeightUnit variants")
    void create_shouldAllowAllWeightUnitVariants() {
        for (WeightUnit unit : WeightUnit.values()) {
            WeightDetails details = WeightDetailsTestDataBuilder.aWeightDetailsWithUnit(unit);
            assertEquals(unit, details.getUnit());
        }
    }

    @Test
    @DisplayName("Should create WeightDetails with LB unit")
    void create_shouldCreateWeightDetailsInLb() {
        WeightDetails details = WeightDetailsTestDataBuilder.aValidWeightDetailsInLb();

        assertNotNull(details);
        assertEquals(WeightUnit.LB, details.getUnit());
    }

    @Test
    @DisplayName("Should allow very small positive value (near zero)")
    void create_shouldAllowSmallPositiveValue() {
        assertDoesNotThrow(() ->
                WeightDetailsTestDataBuilder.aWeightDetailsWithValue(0.01));
    }

    // ================================================================
    // validate() — value
    // ================================================================

    @Test
    @DisplayName("Should throw when value is null")
    void create_shouldFailWhenValueIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> WeightDetailsTestDataBuilder.aWeightDetailsWithValue(null));

        assertTrue(ex.getMessage().contains("Weight - name"));
    }

    @Test
    @DisplayName("Should throw when value is zero")
    void create_shouldFailWhenValueIsZero() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> WeightDetailsTestDataBuilder.aWeightDetailsWithValue(0.0));

        assertTrue(ex.getMessage().contains("Weight - name"));
    }

    @Test
    @DisplayName("Should throw when value is negative")
    void create_shouldFailWhenValueIsNegative() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> WeightDetailsTestDataBuilder.aWeightDetailsWithValue(-5.0));

        assertTrue(ex.getMessage().contains("Weight - name"));
    }

    // ================================================================
    // validate() — unit
    // ================================================================

    @Test
    @DisplayName("Should throw when unit is null")
    void create_shouldFailWhenUnitIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> WeightDetailsTestDataBuilder.aWeightDetailsWithUnit(null));

        assertTrue(ex.getMessage().contains("Weight - unit"));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> WeightDetails.create(null, null));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Weight - name"));
        assertTrue(msg.contains("Weight - unit"));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should always return true (canCorrect is unconditional for WeightDetails)")
    void canCorrect_shouldAlwaysReturnTrue() {
        WeightDetails original  = WeightDetailsTestDataBuilder.aValidWeightDetails();
        WeightDetails identical = WeightDetailsTestDataBuilder.aValidWeightDetails();

        // WeightDetails.canCorrect() always returns true by design
        assertTrue(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when value changes")
    void canCorrect_shouldReturnTrueWhenValueChanges() {
        WeightDetails original   = WeightDetailsTestDataBuilder.aValidWeightDetails();
        WeightDetails correction = WeightDetailsTestDataBuilder.aWeightDetailsWithValue(15.0);

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when unit changes")
    void canCorrect_shouldReturnTrueWhenUnitChanges() {
        WeightDetails original   = WeightDetailsTestDataBuilder.aValidWeightDetails();     // KG
        WeightDetails correction = WeightDetailsTestDataBuilder.aValidWeightDetailsInLb(); // LB

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true even when passed a different MedicalRecordDetails type")
    void canCorrect_shouldReturnTrueEvenWithDifferentType() {
        WeightDetails correction = WeightDetailsTestDataBuilder.aValidWeightDetails();
        AllergyDetails wrongType = AllergyDetails.create(
                "Pollo", AllergyType.FOOD, AllergySeverity.MILD,
                List.of("Urticaria"), false,
                LocalDate.of(2023, 6, 15), null);

        // canCorrect() in WeightDetails always returns true without type checking
        assertTrue(correction.canCorrect(wrongType));
    }
}