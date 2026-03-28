package com.datavet.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Respuesta del paso 1 del onboarding.
 * No devuelve tokens — el usuario aún no está activo.
 */
@Getter
@AllArgsConstructor
public class RegisterResponse {

    private String userId;
    private String clinicId;
    private String email;
    private String message;

    public static RegisterResponse of(String userId, String clinicId, String email) {
        return new RegisterResponse(
                userId,
                clinicId,
                email,
                "Registro completado. Revisa tu email para verificar tu cuenta."
        );
    }
}