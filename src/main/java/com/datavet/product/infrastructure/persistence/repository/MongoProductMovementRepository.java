package com.datavet.product.infrastructure.persistence.repository;

import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.product.infrastructure.persistence.document.ProductMovementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoProductMovementRepository extends MongoRepository<ProductMovementDocument, String> {
    List<ProductMovementDocument> findByProductId(String productId);
    List<ProductMovementDocument> findByProductIdAndType(String productId, ProductMovementType type);
    List<ProductMovementDocument> findByClinicId(String clinicId);
    List<ProductMovementDocument> findByClinicIdAndType(String clinicId, ProductMovementType type);
    List<ProductMovementDocument> findByAppointmentId(String appointmentId);
}
