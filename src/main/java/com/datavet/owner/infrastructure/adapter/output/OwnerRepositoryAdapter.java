package com.datavet.owner.infrastructure.adapter.output;

import com.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.owner.domain.model.Owner;
import com.datavet.owner.infrastructure.persistence.document.OwnerDocument;
import com.datavet.owner.infrastructure.persistence.repository.MongoOwnerRepositoryAdapter;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepositoryPort {

    private final MongoOwnerRepositoryAdapter repository;

    // domain → document
    private OwnerDocument toDocument(Owner owner) {
        return OwnerDocument.builder()
                .id(owner.getOwnerId())
                .clinicId(owner.getClinicId())
                .firstName(owner.getName())
                .lastName(owner.getLastName())
                .documentType(owner.getDocumentNumber().getDocumentType())
                .documentNumber(owner.getDocumentNumber().getDocumentNumber())
                .phone(owner.getPhone().getValue())
                .email(owner.getEmail().toString())
                .address(owner.getAddress().getStreet())
                .city(owner.getAddress().getCity())
                .postalCode(owner.getAddress().getPostalCode())
                .petIds(owner.getPetIds())
                .avatarUrl(owner.getAvatarUrl())
                .active(owner.isActive())
                .acceptTermsAndCond(owner.isAcceptTermsAndCond())
                .build();
    }

    // document → domain
    private Owner toDomain(OwnerDocument doc) {
        return Owner.reconstitute(
                doc.getId(),
                doc.getClinicId(),
                doc.getFirstName(),
                doc.getLastName(),
                DocumentId.of(doc.getDocumentType(), doc.getDocumentNumber()),
                new Phone(doc.getPhone()),
                new Email(doc.getEmail()),
                new Address(doc.getAddress(), doc.getCity(), doc.getPostalCode()),
                doc.getPetIds(),
                doc.getAvatarUrl(),
                doc.isActive(),
                doc.isAcceptTermsAndCond(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
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
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumentNumber(String dni) {
        return repository.existsByDocumentNumber(dni);
    }

    @Override
    public boolean existsByPhone(Phone phone) {
        return repository.existsByPhone(phone);
    }

    @Override
    public boolean existsByDniAndOwnerIdNot(String dni, String id) {
        return repository.existsByDocumentNumberAndIdNot(dni, id);
    }

    @Override
    public Optional<Owner> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

}
