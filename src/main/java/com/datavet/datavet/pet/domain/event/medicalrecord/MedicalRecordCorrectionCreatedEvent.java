package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MedicalRecordCorrectionCreatedEvent implements DomainEvent
{

    public final String correctedRecordId;
    public final String existingRecordId;
    public final String reason;
    public final LocalDateTime occurredOn = LocalDateTime.now();

    public static MedicalRecordCorrectionCreatedEvent of(String correctedRecordId, String existingRecordId, String reason){
        return new MedicalRecordCorrectionCreatedEvent(correctedRecordId, existingRecordId, reason);
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
