package com.datavet.auth.infrastructure.util;

import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    // -------------------------------------------------------------------------
    // Generación
    // -------------------------------------------------------------------------

    public String generateAccessToken(String userId, String employeeId,
                                      String clinicId, String email,
                                      UserRole role) {
        long nowMillis = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userId)
                .claim("employeeId", employeeId)
                .claim("clinicId",   clinicId)
                .claim("email",      email)
                .claim("role",       role.name())
                .claim("scope",      "FULL_ACCESS")
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + jwtProperties.getAccessTokenExpiry() * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Token temporal con scope ONBOARDING_ONLY.
     * Usado entre el paso 2 y el paso 3 del onboarding.
     */
    public String generateOnboardingToken(String userId, String clinicId, String email, UserRole role) {
        long nowMillis = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userId)
                .claim("clinicId", clinicId)
                .claim("email", email)
                .claim("role", role)
                .claim("scope",    "ONBOARDING_ONLY")
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + 3600 * 1000)) // 1 hora
                .signWith(getSigningKey())
                .compact();
    }

    // -------------------------------------------------------------------------
    // Validación y extracción
    // -------------------------------------------------------------------------

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new com.datavet.auth.domain.exception.InvalidCredentialsException(
                    "Token inválido o expirado");
        }
    }

    public String extractUserId(String token) {
        return parseToken(token).getSubject();
    }

    public String extractClinicId(String token) {
        return parseToken(token).get("clinicId", String.class);
    }

    public String extractEmployeeId(String token) {
        return parseToken(token).get("employeeId", String.class);
    }

    public UserRole extractRole(String token) {
        String role = parseToken(token).get("role", String.class);
        return UserRole.valueOf(role);
    }

    public String extractScope(String token) {
        return parseToken(token).get("scope", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpiry();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}