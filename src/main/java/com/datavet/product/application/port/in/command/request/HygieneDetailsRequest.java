package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class HygieneDetailsRequest extends ProductDetailsRequest {

    private String       brand;
    private String       scentType;
    private List<String> speciesSuitability;
    private Integer      volumeMl;

    @Override
    public ProductCategory getCategory() { return ProductCategory.HYGIENE; }
}
