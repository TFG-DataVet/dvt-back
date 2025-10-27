package com.datavet.datavet.clinic.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityNotFoundException;

/**
 * Exception thrown when a clinic is not found in the system.
 * This exception should result in a 404 HTTP status code.
 */
public class ClinicNotFoundException extends EntityNotFoundException {
    
    public ClinicNotFoundException(String message) {
        super(message);
    }
    
    public ClinicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ClinicNotFoundException(Long clinicId) {
        super("Clinic", clinicId);
    }
}