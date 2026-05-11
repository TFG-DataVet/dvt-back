package com.datavet.appointment.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class CancelAppointmentCommand {

    String appointmentId;
    String clinicId;
    String reason;
}
