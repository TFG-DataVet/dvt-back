package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
public class VacationPolicy {

    private final Integer   annualDays;
    private final LocalDate effectiveFrom;

    private VacationPolicy(Integer annualDays, LocalDate effectiveFrom) {
        this.annualDays    = annualDays;
        this.effectiveFrom = effectiveFrom;
    }

    public static VacationPolicy of(Integer annualDays, LocalDate effectiveFrom) {
        ValidationResult result = new ValidationResult();

        if (annualDays == null || annualDays <= 0) {
            result.addError("VacationPolicy", "Los días de vacaciones deben ser mayor a cero");
        }

        if (annualDays != null && annualDays > 365) {
            result.addError("VacationPolicy", "Los días de vacaciones no pueden superar 365");
        }

        if (effectiveFrom == null) {
            result.addError("VacationPolicy", "La fecha de efectividad no puede ser nula");
        }

        if (result.hasErrors()) {
            throw new EmployeeValidationException(result);
        }

        return new VacationPolicy(annualDays, effectiveFrom);
    }

    @Override
    public String toString() {
        return String.format("VacationPolicy{annualDays=%d, effectiveFrom=%s}",
                annualDays, effectiveFrom);
    }
}