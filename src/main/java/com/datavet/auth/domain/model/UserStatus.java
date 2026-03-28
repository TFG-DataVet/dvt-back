package com.datavet.auth.domain.model;

/**
 * Estado del ciclo de vida de un User.
 *
 * PENDING_EMAIL_VERIFICATION — recién registrado, pendiente de verificar email.
 * PENDING_CLINIC_SETUP       — email verificado, pendiente de completar datos clínica.
 * ACTIVE                     — operativo, puede autenticarse con normalidad.
 * INACTIVE                   — desactivado (despido, baja voluntaria...).
 *                              Sus datos se conservan íntegramente.
 */
public enum UserStatus {
    PENDING_EMAIL_VERIFICATION,
    PENDING_CLINIC_SETUP,
    ACTIVE,
    INACTIVE
}