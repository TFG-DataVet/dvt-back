package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class UserAlreadyExistsException extends EntityAlreadyExistsException {

    public UserAlreadyExistsException(String fieldName, String fieldValue) {
        super("User", fieldName, fieldValue);
    }
}