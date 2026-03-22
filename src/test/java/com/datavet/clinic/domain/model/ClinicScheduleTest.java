package com.datavet.clinic.domain.model;

import com.datavet.clinic.domain.exception.ClinicValidationException;

import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ClinicSchedule Value Object Tests")
class ClinicScheduleTest {

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("of: should create schedule with all fields correctly")
    void of_ShouldSetAllFields() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra fines de semana");

        assertThat(schedule.getOpenDays()).isEqualTo("Lunes - Viernes");
        assertThat(schedule.getOpenTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(schedule.getCloseTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(schedule.getNotes()).isEqualTo("Cierra fines de semana");
    }

    @Test
    @DisplayName("of: should create schedule with null notes — notes is optional")
    void of_ShouldAllowNullNotes() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), null);

        assertThat(schedule).isNotNull();
        assertThat(schedule.getNotes()).isNull();
    }

    @Test
    @DisplayName("of: should create schedule with blank notes — notes is optional")
    void of_ShouldAllowBlankNotes() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "");

        assertThat(schedule).isNotNull();
        assertThat(schedule.getNotes()).isEmpty();
    }

    // =========================================================================
    // Igualdad (EqualsAndHashCode)
    // =========================================================================

    @Test
    @DisplayName("Two schedules with same fields should be equal")
    void twoSchedulesWithSameFields_ShouldBeEqual() {
        ClinicSchedule s1 = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Nota");
        ClinicSchedule s2 = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Nota");

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }

    @Test
    @DisplayName("Two schedules with different openTime should not be equal")
    void twoSchedulesWithDifferentOpenTime_ShouldNotBeEqual() {
        ClinicSchedule s1 = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), null);
        ClinicSchedule s2 = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(10, 0), LocalTime.of(18, 0), null);

        assertThat(s1).isNotEqualTo(s2);
    }

    // =========================================================================
    // toString
    // =========================================================================

    @Test
    @DisplayName("toString: should include openDays and times")
    void toString_ShouldIncludeOpenDaysAndTimes() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), null);

        assertThat(schedule.toString())
                .contains("Lunes - Viernes")
                .contains("09:00")
                .contains("18:00");
    }

    @Test
    @DisplayName("toString: should include notes when present")
    void toString_ShouldIncludeNotesWhenPresent() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra fines de semana");

        assertThat(schedule.toString()).contains("Cierra fines de semana");
    }

    @Test
    @DisplayName("toString: should not include parentheses when notes is null")
    void toString_ShouldNotIncludeParenthesesWhenNotesIsNull() {
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), null);

        assertThat(schedule.toString()).doesNotContain("(");
    }

    // =========================================================================
    // Validaciones — excepciones
    // =========================================================================

    @Test
    @DisplayName("of: should throw when openDays is null")
    void of_WhenOpenDaysIsNull_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of(null, LocalTime.of(9, 0), LocalTime.of(18, 0), null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("apertura");
    }

    @Test
    @DisplayName("of: should throw when openDays is blank")
    void of_WhenOpenDaysIsBlank_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of("", LocalTime.of(9, 0), LocalTime.of(18, 0), null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("apertura");
    }

    @Test
    @DisplayName("of: should throw when openTime is null")
    void of_WhenOpenTimeIsNull_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of("Lunes - Viernes", null, LocalTime.of(18, 0), null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("apertura");
    }

    @Test
    @DisplayName("of: should throw when closeTime is null")
    void of_WhenCloseTimeIsNull_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), null, null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("cierre");
    }

    @Test
    @DisplayName("of: should throw when closeTime equals openTime")
    void of_WhenCloseTimeEqualsOpenTime_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(9, 0), null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    @DisplayName("of: should throw when closeTime is before openTime")
    void of_WhenCloseTimeIsBeforeOpenTime_ShouldThrow() {
        assertThatThrownBy(() -> ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(18, 0), LocalTime.of(9, 0), null))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("posterior");
    }
}