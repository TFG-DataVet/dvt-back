package com.datavet.datavet.owner.infrastructure.adapter.output;

import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.persistence.OwnerEntity;
import com.datavet.datavet.owner.infrastructure.persistence.repository.JpaOwnerRepositoryAdapter;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepositoryPort {

    private final JpaOwnerRepositoryAdapter repository;

    private OwnerEntity toEntity(Owner owner){
        return OwnerEntity.builder()
                .ownerId(owner.getClinicID())
                .firstName(owner.getOwnerName())
                .lastName(owner.getOwnerLastName())
                .dni(owner.getOwnerDni())
                .phone(owner.getOwnerPhone())
                .email(owner.getOwnerEmail())
                .address(owner.getOwnerAddress())
                .build();
    }

    private Owner toDomain(OwnerEntity ownerEntity){
        return Owner.builder()
                .ownerID(ownerEntity.getOwnerId())
                .ownerName(ownerEntity.getFirstName())
                .ownerDni(ownerEntity.getDni())
                .ownerPhone(ownerEntity.getPhone())
                .ownerEmail(ownerEntity.getEmail())
                .ownerAddress(ownerEntity.getAddress())
                .build();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByDni(String dni) {
        return false;
    }

    @Override
    public boolean existsByPhone(Phone phone) {
        return false;
    }

    @Override
    public boolean existsByLegalNumberAndIdNot(String legalNumber, Long id) {
        return false;
    }

    @Override
    public Owner save(Owner entity) {
        return null;
    }

    @Override
    public Optional<Owner> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Owner> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }
}
