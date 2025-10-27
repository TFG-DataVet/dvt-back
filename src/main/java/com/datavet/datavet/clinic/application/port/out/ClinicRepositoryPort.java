package com.datavet.datavet.clinic.application.port.out;

import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.application.port.Repository;
import com.datavet.datavet.shared.domain.valueobject.Email;

public interface ClinicRepositoryPort extends Repository<Clinic, Long> {
    // Domain-specific methods preserved
    boolean existsByEmail(Email email);
    boolean existsByLegalNumber(String legalNumber);
    boolean existsByEmailAndIdNot(Email email, Long id);
    boolean existsByLegalNumberAndIdNot(String legalNumber, Long id);
}