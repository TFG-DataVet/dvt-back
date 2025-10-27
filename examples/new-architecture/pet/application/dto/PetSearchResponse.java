// DTO para la respuesta de búsqueda de mascota
package com.datavet.datavet.pet.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetSearchResponse {
    // Información de la mascota
    private final Long petId;
    private final String petName;
    private final String species;
    private final String breed;
    
    // Información del dueño (obtenida de otro dominio)
    private final String ownerName;
    private final String ownerPhone;
    private final String ownerEmail;
    
    // Información de la clínica (obtenida de otro dominio)
    private final String clinicName;
    private final String clinicAddress;
    private final String clinicPhone;
}