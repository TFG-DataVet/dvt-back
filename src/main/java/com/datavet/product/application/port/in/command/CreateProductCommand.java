package com.datavet.product.application.port.in.command;

import com.datavet.product.application.port.in.command.request.ProductDetailsRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@RequiredArgsConstructor
public class CreateProductCommand {

    @NotBlank(message = "El identificador de clínica es obligatorio")
    String clinicId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    String name;

    String description;
    String sku;
    String barcode;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a cero")
    BigDecimal price;

    @NotNull(message = "La tasa de impuesto es obligatoria")
    @DecimalMin(value = "0.0", message = "La tasa de impuesto debe ser mayor o igual a cero")
    BigDecimal taxRate;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    Integer minStock;

    @NotNull(message = "Los detalles del producto son obligatorios")
    ProductDetailsRequest detailsRequest;
}
