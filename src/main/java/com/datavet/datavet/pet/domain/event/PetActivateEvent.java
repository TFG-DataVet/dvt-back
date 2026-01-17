package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetActivateEvent implements DomainEvent {

    private final String id;
    private final LocalDateTime occurredOn;

    public static PetActivateEvent of(String id){
        return new PetActivateEvent(id, LocalDateTime.now());
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
