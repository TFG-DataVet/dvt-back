package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.pet.domain.model.OwnerInfo;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetCreatedEvent implements DomainEvent {

    private final String name;
    private final String clinicId;
    private final OwnerInfo owner;
    private final String chipNumber;
    private final LocalDateTime occurredOn;


    public static PetCreatedEvent of(
            String name,
            String clinicId,
            OwnerInfo owner,
            String chipNumber
    ) {
        return new PetCreatedEvent(
                name,
                clinicId,
                owner,
                chipNumber,
                LocalDateTime.now()
        );
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
