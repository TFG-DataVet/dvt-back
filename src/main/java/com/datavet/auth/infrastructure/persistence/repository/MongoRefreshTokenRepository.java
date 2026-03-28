package com.datavet.auth.infrastructure.persistence.repository;

import com.datavet.auth.infrastructure.persistence.document.RefreshTokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoRefreshTokenRepository
        extends MongoRepository<RefreshTokenDocument, String> {

    Optional<RefreshTokenDocument> findByTokenHash  (String tokenHash);
    void                           deleteByUserId   (String userId);
    void                           deleteByTokenHash(String tokenHash);
    boolean                        existsByTokenHash(String tokenHash);
}