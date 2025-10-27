// Controlador REST para el dominio Pet
package com.datavet.datavet.pet.infrastructure.adapter.input;

import com.datavet.datavet.pet.application.port.in.PetSearchUseCase;
import com.datavet.datavet.pet.application.dto.PetSearchResponse;
import com.datavet.datavet.pet.domain.model.PetId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {
    
    private final PetSearchUseCase petSearchUseCase;
    
    @GetMapping("/{petId}")
    public ResponseEntity<PetSearchResponse> searchPet(@PathVariable Long petId) {
        PetSearchResponse response = petSearchUseCase.searchPet(new PetId(petId));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{petId}/summary")
    public ResponseEntity<PetSearchResponse> getPetWithOwnerAndClinic(@PathVariable Long petId) {
        // Este endpoint devuelve información completa de la mascota
        // incluyendo datos del dueño y clínica sin crear dependencias directas
        PetSearchResponse response = petSearchUseCase.searchPet(new PetId(petId));
        return ResponseEntity.ok(response);
    }
}