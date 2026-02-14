package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MedicalRecordStatusChangeEvent implements DomainEvent {

    private final String medicalRecordId;
    private final String petId;
    private final String clinicId;
    private final String previousStatus;
    private final String newStatus;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static MedicalRecordStatusChangeEvent of(String medicalRecordId,
                                                 String petId,
                                                 String clinicId,
                                                 String previousStatus,
                                                 String newStatus){
        return new MedicalRecordStatusChangeEvent(medicalRecordId, petId, clinicId, previousStatus, newStatus);
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
