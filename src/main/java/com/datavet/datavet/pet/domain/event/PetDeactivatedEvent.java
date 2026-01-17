package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetDeactivatedEvent implements DomainEvent {

    private final String id;
    private final String reason;
    private final LocalDateTime occurredOn;

    public static PetDeactivatedEvent of(String id, String reason){
        return new PetDeactivatedEvent(id, reason, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public int eventVersion() {
        return DomainEvent.super.eventVersion();
    }
}
