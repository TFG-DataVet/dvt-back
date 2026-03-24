package com.datavet.employee.application.mapper;

import com.datavet.employee.application.dto.EmployeeResponse;
import com.datavet.employee.domain.model.Employee;

public class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeResponse toResponse(Employee employee) {

        EmployeeResponse.AddressDto addressDto = employee.getAddress() != null
                ? new EmployeeResponse.AddressDto(
                employee.getAddress().getStreet(),
                employee.getAddress().getCity(),
                employee.getAddress().getPostalCode())
                : null;

        EmployeeResponse.SalaryDto salaryDto = employee.getSalary() != null
                ? new EmployeeResponse.SalaryDto(
                employee.getSalary().getAmount(),
                employee.getSalary().getCurrency(),
                employee.getSalary().getPaymentsPerYear(),
                employee.getSalary().getEffectiveFrom())
                : null;

        EmployeeResponse.VacationPolicyDto vacationDto = employee.getVacationPolicy() != null
                ? new EmployeeResponse.VacationPolicyDto(
                employee.getVacationPolicy().getAnnualDays(),
                employee.getVacationPolicy().getEffectiveFrom())
                : null;

        EmployeeResponse.WorkScheduleDto scheduleDto = employee.getWorkSchedule() != null
                ? new EmployeeResponse.WorkScheduleDto(
                employee.getWorkSchedule().getWeeklyHours(),
                employee.getWorkSchedule().getWorkDays(),
                employee.getWorkSchedule().getEntryTime(),
                employee.getWorkSchedule().getExitTime(),
                employee.getWorkSchedule().getNotes())
                : null;

        return new EmployeeResponse(
                employee.getId(),
                employee.getUserId(),
                employee.getClinicId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDocumentNumber(),
                employee.getPhone() != null ? employee.getPhone().getValue() : null,
                addressDto,
                employee.getAvatarUrl(),
                employee.getSpeciality(),
                employee.getLicenseNumber(),
                employee.getHireDate(),
                employee.isActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt(),
                salaryDto,
                vacationDto,
                scheduleDto
        );
    }
}