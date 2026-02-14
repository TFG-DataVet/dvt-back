package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentStatus;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TreatmentStatusChangedEvent implements DomainEvent {

    private final String medicalRecordId;
    private final TreatmentStatus previousStatus;
    private final TreatmentStatus newStatus;
    private final String veterinarianId;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static TreatmentStatusChangedEvent of(String medicalRecordId,
                                                    TreatmentStatus previousStatus,
                                                    TreatmentStatus newStatus,
                                                    String veterinarianId){
        return  new TreatmentStatusChangedEvent(medicalRecordId, previousStatus, newStatus, veterinarianId);
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
