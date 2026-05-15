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
@TypeAlias("antiparasitic")
@Document
public final class AntiparasiticDetails extends ProductDetails {

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

    public static AntiparasiticDetails create(
            String activeIngredient, String manufacturer, String batchNumber,
            LocalDate expirationDate, List<String> species, String storageConditions,
            String antiparasiticType, String applicationForm,
            BigDecimal weightRangeMinKg, BigDecimal weightRangeMaxKg,
            Integer durationDays, List<String> activeSubstances, String breedSizeTarget) {

        AntiparasiticDetails d = new AntiparasiticDetails(
                activeIngredient, manufacturer, batchNumber, expirationDate, species,
                storageConditions, antiparasiticType, applicationForm,
                weightRangeMinKg, weightRangeMaxKg, durationDays, activeSubstances, breedSizeTarget);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.ANTIPARASITIC; }
    @Override public void validate() { }
}
