package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("VacationPolicy Value Object Tests")
class VacationPolicyTest {

    private static final LocalDate VALID_DATE = LocalDate.now();

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("of: should create with valid data")
    void of_WithValidData_ShouldCreate() {
        VacationPolicy policy = VacationPolicy.of(22, VALID_DATE);

        assertThat(policy.getAnnualDays()).isEqualTo(22);
        assertThat(policy.getEffectiveFrom()).isEqualTo(VALID_DATE);
    }

    @Test
    @DisplayName("of: should accept minimum of 1 day")
    void of_WithOneDayShouldCreate() {
        VacationPolicy policy = VacationPolicy.of(1, VALID_DATE);
        assertThat(policy.getAnnualDays()).isEqualTo(1);
    }

    @Test
    @DisplayName("of: should accept maximum of 365 days")
    void of_With365Days_ShouldCreate() {
        VacationPolicy policy = VacationPolicy.of(365, VALID_DATE);
        assertThat(policy.getAnnualDays()).isEqualTo(365);
    }

    @Test
    @DisplayName("of: two instances with same data should be equal")
    void of_SameData_ShouldBeEqual() {
        VacationPolicy a = VacationPolicy.of(22, VALID_DATE);
        VacationPolicy b = VacationPolicy.of(22, VALID_DATE);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    // =========================================================================
    // Validaciones
    // =========================================================================

    @Test
    @DisplayName("of: should throw when annualDays is null")
    void of_WhenAnnualDaysNull_ShouldThrow() {
        assertThatThrownBy(() -> VacationPolicy.of(null, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("mayor a cero");
    }

    @Test
    @DisplayName("of: should throw when annualDays is zero")
    void of_WhenAnnualDaysZero_ShouldThrow() {
        assertThatThrownBy(() -> VacationPolicy.of(0, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when annualDays is negative")
    void of_WhenAnnualDaysNegative_ShouldThrow() {
        assertThatThrownBy(() -> VacationPolicy.of(-5, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when annualDays exceeds 365")
    void of_WhenAnnualDaysExceeds365_ShouldThrow() {
        assertThatThrownBy(() -> VacationPolicy.of(366, VALID_DATE))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("365");
    }

    @Test
    @DisplayName("of: should throw when effectiveFrom is null")
    void of_WhenEffectiveFromNull_ShouldThrow() {
        assertThatThrownBy(() -> VacationPolicy.of(22, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("efectividad");
    }
}
