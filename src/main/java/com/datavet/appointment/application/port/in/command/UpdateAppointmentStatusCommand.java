package com.datavet.appointment.application.port.in.command;

import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
public class UpdateAppointmentStatusCommand {

    String            appointmentId;
    String            clinicId;
    AppointmentStatus newStatus;
    String            medicalEmployeeId;
}
