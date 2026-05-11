package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Salary Value Object Tests")
class SalaryTest {

    private static final LocalDate VALID_DATE = LocalDate.now().minusDays(1);

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("of: should create with valid data (12 payments)")
    void of_WithValidData12Payments_ShouldCreate() {
        Salary salary = Salary.of(new BigDecimal("30000"), "EUR", 12, VALID_DATE);

        assertThat(salary.getAmount()).isEqualByComparingTo("30000");
        assertThat(salary.getCurrency()).isEqualTo("EUR");
        assertThat(salary.getPaymentsPerYear()).isEqualTo(12);
        assertThat(salary.getEffectiveFrom()).isEqualTo(VALID_DATE);
    }

    @Test
    @DisplayName("of: should create with 14 payments")
    void of_With14Payments_ShouldCreate() {
        Salary salary = Salary.of(new BigDecimal("25000"), "EUR", 14, VALID_DATE);

        assertThat(salary.getPaymentsPerYear()).isEqualTo(14);
    }

    @Test
    @DisplayName("of: two instances with same data should be equal")
    void of_SameData_ShouldBeEqual() {
        Salary a = Salary.of(new BigDecimal("30000"), "EUR", 12, VALID_DATE);
        Salary b = Salary.of(new BigDecimal("30000"), "EUR", 12, VALID_DATE);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    // =========================================================================
    // Validaciones
    // =========================================================================

    @Test
    @DisplayName("of: should throw when amount is null")
    void of_WhenAmountNull_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(null, "EUR", 12, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("mayor a cero");
    }

    @Test
    @DisplayName("of: should throw when amount is zero")
    void of_WhenAmountIsZero_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(BigDecimal.ZERO, "EUR", 12, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when amount is negative")
    void of_WhenAmountIsNegative_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("-100"), "EUR", 12, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when currency is null")
    void of_WhenCurrencyNull_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), null, 12, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("Moneda");
    }

    @Test
    @DisplayName("of: should throw when currency is blank")
    void of_WhenCurrencyBlank_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), "  ", 12, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when paymentsPerYear is null")
    void of_WhenPaymentsNull_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), "EUR", null, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("pagas");
    }

    @Test
    @DisplayName("of: should throw when paymentsPerYear is not 12 or 14")
    void of_WhenPaymentsInvalid_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), "EUR", 13, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("12 o 14");
    }

    @Test
    @DisplayName("of: should throw when effectiveFrom is null")
    void of_WhenEffectiveFromNull_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), "EUR", 12, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("efectividad");
    }

    @Test
    @DisplayName("of: should throw when effectiveFrom is in the future")
    void of_WhenEffectiveFromFuture_ShouldThrow() {
        assertThatThrownBy(() -> Salary.of(new BigDecimal("30000"), "EUR", 12, LocalDate.now().plusDays(1)))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("futura");
    }
}
