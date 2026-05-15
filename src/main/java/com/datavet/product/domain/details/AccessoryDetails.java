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
@TypeAlias("accessory")
@Document
public final class AccessoryDetails extends ProductDetails {

    private String       brand;
    private List<String> sizes;
    private List<String> colors;
    private String       material;
    private List<String> speciesSuitability;

    public static AccessoryDetails create(String brand, List<String> sizes, List<String> colors,
                                           String material, List<String> speciesSuitability) {
        AccessoryDetails d = new AccessoryDetails(brand, sizes, colors, material, speciesSuitability);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.ACCESSORY; }
    @Override public void validate() { }
}
