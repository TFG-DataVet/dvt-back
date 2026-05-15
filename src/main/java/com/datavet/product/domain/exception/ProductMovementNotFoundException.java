package com.datavet.product.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class ProductMovementNotFoundException extends EntityNotFoundException {

    public ProductMovementNotFoundException(String id) {
        super("ProductMovement", id);
    }
}
