package com.datavet.datavet.pet.domain.event;

import com.datavet.datavet.pet.domain.model.OwnerInfo;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetUpdateOwnerInfoEvent implements DomainEvent {

    private final String petId;
    private final OwnerInfo previousOwnerInfo;
    private final OwnerInfo newOwnerInfo;
    private final LocalDateTime occurredOn;

    public static PetUpdateOwnerInfoEvent of(
            String petId,
            OwnerInfo previousOwnerInfo,
            OwnerInfo newOwnerInfo){
        return new PetUpdateOwnerInfoEvent(petId, previousOwnerInfo, newOwnerInfo , LocalDateTime.now());
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
