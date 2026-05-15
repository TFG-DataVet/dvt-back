package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class ProsthesisImplantDetailsRequest extends ProductDetailsRequest {

    private String       implantType;
    private String       manufacturer;
    private String       referenceCode;
    private List<String> compatibleSpecies;
    private Boolean      requiresSurgery;
    private String       isoStandard;
    private String       batchNumber;
    private LocalDate    expirationDate;

    @Override
    public ProductCategory getCategory() { return ProductCategory.PROSTHESIS_IMPLANT; }
}
