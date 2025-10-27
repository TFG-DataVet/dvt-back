package com.datavet.datavet.clinic.application.mapper;

import com.datavet.datavet.clinic.application.dto.ClinicResponse;
import com.datavet.datavet.clinic.domain.model.Clinic;

public class ClinicMapper {
    
    public static ClinicResponse toResponse(Clinic clinic) {
        // Convert Address value object to AddressDto
        ClinicResponse.AddressDto addressDto = new ClinicResponse.AddressDto(
                clinic.getAddress().getStreet(),
                clinic.getAddress().getCity(),
                clinic.getAddress().getPostalCode()
        );
        
        return new ClinicResponse(
                clinic.getClinicID(),
                clinic.getClinicName(),
                clinic.getLegalName(),
                clinic.getLegalNumber(),
                addressDto,
                clinic.getPhone().getValue(),  // Extract value from Phone value object
                clinic.getEmail().getValue(),  // Extract value from Email value object
                clinic.getLogoUrl(),
                clinic.getSuscriptionStatus()
        );
    }
}