// NUEVO ENFOQUE - Servicio de aplicación que orquesta dominios
package com.datavet.datavet.pet.application.service;

import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.pet.application.port.out.PetRepository;
import com.datavet.datavet.pet.application.port.in.PetSearchUseCase;
import com.datavet.datavet.pet.application.dto.PetSearchResponse;

// ✅ Interfaces para comunicación con otros dominios
import com.datavet.datavet.owner.application.port.out.OwnerQueryService;
import com.datavet.datavet.clinic.application.port.out.ClinicQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetSearchService implements PetSearchUseCase {
    
    private final PetRepository petRepository;
    
    // ✅ SOLUCIÓN: Comunicación a través de interfaces, no dependencias directas
    private final OwnerQueryService ownerQueryService;
    private final ClinicQueryService clinicQueryService;
    
    @Override
    public PetSearchResponse searchPet(PetId petId) {
        // 1. Buscar la mascota en su propio dominio
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new PetNotFoundException(petId));
        
        // 2. Obtener información del dueño (otro dominio)
        OwnerInfo ownerInfo = ownerQueryService.getOwnerInfo(pet.getOwnerId());
        
        // 3. Obtener información de la clínica (otro dominio)
        ClinicInfo clinicInfo = clinicQueryService.getClinicInfo(pet.getClinicId());
        
        // 4. Ensamblar la respuesta
        return PetSearchResponse.builder()
            .petId(pet.getId().getValue())
            .petName(pet.getName())
            .species(pet.getSpecies().getName())
            .breed(pet.getBreed())
            // Información del dueño
            .ownerName(ownerInfo.getName())
            .ownerPhone(ownerInfo.getPhone())
            .ownerEmail(ownerInfo.getEmail())
            // Información de la clínica
            .clinicName(clinicInfo.getName())
            .clinicAddress(clinicInfo.getAddress())
            .clinicPhone(clinicInfo.getPhone())
            .build();
    }
}