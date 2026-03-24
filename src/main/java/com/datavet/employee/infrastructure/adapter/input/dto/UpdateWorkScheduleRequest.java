package com.datavet.employee.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class UpdateWorkScheduleRequest {

    @NotNull(message = "Las horas semanales son obligatorias")
    @Min(value = 1,  message = "Las horas semanales deben ser al menos 1")
    @Max(value = 40, message = "Las horas semanales no pueden superar 40")
    private Integer weeklyHours;

    @NotEmpty(message = "Los días laborables son obligatorios")
    private List<DayOfWeek> workDays;

    @NotNull(message = "La hora de entrada es obligatoria")
    private LocalTime entryTime;

    @NotNull(message = "La hora de salida es obligatoria")
    private LocalTime exitTime;

    private String notes;
}