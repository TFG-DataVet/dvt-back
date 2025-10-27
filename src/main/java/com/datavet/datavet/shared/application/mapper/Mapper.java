package com.datavet.datavet.shared.application.mapper;

/**
 * Base interface for mappers that convert between domain objects and DTOs.
 * Provides a common contract for mapping operations across domains.
 */
public interface Mapper<DOMAIN, DTO> {
    
    /**
     * Converts a domain object to a DTO.
     */
    DTO toDto(DOMAIN domain);
    
    /**
     * Converts a DTO to a domain object.
     */
    DOMAIN toDomain(DTO dto);
}