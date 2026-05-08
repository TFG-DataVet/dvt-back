package com.datavet.employee.domain.model;

import com.datavet.employee.domain.event.EmployeeCreatedEvent;
import com.datavet.employee.domain.event.EmployeeDeactivatedEvent;
import com.datavet.employee.domain.event.EmployeeUpdatedEvent;
import com.datavet.employee.domain.exception.EmployeeValidationException;
import com.datavet.employee.domain.valueobject.Salary;
import com.datavet.employee.domain.valueobject.VacationPolicy;
import com.datavet.employee.domain.valueobject.WorkSchedule;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Employee Domain Model Tests")
class EmployeeTest {

    private DocumentId documentNumber;
    private Phone      phone;
    private Address    address;

    @BeforeEach
    void setUp() {
        documentNumber = DocumentId.of("DNI", "12345678Z");
        phone          = new Phone("+34912345678");
        address        = new Address("Calle Test 1", "Madrid", "28001");
    }

    // =========================================================================
    // create
    // =========================================================================

    @Test
    @DisplayName("create: should set all fields correctly")
    void create_ShouldSetAllFields() {
        Employee employee = buildVetEmployee();

        assertThat(employee.getClinicId()).isEqualTo("clinic-1");
        assertThat(employee.getFirstName()).isEqualTo("María");
        assertThat(employee.getLastName()).isEqualTo("López");
        assertThat(employee.getDocumentNumber()).isEqualTo(documentNumber);
        assertThat(employee.getPhone()).isEqualTo(phone);
        assertThat(employee.getAddress()).isEqualTo(address);
        assertThat(employee.getSpeciality()).isEqualTo("Cirugía");
        assertThat(employee.getLicenseNumber()).isEqualTo("VET-12345");
        assertThat(employee.isActive()).isTrue();
    }

