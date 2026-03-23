package com.datavet.clinic.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicPendingCreatedEvent implements DomainEvent {

    private String clinicId;
    private String clinicName;
    private LocalDateTime occurredOn;

    public static ClinicPendingCreatedEvent of(String clinicId, String clinicName) {
        return new ClinicPendingCreatedEvent(clinicId,clinicName, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return String.format("ClinicCreatedEvent{clinicId=%s, clinicName='%s', occurredOn=%s}",
                clinicId, clinicName, occurredOn);
    }


}
