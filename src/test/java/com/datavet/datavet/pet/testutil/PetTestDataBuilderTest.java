package com.datavet.datavet.pet.testutil;

import com.datavet.datavet.pet.domain.exception.PetValidationException;
import com.datavet.datavet.pet.domain.model.OwnerInfo;
import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.pet.domain.model.Sex;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PetTestDataBuilderTest {

    @Test
    @DisplayName("Should create a Pet with name")
    void createPet_shouldCreateAPetWithName() {
        String name = "morita";
        Pet pet = PetTestDataBuilder.aPetWithName(name);

        assertNotNull(pet);
        assertEquals(name, pet.getName());
    }

    @Test
    @DisplayName("Should create a Pet with specie")
    void createPet_shouldCreateAPetWithSpecie() {
        String specie = "perro";
        Pet pet = PetTestDataBuilder.aPetWithSpecie(specie);

        assertNotNull(pet);
        assertEquals("perro", pet.getSpecies());
    }

    @Test
    @DisplayName("Should create a Pet with breed")
    void createPet_shouldCreateAPetWithBreed() {
        String breed = "villera";
        Pet pet = PetTestDataBuilder.aPetWithBreed(breed);

        assertNotNull(pet);
        assertEquals("villera", pet.getBreed());
    }

    @Test
    @DisplayName("Should create a Pet with Sex")
    void createPet_shouldCreateAPetWithSex() {
        Sex sex = Sex.FEMALE;
        Pet pet = PetTestDataBuilder.aPetWithSex(sex);

        assertNotNull(pet);
        assertEquals(Sex.FEMALE, pet.getSex());
    }

    @Test
    @DisplayName("Should create a Pet with Birthdate")
    void createPet_shouldCreateAPetWithBirthdate() {
        LocalDate birth = LocalDate.of(2012, 06, 06);
        Pet pet = PetTestDataBuilder.aPetWithBirthdate(birth);

        assertNotNull(pet);
        assertEquals(birth, pet.getDateOfBirth());
    }

    @Test
    @DisplayName("Should create a Pet with ChipNumber")
    void createPet_shouldCreateAPetWithChipNumber() {
        String chip = "Esto_es_un_numero_de_chip";
        Pet pet = PetTestDataBuilder.aPetWithChip(chip);

        assertNotNull(pet);
        assertEquals(chip, pet.getChipNumber());
    }

    @Test
    @DisplayName("Should create a Pet with OwnerInfo")
    void createPet_shouldCreateAPetWithOwnerInfo() {
        OwnerInfo ownerInfo = OwnerInfo.from("Alejandra", "Talalla", new Phone("+34147852369"));
        Pet pet = PetTestDataBuilder.aPetWithOwner(ownerInfo);

        assertNotNull(pet);
        assertEquals(ownerInfo.getOwnerId(), pet.getOwner().getOwnerId());
        assertEquals(ownerInfo.getName(), pet.getOwner().getName());
        assertEquals(ownerInfo.getLastName(), pet.getOwner().getLastName());
        assertEquals(ownerInfo.getPhone(), pet.getOwner().getPhone());
    }
}