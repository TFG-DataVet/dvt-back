package com.datavet.product.application.mapper;

import com.datavet.product.application.dto.ProductResponse;
import com.datavet.product.domain.model.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getClinicId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getSku(),
                product.getBarcode(),
                product.getPrice(),
                product.getTaxRate(),
                product.getStock(),
                product.getMinStock(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDetails()
        );
    }
}
