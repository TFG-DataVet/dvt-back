package com.datavet.pet.application.service;

import com.datavet.pet.application.port.in.command.pet.CorrectPetBreedCommand;
import com.datavet.pet.application.port.in.command.pet.CreatePetCommand;
import com.datavet.pet.application.port.in.command.pet.DeactivatePetCommand;
import com.datavet.pet.application.port.in.command.pet.UpdatePetCommand;
import com.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.pet.domain.exception.PetAlreadyExistsException;
import com.datavet.pet.domain.exception.PetNotFoundException;
import com.datavet.pet.domain.model.Pet;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.datavet.pet.testutil.PetCommandTestDataBuild.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Tests")
class PetServiceTest {

    @Mock private PetRepositoryPort      petRepositoryPort;
    @Mock private DomainEventPublisher   domainEventPublisher;

    @InjectMocks
    private PetService petService;

    // =========================================================================
    // createPet
    // =========================================================================

    @Nested
    @DisplayName("createPet")
    class CreatePet {

        @Test
        @DisplayName("Debe crear una mascota y retornar el agregado guardado")
        void shouldCreatePetSuccessfully() {
            CreatePetCommand command = aValidCreatePetCommand();
            Pet savedPet = aValidPet();

            when(petRepositoryPort.existsByChipNumber(command.getChipNumber())).thenReturn(false);
            when(petRepositoryPort.save(any(Pet.class))).thenReturn(savedPet);

            // When
            Pet result = petService.createPet(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(DEFAULT_NAME);
            assertThat(result.getClinicId()).isEqualTo(DEFAULT_CLINIC_ID);

            verify(petRepositoryPort).existsByChipNumber(command.getChipNumber());
            verify(petRepositoryPort).save(any(Pet.class));
            verify(domainEventPublisher, atLeastOnce()).publish(any());
        }

        @Test
        @DisplayName("Debe crear mascota sin chip sin verificar unicidad")
        void shouldCreatePetWithoutChipWithoutUniquenessCheck() {
            // Given
            CreatePetCommand command = aCreatePetCommandWithoutChip();
            Pet savedPet = aValidPet();

            when(petRepositoryPort.save(any(Pet.class))).thenReturn(savedPet);

            // When
            Pet result = petService.createPet(command);

            // Then
            assertThat(result).isNotNull();
            verify(petRepositoryPort, never()).existsByChipNumber(any());
            verify(petRepositoryPort).save(any(Pet.class));
        }

        @Test
        @DisplayName("Debe lanzar PetAlreadyExistsException si el chip ya existe")
        void shouldThrowWhenChipAlreadyExists() {
            // Given
            CreatePetCommand command = aValidCreatePetCommand();
            when(petRepositoryPort.existsByChipNumber(command.getChipNumber())).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> petService.createPet(command))
                    .isInstanceOf(PetAlreadyExistsException.class);

            verify(petRepositoryPort).existsByChipNumber(command.getChipNumber());
            verify(petRepositoryPort, never()).save(any());
        }
    }

    // =========================================================================
    // updatePet
    // =========================================================================

    @Nested
    @DisplayName("updatePet")
    class UpdatePet {

        @Test
        @DisplayName("Debe actualizar la mascota y retornar el agregado actualizado")
        void shouldUpdatePetSuccessfully() {
            // Given
            UpdatePetCommand command = aValidUpdatePetCommand();
            Pet existing = aValidPet();
            Pet updated  = aValidPet();

            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.of(existing));
            when(petRepositoryPort.save(any(Pet.class))).thenReturn(updated);

            // When
            Pet result = petService.updatePet(command);

            // Then
            assertThat(result).isNotNull();
            verify(petRepositoryPort).findById(command.getPetId());
            verify(petRepositoryPort).save(any(Pet.class));
        }

        @Test
        @DisplayName("Debe lanzar PetNotFoundException si la mascota no existe")
        void shouldThrowWhenPetNotFound() {
            // Given
            UpdatePetCommand command = aValidUpdatePetCommand();
            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> petService.updatePet(command))
                    .isInstanceOf(PetNotFoundException.class);