    @Test
    @DisplayName("create: should generate a non-null UUID")
    void create_ShouldGenerateUUID() {
        Employee employee = buildVetEmployee();

        assertThat(employee.getId()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("create: should initialize salary, vacationPolicy and workSchedule as null")
    void create_ShouldHaveNullOptionalFields() {
        Employee employee = buildVetEmployee();

        assertThat(employee.getSalary()).isNull();
        assertThat(employee.getVacationPolicy()).isNull();
        assertThat(employee.getWorkSchedule()).isNull();
    }

    @Test
    @DisplayName("create: should raise EmployeeCreatedEvent")
    void create_ShouldRaiseEmployeeCreatedEvent() {
        Employee employee = buildVetEmployee();

        List<DomainEvent> events = employee.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(EmployeeCreatedEvent.class);
    }

    @Test
    @DisplayName("create: should throw when firstName is blank")
    void create_WhenFirstNameBlank_ShouldThrow() {
        assertThatThrownBy(() -> Employee.create(
                "user-1", "clinic-1", "", "López",
                documentNumber, phone, address, null,
                "Cirugía", "VET-12345",
                LocalDate.now().minusDays(1), "CLINIC_VETERINARIAN"))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    @DisplayName("create: should throw when clinicId is blank")
    void create_WhenClinicIdBlank_ShouldThrow() {
        assertThatThrownBy(() -> Employee.create(
                "user-1", "", "María", "López",
                documentNumber, phone, address, null,
                "Cirugía", "VET-12345",
                LocalDate.now().minusDays(1), "CLINIC_VETERINARIAN"))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("clínica");
    }

    @Test
    @DisplayName("create: should throw when hireDate is in the future (non CLINIC_OWNER)")
    void create_WhenHireDateFuture_ShouldThrow() {
        assertThatThrownBy(() -> Employee.create(
                "user-1", "clinic-1", "María", "López",
                documentNumber, phone, address, null,
                "Cirugía", "VET-12345",
                LocalDate.now().plusDays(1), "CLINIC_VETERINARIAN"))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("futura");
    }

    @Test
    @DisplayName("create: should throw when vet has no licenseNumber")
    void create_WhenVetHasNoLicense_ShouldThrow() {
        assertThatThrownBy(() -> Employee.create(
                "user-1", "clinic-1", "María", "López",
                documentNumber, phone, address, null,
                "Cirugía", null,
                LocalDate.now().minusDays(1), "CLINIC_VETERINARIAN"))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("licencia");
    }

    @Test
    @DisplayName("create: CLINIC_OWNER should not require hireDate")
    void create_ClinicOwner_ShouldNotRequireHireDate() {
        Employee owner = Employee.create(
                "user-1", "clinic-1", "Juan", "García",
                documentNumber, phone, address, null,
                null, null, null, "CLINIC_OWNER");

        assertThat(owner).isNotNull();
        assertThat(owner.getHireDate()).isNull();
    }

    // =========================================================================
    // assignUserId
    // =========================================================================

    @Test
    @DisplayName("assignUserId: should assign userId correctly")
    void assignUserId_ShouldAssignUserId() {
        Employee employee = buildVetEmployee();

        employee.assignUserId("user-99");

        assertThat(employee.getUserId()).isEqualTo("user-99");
    }

    @Test
    @DisplayName("assignUserId: should throw when userId is blank")
    void assignUserId_WhenBlank_ShouldThrow() {
        Employee employee = buildVetEmployee();

        assertThatThrownBy(() -> employee.assignUserId(""))
                .isInstanceOf(EmployeeValidationException.class);
    }

    // =========================================================================
    // update
    // =========================================================================

    @Test
    @DisplayName("update: should update all personal fields")
    void update_ShouldUpdateFields() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();
        Address newAddress = new Address("Calle Nueva 5", "Barcelona", "08001");
        Phone   newPhone   = new Phone("+34911111111");

        employee.update("Lucía", "Martínez", documentNumber, newPhone, newAddress,
                "https://new.png", "Cardiología", "VET-99999", "CLINIC_VETERINARIAN");

        assertThat(employee.getFirstName()).isEqualTo("Lucía");
        assertThat(employee.getLastName()).isEqualTo("Martínez");
        assertThat(employee.getPhone()).isEqualTo(newPhone);
        assertThat(employee.getAddress()).isEqualTo(newAddress);
    }

    @Test
    @DisplayName("update: should raise EmployeeUpdatedEvent")
    void update_ShouldRaiseEvent() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();

        employee.update("Lucía", "Martínez", documentNumber, phone, address,
                null, "Cardiología", "VET-99999", "CLINIC_VETERINARIAN");

        assertThat(employee.getDomainEvents()).hasSize(1);
        assertThat(employee.getDomainEvents().get(0)).isInstanceOf(EmployeeUpdatedEvent.class);
    }

    // =========================================================================
    // updateSalary
    // =========================================================================

    @Test
    @DisplayName("updateSalary: should assign salary and raise event")
    void updateSalary_ShouldAssignSalaryAndRaiseEvent() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();
        Salary salary = Salary.of(new BigDecimal("30000"), "EUR", 12, LocalDate.now().minusDays(1));

        employee.updateSalary(salary);

        assertThat(employee.getSalary()).isEqualTo(salary);
        assertThat(employee.getDomainEvents()).hasSize(1);
        assertThat(employee.getDomainEvents().get(0)).isInstanceOf(EmployeeUpdatedEvent.class);
    }

    @Test
    @DisplayName("updateSalary: should throw when employee is inactive")
    void updateSalary_WhenInactive_ShouldThrow() {
        Employee employee = buildVetEmployee();
        employee.deactivate("baja");
        Salary salary = Salary.of(new BigDecimal("30000"), "EUR", 12, LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> employee.updateSalary(salary))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("inactivo");
    }

    // =========================================================================
    // updateVacationPolicy
    // =========================================================================

    @Test
    @DisplayName("updateVacationPolicy: should assign policy and raise event")
    void updateVacationPolicy_ShouldAssignAndRaiseEvent() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();
        VacationPolicy policy = VacationPolicy.of(22, LocalDate.now());

        employee.updateVacationPolicy(policy);

