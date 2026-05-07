package com.datavet.shared.domain.exception.email;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class EmailAlreadyExistsException extends EntityAlreadyExistsException {

    public EmailAlreadyExistsException(String email) {
        super("A record already exists with email: " + email);
    }

    public EmailAlreadyExistsException(String entityType, String email) {
        super(entityType + " already exists with email: " + email);
    }
}