            verify(petRepositoryPort, never()).save(any());
        }
    }

    // =========================================================================
    // deactivatePet
    // =========================================================================

    @Nested
    @DisplayName("deactivatePet")
    class DeactivatePet {

        @Test
        @DisplayName("Debe desactivar la mascota correctamente")
        void shouldDeactivatePetSuccessfully() {
            // Given
            DeactivatePetCommand command = aValidDeactivatePetCommand();
            Pet existing = aValidPet();

            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.of(existing));
            when(petRepositoryPort.save(any(Pet.class))).thenReturn(existing);

            // When
            petService.deactivatePet(command);

            // Then
            verify(petRepositoryPort).findById(command.getPetId());
            verify(petRepositoryPort).save(any(Pet.class));
            verify(domainEventPublisher, atLeastOnce()).publish(any());
        }

        @Test
        @DisplayName("Debe lanzar PetNotFoundException si la mascota no existe")
        void shouldThrowWhenPetNotFound() {
            // Given
            DeactivatePetCommand command = aValidDeactivatePetCommand();
            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> petService.deactivatePet(command))
                    .isInstanceOf(PetNotFoundException.class);

            verify(petRepositoryPort, never()).save(any());
        }
    }

    // =========================================================================
    // activatePet
    // =========================================================================

    @Nested
    @DisplayName("activatePet")
    class ActivatePet {

        @Test
        @DisplayName("Debe activar la mascota y retornar el agregado")
        void shouldActivatePetSuccessfully() {
            // Given
            Pet existing = aValidPet();
            when(petRepositoryPort.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(petRepositoryPort.save(any(Pet.class))).thenReturn(existing);

            existing.deactivate(existing.getId(), "El perrito se desactivo");

            // When
            Pet result = petService.activatePet(existing.getId());

            // Then
            assertThat(result).isNotNull();
            verify(petRepositoryPort).findById(result.getId());
            verify(petRepositoryPort).save(any(Pet.class));
        }
    }

    // =========================================================================
    // Correcciones clínicas
    // =========================================================================

    @Nested
    @DisplayName("correctBreed")
    class CorrectBreed {

        @Test
        @DisplayName("Debe corregir la raza y retornar el agregado actualizado")
        void shouldCorrectBreedSuccessfully() {
            // Given
            CorrectPetBreedCommand command = aValidCorrectBreedCommand();
            Pet existing = aValidPet();

            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.of(existing));
            when(petRepositoryPort.save(any(Pet.class))).thenReturn(existing);

            // When
            Pet result = petService.correctBreed(command);

            // Then
            assertThat(result).isNotNull();
            verify(petRepositoryPort).findById(command.getPetId());
            verify(petRepositoryPort).save(any(Pet.class));
            verify(domainEventPublisher, atLeastOnce()).publish(any());
        }

        @Test
        @DisplayName("Debe lanzar PetNotFoundException si la mascota no existe")
        void shouldThrowWhenPetNotFound() {
            // Given
            CorrectPetBreedCommand command = aValidCorrectBreedCommand();
            when(petRepositoryPort.findById(command.getPetId())).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> petService.correctBreed(command))
                    .isInstanceOf(PetNotFoundException.class);
        }
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    @Nested
    @DisplayName("Consultas")
    class Queries {

        @Test
        @DisplayName("getPetById debe retornar la mascota si existe")
        void shouldReturnPetById() {
            // Given
            Pet expected = aValidPet();
            when(petRepositoryPort.findById(expected.getId())).thenReturn(Optional.of(expected));

            // When
            Pet result = petService.getPetById(expected.getId());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(expected.getId());
            verify(petRepositoryPort).findById(result.getId());
        }

        @Test
        @DisplayName("getPetById debe lanzar PetNotFoundException si no existe")
        void shouldThrowWhenPetNotFoundById() {
            // Given
            when(petRepositoryPort.findById(DEFAULT_PET_ID)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> petService.getPetById(DEFAULT_PET_ID))
                    .isInstanceOf(PetNotFoundException.class);
        }

        @Test
        @DisplayName("getPetsByClinic debe retornar la lista de mascotas de la clínica")
        void shouldReturnPetsByClinic() {
            // Given
            List<Pet> pets = List.of(aValidPet(), aValidPet());
            when(petRepositoryPort.findByClinicId(DEFAULT_CLINIC_ID)).thenReturn(pets);

            // When
            List<Pet> result = petService.getPetsByClinic(DEFAULT_CLINIC_ID);

            // Then
            assertThat(result).hasSize(2);
            verify(petRepositoryPort).findByClinicId(DEFAULT_CLINIC_ID);
        }

        @Test
        @DisplayName("getPetsByOwner debe retornar la lista de mascotas del dueño")
        void shouldReturnPetsByOwner() {
            // Given
            List<Pet> pets = List.of(aValidPet());
            when(petRepositoryPort.findByOwnerId(DEFAULT_OWNER_ID)).thenReturn(pets);

            // When
            List<Pet> result = petService.getPetsByOwner(DEFAULT_OWNER_ID);

            // Then
            assertThat(result).hasSize(1);
            verify(petRepositoryPort).findByOwnerId(DEFAULT_OWNER_ID);
        }
    }
}