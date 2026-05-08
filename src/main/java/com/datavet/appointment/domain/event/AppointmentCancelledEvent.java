package com.datavet.appointment.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AppointmentCancelledEvent implements DomainEvent {

    private final String        appointmentId;
    private final String        clinicId;
    private final String        reason;
    private final LocalDateTime occurredOn;

    public static AppointmentCancelledEvent of(
            String appointmentId, String clinicId, String reason) {

        return new AppointmentCancelledEvent(appointmentId, clinicId, reason, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
