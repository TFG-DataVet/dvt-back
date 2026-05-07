package com.datavet.owner.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class OwnerDomainException extends DomainException {

    protected OwnerDomainException(String message) {
        super(message);
    }

    protected OwnerDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
