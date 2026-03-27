package com.datavet.auth.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * Command para el paso 2 del onboarding.
 * Verifica el token de email y transiciona el User
 * a PENDING_CLINIC_SETUP.
 */
@Value
@Builder
public class VerifyEmailCommand {
    String token;
}