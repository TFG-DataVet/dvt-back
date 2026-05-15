package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class DiagnosticDetailsRequest extends ProductDetailsRequest {

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

    @Override
    public ProductCategory getCategory() { return ProductCategory.DIAGNOSTIC; }
}
