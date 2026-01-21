package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.event.pet.*;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Getter
@Builder
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

        if(clinicId == null || clinicId.isBlank()){
            throw new IllegalArgumentException("El ide de la clinica no debe ser nulo o estar vacio.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la mascota no debe ser nulo o estar vacio.");
        }

        if (species == null || species.isBlank()){
            throw new IllegalArgumentException("La especie no debe ser nulq o estar vacia.");
        }

        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("La especie no debe ser nula o estar vacia.");
        }

        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento de la mascota no debe ser nula o estar vacia.");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Los datos del dueño de la mascota no deben ser nulos o estar vacios.");
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

        Pet pet = Pet.builder()
                .id(uuid)
                .clinicId(clinicId)
                .name(name)
                .species(species)
                .breed(breed)
                .sex(sex)
                .dateOfBirth(birthday)
                .chipNumber(chipNumber)
                .avatarUrl(avatarUrl)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        pet.validate();

        pet.addDomainEvent(PetCreatedEvent.of(uuid, name, clinicId, owner, chipNumber));
        return pet;
    }

    public void update(String id, String name, String avatarUrl) {
        if(this.name.equals(name)) return;

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nuevo nombre de la mascota no puede ser nulo o estar vacio");
        }

        if (avatarUrl == null || avatarUrl.isBlank()){
            throw new IllegalArgumentException("La nueva imagen de la mascota no puede ser nula o estar vacia.");
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

        if (newBreed == null || newBreed.isBlank()) {
            throw new IllegalArgumentException("La raza no puede ser nula o vacía.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Debe indicar un motivo de corrección.");
        }

        String previousBreed = this.breed;

        this.breed = newBreed;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetBreedCorrectedEvent.of(id, previousBreed, this.breed, reason));
    }

    public void correctBirthDate(String id, LocalDate newBirthDate, String reason) {
        if(this.dateOfBirth.equals(newBirthDate)) return;

        if (newBirthDate == null || newBirthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula o ser despues de hoy.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Debe indicar un motivo de corrección.");
        }

        LocalDate previousBirthDate = this.dateOfBirth;

        this.dateOfBirth = newBirthDate;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetBirthDateCorrectedEvent.of(id, previousBirthDate, this.dateOfBirth, reason));
    }

    public void correctSex(String id, Sex newSex, String reason) {
        if(this.sex.equals(newSex)) return;

        if (newSex == null) {
            throw new IllegalArgumentException("La sexo de la mascota no puede ser nulo o estar vacio.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Debe indicar un motivo de corrección.");
        }

        Sex previousSex = this.sex;

        this.sex = newSex;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetSexCorrectedEvent.of(id, previousSex, this.sex, reason));
    }

    public void updateOwnerInfo(String id, OwnerInfo newOwnerInfo) {
        if (newOwnerInfo == null) {
            throw  new IllegalArgumentException("Owner cannot be null");
        }

        OwnerInfo previousOwnerInfo = this.owner;

        this.owner = newOwnerInfo;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(PetUpdateOwnerInfoEvent.of(id, previousOwnerInfo , owner));
    }

    public void deactivate(String id, String reason) {
        this.active = false;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(PetDeactivatedEvent.of(id, reason));
    }

    public void activate(String id) {
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

