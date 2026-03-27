package com.datavet.auth.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailVerifiedEvent implements DomainEvent {

    private String        userId;
    private String        email;
    private LocalDateTime occurredOn;

    public static UserEmailVerifiedEvent of(String userId, String email) {
        return new UserEmailVerifiedEvent(userId, email, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format(
                "UserEmailVerifiedEvent{userId='%s', email='%s', occurredOn=%s}",
                userId, email, occurredOn);
    }
}