package com.datavet.clinic.domain.model;

/**
 * Estado del ciclo de vida de una clínica.
 *
 * PENDING_SETUP   — recién creada en el onboarding, sin datos completos.
 * ACTIVE          — operativa y completamente configurada.
 * SUSPENDED       — suspendida temporalmente por Datavet (ej: impago).
 * DEACTIVATED     — desactivada por el propio dueño o por Datavet.
 *                   Soft-delete: los datos se conservan íntegramente.
 */
public enum ClinicStatus {
    PENDING_SETUP,
    ACTIVE,
    SUSPENDED,
    DEACTIVATED
}