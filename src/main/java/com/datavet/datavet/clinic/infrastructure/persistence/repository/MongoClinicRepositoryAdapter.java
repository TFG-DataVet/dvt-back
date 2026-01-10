package com.datavet.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.datavet.shared.domain.valueobject.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoClinicRepositoryAdapter
        extends MongoRepository<ClinicDocument, String> {
    
    boolean existsByEmail(Email email);
    boolean existsByLegalNumber(String legalNumber);
    boolean existsByEmailAndIdNot(Email email, String id);
    boolean existsByLegalNumberAndIdNot(String legalNumber, String id);
}