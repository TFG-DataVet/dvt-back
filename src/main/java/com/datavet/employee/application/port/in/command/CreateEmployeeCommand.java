package com.datavet.employee.application.port.in.command;

import com.datavet.employee.domain.valueobject.WorkSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Command para crear un empleado con los datos obligatorios.
 * Salary, WorkSchedule y VacationPolicy se añaden después con
 * sus propios commands específicos.
 * El role se incluye solo para validar licenseNumber en el dominio.
 */
@Value
@Builder
public class CreateEmployeeCommand {
    String          userId;
    String          clinicId;
    String          firstName;
    String          lastName;
    DocumentId      documentNumber;
    Phone           phone;
    String          email;
    Address         address;
    String          avatarUrl;
    String          speciality;
    String          licenseNumber;   // obligatorio si role == CLINIC_VETERINARIAN
    LocalDate       hireDate;
    String          role;            // usado solo para validación de licenseNumber
}