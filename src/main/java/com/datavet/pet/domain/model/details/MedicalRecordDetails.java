package com.datavet.pet.domain.model.details;

import com.datavet.pet.domain.exception.MedicalRecordApplyActionException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.pet.domain.valueobject.MedicalRecordType;

public interface MedicalRecordDetails {
    MedicalRecordType getType();
    void validate();
    boolean canCorrect(MedicalRecordDetails previous);

    default StatusChangeResult applyAction(RecordAction action) {
        throw new MedicalRecordApplyActionException("ApplyAction ","Este tipo de registro no soporta cambios de estado.");
    }
}
