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
@TypeAlias("medication")
@Document
public final class MedicationDetails extends ProductDetails {

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

    public static MedicationDetails create(
            String activeIngredient, String dosageForm, String concentration,
            String manufacturer, String registrationNumber, Boolean prescriptionRequired,
            String storageConditions, String batchNumber, LocalDate expirationDate,
            List<String> species, String administrationRoute) {

        MedicationDetails d = new MedicationDetails(
                activeIngredient, dosageForm, concentration, manufacturer, registrationNumber,
                prescriptionRequired, storageConditions, batchNumber, expirationDate,
                species, administrationRoute);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.MEDICATION; }
    @Override public void validate() { }
}
