package com.datavet.datavet.clinic.infrastructure.adapter.output;

import com.datavet.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.clinic.infrastructure.persistence.repository.JpaClinicRepositoryAdapter;
import com.datavet.datavet.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClinicRepositoryAdapter implements ClinicRepositoryPort {

    private final JpaClinicRepositoryAdapter repository;

    private ClinicEntity toEntity(Clinic clinic) {
        return ClinicEntity.builder()
                .clinicId(clinic.getClinicID())
                .clinicName(clinic.getClinicName())
                .legalName(clinic.getLegalName())
                .legalNumber(clinic.getLegalNumber())
                .address(clinic.getAddress())
                .phone(clinic.getPhone())
                .email(clinic.getEmail())
                .logoUrl(clinic.getLogoUrl())
                .suscriptionStatus(clinic.getSuscriptionStatus())
                .build();
    }

    private Clinic toDomain(ClinicEntity entity) {
        return Clinic.builder()
                .clinicID(entity.getClinicId())
                .clinicName(entity.getClinicName())
                .legalName(entity.getLegalName())
                .legalNumber(entity.getLegalNumber())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .logoUrl(entity.getLogoUrl())
                .suscriptionStatus(entity.getSuscriptionStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Clinic save(Clinic clinic) {
        return toDomain(repository.save(toEntity(clinic)));
    }

    @Override
    public Optional<Clinic> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Clinic> findAll() {
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
    public boolean existsByLegalNumber(String legalNumber) {
        return repository.existsByLegalNumber(legalNumber);
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, Long id) {
        return repository.existsByEmailAndClinicIdNot(email, id);
    }

    @Override
    public boolean existsByLegalNumberAndIdNot(String legalNumber, Long id) {
        return repository.existsByLegalNumberAndClinicIdNot(legalNumber, id);
    }
}