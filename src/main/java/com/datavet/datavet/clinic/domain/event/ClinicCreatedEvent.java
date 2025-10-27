package com.datavet.datavet.clinic.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event raised when a new clinic is created.
 */
@Getter
@RequiredArgsConstructor
public class ClinicCreatedEvent implements DomainEvent {
    
    private final Long clinicId;
    private final String clinicName;
    private final String legalName;
    private final LocalDateTime occurredOn;
    
    public static ClinicCreatedEvent of(Long clinicId, String clinicName, String legalName) {
        return new ClinicCreatedEvent(clinicId, clinicName, legalName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
    
    @Override
    public String toString() {
        return String.format("ClinicCreatedEvent{clinicId=%d, clinicName='%s', legalName='%s', occurredOn=%s}", 
                clinicId, clinicName, legalName, occurredOn);
    }
}