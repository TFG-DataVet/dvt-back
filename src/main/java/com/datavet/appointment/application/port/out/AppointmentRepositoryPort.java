package com.datavet.appointment.application.port.out;

import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.application.port.Repository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepositoryPort extends Repository<Appointment, String> {

    List<Appointment> findByClinicIdWithFilters(
            String clinicId,
            LocalDate date,
            AppointmentStatus status,
            AppointmentType type,
            String ownerId);
}
