package com.datavet.employee.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdatedEvent implements DomainEvent {

    private String        employeeId;
    private String        firstName;
    private String        lastName;
    private LocalDateTime occurredOn;

    public static EmployeeUpdatedEvent of(String employeeId,
                                          String firstName, String lastName) {
        return new EmployeeUpdatedEvent(employeeId, firstName, lastName, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "EmployeeUpdatedEvent{employeeId='%s', firstName='%s', lastName='%s', occurredOn=%s}",
                employeeId, firstName, lastName, occurredOn);
    }
}