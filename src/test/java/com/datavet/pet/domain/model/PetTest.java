package com.datavet.pet.domain.model;

import com.datavet.pet.domain.event.pet.*;
import com.datavet.pet.domain.exception.PetValidationException;
import com.datavet.pet.testutil.PetTestDataBuilder;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pet Domain Model Tests")
class PetTest {

    // ================================================================
    // CREATION - Pet.create()
    // ================================================================

    @Test
    @DisplayName("Should create Pet with all valid fields")
    void create_shouldCreatePetWithValidData() {
        Pet pet = PetTestDataBuilder.aValidPet();

        assertNotNull(pet);
        assertNotNull(pet.getId(), "id should be auto-generated");
        assertEquals("Clinic_id", pet.getClinicId());
        assertEquals("Zeus", pet.getName());
        assertEquals("Perro", pet.getSpecies());
        assertEquals("Golden retriver", pet.getBreed());
        assertEquals(Sex.MALE, pet.getSex());
        assertEquals(LocalDate.of(2020, 4, 5), pet.getDateOfBirth());
        assertEquals("Numero_de_chip", pet.getChipNumber());
        assertEquals("Esto_seria_un_link", pet.getAvatarUrl());
        assertNotNull(pet.getOwner());
    }

    @Test
    @DisplayName("Should set active=true and createdAt on creation")
    void create_shouldSetActiveAndTimestamps() {
        LocalDateTime before = LocalDateTime.now();
        Pet pet = PetTestDataBuilder.aValidPet();
        LocalDateTime after = LocalDateTime.now();

        assertTrue(pet.isActive(), "Pet should be active after creation");
        assertNotNull(pet.getCreatedAt());
        assertNull(pet.getUpdatedAt(), "updatedAt should be null on creation");
        assertTrue(!pet.getCreatedAt().isBefore(before) && !pet.getCreatedAt().isAfter(after));
    }

    @Test
    @DisplayName("Should auto-generate unique UUIDs for each Pet")
    void create_shouldGenerateUniqueIds() {
        Pet pet1 = PetTestDataBuilder.aValidPet();
        Pet pet2 = PetTestDataBuilder.aValidPet();

        assertNotEquals(pet1.getId(), pet2.getId());
    }

