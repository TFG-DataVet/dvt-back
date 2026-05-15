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
public final class AntiparasiticDetailsRequest extends ProductDetailsRequest {

    private String       activeIngredient;
    private String       manufacturer;
    private String       batchNumber;
    private LocalDate    expirationDate;
    private List<String> species;
    private String       storageConditions;
    private String       antiparasiticType;
    private String       applicationForm;
    private BigDecimal   weightRangeMinKg;
    private BigDecimal   weightRangeMaxKg;
    private Integer      durationDays;
    private List<String> activeSubstances;
    private String       breedSizeTarget;

    @Override
    public ProductCategory getCategory() { return ProductCategory.ANTIPARASITIC; }
}
