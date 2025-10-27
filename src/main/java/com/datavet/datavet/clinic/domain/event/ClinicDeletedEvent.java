package com.datavet.datavet.clinic.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event raised when a clinic is deleted.
 */
@Getter
@RequiredArgsConstructor
public class ClinicDeletedEvent implements DomainEvent {
    
    private final Long clinicId;
    private final String clinicName;
    private final LocalDateTime occurredOn;
    
    public static ClinicDeletedEvent of(Long clinicId, String clinicName) {
        return new ClinicDeletedEvent(clinicId, clinicName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}