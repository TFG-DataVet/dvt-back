package com.datavet.datavet.pet.domain.model.details;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;

public interface MedicalRecordDetails {
    MedicalRecordType getType();
    void validate();
    boolean canCorrect(MedicalRecordDetails previous);

    default StatusChangeResult applyAction(RecordAction action) {
        throw new UnsupportedOperationException("Este ripo de registro no soporta cambios de estado.");
    }
}
