package com.datavet.employee.application.port.out;

import com.datavet.employee.domain.model.Employee;
import com.datavet.shared.application.port.Repository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort extends Repository<Employee, String> {

    List<Employee>   findByClinicId             (String clinicId);
    List<Employee>   findByClinicIdAndActiveTrue (String clinicId);
    Optional<Employee> findByUserId             (String userId);
    boolean          existsByDocumentNumberAndClinicId (String documentNumber, String clinicId);
    boolean          existsByDocumentNumberAndClinicIdAndIdNot (String documentNumber,
                                                                String clinicId,
                                                                String employeeId);
}