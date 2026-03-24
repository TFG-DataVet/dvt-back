package com.datavet.employee.domain.model;

import com.datavet.employee.domain.event.EmployeeCreatedEvent;
import com.datavet.employee.domain.event.EmployeeDeactivatedEvent;
import com.datavet.employee.domain.event.EmployeeUpdatedEvent;
import com.datavet.employee.domain.exception.EmployeeValidationException;
import com.datavet.employee.domain.valueobject.Salary;
import com.datavet.employee.domain.valueobject.VacationPolicy;
import com.datavet.employee.domain.valueobject.WorkSchedule;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Employee extends AggregateRoot<String> implements Document<String> {

    // Identidad
    private String   id;
    private String   userId;
    private String   clinicId;

    // Datos personales
    private String   firstName;
    private String   lastName;
    private DocumentId documentNumber;
    private Phone    phone;
    private Address  address;
    private String   avatarUrl;

    // Datos laborales
    private String   speciality;
    private String   licenseNumber;   // obligatorio solo para VETERINARIAN
    private LocalDate hireDate;

    // Objetos laborales opcionales
    private Salary         salary;
    private VacationPolicy vacationPolicy;
    private WorkSchedule   workSchedule;

    // Control
    private boolean       active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() {
        return this.id;
    }

    // -------------------------------------------------------------------------
    // Validación
    // -------------------------------------------------------------------------

    private void validate(String role) {
        ValidationResult result = new ValidationResult();

        if (clinicId == null || clinicId.isBlank()) {
            result.addError("Clinic ID", "El ID de la clínica no puede ser nulo o vacío");
        }

        if (userId == null || userId.isBlank()) {
            result.addError("User Id", "El ID del usuario no puede ser nulo o vacío");
        }

        if (firstName == null || firstName.isBlank()) {
            result.addError("Primer nombre", "El nombre no puede ser nulo o vacío");
        }

        if (lastName == null || lastName.isBlank()) {
            result.addError("Apellido", "El apellido no puede ser nulo o vacío");
        }

        if (documentNumber == null) {
            result.addError("Documento de identidad", "El número de documento no puede ser nulo");
        }

        if (phone == null) {
            result.addError("Telefono", "El teléfono no puede ser nulo");
        }

        if (address == null) {
            result.addError("Dirección", "La dirección no puede ser nula");
        }

        if (hireDate == null) {
            result.addError("Fecha de contratación", "La fecha de contratación no puede ser nula");
        }

        if (hireDate != null && hireDate.isAfter(LocalDate.now())) {
            result.addError("Fecha de contratación", "La fecha de contratación no puede ser futura");
        }

        // licenseNumber obligatorio solo para VETERINARIAN
        if ("CLINIC_VETERINARIAN".equals(role) &&
                (licenseNumber == null || licenseNumber.isBlank())) {
            result.addError("Numero de licencia",
                    "El número de tarjeta profesional es obligatorio para veterinarios");
        }

        if (result.hasErrors()) {
            throw new EmployeeValidationException(result);
        }
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /**
     * Crea un empleado con los datos obligatorios.
     * Salary, VacationPolicy y WorkSchedule se completan después.
     * El role se usa solo para validar licenseNumber — no se persiste aquí,
     * vive en User.
     */
    public static Employee create(String userId, String clinicId,
                                  String firstName, String lastName,
                                  DocumentId documentNumber, Phone phone,
                                  Address address, String avatarUrl,
                                  String speciality, String licenseNumber,
                                  LocalDate hireDate, String role) {
        String uuid = UUID.randomUUID().toString();

        Employee employee = new Employee(
                uuid,
                userId,
                clinicId,
                firstName,
                lastName,
                documentNumber,
                phone,
                address,
                avatarUrl,
                speciality,
                licenseNumber,
                hireDate,
                null,   // salary
                null,   // vacationPolicy
                null,   // workSchedule
                true,
                LocalDateTime.now(),
                null
        );

        employee.validate(role);
        employee.addDomainEvent(EmployeeCreatedEvent.of(uuid, firstName, lastName, clinicId, role));
        return employee;
    }

    /**
     * Reconstituye un Employee desde persistencia.
     * Sin validación ni eventos.
     */
    public static Employee reconstitute(String id, String userId, String clinicId,
                                        String firstName, String lastName,
                                        DocumentId documentNumber, Phone phone,
                                        Address address, String avatarUrl,
                                        String speciality, String licenseNumber,
                                        LocalDate hireDate, Salary salary,
                                        VacationPolicy vacationPolicy,
                                        WorkSchedule workSchedule,
                                        boolean active, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        return new Employee(id, userId, clinicId, firstName, lastName,
                documentNumber, phone, address, avatarUrl, speciality,
                licenseNumber, hireDate, salary, vacationPolicy,
                workSchedule, active, createdAt, updatedAt);
    }

    // -------------------------------------------------------------------------
    // Métodos de dominio
    // -------------------------------------------------------------------------

    public void update(String firstName, String lastName, DocumentId documentNumber,
                       Phone phone, Address address, String avatarUrl,
                       String speciality, String licenseNumber, String role) {
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.documentNumber = documentNumber;
        this.phone          = phone;
        this.address        = address;
        this.avatarUrl      = avatarUrl;
        this.speciality     = speciality;
        this.licenseNumber  = licenseNumber;
        this.updatedAt      = LocalDateTime.now();

        this.validate(role);
        addDomainEvent(EmployeeUpdatedEvent.of(this.id, this.firstName, this.lastName));
    }

    public void updateSalary(Salary salary) {
        if (!this.active) {
            throw new EmployeeValidationException("Actualización de salario",
                    "No se puede actualizar el salario de un empleado inactivo");
        }
        this.salary    = salary;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(EmployeeUpdatedEvent.of(this.id, this.firstName, this.lastName));
    }

    public void updateVacationPolicy(VacationPolicy vacationPolicy) {
        if (!this.active) {
            throw new EmployeeValidationException("Actualización de vacaciones",
                    "No se puede actualizar la política de vacaciones de un empleado inactivo");
        }
        this.vacationPolicy = vacationPolicy;
        this.updatedAt      = LocalDateTime.now();
        addDomainEvent(EmployeeUpdatedEvent.of(this.id, this.firstName, this.lastName));
    }

    public void updateWorkSchedule(WorkSchedule workSchedule) {
        if (!this.active) {
            throw new EmployeeValidationException("Actualización de horario",
                    "No se puede actualizar el horario de un empleado inactivo");
        }
        this.workSchedule = workSchedule;
        this.updatedAt    = LocalDateTime.now();
        addDomainEvent(EmployeeUpdatedEvent.of(this.id, this.firstName, this.lastName));
    }

    public void deactivate(String reason) {
        if (!this.active) {
            throw new EmployeeValidationException("Desactivar empleado",
                    "El empleado ya está inactivo");
        }
        this.active    = false;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(EmployeeDeactivatedEvent.of(this.id, this.firstName, this.lastName, reason));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}