package com.datavet.datavet.pet.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityNotFoundException;

public class PetNotFoundException extends EntityNotFoundException {

    public PetNotFoundException(String id) {
        super("Pet", "id", id);
    }

    public PetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
