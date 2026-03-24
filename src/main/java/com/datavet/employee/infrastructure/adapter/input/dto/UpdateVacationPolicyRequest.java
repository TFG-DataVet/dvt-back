package com.datavet.employee.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateVacationPolicyRequest {

    @NotNull(message = "Los días anuales de vacaciones son obligatorios")
    @Min(value = 1, message = "Los días de vacaciones deben ser al menos 1")
    private Integer annualDays;

    @NotNull(message = "La fecha de efectividad es obligatoria")
    private LocalDate effectiveFrom;
}