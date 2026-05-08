package com.datavet.appointment.domain.event;

import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AppointmentStatusChangedEvent implements DomainEvent {

    private final String             appointmentId;
    private final String             clinicId;
    private final AppointmentStatus  previousStatus;
    private final AppointmentStatus  newStatus;
    private final LocalDateTime      occurredOn;

    public static AppointmentStatusChangedEvent of(
            String appointmentId,
            String clinicId,
            AppointmentStatus previousStatus,
            AppointmentStatus newStatus) {

        return new AppointmentStatusChangedEvent(
                appointmentId, clinicId, previousStatus, newStatus, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
