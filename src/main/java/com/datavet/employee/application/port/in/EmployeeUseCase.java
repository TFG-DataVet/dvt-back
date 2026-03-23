package com.datavet.employee.application.port.in;

import com.datavet.employee.application.port.in.command.CreateEmployeeCommand;
import com.datavet.employee.application.port.in.command.DeactivateEmployeeCommand;
import com.datavet.employee.application.port.in.command.UpdateEmployeeCommand;
import com.datavet.employee.application.port.in.command.UpdateEmployeeSalaryCommand;
import com.datavet.employee.application.port.in.command.UpdateEmployeeVacationPolicyCommand;
import com.datavet.employee.application.port.in.command.UpdateEmployeeWorkScheduleCommand;
import com.datavet.employee.domain.model.Employee;
import com.datavet.shared.application.port.UseCase;

import java.util.List;

public interface EmployeeUseCase extends UseCase {

    // Ciclo de vida
    Employee    createEmployee      (CreateEmployeeCommand command);
    Employee    updateEmployee      (UpdateEmployeeCommand command);
    void        deactivateEmployee  (DeactivateEmployeeCommand command);

    // Actualizaciones parciales
    Employee    updateSalary        (UpdateEmployeeSalaryCommand command);
    Employee    updateVacationPolicy(UpdateEmployeeVacationPolicyCommand command);
    Employee    updateWorkSchedule  (UpdateEmployeeWorkScheduleCommand command);

    // Consultas
    Employee        getEmployeeById     (String employeeId);
    List<Employee>  getEmployeesByClinic(String clinicId);
}