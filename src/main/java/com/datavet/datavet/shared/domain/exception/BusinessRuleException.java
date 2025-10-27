package com.datavet.datavet.shared.domain.exception;

/**
 * Base exception for business rule violations.
 * Should result in a 400 HTTP status code.
 */
public abstract class BusinessRuleException extends DomainException {
    
    protected BusinessRuleException(String message) {
        super(message);
    }
    
    protected BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}