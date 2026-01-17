package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetUpdateEvent implements DomainEvent {

    private final String id;
    private final String previousName;
    private final String name;
    private final LocalDateTime occurredOn;

    public static PetUpdateEvent of(String id, String previousName ,String name){
        return new PetUpdateEvent(id, previousName, name, LocalDateTime.now());
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
