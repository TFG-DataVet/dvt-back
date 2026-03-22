package com.datavet.shared.domain.exception.email;

public class EmailInvalidFormatException extends EmailException {

  public EmailInvalidFormatException(String email) {
        super("Invalid email format: " + email);
    }
}
