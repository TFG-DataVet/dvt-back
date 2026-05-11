package com.datavet.employee.application.service;

import com.datavet.employee.application.port.in.command.DeactivateEmployeeCommand;
import com.datavet.employee.application.port.in.command.UpdateEmployeeCommand;
import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.employee.application.port.out.UserCreationPort;
import com.datavet.employee.domain.exception.EmployeeNotFoundException;
import com.datavet.employee.domain.model.Employee;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService - updateEmployee / deactivateEmployee / getEmployeeById Tests")
class EmployeeServiceUpdateTest {

    private EmployeeService employeeService;

    @Mock private EmployeeRepositoryPort employeeRepositoryPort;
    @Mock private DomainEventPublisher   domainEventPublisher;
    @Mock private UserCreationPort       userCreationPort;

    private DocumentId documentNumber;
    private Phone      phone;
    private Address    address;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepositoryPort, domainEventPublisher, userCreationPort);
        documentNumber  = DocumentId.of("DNI", "12345678Z");
        phone           = new Phone("+34912345678");
        address         = new Address("Calle Test 1", "Madrid", "28001");
    }

    // =========================================================================
    // updateEmployee
    // =========================================================================

    @Test
    @DisplayName("updateEmployee: should update and save employee when no conflicts")
    void updateEmployee_WhenNoConflict_ShouldUpdate() {
        Employee existing = buildExistingEmployee("emp-1", "clinic-1");

        when(employeeRepositoryPort.findById("emp-1")).thenReturn(Optional.of(existing));
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicIdAndIdNot("12345678Z", "clinic-1", "emp-1"))
                .thenReturn(false);
        when(employeeRepositoryPort.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        Employee result = employeeService.updateEmployee(buildUpdateCommand("emp-1", "clinic-1"));

        assertThat(result.getFirstName()).isEqualTo("Lucía");
        verify(employeeRepositoryPort).save(any(Employee.class));
    }

    @Test
    @DisplayName("updateEmployee: should throw when employee not found")
    void updateEmployee_WhenNotFound_ShouldThrow() {
        when(employeeRepositoryPort.findById("emp-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(buildUpdateCommand("emp-x", "clinic-1")))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    @DisplayName("updateEmployee: should throw AccessDeniedException when employee belongs to different clinic")
    void updateEmployee_WhenDifferentClinic_ShouldThrowAccessDenied() {
        Employee existing = buildExistingEmployee("emp-1", "other-clinic");

        when(employeeRepositoryPort.findById("emp-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> employeeService.updateEmployee(buildUpdateCommand("emp-1", "clinic-1")))
                .isInstanceOf(AccessDeniedException.class);
    }

    // =========================================================================
    // deactivateEmployee
    // =========================================================================

    @Test
    @DisplayName("deactivateEmployee: should deactivate active employee")
    void deactivateEmployee_ShouldDeactivate() {
        Employee existing = buildExistingEmployee("emp-1", "clinic-1");

        when(employeeRepositoryPort.findById("emp-1")).thenReturn(Optional.of(existing));
        when(employeeRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        employeeService.deactivateEmployee(DeactivateEmployeeCommand.builder()
                .employeeId("emp-1")
                .clinicId("clinic-1")
                .reason("baja voluntaria")
                .build());

        assertThat(existing.isActive()).isFalse();
        verify(employeeRepositoryPort).save(existing);
    }

    @Test
    @DisplayName("deactivateEmployee: should throw when employee not found")
    void deactivateEmployee_WhenNotFound_ShouldThrow() {
        when(employeeRepositoryPort.findById("emp-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deactivateEmployee(DeactivateEmployeeCommand.builder()
                .employeeId("emp-x")
                .clinicId("clinic-1")
                .reason("baja")
                .build()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    // =========================================================================
    // getEmployeesByClinic
    // =========================================================================

    @Test
    @DisplayName("getEmployeesByClinic: should return only active employees")
    void getEmployeesByClinic_ShouldReturnActiveEmployees() {
        Employee active = buildExistingEmployee("emp-1", "clinic-1");
        when(employeeRepositoryPort.findByClinicIdAndActiveTrue("clinic-1")).thenReturn(List.of(active));

        List<Employee> result = employeeService.getEmployeesByClinic("clinic-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("emp-1");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Employee buildExistingEmployee(String id, String clinicId) {
        return Employee.reconstitute(
                id, "user-1", clinicId,
                "María", "López",
                documentNumber, phone, address,
                null, "Cirugía", "VET-12345",
                LocalDate.now().minusDays(1),
                null, null, null,
                true, LocalDateTime.now(), null
        );
    }

    private UpdateEmployeeCommand buildUpdateCommand(String employeeId, String clinicId) {
        return UpdateEmployeeCommand.builder()
                .employeeId(employeeId)
                .clinicId(clinicId)
                .firstName("Lucía")
                .lastName("Martínez")
                .documentNumber(documentNumber)
                .phone(phone)
                .address(address)
                .avatarUrl(null)
                .speciality("Cardiología")
                .licenseNumber("VET-99999")
                .role("CLINIC_VETERINARIAN")
                .build();
    }
}
