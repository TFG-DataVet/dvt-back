package com.datavet.appointment.application.port.in.command;

import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Value
@Builder
public class CreateAppointmentCommand {

    String            clinicId;
    boolean           emergency;
    AppointmentType   type;
    LocalDateTime     scheduledAt;

    // Owner (null when emergency)
    String ownerId;
    String ownerName;
    String ownerEmail;
    String ownerPhone;

    // Pet (null when emergency)
    String petId;
    String petName;
    String petSpecies;

    // Employees
    String creationEmployeeId;
    String medicalEmployeeId;

    // Optional
    String       notes;
    List<String> productIds;
    AppointmentSource source;
}
