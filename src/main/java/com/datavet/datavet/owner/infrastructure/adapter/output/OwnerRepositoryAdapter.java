package com.datavet.datavet.owner.infrastructure.adapter.output;

import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
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
                .ownerID(owner.getOwnerID())
                .clinicID(owner.getClinicID())
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
                .ownerID(ownerEntity.getOwnerID())
                .clinicID(10L)
                .ownerLastName(ownerEntity.getLastName())
                .ownerName(ownerEntity.getFirstName())
                .ownerDni(ownerEntity.getDni())
                .ownerPhone(ownerEntity.getPhone())
                .ownerEmail(ownerEntity.getEmail())
                .ownerAddress(ownerEntity.getAddress())
                .build();
    }

    @Override
    public Owner save(Owner owner) {
        return toDomain(repository.save(toEntity(owner)));
    }

    @Override
    public Optional<Owner> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Owner> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByDni(String dni) {
        return repository.existsByDni(dni);
    }

    @Override
    public boolean existsByPhone(Phone phone) {
        return repository.existsByPhone(phone);
    }

    @Override
    public boolean existsByDniAndOwnerIdNot(String dni, Long id) {
        return repository.existsByDniAndOwnerIDNot(dni,id);
    }

}
