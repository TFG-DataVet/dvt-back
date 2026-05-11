package com.datavet.employee.domain.valueobject;

import com.datavet.employee.domain.exception.EmployeeValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WorkSchedule Value Object Tests")
class WorkScheduleTest {

    private static final List<DayOfWeek> VALID_DAYS  = List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
    private static final LocalTime       ENTRY        = LocalTime.of(8, 0);
    private static final LocalTime       EXIT         = LocalTime.of(16, 0);

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("of: should create with valid data")
    void of_WithValidData_ShouldCreate() {
        WorkSchedule ws = WorkSchedule.of(40, VALID_DAYS, ENTRY, EXIT, "Sin notas");

        assertThat(ws.getWeeklyHours()).isEqualTo(40);
        assertThat(ws.getWorkDays()).containsExactlyInAnyOrderElementsOf(VALID_DAYS);
        assertThat(ws.getEntryTime()).isEqualTo(ENTRY);
        assertThat(ws.getExitTime()).isEqualTo(EXIT);
        assertThat(ws.getNotes()).isEqualTo("Sin notas");
    }

    @Test
    @DisplayName("of: workDays list should be immutable")
    void of_WorkDays_ShouldBeImmutable() {
        WorkSchedule ws = WorkSchedule.of(40, VALID_DAYS, ENTRY, EXIT, null);

        assertThatThrownBy(() -> ws.getWorkDays().add(DayOfWeek.WEDNESDAY))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("of: two instances with same data should be equal")
    void of_SameData_ShouldBeEqual() {
        WorkSchedule a = WorkSchedule.of(40, VALID_DAYS, ENTRY, EXIT, null);
        WorkSchedule b = WorkSchedule.of(40, VALID_DAYS, ENTRY, EXIT, null);

        assertThat(a).isEqualTo(b);
    }

    // =========================================================================
    // Validaciones
    // =========================================================================

    @Test
    @DisplayName("of: should throw when weeklyHours is null")
    void of_WhenWeeklyHoursNull_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(null, VALID_DAYS, ENTRY, EXIT, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("horas semanales");
    }

    @Test
    @DisplayName("of: should throw when weeklyHours is zero")
    void of_WhenWeeklyHoursZero_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(0, VALID_DAYS, ENTRY, EXIT, null))
                .isInstanceOf(EmployeeValidationException.class);
    }

    @Test
    @DisplayName("of: should throw when weeklyHours exceeds 40")
    void of_WhenWeeklyHoursExceed40_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(41, VALID_DAYS, ENTRY, EXIT, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("40");
    }

    @Test
    @DisplayName("of: should throw when workDays is null")
    void of_WhenWorkDaysNull_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(40, null, ENTRY, EXIT, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("of: should throw when workDays is empty")
    void of_WhenWorkDaysEmpty_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(40, List.of(), ENTRY, EXIT, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("laborables");
    }

    @Test
    @DisplayName("of: should throw when entryTime is null")
    void of_WhenEntryTimeNull_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(40, VALID_DAYS, null, EXIT, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("entrada");
    }

    @Test
    @DisplayName("of: should throw when exitTime is null")
    void of_WhenExitTimeNull_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(40, VALID_DAYS, ENTRY, null, null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("salida");
    }

    @Test
    @DisplayName("of: should throw when exitTime is before entryTime")
    void of_WhenExitBeforeEntry_ShouldThrow() {
        assertThatThrownBy(() -> WorkSchedule.of(40, VALID_DAYS, LocalTime.of(16, 0), LocalTime.of(8, 0), null))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    @DisplayName("of: should throw when exitTime equals entryTime")
    void of_WhenExitEqualsEntry_ShouldThrow() {
        LocalTime same = LocalTime.of(9, 0);
        assertThatThrownBy(() -> WorkSchedule.of(40, VALID_DAYS, same, same, null))
                .isInstanceOf(EmployeeValidationException.class);
    }
}
