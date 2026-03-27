package com.datavet.owner.application.mapper;

import com.datavet.owner.application.dto.OwnerResponse;
import com.datavet.owner.domain.model.Owner;

public class OwnerMapper {

    public static OwnerResponse toResponse(Owner owner){
        OwnerResponse.AddressDto addressDto = new OwnerResponse.AddressDto(
                owner.getAddress().getStreet(),
                owner.getAddress().getCity(),
                owner.getAddress().getPostalCode()
        );

        return new OwnerResponse(
                owner.getOwnerId(),
                owner.getName(),
                owner.getLastName(),
                owner.getDocumentNumber(),
                owner.getPhone().getValue(),
                owner.getEmail().getValue(),
                addressDto
        );
    }
}
