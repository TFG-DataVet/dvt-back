package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OwnerUpdatedEvent implements DomainEvent {

    private final String ownerId;
    private final String ownerName;
    private final LocalDateTime timestamp;

    public static OwnerUpdatedEvent of(String ownerId, String ownerName) {
        return new OwnerUpdatedEvent(ownerId, ownerName, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.timestamp;
    }
}
