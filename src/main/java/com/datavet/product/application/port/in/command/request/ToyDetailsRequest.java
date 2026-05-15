package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class ToyDetailsRequest extends ProductDetailsRequest {

    private String       brand;
    private List<String> sizes;
    private List<String> colors;
    private String       material;
    private List<String> speciesSuitability;
    private String       toyType;
    private String       difficultyLevel;
    private List<String> safetyWarnings;

    @Override
    public ProductCategory getCategory() { return ProductCategory.TOY; }
}
