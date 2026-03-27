package com.datavet.auth.domain.event;

import com.datavet.auth.domain.model.UserRole;
import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent implements DomainEvent {

    private String    userId;
    private String    email;
    private UserRole  role;
    private String    clinicId;
    private LocalDateTime occurredOn;

    public static UserCreatedEvent of(String userId, String email,
                                      UserRole role, String clinicId) {
        return new UserCreatedEvent(userId, email, role, clinicId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "UserCreatedEvent{userId='%s', email='%s', role=%s, clinicId='%s', occurredOn=%s}",
                userId, email, role, clinicId, occurredOn);
    }
}