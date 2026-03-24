package com.datavet.employee.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class EmployeeNotFoundException extends EntityNotFoundException {

    public EmployeeNotFoundException(String id) {
        super("Employee", "id", id);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}