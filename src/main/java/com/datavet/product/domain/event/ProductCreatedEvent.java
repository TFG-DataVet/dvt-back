package com.datavet.product.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent implements DomainEvent {

    private String productId;
    private String clinicId;
    private String name;
    private LocalDateTime occurredOn;

    public static ProductCreatedEvent of(String productId, String clinicId, String name) {
        return new ProductCreatedEvent(productId, clinicId, name, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