    @Test
    @DisplayName("Should raise PetCreatedEvent on creation")
    void create_shouldRaisePetCreatedEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetCreatedEvent.class, events.get(0));

        PetCreatedEvent event = (PetCreatedEvent) events.get(0);
        assertEquals(pet.getId(), event.getId());
        assertEquals("Zeus", event.getName());
        assertEquals("Clinic_id", event.getClinicId());
    }

    // ----------------------------------------------------------------
    // CREATION - validation failures
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should throw PetValidationException when clinicId is null")
    void create_shouldFailWhenClinicIdIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithClinicId(null));

        assertTrue(ex.getMessage().contains("[ClinicId]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when clinicId is empty")
    void create_shouldFailWhenClinicIdIsEmpty() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithClinicId("    "));


        assertTrue(ex.getMessage().contains("[ClinicId]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when name is null")
    void create_shouldFailWhenNameIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithName(null));

        assertTrue(ex.getMessage().contains("[Name]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when name is empty")
    void create_shouldFailWhenNameIsEmpty() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithName("    "));

        assertTrue(ex.getMessage().contains("[Name]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when species is null")
    void create_shouldFailWhenSpeciesIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithSpecie(null));

        assertTrue(ex.getMessage().contains("[Specie]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when species is empty")
    void create_shouldFailWhenSpeciesIsEmpty() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithSpecie("    "));

        assertTrue(ex.getMessage().contains("[Specie]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when breed is null")
    void create_shouldFailWhenBreedIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithBreed(null));

        assertTrue(ex.getMessage().contains("[Breed]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when breed is empty")
    void create_shouldFailWhenBreedIsEmpty() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithBreed("   "));

        assertTrue(ex.getMessage().contains("[Breed]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when dateOfBirth is in the future")
    void create_shouldFailWhenDateOfBirthIsInFuture() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithBirthdate(LocalDate.now().plusDays(1)));

        assertTrue(ex.getMessage().contains("[Birthday]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when owner is null")
    void create_shouldFailWhenOwnerIsNull() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> PetTestDataBuilder.aPetWithoutOwner());

        assertTrue(ex.getMessage().contains("[Owner]"));
    }

    @Test
    @DisplayName("Should accumulate multiple validation errors on creation")
    void create_shouldAccumulateMultipleErrors() {
        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> Pet.create(null, null, null, null, Sex.MALE,
                        LocalDate.of(2020, 1, 1), "chip", "avatar", null));

        String msg = ex.getMessage();
        assertTrue(msg.contains("[ClinicId]"));
        assertTrue(msg.contains("[Name]"));
        assertTrue(msg.contains("[Specie]"));
        assertTrue(msg.contains("[Breed]"));
        assertTrue(msg.contains("[Owner]"));
    }

    // ================================================================
    // UPDATE - update(id, name, avatarUrl)
    // ================================================================

    @Test
    @DisplayName("Should update name and avatarUrl correctly")
    void update_shouldUpdateNameAndAvatarUrl() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.update(pet.getId(), "Morita", "https://new-avatar.com");

        assertEquals("Morita", pet.getName());
        assertEquals("https://new-avatar.com", pet.getAvatarUrl());
        assertNotNull(pet.getUpdatedAt());
    }

    @Test
    @DisplayName("Should raise PetUpdateEvent when name changes")
    void update_shouldRaisePetUpdateEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.update(pet.getId(), "Morita", "https://new-avatar.com");

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetUpdateEvent.class, events.get(0));

        PetUpdateEvent event = (PetUpdateEvent) events.get(0);
        assertEquals("Zeus", event.getPreviousName());
        assertEquals("Morita", event.getName());
    }

    @Test
    @DisplayName("Should do nothing when name is the same (no event, no update)")
    void update_shouldDoNothingWhenNameIsTheSame() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.update(pet.getId(), "Zeus", "https://new-avatar.com");

        assertTrue(pet.getDomainEvents().isEmpty(), "No event should be raised when name doesn't change");
        assertNull(pet.getUpdatedAt(), "updatedAt should not change when name is the same");
    }

    @Test
    @DisplayName("Should throw PetValidationException when updating with null name")
    void update_shouldFailWhenNameIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.update(pet.getId(), null, "https://new-avatar.com"));

        assertTrue(ex.getMessage().contains("[Name]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when updating with null avatarUrl")
    void update_shouldFailWhenAvatarUrlIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.update(pet.getId(), "Morita", null));

        assertTrue(ex.getMessage().contains("[AvatarURL]"));
    }

    // ================================================================
    // correctBreed
    // ================================================================

    @Test
    @DisplayName("Should correct breed and raise PetBreedCorrectedEvent")
    void correctBreed_shouldUpdateBreedAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.correctBreed(pet.getId(), "Labrador", "Error en el registro inicial");

        assertEquals("Labrador", pet.getBreed());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetBreedCorrectedEvent.class, events.get(0));

        PetBreedCorrectedEvent event = (PetBreedCorrectedEvent) events.get(0);
        assertEquals("Golden retriver", event.getPreviousBreed());
        assertEquals("Labrador", event.getNewBreed());
        assertEquals("Error en el registro inicial", event.getReason());
    }

    @Test
    @DisplayName("Should do nothing when breed is the same")
    void correctBreed_shouldDoNothingWhenBreedIsTheSame() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.correctBreed(pet.getId(), "Golden retriver", "Sin cambio");

        assertTrue(pet.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should throw PetValidationException when new breed is null")
    void correctBreed_shouldFailWhenNewBreedIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctBreed(pet.getId(), null, "motivo"));

        assertTrue(ex.getMessage().contains("[correctBreed - NewBreed]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when reason is null")
    void correctBreed_shouldFailWhenReasonIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctBreed(pet.getId(), "Labrador", null));

        assertTrue(ex.getMessage().contains("[correctBreed - Reason]"));
    }

    // ================================================================
    // correctBirthDate
    // ================================================================

    @Test
    @DisplayName("Should correct birthDate and raise PetBirthDateCorrectedEvent")
    void correctBirthDate_shouldUpdateBirthDateAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();
        LocalDate newDate = LocalDate.of(2019, 3, 10);

        pet.correctBirthDate(pet.getId(), newDate, "Fecha incorrecta en registro");

        assertEquals(newDate, pet.getDateOfBirth());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetBirthDateCorrectedEvent.class, events.get(0));

        PetBirthDateCorrectedEvent event = (PetBirthDateCorrectedEvent) events.get(0);
        assertEquals(LocalDate.of(2020, 4, 5), event.getPreviousBirthDate());
        assertEquals(newDate, event.getNewBirthDate());
    }

    @Test
    @DisplayName("Should do nothing when new birthDate is the same")
    void correctBirthDate_shouldDoNothingWhenDateIsTheSame() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.correctBirthDate(pet.getId(), LocalDate.of(2020, 4, 5), "Sin cambio");

        assertTrue(pet.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should throw PetValidationException when new birthDate is null")
    void correctBirthDate_shouldFailWhenDateIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctBirthDate(pet.getId(), null, "motivo"));

        assertTrue(ex.getMessage().contains("[correctBirthDate - NewBirthDate]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when new birthDate is in the future")
    void correctBirthDate_shouldFailWhenDateIsInFuture() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctBirthDate(pet.getId(), LocalDate.now().plusDays(1), "motivo"));

        assertTrue(ex.getMessage().contains("[correctBirthDate - NewBirthDate]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when reason is null")
    void correctBirthDate_shouldFailWhenReasonIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctBirthDate(pet.getId(), LocalDate.of(2019, 1, 1), null));

        assertTrue(ex.getMessage().contains("[correctBirthDate - Reason]"));
    }

    // ================================================================
    // correctSex
    // ================================================================

    @Test
    @DisplayName("Should correct sex and raise PetSexCorrectedEvent")
    void correctSex_shouldUpdateSexAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.correctSex(pet.getId(), Sex.FEMALE, "Error en el registro");

        assertEquals(Sex.FEMALE, pet.getSex());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetSexCorrectedEvent.class, events.get(0));

        PetSexCorrectedEvent event = (PetSexCorrectedEvent) events.get(0);
        assertEquals(Sex.MALE, event.getPreviousSex());
        assertEquals(Sex.FEMALE, event.getNewSex());
        assertEquals("Error en el registro", event.getReason());
    }

    @Test
    @DisplayName("Should do nothing when sex is the same")
    void correctSex_shouldDoNothingWhenSexIsTheSame() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.correctSex(pet.getId(), Sex.MALE, "Sin cambio");

        assertTrue(pet.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should throw PetValidationException when new sex is null")
    void correctSex_shouldFailWhenNewSexIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctSex(pet.getId(), null, "motivo"));

        assertTrue(ex.getMessage().contains("[correctSex - NewSex]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when reason is null")
    void correctSex_shouldFailWhenReasonIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.correctSex(pet.getId(), Sex.FEMALE, null));

        assertTrue(ex.getMessage().contains("[correctSex - Reason]"));
    }

    // ================================================================
    // updateOwnerInfo
    // ================================================================

    @Test
    @DisplayName("Should update OwnerInfo and raise PetUpdateOwnerInfoEvent")
    void updateOwnerInfo_shouldUpdateOwnerAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();
        OwnerInfo newOwner = OwnerInfo.create(UUID.randomUUID().toString(), "Alejandra", "Talalla", new Phone("+34999888777"));

        pet.updateOwnerInfo(pet.getId(), newOwner);

        assertEquals(newOwner.getOwnerId(), pet.getOwner().getOwnerId());
        assertEquals("Alejandra", pet.getOwner().getName());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetUpdateOwnerInfoEvent.class, events.get(0));
    }

    @Test
    @DisplayName("Should throw PetValidationException when new ownerInfo is null")
    void updateOwnerInfo_shouldFailWhenOwnerIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.updateOwnerInfo(pet.getId(), null));

        assertTrue(ex.getMessage().contains("[updateOwnerInfo - OwnerInfo]"));
    }

    // ================================================================
    // deactivate / activate
    // ================================================================

    @Test
    @DisplayName("Should deactivate an active pet and raise PetDeactivatedEvent")
    void deactivate_shouldDeactivatePetAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.clearDomainEvents();

        pet.deactivate(pet.getId(), "Mascota fallecida");

        assertFalse(pet.isActive());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetDeactivatedEvent.class, events.get(0));

        PetDeactivatedEvent event = (PetDeactivatedEvent) events.get(0);
        assertEquals("Mascota fallecida", event.getReason());
    }

    @Test
    @DisplayName("Should throw PetValidationException when deactivating an already inactive pet")
    void deactivate_shouldFailWhenPetIsAlreadyInactive() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.deactivate(pet.getId(), "primera desactivación");

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.deactivate(pet.getId(), "segunda vez"));

        assertTrue(ex.getMessage().contains("[desactive - isActive]"));
    }

    @Test
    @DisplayName("Should throw PetValidationException when deactivation reason is null")
    void deactivate_shouldFailWhenReasonIsNull() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.deactivate(pet.getId(), null));

        assertTrue(ex.getMessage().contains("[desactive - Reason]"));
    }

    @Test
    @DisplayName("Should activate an inactive pet and raise PetActivateEvent")
    void activate_shouldActivatePetAndRaiseEvent() {
        Pet pet = PetTestDataBuilder.aValidPet();
        pet.deactivate(pet.getId(), "Viaje largo");
        pet.clearDomainEvents();

        pet.activate(pet.getId());

        assertTrue(pet.isActive());
        assertNotNull(pet.getUpdatedAt());

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(PetActivateEvent.class, events.get(0));
    }

    @Test
    @DisplayName("Should throw PetValidationException when activating an already active pet")
    void activate_shouldFailWhenPetIsAlreadyActive() {
        Pet pet = PetTestDataBuilder.aValidPet();

        PetValidationException ex = assertThrows(PetValidationException.class,
                () -> pet.activate(pet.getId()));

        assertTrue(ex.getMessage().contains("[activate - isActive]"));
    }

    // ================================================================
    // getAgeInYears
    // ================================================================

    @Test
    @DisplayName("Should return correct age in years")
    void getAgeInYears_shouldReturnCorrectAge() {
        // Born 2020-04-05, today is 2026-02-24 → 5 years
        Pet pet = PetTestDataBuilder.aValidPet();

        assertEquals(5, pet.getAgeInYears());
    }

    @Test
    @DisplayName("Should return 0 when dateOfBirth is null")
    void getAgeInYears_shouldReturnZeroWhenDateOfBirthIsNull() {
        // Build via builder to bypass factory validation, injecting null birthdate
        Pet pet = PetTestDataBuilder.aPetWithBirthdate(null);

        assertEquals(0, pet.getAgeInYears());
    }

    // ================================================================
    // Domain events - general behavior
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple domain events without clearing")
    void domainEvents_shouldAccumulateMultipleEvents() {
        Pet pet = PetTestDataBuilder.aValidPet();
        // created → 1 event

        pet.update(pet.getId(), "Morita", "https://new.com");
        pet.correctBreed(pet.getId(), "Labrador", "motivo");
        pet.deactivate(pet.getId(), "motivo");

        List<DomainEvent> events = pet.getDomainEvents();
        assertEquals(4, events.size());
        assertInstanceOf(PetCreatedEvent.class, events.get(0));
        assertInstanceOf(PetUpdateEvent.class, events.get(1));
        assertInstanceOf(PetBreedCorrectedEvent.class, events.get(2));
        assertInstanceOf(PetDeactivatedEvent.class, events.get(3));
    }

    @Test
    @DisplayName("getDomainEvents should return an immutable list")
    void domainEvents_shouldBeImmutable() {
        Pet pet = PetTestDataBuilder.aValidPet();

        List<DomainEvent> events = pet.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> events.add(null));
    }

    @Test
    @DisplayName("Should clear domain events correctly")
    void domainEvents_shouldClearCorrectly() {
        Pet pet = PetTestDataBuilder.aValidPet();
        assertFalse(pet.getDomainEvents().isEmpty());

        pet.clearDomainEvents();

        assertTrue(pet.getDomainEvents().isEmpty());
    }
}