package com.datavet.auth.application.port.in.command;

import com.datavet.auth.domain.model.UserRole;
import lombok.Builder;
import lombok.Value;

/**
 * Command para crear el User de un empleado de clínica.
 * Llamado internamente desde EmployeeService tras crear el Employee.
 * El usuario nace directamente en estado ACTIVE.
 */
@Value
@Builder
public class CreateEmployeeUserCommand {
    String   clinicId;
    String   employeeId;
    String   email;
    String   rawPassword;
    UserRole role;
}