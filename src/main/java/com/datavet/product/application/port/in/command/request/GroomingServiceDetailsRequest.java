package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class GroomingServiceDetailsRequest extends ProductDetailsRequest {

    private String       brand;
    private String       scentType;
    private List<String> speciesSuitability;
    private Integer      volumeMl;
    private Integer      durationMinutes;
    private String       breedSizeTarget;
    private Boolean      includesScent;
    private Boolean      requiresAppointment;

    @Override
    public ProductCategory getCategory() { return ProductCategory.GROOMING_SERVICE; }
}
