package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetBirthDateCorrectedEvent implements DomainEvent {

    private final String petId;
    private final LocalDate previousBirthDate;
    private final LocalDate newBirthDate;
    private final String reason;
    private final LocalDateTime occurredOn;

    public static PetBirthDateCorrectedEvent of(
            String petId,
            LocalDate previousBirthDate,
            LocalDate newBirthDate,
            String reason
    ){
        return new PetBirthDateCorrectedEvent(
                petId,
                previousBirthDate,
                newBirthDate,
                reason,
                LocalDateTime.now());
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
