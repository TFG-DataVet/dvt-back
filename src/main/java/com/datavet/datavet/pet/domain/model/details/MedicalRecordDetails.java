package com.datavet.datavet.pet.domain.model.details;

import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;

public interface MedicalRecordDetails {
    MedicalRecordType getType();
    void validate();
}
