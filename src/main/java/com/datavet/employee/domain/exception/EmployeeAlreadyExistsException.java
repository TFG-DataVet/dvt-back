package com.datavet.employee.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class EmployeeAlreadyExistsException extends EntityAlreadyExistsException {

    public EmployeeAlreadyExistsException(String fieldName, String fieldValue) {
        super("Employee", fieldName, fieldValue);
    }

    public EmployeeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}