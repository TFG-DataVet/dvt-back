package com.datavet.auth.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeactivatedEvent implements DomainEvent {

    private String        userId;
    private String        email;
    private String        reason;
    private LocalDateTime occurredOn;

    public static UserDeactivatedEvent of(String userId, String email, String reason) {
        return new UserDeactivatedEvent(userId, email, reason, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "UserDeactivatedEvent{userId='%s', email='%s', reason='%s', occurredOn=%s}",
                userId, email, reason, occurredOn);
    }
}