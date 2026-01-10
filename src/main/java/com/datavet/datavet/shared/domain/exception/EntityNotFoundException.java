package com.datavet.datavet.shared.domain.exception;

/**
 * Base exception for when an entity is not found in the system.
 * Should result in a 404 HTTP status code.
 */
public abstract class EntityNotFoundException extends DomainException {
    
    protected EntityNotFoundException(String message) {
        super(message);
    }
    
    protected EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected EntityNotFoundException(String entityType, String id) {
        super(entityType + " not found with id: " + id.toString());
    }
    
    protected EntityNotFoundException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " not found with " + fieldName + ": " + fieldValue);
    }
}