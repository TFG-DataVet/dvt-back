package com.datavet.datavet.shared.domain.event;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events.
 * Domain events represent something that happened in the domain that is of interest to other parts of the system.
 */
public interface DomainEvent {
    
    /**
     * Returns the timestamp when the event occurred.
     */
    LocalDateTime occurredOn();
    
    /**
     * Returns the version of the event for evolution purposes.
     */
    default int eventVersion() {
        return 1;
    }
}