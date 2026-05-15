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
public final class VaccineDetailsRequest extends ProductDetailsRequest {

    private String       activeIngredient;
    private String       dosageForm;
    private String       concentration;
    private String       manufacturer;
    private String       registrationNumber;
    private Boolean      prescriptionRequired;
    private String       storageConditions;
    private String       batchNumber;
    private LocalDate    expirationDate;
    private List<String> species;
    private String       administrationRoute;
    private String       vaccineType;
    private Integer      boosterIntervalDays;
    private List<String> diseaseProtection;

    @Override
    public ProductCategory getCategory() { return ProductCategory.VACCINE; }
}
