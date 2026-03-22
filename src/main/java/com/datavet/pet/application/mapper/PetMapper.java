package com.datavet.pet.application.mapper;

import com.datavet.pet.application.dto.OwnerInfoDto;
import com.datavet.pet.application.dto.PetResponse;
import com.datavet.pet.domain.model.Pet;

import java.util.List;

public class PetMapper {

    private PetMapper() {}

    public static PetResponse toResponse(Pet pet) {
        OwnerInfoDto ownerDto = new OwnerInfoDto(
                pet.getOwner().getOwnerId(),
                pet.getOwner().getName(),
                pet.getOwner().getLastName(),
                pet.getOwner().getPhone().getValue()  // Phone VO → String
        );

        return new PetResponse(
                pet.getId(),
                pet.getClinicId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getSex(),
                pet.getDateOfBirth(),
                pet.getAgeInYears(),          // campo calculado del dominio
                pet.getChipNumber(),
                pet.getAvatarUrl(),
                ownerDto,
                pet.getCreatedAt(),
                pet.getUpdatedAt()
        );
    }

    public static List<PetResponse> toResponseList(List<Pet> pets) {
        return pets.stream()
                .map(PetMapper::toResponse)
                .toList();
    }
}