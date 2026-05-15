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
@TypeAlias("supplement")
@Document
public final class SupplementDetails extends ProductDetails {

    private String       brand;
    private Integer      weightGrams;
    private String       lifeStageSuitability;
    private List<String> speciesSuitability;
    private List<String> flavors;
    private List<String> ingredients;
    private String       nutritionalInfo;
    private String       supplementType;

    public static SupplementDetails create(
            String brand, Integer weightGrams, String lifeStageSuitability,
            List<String> speciesSuitability, List<String> flavors, List<String> ingredients,
            String nutritionalInfo, String supplementType) {

        SupplementDetails d = new SupplementDetails(brand, weightGrams, lifeStageSuitability,
                speciesSuitability, flavors, ingredients, nutritionalInfo, supplementType);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.SUPPLEMENT; }
    @Override public void validate() { }
}
