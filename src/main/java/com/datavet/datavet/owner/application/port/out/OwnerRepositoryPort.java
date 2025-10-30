package com.datavet.datavet.owner.application.port.out;

import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.port.Repository;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;

public interface OwnerRepositoryPort extends Repository<Owner, Long> {
    //Domain-specific methods preserved
    boolean existsByEmail(Email email);
    boolean existsByDni(String dni);
    boolean existsByPhone(Phone phone);
    boolean existsByLegalNumberAndIdNot(String legalNumber, Long id);
}
