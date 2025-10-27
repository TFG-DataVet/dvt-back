package com.datavet.datavet.shared.domain.model;

import com.datavet.datavet.shared.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregate roots in the domain model.
 * Provides common functionality for managing domain events and entity identity.
 */
public abstract class AggregateRoot<ID> {
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * Returns the unique identifier of this aggregate.
     */
    public abstract ID getId();
    
    /**
     * Adds a domain event to be published when the aggregate is saved.
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * Returns all domain events that have been raised by this aggregate.
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * Clears all domain events. Should be called after events have been published.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}