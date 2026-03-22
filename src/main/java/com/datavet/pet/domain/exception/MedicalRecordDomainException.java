package com.datavet.pet.domain.exception;

public abstract class MedicalRecordDomainException extends PetDomainException {

    protected MedicalRecordDomainException(String message) {
        super(message);
    }

    protected MedicalRecordDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
