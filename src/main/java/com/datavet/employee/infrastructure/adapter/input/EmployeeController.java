package com.datavet.employee.infrastructure.adapter.input;

import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.employee.application.dto.EmployeeResponse;
import com.datavet.employee.application.mapper.EmployeeMapper;
import com.datavet.employee.application.port.in.EmployeeUseCase;
import com.datavet.employee.application.port.in.command.*;
import com.datavet.employee.domain.model.Employee;
import com.datavet.employee.infrastructure.adapter.input.dto.*;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeUseCase employeeUseCase;

    /**
     * Crea un empleado con los datos obligatorios.
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CreateEmployeeRequest request) {

        CreateEmployeeCommand command = CreateEmployeeCommand.builder()
                .userId(request.getUserId())
                .clinicId(currentUser.getClinicId())   // TODO: vendrá del JWT
//                .clinicId("id_clinic")   // TODO: vendrá del JWT
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentNumber(DocumentId.of(request.getDocumentType(), request.getDocumentNumber()))
                .phone(new Phone(request.getPhone()))
                .address(new Address(request.getAddress(), request.getCity(), request.getPostalCode()))
                .avatarUrl(request.getAvatarUrl())
                .speciality(request.getSpeciality())
                .licenseNumber(request.getLicenseNumber())
                .hireDate(request.getHireDate())
                .role(request.getRole())
                .build();

        Employee employee = employeeUseCase.createEmployee(command);
        return ResponseEntity.status(201).body(EmployeeMapper.toResponse(employee));
    }

    /**
     * Actualiza los datos personales y laborales de un empleado.
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        UpdateEmployeeCommand command = UpdateEmployeeCommand.builder()
                .employeeId(id)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .documentNumber(request.getDocumentNumber())
                .phone(new Phone(request.getPhone()))
                .address(new Address(request.getAddress(), request.getCity(), request.getPostalCode()))
                .avatarUrl(request.getAvatarUrl())
                .speciality(request.getSpeciality())
                .licenseNumber(request.getLicenseNumber())
                .role(request.getRole())
                .build();

        Employee employee = employeeUseCase.updateEmployee(command);
        return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
    }

    /**
     * Desactiva un empleado (soft-delete).
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable String id,
            @Valid @RequestBody DeactivateEmployeeRequest request) {

        employeeUseCase.deactivateEmployee(
                DeactivateEmployeeCommand.builder()
                        .employeeId(id)
                        .reason(request.getReason())
                        .build());

        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza el salario de un empleado.
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @PatchMapping("/{id}/salary")
    public ResponseEntity<EmployeeResponse> updateSalary(
            @PathVariable String id,
            @Valid @RequestBody UpdateSalaryRequest request) {

        UpdateEmployeeSalaryCommand command = UpdateEmployeeSalaryCommand.builder()
                .employeeId(id)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentsPerYear(request.getPaymentsPerYear())
                .effectiveFrom(request.getEffectiveFrom())
                .build();

        Employee employee = employeeUseCase.updateSalary(command);
        return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
    }

    /**
     * Actualiza la política de vacaciones de un empleado.
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @PatchMapping("/{id}/vacation-policy")
    public ResponseEntity<EmployeeResponse> updateVacationPolicy(
            @PathVariable String id,
            @Valid @RequestBody UpdateVacationPolicyRequest request) {

        UpdateEmployeeVacationPolicyCommand command = UpdateEmployeeVacationPolicyCommand.builder()
                .employeeId(id)
                .annualDays(request.getAnnualDays())
                .effectiveFrom(request.getEffectiveFrom())
                .build();

        Employee employee = employeeUseCase.updateVacationPolicy(command);
        return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
    }

    /**
     * Actualiza el horario de trabajo de un empleado.
     * CLINIC_OWNER y CLINIC_ADMIN.
     */
    @PatchMapping("/{id}/work-schedule")
    public ResponseEntity<EmployeeResponse> updateWorkSchedule(
            @PathVariable String id,
            @Valid @RequestBody UpdateWorkScheduleRequest request) {

        UpdateEmployeeWorkScheduleCommand command = UpdateEmployeeWorkScheduleCommand.builder()
                .employeeId(id)
                .weeklyHours(request.getWeeklyHours())
                .workDays(request.getWorkDays())
                .entryTime(request.getEntryTime())
                .exitTime(request.getExitTime())
                .notes(request.getNotes())
                .build();

        Employee employee = employeeUseCase.updateWorkSchedule(command);
        return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                EmployeeMapper.toResponse(employeeUseCase.getEmployeeById(id)));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getByClinic(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        // SUPER_ADMIN podría ver todas las clínicas — por ahora solo ve la suya
        List<EmployeeResponse> responses = employeeUseCase
                .getEmployeesByClinic(currentUser.getClinicId())
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * TODO: Cuando Auth esté implementado, este método leerá el clinicId
     *       directamente del SecurityContext en lugar de recibirlo como parámetro.
     */
    private String extractClinicIdFromContext() {
        return null;
    }
}
