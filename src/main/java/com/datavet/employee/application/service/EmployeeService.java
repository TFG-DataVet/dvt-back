package com.datavet.employee.application.service;

import com.datavet.auth.domain.model.UserRole;
import com.datavet.employee.application.port.in.EmployeeUseCase;
import com.datavet.employee.application.port.in.command.*;
import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.employee.application.port.out.UserCreationPort;
import com.datavet.employee.domain.exception.EmployeeAlreadyExistsException;
import com.datavet.employee.domain.exception.EmployeeNotFoundException;
import com.datavet.employee.domain.model.Employee;
import com.datavet.employee.domain.valueobject.Salary;
import com.datavet.employee.domain.valueobject.VacationPolicy;
import com.datavet.employee.domain.valueobject.WorkSchedule;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService implements EmployeeUseCase, ApplicationService {

    private final EmployeeRepositoryPort employeeRepositoryPort;
    private final DomainEventPublisher   domainEventPublisher;
    private final UserCreationPort userCreationPort;

    @Override
    @Transactional
    public Employee createEmployee(CreateEmployeeCommand command) {
        if (employeeRepositoryPort.existsByDocumentNumberAndClinicId(
                command.getDocumentNumber().getDocumentNumber(), command.getClinicId())) {
            throw new EmployeeAlreadyExistsException("documentNumber", command.getDocumentNumber().getDocumentNumber());
        }

        // Creamos el Employee primero para obtener su ID
        Employee employee = Employee.create(
                null,           // userId — se asigna tras crear el User
                command.getClinicId(),
                command.getFirstName(),
                command.getLastName(),
                command.getDocumentNumber(),
                command.getPhone(),
                command.getAddress(),
                command.getAvatarUrl(),
                command.getSpeciality(),
                command.getLicenseNumber(),
                command.getHireDate(),
                command.getRole()
        );


        // Creamos el User pendiente de activación
        String userId = userCreationPort.createPendingEmployeeUser(
                command.getClinicId(),
                employee.getId(),
                command.getEmail(),
                employee.getFirstName(),
                UserRole.valueOf(command.getRole())
        );
        employee.assignUserId(userId);

        Employee savedEmployee = employeeRepositoryPort.save(employee);

        publishDomainEvents(savedEmployee);
        return employeeRepositoryPort.save(savedEmployee);
    }

    @Override
    @Transactional
    public Employee updateEmployee(UpdateEmployeeCommand command) {
        Employee employee = getEmployeeById(command.getEmployeeId());

        // Unicidad del documentNumber excluyendo el propio empleado
        if (employeeRepositoryPort.existsByDocumentNumberAndClinicIdAndIdNot(
                command.getDocumentNumber().getDocumentNumber(), employee.getClinicId(), command.getEmployeeId())) {
            throw new EmployeeAlreadyExistsException("documentNumber", command.getDocumentNumber().getDocumentNumber());
        }

        employee.update(
                command.getFirstName(),
                command.getLastName(),
                command.getDocumentNumber(),
                command.getPhone(),
                command.getAddress(),
                command.getAvatarUrl(),
                command.getSpeciality(),
                command.getLicenseNumber(),
                command.getRole()
        );

        publishDomainEvents(employee);
        return employeeRepositoryPort.save(employee);
    }

    @Override
    @Transactional
    public void deactivateEmployee(DeactivateEmployeeCommand command) {
        Employee employee = getEmployeeById(command.getEmployeeId());

        // El dominio valida que no esté ya inactivo
        employee.deactivate(command.getReason());

        publishDomainEvents(employee);
        employeeRepositoryPort.save(employee);
    }

    @Override
    @Transactional
    public Employee updateSalary(UpdateEmployeeSalaryCommand command) {
        Employee employee = getEmployeeById(command.getEmployeeId());

        // El value object valida los datos internamente
        Salary salary = Salary.of(
                command.getAmount(),
                command.getCurrency(),
                command.getPaymentsPerYear(),
                command.getEffectiveFrom()
        );

        // El dominio valida que el empleado esté activo
        employee.updateSalary(salary);

        publishDomainEvents(employee);
        return employeeRepositoryPort.save(employee);
    }

    @Override
    @Transactional
    public Employee updateVacationPolicy(UpdateEmployeeVacationPolicyCommand command) {
        Employee employee = getEmployeeById(command.getEmployeeId());

        VacationPolicy vacationPolicy = VacationPolicy.of(
                command.getAnnualDays(),
                command.getEffectiveFrom()
        );

        employee.updateVacationPolicy(vacationPolicy);

        publishDomainEvents(employee);
        return employeeRepositoryPort.save(employee);
    }

    @Override
    @Transactional
    public Employee updateWorkSchedule(UpdateEmployeeWorkScheduleCommand command) {
        Employee employee = getEmployeeById(command.getEmployeeId());

        WorkSchedule workSchedule = WorkSchedule.of(
                command.getWeeklyHours(),
                command.getWorkDays(),
                command.getEntryTime(),
                command.getExitTime(),
                command.getNotes()
        );

        employee.updateWorkSchedule(workSchedule);

        publishDomainEvents(employee);
        return employeeRepositoryPort.save(employee);
    }

    @Override
    public Employee getEmployeeById(String employeeId) {
        return employeeRepositoryPort.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    @Override
    public List<Employee> getEmployeesByClinic(String clinicId) {
        return employeeRepositoryPort.findByClinicIdAndActiveTrue(clinicId);
    }

    private void publishDomainEvents(Employee employee) {
        List<DomainEvent> events = employee.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        employee.clearDomainEvents();
    }
}