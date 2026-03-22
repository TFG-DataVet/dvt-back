package com.datavet.clinic.domain.valueobject;

import com.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

/**
 * Value object que representa el horario de atención de una clínica.
 * openTime y closeTime son LocalTime — el tipo garantiza horas válidas.
 * notes es opcional: describe excepciones (ej: "Sábados hasta las 14:00").
 */
@Getter
@EqualsAndHashCode
public class ClinicSchedule {

    private final String    openDays;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final String    notes;

    private ClinicSchedule(String openDays, LocalTime openTime,
                           LocalTime closeTime, String notes) {
        this.openDays  = openDays;
        this.openTime  = openTime;
        this.closeTime = closeTime;
        this.notes     = notes;
    }

    public static ClinicSchedule of(String openDays, LocalTime openTime,
                                    LocalTime closeTime, String notes) {
        ValidationResult result = new ValidationResult();

        if (openDays == null || openDays.isBlank()) {
            result.addError("ClinicSchedule", "Los días de apertura no pueden estar vacíos");
        }

        if (openTime == null) {
            result.addError("ClinicSchedule", "La hora de apertura no puede ser nula");
        }

        if (closeTime == null) {
            result.addError("ClinicSchedule", "La hora de cierre no puede ser nula");
        }

        // Solo comparamos si ambas horas son válidas
        if (openTime != null && closeTime != null && !closeTime.isAfter(openTime)) {
            result.addError("ClinicSchedule",
                    "La hora de cierre debe ser posterior a la hora de apertura");
        }

        if (result.hasErrors()) {
            throw new ClinicValidationException(result);
        }

        return new ClinicSchedule(openDays, openTime, closeTime, notes);
    }

    @Override
    public String toString() {
        return openDays + " " + openTime + "-" + closeTime
                + (notes != null && !notes.isBlank() ? " (" + notes + ")" : "");
    }
}