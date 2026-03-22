package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class MedicalRecordAlreadyExistsException extends EntityAlreadyExistsException {

    public MedicalRecordAlreadyExistsException(String message) {
        super(message);
    }

    public MedicalRecordAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MedicalRecordAlreadyExistsException(String fieldName, String fieldValue) {
        super("MedicalRecord", fieldName, fieldValue);
    }
}
