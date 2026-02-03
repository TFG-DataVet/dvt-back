package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationStatus;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class HospitalizationStatusChangedEvent implements DomainEvent {

    private final String medicalRecordId;
    private final HospitalizationStatus previousStatus;
    private final HospitalizationStatus newStatus;
    private final String veterinarianId;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static HospitalizationStatusChangedEvent of (String medicalRecordId,
                                                        HospitalizationStatus previousStatus,
                                                        HospitalizationStatus newStatus,
                                                        String veterinarianId){
        return new HospitalizationStatusChangedEvent(medicalRecordId, previousStatus, newStatus, veterinarianId);
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
