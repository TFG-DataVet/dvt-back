package com.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.shared.domain.valueobject.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoClinicRepositoryAdapter
        extends MongoRepository<ClinicDocument, String> {
    
    boolean existsByEmail(String email);
    boolean existsByLegalNumber(String legalNumber);
    boolean existsByEmailAndIdNot(String email, String id);
    boolean existsByLegalNumberAndIdNot(String legalNumber, String id);
}