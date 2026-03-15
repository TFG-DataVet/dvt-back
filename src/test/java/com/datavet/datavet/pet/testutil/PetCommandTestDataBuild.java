package com.datavet.datavet.pet.testutil;

import com.datavet.datavet.pet.application.port.in.command.owner.UpdatePetOwnerInfoCommand;
import com.datavet.datavet.pet.application.port.in.command.pet.*;
import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.pet.domain.model.Sex;
import com.datavet.datavet.shared.domain.valueobject.Phone;

import java.time.LocalDate;

public class PetCommandTestDataBuild {

    // ── Defaults ────────────────────────────────────────────────────────────
    public static final String DEFAULT_PET_ID      = "pet-001";
    public static final String DEFAULT_CLINIC_ID   = "Clinic_id";
    public static final String DEFAULT_OWNER_ID    = "owner-001";
    public static final String DEFAULT_NAME        = "Zeus";
    public static final String DEFAULT_SPECIES     = "Perro";
    public static final String DEFAULT_BREED       = "Golden retriver";
    public static final Sex    DEFAULT_SEX         = Sex.MALE;
    public static final String DEFAULT_CHIP        = "Numero_de_chip";
    public static final String DEFAULT_AVATAR_URL  = "hEsto_seria_un_link";
    public static final LocalDate DEFAULT_DOB      = LocalDate.of(2020, 04, 05);

    // ── Commands ─────────────────────────────────────────────────────────────

    public static CreatePetCommand aValidCreatePetCommand() {
        return CreatePetCommand.builder()
                .clinicId(DEFAULT_CLINIC_ID)
                .name(DEFAULT_NAME)
                .species(DEFAULT_SPECIES)
                .breed(DEFAULT_BREED)
                .sex(DEFAULT_SEX)
                .dateOfBirth(DEFAULT_DOB)
                .chipNumber(DEFAULT_CHIP)
                .avatarUrl(DEFAULT_AVATAR_URL)
                .ownerId(DEFAULT_OWNER_ID)
                .ownerName("Carlos")
                .ownerLastName("Pérez")
                .ownerPhone(new Phone("+34612345678"))
                .build();
    }

    public static CreatePetCommand aCreatePetCommandWithoutChip() {
        return CreatePetCommand.builder()
                .clinicId(DEFAULT_CLINIC_ID)
                .name(DEFAULT_NAME)
                .species(DEFAULT_SPECIES)
                .breed(DEFAULT_BREED)
                .sex(DEFAULT_SEX)
                .ownerId(DEFAULT_OWNER_ID)
                .ownerName("Carlos")
                .ownerLastName("Pérez")
                .ownerPhone(new Phone("+34612345678"))
                .build();
    }

    public static UpdatePetCommand aValidUpdatePetCommand() {
        return UpdatePetCommand.builder()
                .petId(DEFAULT_PET_ID)
                .name("Firulais Actualizado")
                .avatarUrl("https://example.com/new.jpg")
                .build();
    }

    public static DeactivatePetCommand aValidDeactivatePetCommand() {
        return DeactivatePetCommand.builder()
                .petId(DEFAULT_PET_ID)
                .reason("Mascota fallecida")
                .build();
    }

    public static CorrectPetBreedCommand aValidCorrectBreedCommand() {
        return CorrectPetBreedCommand.builder()
                .petId(DEFAULT_PET_ID)
                .newBreed("Golden Retriever")
                .reason("Error en el registro inicial")
                .build();
    }

    public static CorrectPetBirthDateCommand aValidCorrectBirthDateCommand() {
        return CorrectPetBirthDateCommand.builder()
                .petId(DEFAULT_PET_ID)
                .newBirthDate(LocalDate.of(2019, 6, 10))
                .reason("Fecha incorrecta en historia clínica")
                .build();
    }

    public static CorrectPetSexCommand aValidCorrectSexCommand() {
        return CorrectPetSexCommand.builder()
                .petId(DEFAULT_PET_ID)
                .sex(Sex.FEMALE)
                .reason("Error administrativo")
                .build();
    }

    public static UpdatePetOwnerInfoCommand aValidUpdateOwnerInfoCommand() {
        return UpdatePetOwnerInfoCommand.builder()
                .petId(DEFAULT_PET_ID)
                .ownerId("owner-002")
                .ownerName("María")
                .ownerLastName("González")
                .ownerPhone(new Phone("+34699887766"))
                .build();
    }

    public static Pet aValidPet() {
        Pet pet = PetTestDataBuilder.aValidPet();
        return pet;
    }
}
