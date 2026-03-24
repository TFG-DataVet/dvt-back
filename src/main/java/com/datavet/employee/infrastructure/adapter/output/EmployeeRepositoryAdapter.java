package com.datavet.employee.infrastructure.adapter.output;

import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.employee.domain.model.Employee;
import com.datavet.employee.domain.valueobject.Salary;
import com.datavet.employee.domain.valueobject.VacationPolicy;
import com.datavet.employee.domain.valueobject.WorkSchedule;
import com.datavet.employee.infrastructure.persistence.document.EmployeeDocument;
import com.datavet.employee.infrastructure.persistence.repository.MongoEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {

    private final MongoEmployeeRepository repository;

    // -------------------------------------------------------------------------
    // Mappers internos
    // -------------------------------------------------------------------------

    private EmployeeDocument toDocument(Employee employee) {
        return EmployeeDocument.builder()
                .id(employee.getId())
                .userId(employee.getUserId())
                .clinicId(employee.getClinicId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .documentNumber(employee.getDocumentNumber())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .avatarUrl(employee.getAvatarUrl())
                .speciality(employee.getSpeciality())
                .licenseNumber(employee.getLicenseNumber())
                .hireDate(employee.getHireDate())
                // Salary aplanado
                .salaryAmount(employee.getSalary() != null
                        ? employee.getSalary().getAmount() : null)
                .salaryCurrency(employee.getSalary() != null
                        ? employee.getSalary().getCurrency() : null)
                .salaryPaymentsPerYear(employee.getSalary() != null
                        ? employee.getSalary().getPaymentsPerYear() : null)
                .salaryEffectiveFrom(employee.getSalary() != null
                        ? employee.getSalary().getEffectiveFrom() : null)
                // VacationPolicy aplanado
                .vacationAnnualDays(employee.getVacationPolicy() != null
                        ? employee.getVacationPolicy().getAnnualDays() : null)
                .vacationEffectiveFrom(employee.getVacationPolicy() != null
                        ? employee.getVacationPolicy().getEffectiveFrom() : null)
                // WorkSchedule aplanado
                .scheduleWeeklyHours(employee.getWorkSchedule() != null
                        ? employee.getWorkSchedule().getWeeklyHours() : null)
                .scheduleWorkDays(employee.getWorkSchedule() != null
                        ? employee.getWorkSchedule().getWorkDays() : null)
                .scheduleEntryTime(employee.getWorkSchedule() != null
                        ? employee.getWorkSchedule().getEntryTime() : null)
                .scheduleExitTime(employee.getWorkSchedule() != null
                        ? employee.getWorkSchedule().getExitTime() : null)
                .scheduleNotes(employee.getWorkSchedule() != null
                        ? employee.getWorkSchedule().getNotes() : null)
                .active(employee.isActive())
                .build();
    }

    private Employee toDomain(EmployeeDocument doc) {
        // Reconstruimos Salary solo si hay datos
        Salary salary = null;
        if (doc.getSalaryAmount() != null) {
            salary = Salary.of(
                    doc.getSalaryAmount(),
                    doc.getSalaryCurrency(),
                    doc.getSalaryPaymentsPerYear(),
                    doc.getSalaryEffectiveFrom()
            );
        }

        // Reconstruimos VacationPolicy solo si hay datos
        VacationPolicy vacationPolicy = null;
        if (doc.getVacationAnnualDays() != null) {
            vacationPolicy = VacationPolicy.of(
                    doc.getVacationAnnualDays(),
                    doc.getVacationEffectiveFrom()
            );
        }

        // Reconstruimos WorkSchedule solo si hay datos
        WorkSchedule workSchedule = null;
        if (doc.getScheduleWeeklyHours() != null) {
            workSchedule = WorkSchedule.of(
                    doc.getScheduleWeeklyHours(),
                    doc.getScheduleWorkDays(),
                    doc.getScheduleEntryTime(),
                    doc.getScheduleExitTime(),
                    doc.getScheduleNotes()
            );
        }

        return Employee.reconstitute(
                doc.getId(),
                doc.getUserId(),
                doc.getClinicId(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.getDocumentNumber(),
                doc.getPhone(),
                doc.getAddress(),
                doc.getAvatarUrl(),
                doc.getSpeciality(),
                doc.getLicenseNumber(),
                doc.getHireDate(),
                salary,
                vacationPolicy,
                workSchedule,
                doc.isActive(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    // -------------------------------------------------------------------------
    // Repository base
    // -------------------------------------------------------------------------

    @Override
    public Employee save(Employee employee) {
        return toDomain(repository.save(toDocument(employee)));
    }

    @Override
    public Optional<Employee> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        // No se usa — soft delete mediante deactivate()
        throw new UnsupportedOperationException(
                "Employee no soporta hard delete. Usa deactivateEmployee()");
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    // -------------------------------------------------------------------------
    // Domain-specific
    // -------------------------------------------------------------------------

    @Override
    public List<Employee> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Employee> findByClinicIdAndActiveTrue(String clinicId) {
        return repository.findByClinicIdAndActiveTrue(clinicId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<Employee> findByUserId(String userId) {
        return repository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByDocumentNumberAndClinicId(String documentNumber, String clinicId) {
        return repository.existsByDocumentNumberAndClinicId(documentNumber, clinicId);
    }

    @Override
    public boolean existsByDocumentNumberAndClinicIdAndIdNot(String documentNumber,
                                                             String clinicId,
                                                             String employeeId) {
        return repository.existsByDocumentNumberAndClinicIdAndIdNot(
                documentNumber, clinicId, employeeId);
    }
}