package com.datavet.employee.application.dto;

import com.datavet.shared.domain.valueobject.DocumentId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EmployeeResponse {

    private String      employeeId;
    private String      userId;
    private String      clinicId;
    private String      firstName;
    private String      lastName;
    private DocumentId documentNumber;
    private String      phone;
    private AddressDto  address;
    private String      avatarUrl;
    private String      speciality;
    private String      licenseNumber;
    private LocalDate   hireDate;
    private boolean     active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Opcionales — null si no han sido configurados aún
    private SalaryDto         salary;
    private VacationPolicyDto vacationPolicy;
    private WorkScheduleDto   workSchedule;

    @JsonProperty("fullName")
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // --- DTOs anidados ---

    @Getter
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String postalCode;

        @JsonProperty("fullAddress")
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder(street);
            sb.append(", ").append(city);
            if (postalCode != null && !postalCode.isEmpty()) {
                sb.append(" ").append(postalCode);
            }
            return sb.toString();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SalaryDto {
        private BigDecimal amount;
        private String     currency;
        private Integer    paymentsPerYear;
        private LocalDate  effectiveFrom;
    }

    @Getter
    @AllArgsConstructor
    public static class VacationPolicyDto {
        private Integer   annualDays;
        private LocalDate effectiveFrom;
    }

    @Getter
    @AllArgsConstructor
    public static class WorkScheduleDto {
        private Integer         weeklyHours;
        private List<DayOfWeek> workDays;
        private LocalTime       entryTime;
        private LocalTime       exitTime;
        private String          notes;
    }
}