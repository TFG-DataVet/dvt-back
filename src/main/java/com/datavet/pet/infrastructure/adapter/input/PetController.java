package com.datavet.pet.infrastructure.adapter.input;

import com.datavet.pet.application.dto.PetResponse;
import com.datavet.pet.application.mapper.PetMapper;
import com.datavet.pet.application.port.in.PetUseCase;
import com.datavet.pet.application.port.in.command.owner.UpdatePetOwnerInfoCommand;
import com.datavet.pet.application.port.in.command.pet.*;
import com.datavet.pet.domain.model.Pet;
import com.datavet.pet.infrastructure.adapter.input.dto.*;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetUseCase petUseCase;

    // =========================================================================
    // Ciclo de vida
    // =========================================================================

    /**
     * POST /pet
     * Crea una nueva mascota con su dueño embebido.
     */
    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody CreatePetRequest request) {

        CreatePetCommand command = CreatePetCommand.builder()
                .clinicId(request.getClinicId())
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .sex(request.getSex())
                .dateOfBirth(request.getDateOfBirth())
                .chipNumber(request.getChipNumber())
                .avatarUrl(request.getAvatarUrl())
                .ownerId(request.getOwner().getOwnerId())
                .ownerName(request.getOwner().getOwnerName())
                .ownerLastName(request.getOwner().getOwnerLastName())
                .ownerPhone(new Phone(request.getOwner().getOwnerPhone()))
                .build();

        Pet pet = petUseCase.createPet(command);
        return ResponseEntity.status(201).body(PetMapper.toResponse(pet));
    }

    /**
     * PUT /pet/{id}
     * Actualiza el nombre y avatar de la mascota.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdatePetRequest request) {

        UpdatePetCommand command = UpdatePetCommand.builder()
                .petId(id)
                .name(request.getName())
                .avatarUrl(request.getAvatarUrl())
                .build();

        Pet pet = petUseCase.updatePet(command);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    /**
     * PATCH /pet/{id}/deactivate
     * Desactiva la mascota indicando el motivo.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable String id,
            @Valid @RequestBody DeactivatePetRequest request) {

        DeactivatePetCommand command = DeactivatePetCommand.builder()
                .petId(id)
                .reason(request.getReason())
                .build();

        petUseCase.deactivatePet(command);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /pet/{id}/activate
     * Reactiva una mascota previamente desactivada.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<PetResponse> activate(@PathVariable String id) {
        Pet pet = petUseCase.activatePet(id);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    // =========================================================================
    // Correcciones clínicas
    // =========================================================================

    /**
     * PATCH /pet/{id}/correct-breed
     * Corrige la raza de la mascota justificando el cambio.
     */
    @PatchMapping("/{id}/correct-breed")
    public ResponseEntity<PetResponse> correctBreed(
            @PathVariable String id,
            @Valid @RequestBody CorrectBreedRequest request) {

        CorrectPetBreedCommand command = CorrectPetBreedCommand.builder()
                .petId(id)
                .newBreed(request.getNewBreed())
                .reason(request.getReason())
                .build();

        Pet pet = petUseCase.correctBreed(command);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    /**
     * PATCH /pet/{id}/correct-birthdate
     * Corrige la fecha de nacimiento de la mascota justificando el cambio.
     */
    @PatchMapping("/{id}/correct-birthdate")
    public ResponseEntity<PetResponse> correctBirthDate(
            @PathVariable String id,
            @Valid @RequestBody CorrectBirthDateRequest request) {

        CorrectPetBirthDateCommand command = CorrectPetBirthDateCommand.builder()
                .petId(id)
                .newBirthDate(request.getNewBirthDate())
                .reason(request.getReason())
                .build();

        Pet pet = petUseCase.correctBirthDate(command);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    /**
     * PATCH /pet/{id}/correct-sex
     * Corrige el sexo de la mascota justificando el cambio.
     */
    @PatchMapping("/{id}/correct-sex")
    public ResponseEntity<PetResponse> correctSex(
            @PathVariable String id,
            @Valid @RequestBody CorrectSexRequest request) {

        CorrectPetSexCommand command = CorrectPetSexCommand.builder()
                .petId(id)
                .sex(request.getSex())
                .reason(request.getReason())
                .build();

        Pet pet = petUseCase.correctSex(command);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    // =========================================================================
    // Dueño embebido
    // =========================================================================

    /**
     * PATCH /pet/{id}/owner
     * Actualiza los datos del dueño embebido de la mascota.
     */
    @PatchMapping("/{id}/owner")
    public ResponseEntity<PetResponse> updateOwnerInfo(
            @PathVariable String id,
            @Valid @RequestBody UpdateOwnerInfoRequest request) {

        UpdatePetOwnerInfoCommand command = UpdatePetOwnerInfoCommand.builder()
                .petId(id)
                .ownerId(request.getOwnerId())
                .ownerName(request.getOwnerName())
                .ownerLastName(request.getOwnerLastName())
                .ownerPhone(new Phone(request.getOwnerPhone()))
                .build();

        Pet pet = petUseCase.updateOwnerInfo(command);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    /**
     * GET /pet/{id}
     * Obtiene una mascota por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getById(@PathVariable String id) {
        Pet pet = petUseCase.getPetById(id);
        return ResponseEntity.ok(PetMapper.toResponse(pet));
    }

    /**
     * GET /pet/clinic/{clinicId}
     * Obtiene todas las mascotas asociadas a una clínica.
     */
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<PetResponse>> getByClinic(@PathVariable String clinicId) {
        List<Pet> pets = petUseCase.getPetsByClinic(clinicId);
        return ResponseEntity.ok(PetMapper.toResponseList(pets));
    }

    /**
     * GET /pet/owner/{ownerId}
     * Obtiene todas las mascotas asociadas a un dueño.
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetResponse>> getByOwner(@PathVariable String ownerId) {
        List<Pet> pets = petUseCase.getPetsByOwner(ownerId);
        return ResponseEntity.ok(PetMapper.toResponseList(pets));
    }
}