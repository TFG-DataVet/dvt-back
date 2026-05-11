package com.datavet.appointment.application.mapper;

import com.datavet.appointment.application.dto.AppointmentResponse;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.model.PetSnapshot;

import java.util.List;

public class AppointmentMapper {

    private AppointmentMapper() {}

    public static AppointmentResponse toResponse(Appointment appt) {
        AppointmentResponse.PetSnapshotDto petDto = null;
        if (appt.getPet() != null) {
            PetSnapshot p = appt.getPet();
            petDto = new AppointmentResponse.PetSnapshotDto(p.getPetId(), p.getName(), p.getSpecies());
        }

        return new AppointmentResponse(
                appt.getId(),
                appt.getClinicId(),
                appt.isEmergency(),
                appt.getType(),
                appt.getStatus(),
                appt.getScheduledAt(),
                appt.getOwnerId(),
                appt.getOwnerName(),
                appt.getOwnerEmail(),
                appt.getOwnerPhone(),
                petDto,
                appt.getCreationEmployeeId(),
                appt.getMedicalEmployeeId(),
                appt.getNotes(),
                appt.getProductIds(),
                appt.getSource(),
                appt.getCreatedAt(),
                appt.getUpdatedAt()
        );
    }

    public static List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        return appointments.stream().map(AppointmentMapper::toResponse).toList();
    }
}
