package com.datavet.product.application.port.out;

import com.datavet.product.domain.model.ProductMovement;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.application.port.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ProductMovementPort extends Repository<ProductMovement, String> {
    List<ProductMovement> findByProductId(String productId);
    List<ProductMovement> findByProductIdAndType(String productId, ProductMovementType type);
    List<ProductMovement> findByClinicId(String clinicId);
    List<ProductMovement> findByClinicIdAndType(String clinicId, ProductMovementType type);
    List<ProductMovement> findByAppointmentId(String appointmentId);
}
