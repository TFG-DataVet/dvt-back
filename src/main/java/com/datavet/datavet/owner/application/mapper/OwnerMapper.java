package com.datavet.datavet.owner.application.mapper;

import com.datavet.datavet.clinic.application.dto.ClinicResponse;
import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.domain.model.Owner;

public class OwnerMapper {

    public static OwnerResponse toResponse(Owner owner){
        OwnerResponse.AddressDto addressDto = new OwnerResponse.AddressDto(
                owner.getOwnerAddress().getStreet(),
                owner.getOwnerAddress().getCity(),
                owner.getOwnerAddress().getPostalCode()
        );

        return new OwnerResponse(
                owner.getOwnerID(),
                owner.getOwnerName(),
                owner.getOwnerLastName(),
                owner.getOwnerDni(),
                owner.getOwnerPhone(),
                owner.getOwnerEmail(),
                addressDto
        );
    }
}
