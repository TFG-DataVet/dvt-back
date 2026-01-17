package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetBreedCorrectedEvent implements DomainEvent {

    private final String petId;
    private final String previousBreed;
    private final String newBreed;
    private final String reason;
    private final LocalDateTime occurredOn;

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    public static PetBreedCorrectedEvent of (
            String petId,
            String previousBreed,
            String newBreed,
            String reason
    ){
        return new PetBreedCorrectedEvent(
                petId,
                previousBreed,
                newBreed,
                reason,
                LocalDateTime.now()
        );
    }
}
