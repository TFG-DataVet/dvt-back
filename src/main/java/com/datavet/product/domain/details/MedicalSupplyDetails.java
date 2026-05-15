package com.datavet.product.domain.details;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("medical_supply")
@Document
public final class MedicalSupplyDetails extends ProductDetails {

    private String    manufacturer;
    private String    referenceCode;
    private String    unitOfMeasure;
    private Integer   quantityPerUnit;
    private Boolean   isSterile;
    private String    batchNumber;
    private LocalDate expirationDate;

    public static MedicalSupplyDetails create(String manufacturer, String referenceCode,
                                               String unitOfMeasure, Integer quantityPerUnit,
                                               Boolean isSterile, String batchNumber,
                                               LocalDate expirationDate) {
        MedicalSupplyDetails d = new MedicalSupplyDetails(manufacturer, referenceCode,
                unitOfMeasure, quantityPerUnit, isSterile, batchNumber, expirationDate);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.MEDICAL_SUPPLY; }
    @Override public void validate() { }
}
