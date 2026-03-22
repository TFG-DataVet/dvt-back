package com.datavet.shared.domain.exception.email;

public class EmailAlreadyExistsException extends EmailException {

    public EmailAlreadyExistsException(String email) {
        super("A record already exists with email: " + email);
    }

    public EmailAlreadyExistsException(String entityType, String email) {
        super(entityType + " already exists with email: " + email);
    }
}
