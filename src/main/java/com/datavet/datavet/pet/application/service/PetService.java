package com.datavet.datavet.pet.application.service;

import com.datavet.datavet.pet.application.port.in.PetUseCase;
import com.datavet.datavet.pet.application.port.in.command.owner.UpdatePetOwnerInfoCommand;
import com.datavet.datavet.pet.application.port.in.command.pet.*;
import com.datavet.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.datavet.pet.domain.exception.PetAlreadyExistsException;
import com.datavet.datavet.pet.domain.exception.PetNotFoundException;
import com.datavet.datavet.pet.domain.model.OwnerInfo;
import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService implements PetUseCase, ApplicationService {

    private final PetRepositoryPort          petRepositoryPort;
    private final DomainEventPublisher       domainEventPublisher;

    @Override
    @Transactional
    public Pet createPet(CreatePetCommand command) {

        // 1. VErificamos que no exista una mascota el numero de chip
        if (command.getChipNumber() != null
                && petRepositoryPort.existsByChipNumber(command.getChipNumber())) {
            throw new PetAlreadyExistsException("chipNumber", command.getChipNumber());
        }

        // 2. Construir OwnerInfo
        OwnerInfo ownerInfo = OwnerInfo.create(
                command.getOwnerId(),
                command.getOwnerName(),
                command.getOwnerLastName(),
                command.getOwnerPhone()
        );

        // 3. Creamos la Pet
        Pet pet = Pet.create(
                command.getClinicId(),
                command.getName(),
                command.getSpecies(),
                command.getBreed(),
                command.getSex(),
                command.getDateOfBirth(),
                command.getChipNumber(),
                command.getAvatarUrl(),
                ownerInfo
        );

        // 5. Publicar eventos ANTES de persistir
        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet updatePet(UpdatePetCommand command) {

        // 1. Verificar existencia
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        // 2. Delegar al agregado
        pet.update(command.getPetId(), command.getName(), command.getAvatarUrl());

        // 3. Publicar eventos y persistir
        publishDomainEvents(pet);

        // 4. Guafamos y devolvemos
        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public void deactivatePet(DeactivatePetCommand command) {

        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        pet.deactivate(command.getPetId(), command.getReason());

        publishDomainEvents(pet);

        petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet activatePet(String petId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));

        pet.activate(petId);

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    // -------------------------------------------------------------------------
    // Correcciones clínicas
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Pet correctBreed(CorrectPetBreedCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        // El dominio valida que la nueva raza no sea igual a la actual y que reason no sea nulo
        pet.correctBreed(command.getPetId(), command.getNewBreed(), command.getReason());

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet correctBirthDate(CorrectPetBirthDateCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        pet.correctBirthDate(command.getPetId(), command.getNewBirthDate(), command.getReason());

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet correctSex(CorrectPetSexCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        pet.correctSex(command.getPetId(), command.getSex(), command.getReason());

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    // -------------------------------------------------------------------------
    // Dueño embebido
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Pet updateOwnerInfo(UpdatePetOwnerInfoCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));

        OwnerInfo newOwnerInfo = OwnerInfo.create(
                command.getOwnerId(),
                command.getOwnerName(),
                command.getOwnerLastName(),
                command.getOwnerPhone()
        );

        pet.updateOwnerInfo(command.getOwnerId(), newOwnerInfo);

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    @Override
    public Pet getPetById(String petId) {
        return petRepositoryPort.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));
    }

    @Override
    public List<Pet> getPetsByClinic(String clinicId) {
        return petRepositoryPort.findByClinicId(clinicId);
    }

    @Override
    public List<Pet> getPetsByOwner(String ownerId) {
        return petRepositoryPort.findByOwnerId(ownerId);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private void publishDomainEvents(Pet pet) {
        List<DomainEvent> events = pet.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        pet.clearDomainEvents();
    }
}