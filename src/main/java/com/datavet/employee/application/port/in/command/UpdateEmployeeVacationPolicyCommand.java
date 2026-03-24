package com.datavet.employee.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UpdateEmployeeVacationPolicyCommand {
    String    employeeId;
    Integer   annualDays;
    LocalDate effectiveFrom;
}