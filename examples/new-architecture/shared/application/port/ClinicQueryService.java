// Interface compartida para consultar información de clínicas
package com.datavet.datavet.clinic.application.port.out;

import com.datavet.datavet.shared.domain.valueobject.ClinicId;

public interface ClinicQueryService {
    
    /**
     * Obtiene información básica de la clínica para consultas cross-domain
     */
    ClinicInfo getClinicInfo(ClinicId clinicId);
    
    /**
     * DTO para información básica de la clínica
     */
    record ClinicInfo(
        String name,
        String address,
        String phone,
        String email
    ) {}
}