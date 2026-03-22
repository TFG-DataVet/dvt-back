package com.datavet.datavet.pet.infrastructure.persistence.repository;

import com.datavet.datavet.pet.infrastructure.persistence.document.PetDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoPetRepository extends MongoRepository<PetDocument, String> {

    List<PetDocument> findByClinicId(String clinicId);

    List<PetDocument> findByClinicIdAndActiveTrue(String clinicId);

    List<PetDocument> findByOwnerOwnerId(String ownerId);

    List<PetDocument> findByOwnerOwnerIdAndActiveTrue(String ownerId);

    Optional<PetDocument> findByChipNumber(String chipNumber);

    boolean existsByChipNumber(String chipNumber);

    boolean existsByChipNumberAndIdNot(String chipNumber, String id);
}
