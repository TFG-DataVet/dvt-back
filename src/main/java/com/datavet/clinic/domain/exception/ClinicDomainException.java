package com.datavet.clinic.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class ClinicDomainException extends DomainException {

    protected ClinicDomainException(String message) {
        super(message);
    }

    protected ClinicDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
