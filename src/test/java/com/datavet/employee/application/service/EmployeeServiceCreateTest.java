package com.datavet.employee.application.service;

import com.datavet.employee.application.port.in.command.CreateEmployeeCommand;
import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.employee.application.port.out.UserCreationPort;
import com.datavet.employee.domain.exception.EmployeeAlreadyExistsException;
import com.datavet.employee.domain.model.Employee;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService - createEmployee Tests")
class EmployeeServiceCreateTest {

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
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should create employee when no documentNumber conflict exists")
    void createEmployee_WhenNoConflict_ShouldCreate() {
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicId("12345678Z", "clinic-1")).thenReturn(false);
        when(userCreationPort.createPendingEmployeeUser(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn("user-uuid");
        when(employeeRepositoryPort.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        Employee result = employeeService.createEmployee(buildVetCommand());

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("María");
        assertThat(result.getClinicId()).isEqualTo("clinic-1");
        verify(employeeRepositoryPort).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should assign userId returned by UserCreationPort")
    void createEmployee_ShouldAssignUserId() {
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicId("12345678Z", "clinic-1")).thenReturn(false);
        when(userCreationPort.createPendingEmployeeUser(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn("user-uuid-123");
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        when(employeeRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        employeeService.createEmployee(buildVetCommand());

        assertThat(captor.getValue().getUserId()).isEqualTo("user-uuid-123");
    }

    @Test
    @DisplayName("Should publish domain events after creating employee")
    void createEmployee_ShouldPublishDomainEvents() {
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicId("12345678Z", "clinic-1")).thenReturn(false);
        when(userCreationPort.createPendingEmployeeUser(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn("user-uuid");
        when(employeeRepositoryPort.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        employeeService.createEmployee(buildVetCommand());

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw EmployeeAlreadyExistsException when documentNumber already exists in clinic")
    void createEmployee_WhenDocumentNumberExists_ShouldThrow() {
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicId("12345678Z", "clinic-1")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(buildVetCommand()))
                .isInstanceOf(EmployeeAlreadyExistsException.class)
                .hasMessageContaining("documentNumber");

        verify(employeeRepositoryPort, never()).save(any());
        verify(userCreationPort, never()).createPendingEmployeeUser(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should propagate exception when save fails")
    void createEmployee_WhenSaveFails_ShouldPropagate() {
        when(employeeRepositoryPort.existsByDocumentNumberAndClinicId("12345678Z", "clinic-1")).thenReturn(false);
        when(userCreationPort.createPendingEmployeeUser(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn("user-uuid");
        when(employeeRepositoryPort.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> employeeService.createEmployee(buildVetCommand()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private CreateEmployeeCommand buildVetCommand() {
        return CreateEmployeeCommand.builder()
                .clinicId("clinic-1")
                .firstName("María")
                .lastName("López")
                .documentNumber(documentNumber)
                .phone(phone)
                .email("maria@clinic.com")
                .address(address)
                .avatarUrl(null)
                .speciality("Cirugía")
                .licenseNumber("VET-12345")
                .hireDate(LocalDate.now().minusDays(1))
                .role("CLINIC_VETERINARIAN")
                .build();
    }
}
