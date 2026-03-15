package com.datavet.datavet.pet.application.port.out;

import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.shared.application.port.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface PetRepositoryPort extends Repository<Pet, String> {

    // Consultas por CLinic
    List<Pet> findByClinicId(String clinicId);

    List<Pet> findByClinicIdAndActiveTrue(String clinic);

    // Consulta por Owner
    List<Pet> findByOwnerId(String ownerId);

    List<Pet> findByOwnerIdAndActiveTrue(String ownerId);

    // Consulta por atributos
    Optional<Pet> findByChipNumber(String chipNumber);

    boolean existsByChipNumber(String chipNumber);

    boolean existsByNumberAndIdNot(String chipNumber, String petId);



}
