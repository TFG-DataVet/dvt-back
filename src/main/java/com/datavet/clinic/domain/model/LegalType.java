package com.datavet.clinic.domain.model;

/**
 * Tipo de persona jurídica de la clínica.
 * Cubre las formas más comunes en España.
 * OTHER cubre cualquier caso no contemplado.
 */
public enum LegalType {
    AUTONOMO,
    SOCIEDAD_LIMITADA,       // S.L.
    SOCIEDAD_ANONIMA,        // S.A.
    SOCIEDAD_CIVIL,
    COMUNIDAD_DE_BIENES,
    OTHER
}