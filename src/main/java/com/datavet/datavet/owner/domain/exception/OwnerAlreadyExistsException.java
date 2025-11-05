package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.datavet.shared.domain.exception.DomainException;
import com.datavet.datavet.shared.domain.exception.EntityAlreadyExistsException;
import com.datavet.datavet.shared.domain.validation.ValidationResult;

public class OwnerAlreadyExistsException extends EntityAlreadyExistsException {

    public OwnerAlreadyExistsException(String message) {
        super(message);
    }

    public OwnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OwnerAlreadyExistsException(String fieldName, String fieldValue) {
        super("Owner", fieldName, fieldValue);
    }
}
