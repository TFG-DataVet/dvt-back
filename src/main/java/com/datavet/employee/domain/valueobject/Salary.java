package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
public class Salary {

    private final BigDecimal amount;
    private final String     currency;
    private final Integer    paymentsPerYear;  // 12 o 14
    private final LocalDate  effectiveFrom;

    private Salary(BigDecimal amount, String currency,
                   Integer paymentsPerYear, LocalDate effectiveFrom) {
        this.amount          = amount;
        this.currency        = currency;
        this.paymentsPerYear = paymentsPerYear;
        this.effectiveFrom   = effectiveFrom;
    }

    public static Salary of(BigDecimal amount, String currency,
                            Integer paymentsPerYear, LocalDate effectiveFrom) {
        ValidationResult result = new ValidationResult();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("Salario - Cantidad bruta", "El salario debe ser mayor a cero");
        }

        if (currency == null || currency.isBlank()) {
            result.addError("Salario - Moneda", "La moneda no puede estar vacía");
        }

        if (paymentsPerYear == null || (paymentsPerYear != 12 && paymentsPerYear != 14)) {
            result.addError("Salario - cantidad de pagas", "El número de pagas debe ser 12 o 14");
        }

        if (effectiveFrom == null) {
            result.addError("Salario - Sueldo aplicado desde", "La fecha de efectividad no puede ser nula");
        }

        if (effectiveFrom != null && effectiveFrom.isAfter(LocalDate.now())) {
            result.addError("Salario - Sueldo aplicado desde", "La fecha de efectividad no puede ser futura");
        }

        if (result.hasErrors()) {
            throw new EmployeeValidationException(result);
        }

        return new Salary(amount, currency, paymentsPerYear, effectiveFrom);
    }

    @Override
    public String toString() {
        return String.format("Salary{amount=%s, currency='%s', paymentsPerYear=%d, effectiveFrom=%s}",
                amount, currency, paymentsPerYear, effectiveFrom);
    }
}