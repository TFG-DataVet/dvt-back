package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityNotFoundException;

public class OwnerNotFoundException extends EntityNotFoundException {

    public OwnerNotFoundException(String message) {
        super(message);
    }

    public OwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OwnerNotFoundException(Long clinicId) {
        super("Owner with id " + clinicId + " not found");
    }
}
