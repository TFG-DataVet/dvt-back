package com.datavet.auth.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * Command para el paso 1 del onboarding.
 * Crea la clínica en PENDING_SETUP y el User del CLINIC_OWNER
 * en PENDING_EMAIL_VERIFICATION.
 */
@Value
@Builder
public class RegisterClinicOwnerCommand {
    String clinicName;
    String firstName;
    String lastName;
    String email;
    String rawPassword;
    String phone;
}