package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet extends AggregateRoot<String> implements Document<String> {

    private String id;

    private String clinicId;

    private String name;

    private String species;

    private String breed;

    private String sex;

    private LocalDate dateOfBirth;

    private String chipNumber;

    private Double currentWeight;

    private String avatarUrl;

    private OwnerInfo onwer;

    private MedicalRecord medicalRecords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean active;
    @Override
    public String getId() { return this.id; }

    public static Pet create(
            String id,
            String clinicId,
            String name,
            String species,
            String breed,
            String sex,
            LocalDate birthday,
            String chipNumber,
            Double currentWeight,
            String avatarUrl,
            OwnerInfo owner) {

        Pet pet = Pet.builder()
                .id(id)
                .clinicId(clinicId)
                .name(name)
                .species(species)
                .breed(breed)
                .sex(sex)
                .dateOfBirth(birthday)
                .chipNumber(chipNumber)
                .currentWeight(currentWeight)
                .avatarUrl(avatarUrl)
                .onwer(owner)
                .build();

//        Falta agregar el evento de crear dominio de pet
//        pet.addDomainEvent(PetCreatedEvent.of(id, name));
        return pet;
    }

    public void update(
            String name,
            String species,
            String breed,
            String sex,
            LocalDate dateOfBirth,
            String avatarUrl) {

        this.name = name;
        this.species = species;
        this.breed = breed;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.avatarUrl = avatarUrl;
    }

    public void updateOnwerInfo(OwnerInfo newOwnerInfo) {
        if (newOwnerInfo == null) {
            throw  new IllegalArgumentException("Owner cannot be null");
        }

        this.onwer = newOwnerInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public void desactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public int getAgeInYears() {
        if (this.dateOfBirth == null) {
            return 0;
        }

        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }
}