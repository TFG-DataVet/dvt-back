package com.datavet.product.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class ProductNotFoundException extends EntityNotFoundException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String entityType, String id) {
        super(entityType, id);
    }
}
