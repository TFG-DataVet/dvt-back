package com.datavet.datavet.shared.domain.event;

/**
 * Interface for publishing domain events.
 * Implementations should handle the actual event publishing mechanism.
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes a domain event to interested subscribers.
     */
    void publish(DomainEvent event);
}