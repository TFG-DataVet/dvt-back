package com.datavet.employee.application.port.out;

import com.datavet.auth.domain.model.UserRole;

public interface UserCreationPort {
    String createPendingEmployeeUser(String clinicId, String employeeId,
                                     String email, UserRole role);
    
}