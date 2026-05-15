package com.datavet.product.application.dto;

import com.datavet.product.domain.details.ProductDetails;
import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private String          productId;
    private String          clinicId;
    private String          name;
    private String          description;
    private ProductCategory category;
    private String          sku;
    private String          barcode;
    private BigDecimal      price;
    private BigDecimal      taxRate;
    private Integer         stock;
    private Integer         minStock;
    private Boolean         isActive;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
    private ProductDetails  details;
}
