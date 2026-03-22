package com.datavet.pet.domain.model;

import com.datavet.pet.domain.event.medicalrecord.MedicalRecordCreatedEvent;
import com.datavet.pet.domain.event.medicalrecord.MedicalRecordCorrectedEvent;
import com.datavet.pet.domain.event.medicalrecord.MedicalRecordCorrectionCreatedEvent;
import com.datavet.pet.domain.event.medicalrecord.MedicalRecordStatusChangeEvent;
import com.datavet.pet.domain.exception.MedicalRecordStateException;
import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.MedicalRecordTestDataBuilder;
import com.datavet.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MedicalRecord Domain Model Tests")
class MedicalRecordTest {

    // ================================================================
    // create()
    // ================================================================

    @Test
    @DisplayName("Should create MedicalRecord with all valid fields")
    void create_shouldCreateMedicalRecordWithValidData() {
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();

        assertNotNull(record);
        assertNotNull(record.getId(), "id should be auto-generated");
        assertEquals("pet-id-123", record.getPetId());
        assertEquals("clinic-id-456", record.getClinicId());
        assertEquals("vet-id-789", record.getVeterinarianId());
        assertEquals(MedicalRecordType.WEIGHT, record.getType());
        assertEquals(MedicalRecordLifecycleStatus.ACTIVE, record.getStatus());
        assertNotNull(record.getDetails());
        assertNotNull(record.getCreatedAt());
        assertNull(record.getUpdatedAt());
        assertNull(record.getCorrectedRecordId(), "correctedRecordId should be null on fresh creation");
    }

    @Test
    @DisplayName("Should auto-generate unique UUIDs for each MedicalRecord")
    void create_shouldGenerateUniqueIds() {
        MedicalRecord r1 = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        MedicalRecord r2 = MedicalRecordTestDataBuilder.aValidMedicalRecord();

        assertNotEquals(r1.getId(), r2.getId());
    }

    @Test
    @DisplayName("Should set recordedAt and updatedAt on creation within expected time window")
    void create_shouldSetTimestampsOnCreation() {
        LocalDateTime before = LocalDateTime.now();
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        LocalDateTime after = LocalDateTime.now();

        assertFalse(record.getCreatedAt().isBefore(before));
        assertFalse(record.getCreatedAt().isAfter(after));
        assertNull(record.getUpdatedAt());
        assertNull(record.getUpdatedAt());
    }

    @Test
    @DisplayName("Should raise MedicalRecordCreatedEvent on creation")
    void create_shouldRaiseMedicalRecordCreatedEvent() {
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();

        List<DomainEvent> events = record.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(MedicalRecordCreatedEvent.class, events.get(0));

        MedicalRecordCreatedEvent event = (MedicalRecordCreatedEvent) events.get(0);
        assertEquals(record.getId(), event.getMedicalRecordId());
        assertEquals(record.getPetId(), event.getPetId());
        assertEquals(record.getClinicId(), event.getClinicId());
        assertEquals(MedicalRecordType.WEIGHT, event.getType());
    }

