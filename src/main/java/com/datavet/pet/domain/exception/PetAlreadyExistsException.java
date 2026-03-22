package com.datavet.pet.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class PetAlreadyExistsException extends EntityAlreadyExistsException {

    public PetAlreadyExistsException(String message) {
        super(message);
    }

    public PetAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PetAlreadyExistsException(String fieldName, String fieldValue) {
        super("Pet", fieldName, fieldValue);
    }
}
