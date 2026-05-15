package com.datavet.product.infrastructure.adapter.input.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeactivateProductRequest {
    private String reason;
}
