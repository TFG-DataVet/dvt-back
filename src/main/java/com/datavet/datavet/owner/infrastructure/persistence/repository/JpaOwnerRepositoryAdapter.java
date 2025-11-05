package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface JpaOwnerRepositoryAdapter
        extends JpaRepository<OwnerEntity, Long>, Repository<OwnerEntity, Long> {

    boolean existsByEmail(Email email);
    boolean existsByDni(String dni);
    boolean existsByEmailAndDni(Email email, String dni);
    boolean existsByPhone(Phone phone);
    boolean existsByDniAndOwnerIDNot(String dni, Long ownerID);
}
