package com.datavet.employee.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDeactivatedEvent implements DomainEvent {

    private String        employeeId;
    private String        firstName;
    private String        lastName;
    private String        reason;
    private LocalDateTime occurredOn;

    public static EmployeeDeactivatedEvent of(String employeeId, String firstName,
                                              String lastName, String reason) {
        return new EmployeeDeactivatedEvent(employeeId, firstName, lastName,
                reason, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "EmployeeDeactivatedEvent{employeeId='%s', firstName='%s', lastName='%s', reason='%s', occurredOn=%s}",
                employeeId, firstName, lastName, reason, occurredOn);
    }
}
