package com.datavet.shared.domain.exception.email;

import com.datavet.shared.domain.exception.DomainException;

public abstract class EmailException extends DomainException {

    protected EmailException(String message) {
        super(message);
    }

    protected EmailException(String message, Throwable cause){
        super(message, cause);
    }
}
