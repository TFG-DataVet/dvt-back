package com.datavet.product.application.port.in.command;

import com.datavet.product.domain.valueobject.ProductMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CreateProductMovementCommand {

    @NotBlank(message = "El identificador del producto es obligatorio")
    String productId;

    @NotBlank(message = "El identificador de clínica es obligatorio")
    String clinicId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    ProductMovementType type;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    Integer quantity;

    @NotNull(message = "La fecha del movimiento es obligatoria")
    LocalDateTime date;

    @NotBlank(message = "El identificador del empleado es obligatorio")
    String employeeId;

    String appointmentId;

    String notes;
}
