package com.datavet.pet.infrastructure.adapter.output;

import com.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.pet.domain.model.OwnerInfo;
import com.datavet.pet.domain.model.Pet;
import com.datavet.pet.infrastructure.persistence.document.OwnerInfoDocument;
import com.datavet.pet.infrastructure.persistence.document.PetDocument;
import com.datavet.pet.infrastructure.persistence.repository.MongoPetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PetRepositoryAdapter implements PetRepositoryPort {

    private final MongoPetRepository repository;

    private PetDocument toDocument(Pet pet) {
        return PetDocument.builder()
                .id(pet.getId())
                .clinicId(pet.getClinicId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .sex(pet.getSex())
                .dateOfBirth(pet.getDateOfBirth())
                .chipNumber(pet.getChipNumber())
                .avatarUrl(pet.getAvatarUrl())
                .owner(toOwnerInfoDocument(pet.getOwner()))
                .active(pet.isActive())
                .build();
    }

    private Pet toDomain(PetDocument doc) {
        OwnerInfo ownerInfo = new OwnerInfo(
                doc.getOwner().getOwnerId(),
                doc.getOwner().getName(),
                doc.getOwner().getLastName(),
                doc.getOwner().getPhone()
        );

        return Pet.reconstitute(
                doc.getId(),
                doc.getClinicId(),
                doc.getName(),
                doc.getSpecies(),
                doc.getBreed(),
                doc.getSex(),
                doc.getDateOfBirth(),
                doc.getChipNumber(),
                doc.getAvatarUrl(),
                ownerInfo,
                doc.getCreatedAt(),
                doc.getUpdatedAt(),
                doc.isActive());
    }

    private OwnerInfoDocument toOwnerInfoDocument(OwnerInfo ownerInfo) {
        return OwnerInfoDocument.builder()
                .ownerId(ownerInfo.getOwnerId())
                .name(ownerInfo.getName())
                .lastName(ownerInfo.getLastName())
                .phone(ownerInfo.getPhone())
                .build();
    }

    private OwnerInfo toOwnerInfo(OwnerInfoDocument doc) {
        if (doc == null) return null;
        return OwnerInfo.create(doc.getOwnerId(), doc.getName(), doc.getLastName(), doc.getPhone());
    }

    @Override
    public List<Pet> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pet> findByClinicIdAndActiveTrue(String clinicId) {
        return repository.findByClinicIdAndActiveTrue(clinicId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pet> findByOwnerId(String ownerId) {
        return repository.findByOwnerOwnerId(ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Pet> findByOwnerIdAndActiveTrue(String ownerId) {
        return repository.findByOwnerOwnerIdAndActiveTrue(ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Pet> findByChipNumber(String chipNumber) {
        return repository.findByChipNumber(chipNumber).map(this::toDomain);
    }

    @Override
    public boolean existsByChipNumber(String chipNumber) {
        return repository.existsByChipNumber(chipNumber);
    }

    @Override
    public boolean existsByNumberAndIdNot(String chipNumber, String petId) {
        return repository.existsByChipNumberAndIdNot(chipNumber, petId);
    }

    @Override
    public Pet save(Pet entity) {
        return toDomain(repository.save(toDocument(entity)));
    }

    @Override
    public Optional<Pet> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pet> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
