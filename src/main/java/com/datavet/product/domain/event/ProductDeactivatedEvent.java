package com.datavet.product.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeactivatedEvent implements DomainEvent {

    private String productId;
    private String clinicId;
    private String name;
    private String reason;
    private LocalDateTime occurredOn;

    public static ProductDeactivatedEvent of(String productId, String clinicId, String name, String reason) {
        return new ProductDeactivatedEvent(productId, clinicId, name, reason, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