        assertThat(employee.getVacationPolicy()).isEqualTo(policy);
        assertThat(employee.getDomainEvents()).hasSize(1);
    }

    @Test
    @DisplayName("updateVacationPolicy: should throw when employee is inactive")
    void updateVacationPolicy_WhenInactive_ShouldThrow() {
        Employee employee = buildVetEmployee();
        employee.deactivate("baja");
        VacationPolicy policy = VacationPolicy.of(22, LocalDate.now());

        assertThatThrownBy(() -> employee.updateVacationPolicy(policy))
                .isInstanceOf(EmployeeValidationException.class);
    }

    // =========================================================================
    // updateWorkSchedule
    // =========================================================================

    @Test
    @DisplayName("updateWorkSchedule: should assign schedule and raise event")
    void updateWorkSchedule_ShouldAssignAndRaiseEvent() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();
        WorkSchedule ws = WorkSchedule.of(40,
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                LocalTime.of(8, 0), LocalTime.of(16, 0), null);

        employee.updateWorkSchedule(ws);

        assertThat(employee.getWorkSchedule()).isEqualTo(ws);
        assertThat(employee.getDomainEvents()).hasSize(1);
    }

    @Test
    @DisplayName("updateWorkSchedule: should throw when employee is inactive")
    void updateWorkSchedule_WhenInactive_ShouldThrow() {
        Employee employee = buildVetEmployee();
        employee.deactivate("baja");
        WorkSchedule ws = WorkSchedule.of(40,
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                LocalTime.of(8, 0), LocalTime.of(16, 0), null);

        assertThatThrownBy(() -> employee.updateWorkSchedule(ws))
                .isInstanceOf(EmployeeValidationException.class);
    }

    // =========================================================================
    // deactivate
    // =========================================================================

    @Test
    @DisplayName("deactivate: should set active to false")
    void deactivate_ShouldSetActiveFalse() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();

        employee.deactivate("baja voluntaria");

        assertThat(employee.isActive()).isFalse();
    }

    @Test
    @DisplayName("deactivate: should raise EmployeeDeactivatedEvent with reason")
    void deactivate_ShouldRaiseEvent() {
        Employee employee = buildVetEmployee();
        employee.clearDomainEvents();

        employee.deactivate("baja voluntaria");

        assertThat(employee.getDomainEvents()).hasSize(1);
        EmployeeDeactivatedEvent event = (EmployeeDeactivatedEvent) employee.getDomainEvents().get(0);
        assertThat(event.getReason()).isEqualTo("baja voluntaria");
    }

    @Test
    @DisplayName("deactivate: should throw when employee is already inactive")
    void deactivate_WhenAlreadyInactive_ShouldThrow() {
        Employee employee = buildVetEmployee();
        employee.deactivate("baja voluntaria");

        assertThatThrownBy(() -> employee.deactivate("again"))
                .isInstanceOf(EmployeeValidationException.class)
                .hasMessageContaining("inactivo");
    }

    // =========================================================================
    // getFullName
    // =========================================================================

    @Test
    @DisplayName("getFullName: should concatenate firstName and lastName")
    void getFullName_ShouldConcatenate() {
        Employee employee = buildVetEmployee();
        assertThat(employee.getFullName()).isEqualTo("María López");
    }

    // =========================================================================
    // Domain events lifecycle
    // =========================================================================

    @Test
    @DisplayName("getDomainEvents: should return immutable list")
    void getDomainEvents_ShouldReturnImmutableList() {
        Employee employee = buildVetEmployee();

        assertThatThrownBy(() -> employee.getDomainEvents().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("clearDomainEvents: should empty the events list")
    void clearDomainEvents_ShouldEmptyEvents() {
        Employee employee = buildVetEmployee();
        assertThat(employee.getDomainEvents()).isNotEmpty();

        employee.clearDomainEvents();

        assertThat(employee.getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Employee buildVetEmployee() {
        return Employee.create(
                "user-1", "clinic-1",
                "María", "López",
                documentNumber, phone, address,
                "https://avatar.png",
                "Cirugía", "VET-12345",
                LocalDate.now().minusDays(1),
                "CLINIC_VETERINARIAN"
        );
    }
}
