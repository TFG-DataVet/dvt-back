package com.datavet.owner.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class OwnerAlreadyExistsException extends EntityAlreadyExistsException {

    public OwnerAlreadyExistsException(String message) {
        super(message);
    }

    public OwnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OwnerAlreadyExistsException(String fieldName, String fieldValue) {
        super("Owner", fieldName, fieldValue);
    }
}
