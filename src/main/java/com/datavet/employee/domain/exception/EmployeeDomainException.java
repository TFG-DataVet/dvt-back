package com.datavet.employee.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

public abstract class EmployeeDomainException extends DomainException {

    protected EmployeeDomainException(String message) {
        super(message);
    }

    protected EmployeeDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
