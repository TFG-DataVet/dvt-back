package com.datavet.product.domain.details;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("grooming_service")
@Document
public final class GroomingServiceDetails extends ProductDetails {

    private String       brand;
    private String       scentType;
    private List<String> speciesSuitability;
    private Integer      volumeMl;
    private Integer      durationMinutes;
    private String       breedSizeTarget;
    private Boolean      includesScent;
    private Boolean      requiresAppointment;

    public static GroomingServiceDetails create(
            String brand, String scentType, List<String> speciesSuitability, Integer volumeMl,
            Integer durationMinutes, String breedSizeTarget,
            Boolean includesScent, Boolean requiresAppointment) {

        GroomingServiceDetails d = new GroomingServiceDetails(brand, scentType, speciesSuitability,
                volumeMl, durationMinutes, breedSizeTarget, includesScent, requiresAppointment);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.GROOMING_SERVICE; }
    @Override public void validate() { }
}
