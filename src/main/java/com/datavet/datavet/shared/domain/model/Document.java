package com.datavet.datavet.shared.domain.model;

/**
 * Base interface for domain entities.
 * Entities are objects that have a distinct identity that runs through time and different representations.
 */
public interface Document<ID> {
    
    /**
     * Returns the unique identifier of this entity.
     */
    ID getId();
}