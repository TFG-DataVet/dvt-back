package com.datavet.product.domain.event;

import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockUpdatedEvent implements DomainEvent {

    private String              productId;
    private String              clinicId;
    private Integer             newStock;
    private ProductMovementType movementType;
    private LocalDateTime       occurredOn;

    public static ProductStockUpdatedEvent of(String productId, String clinicId,
                                               Integer newStock, ProductMovementType movementType) {
        return new ProductStockUpdatedEvent(productId, clinicId, newStock, movementType, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() { return this.occurredOn; }
}