package com.datavet.product.domain.exception;

import com.datavet.shared.domain.exception.EntityAlreadyExistsException;

public class ProductAlreadyExistsException extends EntityAlreadyExistsException {

    public ProductAlreadyExistsException(String message) {
        super(message);
    }

    public ProductAlreadyExistsException(String fieldName, String fieldValue) {
        super("Product", fieldName, fieldValue);
    }
}
