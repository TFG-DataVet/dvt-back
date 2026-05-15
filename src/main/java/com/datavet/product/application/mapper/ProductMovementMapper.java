package com.datavet.product.application.mapper;

import com.datavet.product.application.dto.ProductMovementResponse;
import com.datavet.product.domain.model.ProductMovement;

public class ProductMovementMapper {

    private ProductMovementMapper() {}

    public static ProductMovementResponse toResponse(ProductMovement movement) {
        return new ProductMovementResponse(
                movement.getId(),
                movement.getProductId(),
                movement.getClinicId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getDate(),
                movement.getEmployeeId(),
                movement.getSaleId(),
                movement.getAppointmentId(),
                movement.getNotes(),
                movement.getCreatedAt()
        );
    }
}
