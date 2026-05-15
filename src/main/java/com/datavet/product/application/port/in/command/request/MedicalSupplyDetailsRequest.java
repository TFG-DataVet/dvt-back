package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public final class MedicalSupplyDetailsRequest extends ProductDetailsRequest {

    private String    manufacturer;
    private String    referenceCode;
    private String    unitOfMeasure;
    private Integer   quantityPerUnit;
    private Boolean   isSterile;
    private String    batchNumber;
    private LocalDate expirationDate;

    @Override
    public ProductCategory getCategory() { return ProductCategory.MEDICAL_SUPPLY; }
}
