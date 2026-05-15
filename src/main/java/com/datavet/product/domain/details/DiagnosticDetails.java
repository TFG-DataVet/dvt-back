package com.datavet.product.domain.details;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("diagnostic")
@Document
public final class DiagnosticDetails extends ProductDetails {

    private String       manufacturer;
    private String       referenceCode;
    private String       unitOfMeasure;
    private Integer      quantityPerUnit;
    private Boolean      isSterile;
    private String       batchNumber;
    private LocalDate    expirationDate;
    private String       targetAnalyte;
    private List<String> compatibleSpecies;
    private BigDecimal   sensitivityPercent;
    private BigDecimal   specificityPercent;

    public static DiagnosticDetails create(
            String manufacturer, String referenceCode, String unitOfMeasure,
            Integer quantityPerUnit, Boolean isSterile, String batchNumber,
            LocalDate expirationDate, String targetAnalyte, List<String> compatibleSpecies,
            BigDecimal sensitivityPercent, BigDecimal specificityPercent) {

        DiagnosticDetails d = new DiagnosticDetails(manufacturer, referenceCode, unitOfMeasure,
                quantityPerUnit, isSterile, batchNumber, expirationDate,
                targetAnalyte, compatibleSpecies, sensitivityPercent, specificityPercent);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.DIAGNOSTIC; }
    @Override public void validate() { }
}
