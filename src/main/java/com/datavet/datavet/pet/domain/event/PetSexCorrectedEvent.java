package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.pet.domain.model.Sex;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetSexCorrectedEvent implements DomainEvent {

    private final String petId;
    private final Sex previousSex;
    private final Sex newSex;
    private final String reason;
    private final LocalDateTime occurredOn;

    public static PetSexCorrectedEvent of(
            String petId,
            Sex previousSex,
            Sex newSex,
            String reason
    ) {
        return new PetSexCorrectedEvent(
                petId, previousSex, newSex, reason, LocalDateTime.now()
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
