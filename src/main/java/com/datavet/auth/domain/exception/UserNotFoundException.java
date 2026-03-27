package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String id) {
        super("User", "id", id);
    }

    public UserNotFoundException(String field, String value, boolean byField) {
        super("User", field, value);
    }
}