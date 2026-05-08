package com.datavet.appointment.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class AppointmentDomainException extends DomainException {

    protected AppointmentDomainException(String message) {
        super(message);
    }

    protected AppointmentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
