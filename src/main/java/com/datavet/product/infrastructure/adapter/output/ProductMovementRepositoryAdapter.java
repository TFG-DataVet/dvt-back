package com.datavet.product.infrastructure.adapter.output;

import com.datavet.product.application.port.out.ProductMovementPort;
import com.datavet.product.domain.model.ProductMovement;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.product.infrastructure.persistence.document.ProductMovementDocument;
import com.datavet.product.infrastructure.persistence.repository.MongoProductMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMovementRepositoryAdapter implements ProductMovementPort {

    private final MongoProductMovementRepository repository;

    private ProductMovementDocument toDocument(ProductMovement movement) {
        return ProductMovementDocument.builder()
                .id(movement.getId())
                .productId(movement.getProductId())
                .clinicId(movement.getClinicId())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .date(movement.getDate())
                .employeeId(movement.getEmployeeId())
                .saleId(movement.getSaleId())
                .appointmentId(movement.getAppointmentId())
                .notes(movement.getNotes())
                .createdAt(movement.getCreatedAt())
                .build();
    }

    private ProductMovement toDomain(ProductMovementDocument doc) {
        return ProductMovement.reconstitute(
                doc.getId(), doc.getProductId(), doc.getClinicId(), doc.getType(),
                doc.getQuantity(), doc.getDate(), doc.getEmployeeId(),
                doc.getSaleId(), doc.getAppointmentId(), doc.getNotes(), doc.getCreatedAt());
    }

    @Override public ProductMovement         save(ProductMovement movement)  { return toDomain(repository.save(toDocument(movement))); }
    @Override public Optional<ProductMovement> findById(String id)            { return repository.findById(id).map(this::toDomain); }
    @Override public List<ProductMovement>   findAll()                        { return repository.findAll().stream().map(this::toDomain).toList(); }
    @Override public void                    deleteById(String id)            { repository.deleteById(id); }
    @Override public boolean                 existsById(String id)            { return repository.existsById(id); }

    @Override
    public List<ProductMovement> findByProductId(String productId) {
        return repository.findByProductId(productId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProductMovement> findByProductIdAndType(String productId, ProductMovementType type) {
        return repository.findByProductIdAndType(productId, type).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProductMovement> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProductMovement> findByClinicIdAndType(String clinicId, ProductMovementType type) {
        return repository.findByClinicIdAndType(clinicId, type).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProductMovement> findByAppointmentId(String appointmentId) {
        return repository.findByAppointmentId(appointmentId).stream().map(this::toDomain).toList();
    }
}
