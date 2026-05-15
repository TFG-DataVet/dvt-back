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
@TypeAlias("hygiene")
@Document
public final class HygieneDetails extends ProductDetails {

    private String       brand;
    private String       scentType;
    private List<String> speciesSuitability;
    private Integer      volumeMl;

    public static HygieneDetails create(String brand, String scentType,
                                         List<String> speciesSuitability, Integer volumeMl) {
        HygieneDetails d = new HygieneDetails(brand, scentType, speciesSuitability, volumeMl);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.HYGIENE; }
    @Override public void validate() { }
}
