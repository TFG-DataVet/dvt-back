package com.datavet.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.shared.application.port.Repository;
import com.datavet.datavet.shared.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClinicRepositoryAdapter
        extends JpaRepository<ClinicEntity, Long>, Repository<ClinicEntity, Long> {
    
    boolean existsByEmail(Email email);
    boolean existsByLegalNumber(String legalNumber);
    boolean existsByEmailAndClinicIdNot(Email email, Long clinicId);
    boolean existsByLegalNumberAndClinicIdNot(String legalNumber, Long clinicId);
}