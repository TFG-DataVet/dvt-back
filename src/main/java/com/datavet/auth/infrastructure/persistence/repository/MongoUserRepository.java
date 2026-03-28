package com.datavet.auth.infrastructure.persistence.repository;

import com.datavet.auth.infrastructure.persistence.document.UserDocument;
import com.datavet.shared.domain.valueobject.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmail                      (String email);
    boolean                existsByEmail                    (String email);
    boolean                existsByEmailAndIdNot            (String email, String id);
    Optional<UserDocument> findByEmailVerificationToken     (String token);
}