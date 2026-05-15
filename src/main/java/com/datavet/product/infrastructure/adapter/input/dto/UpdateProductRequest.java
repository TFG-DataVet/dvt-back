package com.datavet.product.infrastructure.adapter.input.dto;

import com.datavet.product.application.port.in.command.request.ProductDetailsRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String description;
    private String sku;
    private String barcode;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a cero")
    private BigDecimal price;

    @NotNull(message = "La tasa de impuesto es obligatoria")
    @DecimalMin(value = "0.0", message = "La tasa de impuesto debe ser mayor o igual a cero")
    private BigDecimal taxRate;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minStock;

    @NotNull(message = "Los detalles del producto son obligatorios")
    private ProductDetailsRequest details;
}
