package com.datavet.employee.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class UpdateEmployeeSalaryCommand {
    String     employeeId;
    String     clinicId;
    BigDecimal amount;
    String     currency;
    Integer    paymentsPerYear;
    LocalDate  effectiveFrom;
}