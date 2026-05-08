package com.datavet.shared.domain.exception.email;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class EmailNotFoundException extends EntityNotFoundException {

    public EmailNotFoundException(String email) {
        super("No record found with email: " + email);
    }

    public EmailNotFoundException(String entityType, String email) {
        super(entityType + " not found with email: " + email);
    }
}
