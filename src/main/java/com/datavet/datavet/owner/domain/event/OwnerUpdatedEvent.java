package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerUpdatedEvent implements DomainEvent {

    private Long ownerId;
    private String ownerName;
    private LocalDateTime occurredOn;

    public static OwnerUpdatedEvent of(Long ownerId, String ownerName) {
        return new OwnerUpdatedEvent(ownerId, ownerName, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
