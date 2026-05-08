package com.datavet.appointment.domain.exception;

import com.datavet.appointment.domain.valueobject.AppointmentStatus;

public class AppointmentStateException extends AppointmentDomainException {

    public AppointmentStateException(AppointmentStatus current, AppointmentStatus target) {
        super("No se puede cambiar el estado de " + current + " a " + target);
    }

    public AppointmentStateException(String message) {
        super(message);
    }
}
