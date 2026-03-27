package com.datavet.auth.infrastructure.adapter.output;

import com.datavet.auth.application.port.out.RefreshTokenRepositoryPort;
import com.datavet.auth.infrastructure.persistence.document.RefreshTokenDocument;
import com.datavet.auth.infrastructure.persistence.repository.MongoRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final MongoRefreshTokenRepository repository;

    @Override
    public RefreshTokenDocument save(RefreshTokenDocument token) {
        return repository.save(token);
    }

    @Override
    public Optional<RefreshTokenDocument> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        repository.deleteByTokenHash(tokenHash);
    }

    @Override
    public boolean existsByTokenHash(String tokenHash) {
        return repository.existsByTokenHash(tokenHash);
    }
}