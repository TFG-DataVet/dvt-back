package com.datavet.appointment.application.port.out;

import com.datavet.appointment.domain.model.Appointment;

public interface AppointmentEmailPort {

    void sendAppointmentCreatedEmail(
            String toEmail,
            String ownerName,
            Appointment appointment);
}
