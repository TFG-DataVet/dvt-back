package com.datavet.auth.application.dto;

import com.datavet.auth.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Respuesta del login y del refresh token.
 * Contiene el access token, el refresh token
 * y los datos del usuario autenticado.
 */
@Getter
@AllArgsConstructor
public class TokenResponse {

    private String   accessToken;
    private String   refreshToken;
    private String   tokenType;      // siempre "Bearer"
    private long     expiresIn;      // segundos hasta que expira el access token
    private UserInfo user;
    private String nextStep;


    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private String   userId;
        private String   employeeId;
        private String   clinicId;
        private String   email;
        private UserRole role;
    }

    public static TokenResponse of(String accessToken, String refreshToken,
                                   long expiresIn, UserInfo user, String nextStep) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn, user, nextStep);
    }
}