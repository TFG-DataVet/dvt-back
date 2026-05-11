package com.datavet.appointment.application.dto;

import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AppointmentResponse {

    private String            id;
    private String            clinicId;
    private boolean           emergency;
    private AppointmentType   type;
    private AppointmentStatus status;
    private LocalDateTime     scheduledAt;

    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

    private PetSnapshotDto pet;

    private String creationEmployeeId;
    private String medicalEmployeeId;

    private String       notes;
    private List<String> productIds;
    private AppointmentSource source;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public static class PetSnapshotDto {
        private String petId;
        private String name;
        private String species;
    }
}
