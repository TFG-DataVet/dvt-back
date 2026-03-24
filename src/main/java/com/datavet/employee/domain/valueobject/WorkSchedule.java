package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@EqualsAndHashCode
public class WorkSchedule {

    private final Integer        weeklyHours;
    private final List<DayOfWeek> workDays;
    private final LocalTime      entryTime;
    private final LocalTime      exitTime;
    private final String         notes;

    private WorkSchedule(Integer weeklyHours, List<DayOfWeek> workDays,
                         LocalTime entryTime, LocalTime exitTime, String notes) {
        this.weeklyHours = weeklyHours;
        this.workDays    = workDays;
        this.entryTime   = entryTime;
        this.exitTime    = exitTime;
        this.notes       = notes;
    }

    public static WorkSchedule of(Integer weeklyHours, List<DayOfWeek> workDays,
                                  LocalTime entryTime, LocalTime exitTime, String notes) {
        ValidationResult result = new ValidationResult();

        if (weeklyHours == null || weeklyHours <= 0) {
            result.addError("WorkSchedule", "Las horas semanales deben ser mayor a cero");
        }

        if (weeklyHours != null && weeklyHours > 40) {
            result.addError("WorkSchedule", "Las horas semanales no pueden superar 40");
        }

        if (workDays == null || workDays.isEmpty()) {
            result.addError("WorkSchedule", "Los días laborables no pueden estar vacíos");
        }

        if (entryTime == null) {
            result.addError("WorkSchedule", "La hora de entrada no puede ser nula");
        }

        if (exitTime == null) {
            result.addError("WorkSchedule", "La hora de salida no puede ser nula");
        }

        if (entryTime != null && exitTime != null && !exitTime.isAfter(entryTime)) {
            result.addError("WorkSchedule", "La hora de salida debe ser posterior a la hora de entrada");
        }

        if (result.hasErrors()) {
            throw new EmployeeValidationException(result);
        }

        return new WorkSchedule(weeklyHours, List.copyOf(workDays), entryTime, exitTime, notes);
    }

    @Override
    public String toString() {
        return String.format("WorkSchedule{weeklyHours=%d, workDays=%s, entryTime=%s, exitTime=%s}",
                weeklyHours, workDays, entryTime, exitTime);
    }
}