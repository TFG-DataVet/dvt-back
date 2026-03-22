package com.datavet.owner.domain.event;

import com.datavet.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerUpdatedEvent implements DomainEvent {

    private  String ownerId;
    private  String ownerName;
    private  LocalDateTime timestamp;

    public static OwnerUpdatedEvent of(String ownerId, String ownerName) {
        return new OwnerUpdatedEvent(ownerId, ownerName, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.timestamp;
    }
}
