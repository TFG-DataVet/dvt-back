package com.datavet.employee.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeactivateEmployeeRequest {

    @NotBlank(message = "El motivo de desactivación es obligatorio")
    @Size(max = 255)
    private String reason;
}