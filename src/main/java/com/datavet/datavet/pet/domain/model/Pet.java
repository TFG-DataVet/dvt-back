package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.event.pet.*;
import com.datavet.datavet.pet.domain.exception.PetValidationException;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends AggregateRoot<String> implements Document<String> {

    private String id;
    private String clinicId;
    private String name;
    private String species;
    private String breed;
    private Sex sex;
    private LocalDate dateOfBirth;
    private String chipNumber;
    private String avatarUrl;
    private OwnerInfo owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public void validate(){
        ValidationResult result = new ValidationResult();

        if(clinicId == null || clinicId.isBlank()){
            result.addError("[ClinicId]", "El id de la clinica no debe ser nulo o estar vacio.");
        }

        if (name == null || name.isBlank()) {
            result.addError("[Name]", "El nombre de la mascota no debe ser nulo o estar vacio.");
        }

        if (species == null || species.isBlank()){
            result.addError("[Specie]", "La especie no debe ser nula o estar vacia.");
        }

        if (breed == null || breed.isBlank()) {
            result.addError("[Breed]","La raza no debe ser nula o estar vacia.");
        }

        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            result.addError("[Birthday]", "La fecha de nacimiento de la mascota no debe ser nula o estar vacia.");
        }

        if (owner == null) {
            result.addError("[Owner]", "Los datos del dueño de la mascota no deben ser nulos o estar vacios.");
        }

        if(result.hasErrors()) {
            throw new PetValidationException(result);
        }
    }

    @Override
    public String getId() { return this.id; }

    public static Pet create(
            String clinicId,
            String name,
            String species,
            String breed,
            Sex sex,
            LocalDate birthday,
            String chipNumber,
            String avatarUrl,
            OwnerInfo owner) {
        String uuid = UUID.randomUUID().toString();

        Pet pet = new Pet(uuid,
                clinicId,
                name,
                species,
                breed,
                sex,
                birthday,
                chipNumber,
                avatarUrl,
                owner,
                LocalDateTime.now(),
                null,
                true);

        pet.validate();
        pet.addDomainEvent(PetCreatedEvent.of(uuid, name, clinicId, owner, chipNumber));
        return pet;
    }

    public void update(String id, String name, String avatarUrl) {
        if(this.name.equals(name)) return;

        ValidationResult result = new ValidationResult();

        if (name == null || name.isBlank()) {
            result.addError("[Name]","El nuevo nombre de la mascota no puede ser nulo o estar vacio");
        }

        if (avatarUrl == null || avatarUrl.isBlank()){
            result.addError("[AvatarURL]", "La nueva imagen de la mascota no puede ser nula o estar vacia.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        String previousName = this.name;

        this.name = name;
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
        validate();

        addDomainEvent(PetUpdateEvent.of(id, previousName, name));
    }

    public void correctBreed(String id, String newBreed, String reason) {
        if(this.breed.equals(newBreed)) return;

        ValidationResult result = new ValidationResult();

        if (newBreed == null || newBreed.isBlank()) {
            result.addError("[correctBreed - NewBreed]", "La raza no puede ser nula o vacía.");
        }

        if (reason == null || reason.isBlank()) {
            result.addError("[correctBreed - Reason]", "Debe indicar un motivo de corrección.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        String previousBreed = this.breed;

        this.breed = newBreed;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetBreedCorrectedEvent.of(id, previousBreed, this.breed, reason));
    }

    public void correctBirthDate(String id, LocalDate newBirthDate, String reason) {
        ValidationResult result = new ValidationResult();

        if (newBirthDate == null || newBirthDate.isAfter(LocalDate.now())) {
            result.addError("[correctBirthDate - NewBirthDate]", "La fecha de nacimiento no puede ser nula o ser despues de hoy.");
        }

        if (reason == null || reason.isBlank()) {
            result.addError("[correctBirthDate - Reason]", "Debe indicar un motivo de corrección.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        if(this.dateOfBirth.equals(newBirthDate)) return;


        LocalDate previousBirthDate = this.dateOfBirth;

        this.dateOfBirth = newBirthDate;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetBirthDateCorrectedEvent.of(id, previousBirthDate, this.dateOfBirth, reason));
    }

    public void correctSex(String id, Sex newSex, String reason) {
        if(this.sex.equals(newSex)) return;

        ValidationResult result = new ValidationResult();

        if (newSex == null) {
            result.addError("[correctSex - NewSex]", "La sexo de la mascota no puede ser nulo o estar vacio.");
        }

        if (reason == null || reason.isBlank()) {
            result.addError("[correctSex - Reason]", "Debe indicar un motivo de corrección.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        Sex previousSex = this.sex;

        this.sex = newSex;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetSexCorrectedEvent.of(id, previousSex, this.sex, reason));
    }

    public void updateOwnerInfo(String id, OwnerInfo newOwnerInfo) {
        ValidationResult result = new ValidationResult();

        if (newOwnerInfo == null) {
            result.addError("[updateOwnerInfo - OwnerInfo]","Owner cannot be null");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        OwnerInfo previousOwnerInfo = this.owner;

        this.owner = newOwnerInfo;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(PetUpdateOwnerInfoEvent.of(id, previousOwnerInfo , owner));
    }

    public void deactivate(String id, String reason) {
        ValidationResult result = new ValidationResult();

        if (!this.active) {
            result.addError("[desactive - isActive]", "No se puede desactivar una mascota que ya esta desactivada.");
        }

        if (reason == null) {
            result.addError("[desactive - Reason]", "la razón por del cambio de estado no puede estar vacia.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        this.active = false;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetDeactivatedEvent.of(id, reason));
    }

    public void activate(String id) {
        ValidationResult result = new ValidationResult();

        if (this.active){
            result.addError("[activate - isActive]", "No puede reactivarse una mascota que ya esta activa.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }

        this.active = true;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetActivateEvent.of(id));
    }

    public int getAgeInYears() {
        if (this.dateOfBirth == null) {
            return 0;
        }

        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }
}

