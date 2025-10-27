// ENFOQUE ANTIGUO - Servicio monolítico
package com.datavet.datavet.service;

import com.datavet.datavet.entity.*;
import com.datavet.datavet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {
    
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final ClinicRepository clinicRepository;
    
    // ❌ PROBLEMA: Un servicio maneja múltiples dominios
    public PetSearchResult searchPet(Long petId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Pet not found"));
        
        // Acceso directo a entidades relacionadas
        Owner owner = pet.getOwner();
        Clinic clinic = pet.getClinic();
        
        return PetSearchResult.builder()
            .petId(pet.getId())
            .petName(pet.getName())
            .ownerName(owner.getName())        // Datos del dueño
            .ownerPhone(owner.getPhone())
            .clinicName(clinic.getClinicName()) // Datos de la clínica
            .clinicAddress(clinic.getAddress())
            .build();
    }
}