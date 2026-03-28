package com.datavet.auth.domain.model;

/**
 * Roles del sistema.
 *
 * SUPER_ADMIN        — Administrador de la plataforma Datavet.
 *                      Sin clinicId. Acceso total a todas las clínicas.
 *                      No entra en el MVP pero el enum lo incluye.
 *
 * CLINIC_OWNER       — Dueño fundador de la clínica. Acceso total
 *                      dentro de su clínica. Solo 1 por clínica (MVP).
 *
 * CLINIC_ADMIN       — Administrador. Acceso a finanzas, RRHH
 *                      y creación de empleados.
 *
 * CLINIC_VETERINARIAN — Veterinario. Acceso a operaciones clínicas
 *                       (pacientes, registros médicos).
 *
 * CLINIC_STAFF       — Resto de empleados (recepción, baño, limpieza...).
 *                      Acceso limitado a operaciones básicas.
 */
public enum UserRole {
    SUPER_ADMIN,
    CLINIC_OWNER,
    CLINIC_ADMIN,
    CLINIC_VETERINARIAN,
    CLINIC_STAFF
}