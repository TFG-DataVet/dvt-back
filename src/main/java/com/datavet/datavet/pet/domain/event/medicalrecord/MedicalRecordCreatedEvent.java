package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MedicalRecordCreatedEvent implements DomainEvent {

    private final String medicalRecordId;
    private final String petId;
    private final String clinicId;
    private final MedicalRecordType type;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static MedicalRecordCreatedEvent of(String medicalRecordId,
                                               String petId,
                                               String clinicId,
                                               MedicalRecordType type){
        return new MedicalRecordCreatedEvent(medicalRecordId, petId, clinicId, type);
    }

    @Override
    public LocalDateTime occurredOn() { return occurredOn; }

    @Override
    public int eventVersion() {
        return DomainEvent.super.eventVersion();
    }
}
