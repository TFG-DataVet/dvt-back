package com.datavet.appointment.domain.exception;

import com.datavet.shared.domain.exception.EntityNotFoundException;

public class AppointmentNotFoundException extends EntityNotFoundException {

    public AppointmentNotFoundException(String id) {
        super("Appointment", "id", id);
    }
}
