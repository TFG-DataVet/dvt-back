package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class AuthDomainException extends DomainException {

    protected AuthDomainException(String message) {
        super(message);
    }

    protected AuthDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
