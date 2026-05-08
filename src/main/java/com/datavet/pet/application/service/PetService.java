package com.datavet.pet.application.service;

import com.datavet.pet.application.port.in.PetUseCase;
import com.datavet.pet.application.port.in.command.owner.UpdatePetOwnerInfoCommand;
import com.datavet.pet.application.port.in.command.pet.*;
import com.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.pet.domain.exception.PetAlreadyExistsException;
import com.datavet.pet.domain.exception.PetNotFoundException;
import com.datavet.pet.domain.model.OwnerInfo;
import com.datavet.pet.domain.model.Pet;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

        // 1. Verificamos que no exista una mascota con el numero de chip
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

        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

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
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

        pet.deactivate(command.getPetId(), command.getReason());

        publishDomainEvents(pet);

        petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet activatePet(String petId, String clinicId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));
        if (!pet.getClinicId().equals(clinicId)) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

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
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

        pet.correctBreed(command.getPetId(), command.getNewBreed(), command.getReason());

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet correctBirthDate(CorrectPetBirthDateCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

        pet.correctBirthDate(command.getPetId(), command.getNewBirthDate(), command.getReason());

        publishDomainEvents(pet);

        return petRepositoryPort.save(pet);
    }

    @Override
    @Transactional
    public Pet correctSex(CorrectPetSexCommand command) {
        Pet pet = petRepositoryPort.findById(command.getPetId())
                .orElseThrow(() -> new PetNotFoundException(command.getPetId()));
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

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
        if (!pet.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }

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
    public Pet getPetById(String petId, String clinicId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));
        if (!pet.getClinicId().equals(clinicId)) {
            throw new AccessDeniedException("La mascota no pertenece a tu clínica");
        }
        return pet;
    }

    @Override
    public List<Pet> getPetsByClinic(String clinicId) {
        return petRepositoryPort.findByClinicId(clinicId);
    }

    @Override
    public List<Pet> getPetsByOwner(String ownerId, String clinicId) {
        return petRepositoryPort.findByOwnerId(ownerId).stream()
                .filter(pet -> pet.getClinicId().equals(clinicId))
                .toList();
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