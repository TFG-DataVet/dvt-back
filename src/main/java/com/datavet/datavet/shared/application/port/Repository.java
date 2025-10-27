package com.datavet.datavet.shared.application.port;

import java.util.List;
import java.util.Optional;

/**
 * Base interface for repositories (output ports).
 * Provides common CRUD operations that can be extended by domain-specific repositories.
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity and returns the saved entity.
     */
    T save(T entity);
    
    /**
     * Finds an entity by its identifier.
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all entities.
     */
    List<T> findAll();
    
    /**
     * Deletes an entity by its identifier.
     */
    void deleteById(ID id);
    
    /**
     * Checks if an entity exists by its identifier.
     */
    boolean existsById(ID id);
}