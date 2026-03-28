package com.datavet.auth.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades JWT leídas desde application.properties.
 *
 * datavet.jwt.secret              — clave secreta para firmar los tokens
 * datavet.jwt.access-token-expiry — expiración del access token en segundos
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "datavet.jwt")
public class JwtProperties {

    private String secret;
    private long   accessTokenExpiry = 3600; // 1 hora por defecto
}