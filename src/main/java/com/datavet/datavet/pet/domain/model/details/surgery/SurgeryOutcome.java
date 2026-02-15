package com.datavet.datavet.pet.domain.model.details.surgery;

/* SOLO EXISTEN CUANDO EL ESTADO ES COMPLETED O DECEASED*/
public enum SurgeryOutcome {
    SUCCESSFUL,
    SUCCESSFUL_WITH_COMPLICATIONS,
    FAILED,
}