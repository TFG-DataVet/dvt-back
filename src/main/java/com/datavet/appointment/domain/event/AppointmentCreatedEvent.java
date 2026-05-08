package com.datavet.appointment.domain.event;

import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AppointmentCreatedEvent implements DomainEvent {

    private final String            appointmentId;
    private final String            clinicId;
    private final AppointmentType   type;
    private final boolean           emergency;
    private final LocalDateTime     scheduledAt;
    private final LocalDateTime     occurredOn;

    public static AppointmentCreatedEvent of(
            String appointmentId,
            String clinicId,
            AppointmentType type,
            boolean emergency,
            LocalDateTime scheduledAt) {

        return new AppointmentCreatedEvent(
                appointmentId, clinicId, type, emergency, scheduledAt, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
