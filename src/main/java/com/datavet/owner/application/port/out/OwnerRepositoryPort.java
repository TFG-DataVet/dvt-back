package com.datavet.owner.application.port.out;

import com.datavet.owner.domain.model.Owner;
import com.datavet.shared.application.port.Repository;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import java.util.Optional;

public interface OwnerRepositoryPort extends Repository<Owner, String> {
    //Domain-specific methods preserved
    boolean existsByEmail(String email);
    boolean existsByDocumentNumber(String dni);
    boolean existsByPhone(Phone phone);
    boolean existsByDniAndOwnerIdNot(String legalNumber, String id);
    Optional<Owner> findByEmail(String email);
}
