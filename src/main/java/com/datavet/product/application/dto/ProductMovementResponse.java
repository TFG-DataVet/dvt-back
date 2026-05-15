package com.datavet.product.application.dto;

import com.datavet.product.domain.valueobject.ProductMovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductMovementResponse {

    private String              id;
    private String              productId;
    private String              clinicId;
    private ProductMovementType type;
    private Integer             quantity;
    private LocalDateTime       date;
    private String              employeeId;
    private String              saleId;
    private String              appointmentId;
    private String              notes;
    private LocalDateTime       createdAt;
}
