package com.datavet.datavet.owner.infrastructure.adapter.output;

import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.persistence.document.OwnerDocument;
import com.datavet.datavet.owner.infrastructure.persistence.repository.MongoOwnerRepositoryAdapter;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepositoryPort {

    private final MongoOwnerRepositoryAdapter repository;

    private OwnerDocument toDocument(Owner owner){
        return OwnerDocument.builder()
                .id(owner.getId())
                .clinicId(owner.getClinicId())
                .firstName(owner.getName())
                .lastName(owner.getLastName())
                .dni(owner.getDocumentNumber())
                .phone(owner.getPhone())
                .email(owner.getEmail())
                .address(owner.getAddress())
                .build();
    }

    private Owner toDomain(OwnerDocument document){
        return Owner.builder()
                .id(document.getId())
                .clinicId(document.getClinicId())
                .lastName(document.getLastName())
                .name(document.getFirstName())
                .documentNumber(document.getDni())
                .phone(document.getPhone())
                .email(document.getEmail())
                .address(document.getAddress())
                .build();
    }

    @Override
    public Owner save(Owner owner) {
        return toDomain(repository.save(toDocument(owner)));
    }

    @Override
    public Optional<Owner> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Owner> findAll() {
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
    public boolean existsByDniAndOwnerIdNot(String dni, String id) {
        return repository.existsByDniAndIdNot(dni, id);
    }

}
