package com.datavet.product.infrastructure.adapter.input.dto;

import com.datavet.product.domain.valueobject.ProductMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateProductMovementRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private ProductMovementType type;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    private Integer quantity;

    @NotNull(message = "La fecha del movimiento es obligatoria")
    private LocalDateTime date;

    @NotBlank(message = "El identificador del empleado es obligatorio")
    private String employeeId;

    private String appointmentId;

    private String notes;
}
