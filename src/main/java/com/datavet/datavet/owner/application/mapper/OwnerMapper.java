package com.datavet.datavet.owner.application.mapper;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.domain.model.Owner;

public class OwnerMapper {

    public static OwnerResponse toResponse(Owner owner){
        OwnerResponse.AddressDto addressDto = new OwnerResponse.AddressDto(
                owner.getAddress().getStreet(),
                owner.getAddress().getCity(),
                owner.getAddress().getPostalCode()
        );

        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getLastName(),
                owner.getDocumentNumber(),
                owner.getPhone(),
                owner.getEmail(),
                addressDto
        );
    }
}
