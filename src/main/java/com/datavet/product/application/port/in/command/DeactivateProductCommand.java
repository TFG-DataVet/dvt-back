package com.datavet.product.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class DeactivateProductCommand {

    @NotBlank(message = "El identificador del producto es obligatorio")
    String productId;

    String reason;
}
