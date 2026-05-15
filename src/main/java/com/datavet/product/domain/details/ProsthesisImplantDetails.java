package com.datavet.product.domain.details;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("prosthesis_implant")
@Document
public final class ProsthesisImplantDetails extends ProductDetails {

    private String       implantType;
    private String       manufacturer;
    private String       referenceCode;
    private List<String> compatibleSpecies;
    private Boolean      requiresSurgery;
    private String       isoStandard;
    private String       batchNumber;
    private LocalDate    expirationDate;

    public static ProsthesisImplantDetails create(
            String implantType, String manufacturer, String referenceCode,
            List<String> compatibleSpecies, Boolean requiresSurgery, String isoStandard,
            String batchNumber, LocalDate expirationDate) {

        ProsthesisImplantDetails d = new ProsthesisImplantDetails(implantType, manufacturer,
                referenceCode, compatibleSpecies, requiresSurgery, isoStandard,
                batchNumber, expirationDate);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.PROSTHESIS_IMPLANT; }
    @Override public void validate() { }
}
