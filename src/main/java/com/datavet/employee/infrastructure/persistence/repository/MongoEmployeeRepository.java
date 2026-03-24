package com.datavet.employee.infrastructure.persistence.repository;

import com.datavet.employee.infrastructure.persistence.document.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoEmployeeRepository extends MongoRepository<EmployeeDocument, String> {

    List<EmployeeDocument>     findByClinicId             (String clinicId);
    List<EmployeeDocument>     findByClinicIdAndActiveTrue (String clinicId);
    Optional<EmployeeDocument> findByUserId               (String userId);

    boolean existsByDocumentNumberAndClinicId(String documentNumber, String clinicId);

    boolean existsByDocumentNumberAndClinicIdAndIdNot(String documentNumber,
                                                      String clinicId,
                                                      String id);
}