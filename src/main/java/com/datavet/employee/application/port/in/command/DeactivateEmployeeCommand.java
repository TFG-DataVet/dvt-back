package com.datavet.employee.application.port.in.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeactivateEmployeeCommand {
    String employeeId;
    String reason;
}