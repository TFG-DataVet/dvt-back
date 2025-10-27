package com.datavet.datavet.shared.domain.exception;

/**
 * Base exception for when attempting to create an entity that already exists.
 * Should result in a 409 HTTP status code.
 */
public abstract class EntityAlreadyExistsException extends DomainException {
    
    protected EntityAlreadyExistsException(String message) {
        super(message);
    }
    
    protected EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected EntityAlreadyExistsException(String entityType, String fieldName, String fieldValue) {
        super(entityType + " already exists with " + fieldName + ": " + fieldValue);
    }
}