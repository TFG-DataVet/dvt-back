package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OwnerCreatedEvent implements DomainEvent {

    private final Long ownerID;
    private final String name;
    private final String dni;
    private final LocalDateTime occurredOn;

    public static OwnerCreatedEvent of(Long ownerID, String name, String dni){
        return new OwnerCreatedEvent(ownerID, name, dni, LocalDateTime.now());
    }


    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString(){
        return String.format("OwnerCreatedEvent{ownerID=%d, ownerName='%s', dni='%s', ocurredOn=%s´}",
                ownerID, name, dni, occurredOn());
    }

}
