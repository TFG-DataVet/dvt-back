package com.datavet.auth.application.port.out;

import com.datavet.auth.infrastructure.persistence.document.RefreshTokenDocument;

import java.util.Optional;

/**
 * Puerto de salida para la gestión de refresh tokens.
 * Los refresh tokens no son un agregado de dominio — son infraestructura
 * de seguridad, por eso su documento se maneja directamente aquí.
 */
public interface RefreshTokenRepositoryPort {

    RefreshTokenDocument save       (RefreshTokenDocument token);
    Optional<RefreshTokenDocument> findByTokenHash (String tokenHash);
    void deleteByUserId             (String userId);
    void deleteByTokenHash          (String tokenHash);
    boolean existsByTokenHash       (String tokenHash);
}