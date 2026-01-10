package com.datavet.datavet.clinic.infrastructure.adapter.output;

import com.datavet.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.datavet.clinic.infrastructure.persistence.repository.MongoClinicRepositoryAdapter;
import com.datavet.datavet.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClinicRepositoryAdapter implements ClinicRepositoryPort {

    private final MongoClinicRepositoryAdapter repository;

    private ClinicDocument toDocument(Clinic clinic) {
        return ClinicDocument.builder()
                .id(clinic.getClinicID())
                .name(clinic.getClinicName())
                .legalName(clinic.getLegalName())
                .legalNumber(clinic.getLegalNumber())
                .address(clinic.getAddress())
                .phone(clinic.getPhone())
                .email(clinic.getEmail())
                .logoUrl(clinic.getLogoUrl())
                .suscriptionStatus(clinic.getSuscriptionStatus())
                .build();
    }

    private Clinic toDomain(ClinicDocument clinicDoc) {
        return Clinic.builder()
                .clinicID(clinicDoc.getId())
                .clinicName(clinicDoc.getName())
                .legalName(clinicDoc.getLegalName())
                .legalNumber(clinicDoc.getLegalNumber())
                .address(clinicDoc.getAddress())
                .phone(clinicDoc.getPhone())
                .email(clinicDoc.getEmail())
                .logoUrl(clinicDoc.getLogoUrl())
                .suscriptionStatus(clinicDoc.getSuscriptionStatus())
                .createdAt(clinicDoc.getCreatedAt())
                .updatedAt(clinicDoc.getUpdatedAt())
                .build();
    }

    @Override
    public Clinic save(Clinic clinic) {
        return toDomain(repository.save(toDocument(clinic)));
    }

    @Override
    public Optional<Clinic> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Clinic> findAll() {
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
    public boolean existsByLegalNumber(String legalNumber) {
        return repository.existsByLegalNumber(legalNumber);
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, String id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByLegalNumberAndIdNot(String legalNumber, String id) {
        return repository.existsByLegalNumberAndIdNot(legalNumber, id);
    }
}