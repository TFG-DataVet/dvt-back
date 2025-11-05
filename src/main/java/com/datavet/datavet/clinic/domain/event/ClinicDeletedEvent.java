package com.datavet.datavet.clinic.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
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
public class ClinicDeletedEvent implements DomainEvent {
    
    private Long clinicId;
    private String clinicName;
    private LocalDateTime occurredOn;
    
    public static ClinicDeletedEvent of(Long clinicId, String clinicName) {
        return new ClinicDeletedEvent(clinicId, clinicName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}