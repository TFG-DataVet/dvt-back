package com.datavet.datavet.shared.domain.exception.email;

public class EmailNotFoundException extends EmailException {

    public EmailNotFoundException(String email) {
        super("No record found with email: " + email);
    }

    public EmailNotFoundException(String entityType, String email) {
        super(entityType + " not found with email: " + email);
    }
}
