package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class MedicalRecordNotFoundException extends EntityNotFoundException {

    public MedicalRecordNotFoundException(String id) {
        super("MedicalRecord", "id", id);
    }

    public MedicalRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
