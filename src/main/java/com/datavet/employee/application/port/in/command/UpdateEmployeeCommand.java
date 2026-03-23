package com.datavet.employee.application.port.in.command;

import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateEmployeeCommand {
    String  employeeId;
    String  firstName;
    String  lastName;
    DocumentId documentNumber;
    Phone   phone;
    Address address;
    String  avatarUrl;
    String  speciality;
    String  licenseNumber;
    String  role;
}