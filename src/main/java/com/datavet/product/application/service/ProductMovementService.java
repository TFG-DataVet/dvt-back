package com.datavet.product.application.service;

import com.datavet.product.application.port.in.ProductMovementUseCase;
import com.datavet.product.application.port.in.command.CreateProductMovementCommand;
import com.datavet.product.application.port.out.ProductMovementPort;
import com.datavet.product.application.port.out.ProductRepositoryPort;
import com.datavet.product.domain.exception.ProductMovementNotFoundException;
import com.datavet.product.domain.exception.ProductNotFoundException;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.model.ProductMovement;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.model.AggregateRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductMovementService implements ProductMovementUseCase, ApplicationService {

    private final ProductMovementPort   productMovementPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final DomainEventPublisher  domainEventPublisher;

    @Override
    @Transactional
    public ProductMovement createMovement(CreateProductMovementCommand command) {
        Product product = productRepositoryPort.findById(command.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product", command.getProductId()));

        if (!product.getClinicId().equals(command.getClinicId()))
            throw new AccessDeniedException("El producto no pertenece a tu clínica");

        ProductMovement movement = ProductMovement.create(
                command.getProductId(),
                command.getClinicId(),
                command.getType(),
                command.getQuantity(),
                command.getDate(),
                command.getEmployeeId(),
                command.getAppointmentId(),
                command.getNotes());

        product.applyMovement(command.getQuantity(), command.getType());

        publishDomainEvents(movement);
        publishDomainEvents(product);

        productRepositoryPort.save(product);
        return productMovementPort.save(movement);
    }

    @Override
    public ProductMovement getMovementById(String movementId, String clinicId) {
        ProductMovement movement = productMovementPort.findById(movementId)
                .orElseThrow(() -> new ProductMovementNotFoundException(movementId));
        if (!movement.getClinicId().equals(clinicId))
            throw new AccessDeniedException("El movimiento no pertenece a tu clínica");
        return movement;
    }

    @Override
    public List<ProductMovement> getMovementsByProduct(String productId, String clinicId) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product", productId));
        if (!product.getClinicId().equals(clinicId))
            throw new AccessDeniedException("El producto no pertenece a tu clínica");
        return productMovementPort.findByProductId(productId);
    }

    @Override
    public List<ProductMovement> getMovementsByProductAndType(String productId, ProductMovementType type, String clinicId) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product", productId));
        if (!product.getClinicId().equals(clinicId))
            throw new AccessDeniedException("El producto no pertenece a tu clínica");
        return productMovementPort.findByProductIdAndType(productId, type);
    }

    @Override
    public List<ProductMovement> getMovementsByClinic(String clinicId) {
        return productMovementPort.findByClinicId(clinicId);
    }

    private void publishDomainEvents(AggregateRoot<?> aggregate) {
        List<DomainEvent> events = aggregate.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        aggregate.clearDomainEvents();
    }
}
