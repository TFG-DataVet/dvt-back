package com.datavet.datavet.pet.domain.details;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.testutil.medicalrecord.TreatmentDetailsTestDataBuilder;
import com.datavet.datavet.pet.testutil.medicalrecord.VaccineDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VaccineDetails Domain Model Tests")
class VaccineDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create VaccineDetails with all valid fields")
    void create_shouldCreateVaccineDetailsWithValidData() {
        VaccineDetails details = VaccineDetailsTestDataBuilder.aValidVaccineDetails();

        assertNotNull(details);
        assertEquals("Antirrábica", details.getVaccineName());
        assertEquals(LocalDate.of(2024, 3, 10), details.getApplicationDate());
        assertEquals(LocalDate.of(2025, 3, 10), details.getNextDoseDate());
        assertEquals("BATCH-2024-001", details.getBatchNumber());
        assertEquals("Zoetis", details.getManufacturer());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.VACCINE")
    void getType_shouldReturnVaccineType() {
        VaccineDetails details = VaccineDetailsTestDataBuilder.aValidVaccineDetails();

        assertEquals(MedicalRecordType.VACCINE, details.getType());
    }

    @Test
    @DisplayName("Should allow nextDoseDate to be null (optional field)")
    void create_shouldAllowNullNextDoseDate() {
        VaccineDetails details = VaccineDetailsTestDataBuilder.aValidVaccineDetailsWithoutNextDose();

        assertNotNull(details);
        assertNull(details.getNextDoseDate());
    }

    @Test
    @DisplayName("Should allow manufacturer to be null (optional field)")
    void create_shouldAllowNullManufacturer() {
        VaccineDetails details = VaccineDetailsTestDataBuilder.aValidVaccineDetailsWithoutManufacturer();

        assertNotNull(details);
        assertNull(details.getManufacturer());
    }

    @Test
    @DisplayName("Should allow nextDoseDate equal to applicationDate (same-day dose)")
    void create_shouldAllowNextDoseDateEqualToApplicationDate() {
        assertDoesNotThrow(() ->
                VaccineDetailsTestDataBuilder.aVaccineDetailsWithNextDoseDate(
                        LocalDate.of(2024, 3, 10))); // same as DEFAULT_APPLICATION_DATE
    }

    @Test
    @DisplayName("Should allow applicationDate = today")
    void create_shouldAllowApplicationDateToday() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithApplicationDate(LocalDate.now()));

        assertTrue(ex.getMessage().contains("Vaccine - nextDoseDate"));

    }

    // ================================================================
    // validate() — vaccineName
    // ================================================================

    @Test
    @DisplayName("Should throw when vaccineName is null")
    void create_shouldFailWhenVaccineNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithName(null));

        assertTrue(ex.getMessage().contains("Vaccine - name"));
    }

    @Test
    @DisplayName("Should throw when vaccineName is blank")
    void create_shouldFailWhenVaccineNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithName("   "));

        assertTrue(ex.getMessage().contains("Vaccine - name"));
    }

    // ================================================================
    // validate() — applicationDate
    // ================================================================

    @Test
    @DisplayName("Should throw when applicationDate is null")
    void create_shouldFailWhenApplicationDateIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithApplicationDate(null));

        assertTrue(ex.getMessage().contains("Vaccine - applicationDate"));
    }

    @Test
    @DisplayName("Should throw when applicationDate is in the future")
    void create_shouldFailWhenApplicationDateIsInFuture() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithApplicationDate(
                        LocalDate.now().plusDays(1)));

        assertTrue(ex.getMessage().contains("Vaccine - applicationDate"));
    }

    // ================================================================
    // validate() — nextDoseDate
    // ================================================================

    @Test
    @DisplayName("Should throw when nextDoseDate is before applicationDate")
    void create_shouldFailWhenNextDoseDateIsBeforeApplicationDate() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetails.create(
                        "Antirrábica",
                        LocalDate.of(2024, 3, 10),
                        LocalDate.of(2024, 3, 9), // one day before applicationDate
                        "BATCH-2024-001",
                        "Zoetis"));

        assertTrue(ex.getMessage().contains("Vaccine - nextDoseDate"));
    }

    // ================================================================
    // validate() — batchNumber
    // ================================================================

    @Test
    @DisplayName("Should throw when batchNumber is null")
    void create_shouldFailWhenBatchNumberIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithBatchNumber(null));

        assertTrue(ex.getMessage().contains("Vaccine - batchNumber"));
    }

    @Test
    @DisplayName("Should throw when batchNumber is blank")
    void create_shouldFailWhenBatchNumberIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetailsTestDataBuilder.aVaccineDetailsWithBatchNumber("   "));

        assertTrue(ex.getMessage().contains("Vaccine - batchNumber"));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> VaccineDetails.create(
                        null,               // vaccineName null
                        null,               // applicationDate null
                        null,
                        null,               // batchNumber null
                        "Zoetis"));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Vaccine - name"));
        assertTrue(msg.contains("Vaccine - applicationDate"));
        assertTrue(msg.contains("Vaccine - batchNumber"));
    }

    // ================================================================
    // canCorrect()
    // ================================================================

    @Test
    @DisplayName("Should return true when vaccineName changes")
    void canCorrect_shouldReturnTrueWhenVaccineNameChanges() {
        VaccineDetails original   = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        VaccineDetails correction = VaccineDetailsTestDataBuilder.aVaccineDetailsWithName("Parvovirus");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when applicationDate changes")
    void canCorrect_shouldReturnTrueWhenApplicationDateChanges() {
        VaccineDetails original   = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        VaccineDetails correction = VaccineDetailsTestDataBuilder
                .aVaccineDetailsWithApplicationDate(LocalDate.of(2024, 1, 1));

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when batchNumber changes")
    void canCorrect_shouldReturnTrueWhenBatchNumberChanges() {
        VaccineDetails original   = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        VaccineDetails correction = VaccineDetailsTestDataBuilder
                .aVaccineDetailsWithBatchNumber("BATCH-2024-999");

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return true when manufacturer changes")
    void canCorrect_shouldReturnTrueWhenManufacturerChanges() {
        VaccineDetails original = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        VaccineDetails correction = VaccineDetails.create(
                "Antirrábica",
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2025, 3, 10),
                "BATCH-2024-001",
                "Merck"); // different manufacturer

        assertTrue(correction.canCorrect(original));
    }

    @Test
    @DisplayName("Should return false when no relevant field differs")
    void canCorrect_shouldReturnFalseWhenNothingChanges() {
        VaccineDetails original  = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        VaccineDetails identical = VaccineDetailsTestDataBuilder.aValidVaccineDetails();

        assertFalse(identical.canCorrect(original));
    }

    @Test
    @DisplayName("Should throw when previous detail is not a VaccineDetails instance")
    void canCorrect_shouldThrowWhenPreviousIsWrongType() {
        VaccineDetails correction = VaccineDetailsTestDataBuilder.aValidVaccineDetails();
        WeightDetails wrongType   = WeightDetails.create(10.0, WeightUnit.KG);

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> correction.canCorrect(wrongType));

        assertTrue(ex.getMessage().contains("Vaccine instanceOf"));
    }
}