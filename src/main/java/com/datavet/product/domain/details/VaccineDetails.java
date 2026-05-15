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
@TypeAlias("vaccine")
@Document
public final class VaccineDetails extends ProductDetails {

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

    public static VaccineDetails create(
            String activeIngredient, String dosageForm, String concentration,
            String manufacturer, String registrationNumber, Boolean prescriptionRequired,
            String storageConditions, String batchNumber, LocalDate expirationDate,
            List<String> species, String administrationRoute,
            String vaccineType, Integer boosterIntervalDays, List<String> diseaseProtection) {

        VaccineDetails d = new VaccineDetails(
                activeIngredient, dosageForm, concentration, manufacturer, registrationNumber,
                prescriptionRequired, storageConditions, batchNumber, expirationDate,
                species, administrationRoute, vaccineType, boosterIntervalDays, diseaseProtection);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.VACCINE; }
    @Override public void validate() { }
}
