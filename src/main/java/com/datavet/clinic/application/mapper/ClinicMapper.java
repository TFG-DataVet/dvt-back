package com.datavet.clinic.application.mapper;

import com.datavet.clinic.application.dto.ClinicResponse;
import com.datavet.clinic.domain.model.Clinic;

public class ClinicMapper {

    private ClinicMapper() {}

    public static ClinicResponse toResponse(Clinic clinic) {

        ClinicResponse.AddressDto addressDto = clinic.getAddress() != null
                ? new ClinicResponse.AddressDto(
                clinic.getAddress().getStreet(),
                clinic.getAddress().getCity(),
                clinic.getAddress().getPostalCode())
                : null;

        ClinicResponse.ScheduleDto scheduleDto = clinic.getSchedule() != null
                ? new ClinicResponse.ScheduleDto(
                clinic.getSchedule().getOpenDays(),
                clinic.getSchedule().getOpenTime(),
                clinic.getSchedule().getCloseTime(),
                clinic.getSchedule().getNotes())
                : null;

        return new ClinicResponse(
                clinic.getClinicID(),
                clinic.getClinicName(),
                clinic.getLegalName(),
                clinic.getLegalNumber(),
                clinic.getLegalType(),
                addressDto,
                clinic.getPhone()  != null ? clinic.getPhone().getValue()  : null,
                clinic.getEmail()  != null ? clinic.getEmail().getValue()  : null,
                clinic.getLogoUrl(),
                scheduleDto,
                clinic.getStatus()
        );
    }
}