package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class PetDomainException extends DomainException {

    protected PetDomainException(String message) {
        super(message);
    }

    protected PetDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
