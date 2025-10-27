// NUEVO ENFOQUE - Dominio Pet independiente
package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.shared.domain.valueobject.ClinicId;
import com.datavet.datavet.shared.domain.valueobject.OwnerId;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Pet {
    private PetId id;
    private String name;
    private Species species;
    private String breed;
    private LocalDateTime birthDate;
    
    // ✅ SOLUCIÓN: Solo referencias por ID, no dependencias directas
    private OwnerId ownerId;    // Solo el ID del dueño
    private ClinicId clinicId;  // Solo el ID de la clínica
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Métodos de dominio
    public boolean belongsToClinic(ClinicId clinicId) {
        return this.clinicId.equals(clinicId);
    }
    
    public boolean belongsToOwner(OwnerId ownerId) {
        return this.ownerId.equals(ownerId);
    }
}