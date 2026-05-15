package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class SupplementDetailsRequest extends ProductDetailsRequest {

    private String       brand;
    private Integer      weightGrams;
    private String       lifeStageSuitability;
    private List<String> speciesSuitability;
    private List<String> flavors;
    private List<String> ingredients;
    private String       nutritionalInfo;
    private String       supplementType;

    @Override
    public ProductCategory getCategory() { return ProductCategory.SUPPLEMENT; }
}
