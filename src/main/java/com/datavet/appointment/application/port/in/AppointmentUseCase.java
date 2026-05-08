package com.datavet.appointment.application.port.in;

import com.datavet.appointment.application.port.in.command.CancelAppointmentCommand;
import com.datavet.appointment.application.port.in.command.CreateAppointmentCommand;
import com.datavet.appointment.application.port.in.command.UpdateAppointmentStatusCommand;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.application.port.UseCase;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentUseCase extends UseCase {

    Appointment create(CreateAppointmentCommand command);

    Appointment updateStatus(UpdateAppointmentStatusCommand command);

    void cancel(CancelAppointmentCommand command);

    Appointment getById(String appointmentId, String clinicId);

    List<Appointment> getByClinic(
            String clinicId,
            LocalDate date,
            AppointmentStatus status,
            AppointmentType type,
            String ownerId);
}