    @Test
    @DisplayName("Should throw MedicalRecordValidationException when details is null")
    void create_shouldFailWhenDetailsIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> MedicalRecord.create(
                        "pet-id", "clinic-id", MedicalRecordType.WEIGHT,
                        "vet-id", "notes", null));

        assertTrue(ex.getMessage().contains("[details]"));
    }

    @Test
    @DisplayName("Should throw MedicalRecordValidationException when type does not match details type")
    void create_shouldFailWhenTypeMismatchWithDetails() {
        WeightDetails weightDetails = WeightDetails.create(10.0, WeightUnit.KG);

        // type=VACCINE but details=WEIGHT → mismatch
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> MedicalRecord.create(
                        "pet-id", "clinic-id", MedicalRecordType.VACCINE,
                        "vet-id", "notes", weightDetails));

        assertTrue(ex.getMessage().contains("[type]"));
    }

    // ================================================================
    // createCorrectionOf()
    // ================================================================

    @Test
    @DisplayName("Should create a correction record referencing the original")
    void createCorrectionOf_shouldCreateCorrectionRecord() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG); // different weight → canCorrect=true

        MedicalRecord correction = MedicalRecord.createCorrectionOf(
                original, correctedDetails, "vet-id-789", "Peso registrado incorrectamente");

        assertNotNull(correction);
        assertNotNull(correction.getId());
        assertNotEquals(original.getId(), correction.getId());
        assertEquals(original.getId(), correction.getCorrectedRecordId(),
                "correction must reference the original record's id");
        assertEquals(original.getPetId(), correction.getPetId());
        assertEquals(original.getClinicId(), correction.getClinicId());
        assertEquals(MedicalRecordLifecycleStatus.ACTIVE, correction.getStatus());
    }

    @Test
    @DisplayName("Should raise MedicalRecordCorrectionCreatedEvent when creating a correction")
    void createCorrectionOf_shouldRaiseCorrectionCreatedEvent() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG);
        original.clearDomainEvents();

        MedicalRecord correction = MedicalRecord.createCorrectionOf(
                original, correctedDetails, "vet-id-789", "Corrección de peso");

        List<DomainEvent> events = correction.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(MedicalRecordCorrectionCreatedEvent.class, events.get(0));

        MedicalRecordCorrectionCreatedEvent event = (MedicalRecordCorrectionCreatedEvent) events.get(0);
        assertEquals(correction.getId(), event.getCorrectedRecordId());
        assertEquals(original.getId(), event.getExistingRecordId());
        assertEquals("Corrección de peso", event.getReason());
    }

    @Test
    @DisplayName("Should throw when correction type does not match original type")
    void createCorrectionOf_shouldFailWhenTypeMismatch() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord(); // WEIGHT type
        // A VaccineDetails would be type=VACCINE → mismatch
        // We simulate this by using a details stub with wrong type via a lambda, but
        // since we can't easily create a fake, we use HospitalizationDetails (type=HOSPITALIZATION)
        HospitalizationDetails wrongDetails = HospitalizationDetails.create(
                "razón", "diagnóstico", false, "Sala A", "notas");

        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> MedicalRecord.createCorrectionOf(
                        original, wrongDetails, "vet-id", "motivo"));

        assertTrue(ex.getMessage().contains("Hospitalization - instanceOf"));
    }

    @Test
    @DisplayName("Should throw when trying to correct a non-ACTIVE record")
    void createCorrectionOf_shouldFailWhenOriginalIsNotActive() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG);

        // Mark original as CORRECTED first
        MedicalRecord firstCorrection = MedicalRecord.createCorrectionOf(
                original, correctedDetails, "vet-id", "primera corrección");
        original.markAsCorrected(firstCorrection, "primera corrección");

        // Now try to create another correction on the already-CORRECTED original
        WeightDetails secondDetails = WeightDetails.create(20.0, WeightUnit.KG);
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> MedicalRecord.createCorrectionOf(
                        original, secondDetails, "vet-id", "segunda corrección"));

        assertTrue(ex.getMessage().contains("[status]"));
    }

    // ================================================================
    // markAsCorrected()
    // ================================================================

    @Test
    @DisplayName("Should mark record as CORRECTED and raise MedicalRecordCorrectedEvent")
    void markAsCorrected_shouldMarkRecordAndRaiseEvent() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG);
        MedicalRecord correction = MedicalRecord.createCorrectionOf(
                original, correctedDetails, "vet-id", "corrección");
        original.clearDomainEvents();

        original.markAsCorrected(correction, "Peso incorrecto");

        assertEquals(MedicalRecordLifecycleStatus.CORRECTED, original.getStatus());
        assertNotNull(original.getUpdatedAt());

        List<DomainEvent> events = original.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(MedicalRecordCorrectedEvent.class, events.get(0));

        MedicalRecordCorrectedEvent event = (MedicalRecordCorrectedEvent) events.get(0);
        assertEquals(original.getId(), event.getOriginalRecordId());
        assertEquals(correction.getId(), event.getCorrectedRecordId());
        assertEquals("Peso incorrecto", event.getReason());
    }

    @Test
    @DisplayName("Should throw MedicalRecordStateException when marking a non-ACTIVE record as corrected")
    void markAsCorrected_shouldFailWhenRecordIsNotActive() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG);
        MedicalRecord correction = MedicalRecord.createCorrectionOf(
                original, correctedDetails, "vet-id", "corrección");

        // Mark it once → status becomes CORRECTED
        original.markAsCorrected(correction, "primera vez");

        // Try to mark it again → should fail
        assertThrows(MedicalRecordStateException.class,
                () -> original.markAsCorrected(correction, "segunda vez"));
    }

    @Test
    @DisplayName("Should throw MedicalRecordStateException when correction does not reference this record")
    void markAsCorrected_shouldFailWhenCorrectionDoesNotReferenceThisRecord() {
        MedicalRecord original = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        MedicalRecord unrelated = MedicalRecordTestDataBuilder.aValidMedicalRecord(); // different id

        WeightDetails correctedDetails = WeightDetails.create(15.0, WeightUnit.KG);
        MedicalRecord correctionOfUnrelated = MedicalRecord.createCorrectionOf(
                unrelated, correctedDetails, "vet-id", "corrección de otro registro");

        // correctionOfUnrelated references unrelated.id, not original.id
        assertThrows(MedicalRecordStateException.class,
                () -> original.markAsCorrected(correctionOfUnrelated, "motivo"));
    }

    // ================================================================
    // applyAction()  — tested via HospitalizationDetails (which supports it)
    // ================================================================

    @Test
    @DisplayName("Should apply action, update updatedAt and raise MedicalRecordStatusChangeEvent")
    void applyAction_shouldUpdateStatusAndRaiseEvent() {
        HospitalizationDetails details = HospitalizationDetails.create(
                "Cirugía de urgencia", "Fractura", false, "Sala B", "Observación post-op");

        MedicalRecord record = MedicalRecordTestDataBuilder.aMedicalRecordWithDetails(
                MedicalRecordType.HOSPITALIZATION, details);
        record.clearDomainEvents();

        LocalDateTime beforeAction = LocalDateTime.now();
        record.applyAction(RecordAction.ADMIT, "vet-id");
        LocalDateTime afterAction = LocalDateTime.now();

        assertFalse(record.getUpdatedAt().isBefore(beforeAction));
        assertFalse(record.getUpdatedAt().isAfter(afterAction));

        List<DomainEvent> events = record.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(MedicalRecordStatusChangeEvent.class, events.get(0));

        MedicalRecordStatusChangeEvent event = (MedicalRecordStatusChangeEvent) events.get(0);
        assertEquals(record.getId(), event.getMedicalRecordId());
        assertEquals("SCHEDULED", event.getPreviousStatus());
        assertEquals("ADMITTED", event.getNewStatus());
    }

    @Test
    @DisplayName("Should throw when applying an invalid action for current status")
    void applyAction_shouldFailWithInvalidActionForCurrentStatus() {
        HospitalizationDetails details = HospitalizationDetails.create(
                "razón", "diagnóstico", false, "Sala A", "notas");

        MedicalRecord record = MedicalRecordTestDataBuilder.aMedicalRecordWithDetails(
                MedicalRecordType.HOSPITALIZATION, details);

        // SCHEDULED → COMPLETE is invalid (must go ADMIT → START → COMPLETE)
        assertThrows(RuntimeException.class,
                () -> record.applyAction(RecordAction.COMPLETE, "vet-id"));
    }

    @Test
    @DisplayName("Should throw when applying action to a detail type that does not support it")
    void applyAction_shouldThrowForDetailTypesThatDoNotSupportActions() {
        // WeightDetails uses the default applyAction which throws MedicalRecordApplyActionException
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();

        assertThrows(RuntimeException.class,
                () -> record.applyAction(RecordAction.ADMIT, "vet-id"));
    }

    // ================================================================
    // Domain events — general behavior
    // ================================================================

    @Test
    @DisplayName("getDomainEvents should return an immutable list")
    void domainEvents_shouldBeImmutable() {
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();

        List<DomainEvent> events = record.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> events.add(null));
    }

    @Test
    @DisplayName("Should clear domain events correctly")
    void domainEvents_shouldClearCorrectly() {
        MedicalRecord record = MedicalRecordTestDataBuilder.aValidMedicalRecord();
        assertFalse(record.getDomainEvents().isEmpty());

        record.clearDomainEvents();

        assertTrue(record.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should accumulate events across multiple operations")
    void domainEvents_shouldAccumulateAcrossOperations() {
        HospitalizationDetails details = HospitalizationDetails.create(
                "razón", "diagnóstico", false, "Sala A", "notas");

        MedicalRecord original = MedicalRecordTestDataBuilder.aMedicalRecordWithDetails(
                MedicalRecordType.HOSPITALIZATION, details);
        // 1 event: MedicalRecordCreatedEvent

        WeightDetails correctedDetails = WeightDetails.create(10.0, WeightUnit.KG);
        // We need a same-type correction — use a new hospitalization record to test accumulation
        HospitalizationDetails correctionDetails = HospitalizationDetails.create(
                "razón corregida", "diagnóstico corregido", true, "Sala B", "notas corregidas");

        MedicalRecord correction = MedicalRecord.createCorrectionOf(
                original, correctionDetails, "vet-id", "corrección");
        // correction has 1 event: MedicalRecordCorrectionCreatedEvent

        original.markAsCorrected(correction, "corrección de hospitalización");
        // original now has 2 events: Created + Corrected

        List<DomainEvent> originalEvents = original.getDomainEvents();
        assertEquals(2, originalEvents.size());
        assertInstanceOf(MedicalRecordCreatedEvent.class, originalEvents.get(0));
        assertInstanceOf(MedicalRecordCorrectedEvent.class, originalEvents.get(1));
    }
}