package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MedicalRecordCorrectedEvent implements DomainEvent {

    private final String originalRecordId;
    private final String correctedRecordId;
    private final String reason;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static MedicalRecordCorrectedEvent of(String originalRecordId,
                                                 String correctedRecordId,
                                                 String reason){
        return new MedicalRecordCorrectedEvent(
                originalRecordId, correctedRecordId, reason);
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
