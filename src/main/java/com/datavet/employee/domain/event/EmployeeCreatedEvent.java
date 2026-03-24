package com.datavet.employee.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreatedEvent implements DomainEvent {

    private String        employeeId;
    private String        firstName;
    private String        lastName;
    private String        clinicId;
    private String        role;
    private LocalDateTime occurredOn;

    public static EmployeeCreatedEvent of(String employeeId, String firstName,
                                          String lastName, String clinicId, String role) {
        return new EmployeeCreatedEvent(employeeId, firstName, lastName,
                clinicId, role, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "EmployeeCreatedEvent{employeeId='%s', firstName='%s', lastName='%s', clinicId='%s', role='%s', occurredOn=%s}",
                employeeId, firstName, lastName, clinicId, role, occurredOn);
    }
}