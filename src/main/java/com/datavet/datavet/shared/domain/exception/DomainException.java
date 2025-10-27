package com.datavet.datavet.shared.domain.exception;

/**
 * Base class for all domain-specific exceptions.
 * Provides common functionality for domain exceptions across all bounded contexts.
 */
public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}