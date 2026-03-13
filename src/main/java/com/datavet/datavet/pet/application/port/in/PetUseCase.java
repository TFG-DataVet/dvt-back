package com.datavet.datavet.pet.application.port.in;

import com.datavet.datavet.pet.application.port.in.command.owner.*;
import com.datavet.datavet.pet.application.port.in.command.pet.*;
import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.shared.application.port.UseCase;

import java.util.List;

public interface PetUseCase extends UseCase {

    // --- Ciclo de vida -------------------------------------------------------

    Pet createPet       (CreatePetCommand command);
    Pet updatePet       (UpdatePetCommand command);
    void deactivatePet  (DeactivatePetCommand command);
    Pet activatePet     (String petId);

    // --- Correcciones clínicas -----------------------------------------------

    Pet correctBreed    (CorrectPetBreedCommand command);
    Pet correctBirthDate(CorrectPetBirthDateCommand command);
    Pet correctSex      (CorrectPetSexCommand command);

    // --- Dueño embebido ------------------------------------------------------

    Pet updateOwnerInfo (UpdatePetOwnerInfoCommand command);

    // --- Consultas -----------------------------------------------------------

    Pet         getPetById      (String petId);
    List<Pet>   getPetsByClinic (String clinicId);
    List<Pet>   getPetsByOwner  (String ownerId);

}
