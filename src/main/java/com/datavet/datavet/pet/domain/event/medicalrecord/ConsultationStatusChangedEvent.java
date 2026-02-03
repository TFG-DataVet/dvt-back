package com.datavet.datavet.pet.domain.event.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationStatus;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ConsultationStatusChangedEvent implements DomainEvent {

    private final String id;
    private final ConsultationStatus previous;
    private final ConsultationStatus actual;
    private final String veterinarianId;
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public static ConsultationStatusChangedEvent of(String id,
                                                    ConsultationStatus previous,
                                                    ConsultationStatus actual,
                                                    String veterinarianId){
        return new ConsultationStatusChangedEvent(id, previous, actual, veterinarianId);
    };

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public int eventVersion() {
        return DomainEvent.super.eventVersion();
    }
}
