package com.datavet.owner.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class OwnerNotFoundException extends EntityNotFoundException {

    public OwnerNotFoundException(String id) {
        super("Owner", "id", id);
    }

    public OwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
