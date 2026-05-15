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
@TypeAlias("toy")
@Document
public final class ToyDetails extends ProductDetails {

    private String       brand;
    private List<String> sizes;
    private List<String> colors;
    private String       material;
    private List<String> speciesSuitability;
    private String       toyType;
    private String       difficultyLevel;
    private List<String> safetyWarnings;

    public static ToyDetails create(String brand, List<String> sizes, List<String> colors,
                                     String material, List<String> speciesSuitability,
                                     String toyType, String difficultyLevel,
                                     List<String> safetyWarnings) {
        ToyDetails d = new ToyDetails(brand, sizes, colors, material, speciesSuitability,
                toyType, difficultyLevel, safetyWarnings);
        d.validate();
        return d;
    }

    @Override public ProductCategory getCategory() { return ProductCategory.TOY; }
    @Override public void validate() { }
}
