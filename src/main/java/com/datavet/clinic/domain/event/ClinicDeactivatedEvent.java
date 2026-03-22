package com.datavet.clinic.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event raised when a clinic is deleted.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicDeactivatedEvent implements DomainEvent {
    
    private String clinicId;
    private String clinicName;
    private String reason;
    private LocalDateTime occurredOn;
    
    public static ClinicDeactivatedEvent of(String clinicId, String clinicName, String reason) {
        return new ClinicDeactivatedEvent(clinicId, clinicName, reason, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format("ClinicCreatedEvent{clinicId=%s, clinicName='%s', legalName='%s', occurredOn=%s}",
                clinicId, clinicName, reason, occurredOn);
    }
}