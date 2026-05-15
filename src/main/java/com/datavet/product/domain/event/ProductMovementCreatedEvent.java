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
public class ProductMovementCreatedEvent implements DomainEvent {

    private String              productMovementId;
    private String              productId;
    private String              clinicId;
    private ProductMovementType type;
    private Integer             quantity;
    private LocalDateTime       occurredOn;

    public static ProductMovementCreatedEvent of(String movementId, String productId,
                                                  String clinicId, ProductMovementType type,
                                                  Integer quantity) {
        return new ProductMovementCreatedEvent(movementId, productId, clinicId, type, quantity, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() { return this.occurredOn; }
}