package com.datavet.employee.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateSalaryRequest {

    @NotNull(message = "El importe del salario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor a cero")
    private BigDecimal amount;

    @NotBlank(message = "La moneda es obligatoria")
    @Size(max = 3)
    private String currency;

    @NotNull(message = "El número de pagas es obligatorio")
    private Integer paymentsPerYear;

    @NotNull(message = "La fecha de efectividad es obligatoria")
    private LocalDate effectiveFrom;
}