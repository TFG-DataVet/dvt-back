package com.datavet.datavet.clinic.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event raised when a clinic is updated.
 */
@Getter
@RequiredArgsConstructor
public class ClinicUpdatedEvent implements DomainEvent {
    
    private final Long clinicId;
    private final String clinicName;
    private final LocalDateTime occurredOn;
    
    public static ClinicUpdatedEvent of(Long clinicId, String clinicName) {
        return new ClinicUpdatedEvent(clinicId, clinicName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}