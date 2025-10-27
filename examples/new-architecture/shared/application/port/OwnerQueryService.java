// Interface compartida para consultar información de dueños
package com.datavet.datavet.owner.application.port.out;

import com.datavet.datavet.shared.domain.valueobject.OwnerId;

public interface OwnerQueryService {
    
    /**
     * Obtiene información básica del dueño para consultas cross-domain
     */
    OwnerInfo getOwnerInfo(OwnerId ownerId);
    
    /**
     * DTO para información básica del dueño
     */
    record OwnerInfo(
        String name,
        String phone,
        String email,
        String address
    ) {}
}