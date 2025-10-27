package com.datavet.datavet.clinic.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityAlreadyExistsException;

/**
 * Exception thrown when attempting to create a clinic that already exists.
 * This exception should result in a 409 HTTP status code.
 */
public class ClinicAlreadyExistsException extends EntityAlreadyExistsException {
    
    public ClinicAlreadyExistsException(String message) {
        super(message);
    }
    
    public ClinicAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ClinicAlreadyExistsException(String fieldName, String fieldValue) {
        super("Clinic", fieldName, fieldValue);
    }
}