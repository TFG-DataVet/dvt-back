package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OwnerDeletedEvent implements DomainEvent {

    private final Long ownerID;
    private final String name;
    private final LocalDateTime occurredOn;

    public static OwnerDeletedEvent of(Long ownerID, String name) {
        return new OwnerDeletedEvent(ownerID, name, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
