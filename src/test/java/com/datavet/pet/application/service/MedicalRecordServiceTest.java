package com.datavet.pet.application.service;

import com.datavet.pet.application.factory.MedicalRecordDetailsFactory;
import com.datavet.pet.application.port.in.command.medicalrecord.ApplyMedicalRecordActionCommand;
import com.datavet.pet.application.port.in.command.medicalrecord.CorrectMedicalRecordCommand;
import com.datavet.pet.application.port.in.command.medicalrecord.CreateMedicalRecordCommand;
import com.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.pet.domain.exception.MedicalRecordNotFoundException;
import com.datavet.pet.domain.exception.PetNotFoundException;
import com.datavet.pet.domain.model.MedicalRecord;
import com.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.infrastructure.adapter.output.MedicalRecordRepositoryAdapter;
import com.datavet.pet.testutil.MedicalRecordServiceTestDataBuilder;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.datavet.pet.testutil.MedicalRecordServiceTestDataBuilder.aValidCorrectMedicalRecordCommand;
import static com.datavet.pet.testutil.MedicalRecordServiceTestDataBuilder.aValidCreateVaccineCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordService Tests")
class MedicalRecordServiceTest {

    @Mock private MedicalRecordRepositoryAdapter medicalRecordRepositoryAdapter;
    @Mock private PetRepositoryPort            petRepositoryPort;
    @Mock private MedicalRecordDetailsFactory  detailsFactory;
    @Mock private DomainEventPublisher         domainEventPublisher;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    // =========================================================================
    // createMedicalRecord
    // =========================================================================

    @Nested
    @DisplayName("createMedicalRecord")
    class CreateMedicalRecord {

        @Test
        @DisplayName("Debe crear un registro médico y retornar el agregado guardado")
        void shouldCreateMedicalRecordSuccessfully() {
            // Given
            CreateMedicalRecordCommand command = aValidCreateVaccineCommand();
            MedicalRecordDetails vaccineDetails = buildVaccineDetails();
            MedicalRecord savedRecord = MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord();

            when(petRepositoryPort.existsById(command.getPetId())).thenReturn(true);
            when(detailsFactory.create(command.getDetailsRequest())).thenReturn(vaccineDetails);
            when(medicalRecordRepositoryAdapter.save(any(MedicalRecord.class))).thenReturn(savedRecord);

            // When
            MedicalRecord result = medicalRecordService.createMedicalRecord(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(savedRecord.getId());
            assertThat(result.getType()).isEqualTo(MedicalRecordType.VACCINE);
            assertThat(result.getPetId()).isEqualTo(savedRecord.getPetId());

            verify(petRepositoryPort).existsById(command.getPetId());
            verify(detailsFactory).create(command.getDetailsRequest());
            verify(medicalRecordRepositoryAdapter).save(any(MedicalRecord.class));
            verify(domainEventPublisher, atLeastOnce()).publish(any());
        }

        @Test
        @DisplayName("Debe lanzar PetNotFoundException si la mascota no existe")
        void shouldThrowWhenPetNotFound() {
            // Given
            CreateMedicalRecordCommand command = aValidCreateVaccineCommand();
            when(petRepositoryPort.existsById(command.getPetId())).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.createMedicalRecord(command))
                    .isInstanceOf(PetNotFoundException.class);

            verify(detailsFactory, never()).create(any());
            verify(medicalRecordRepositoryAdapter, never()).save(any());
        }

        @Test
        @DisplayName("Debe llamar a la factory para construir el details antes de crear el MedicalRecord")
        void shouldDelegateDetailsCreationToFactory() {
            // Given
            CreateMedicalRecordCommand command = aValidCreateVaccineCommand();
            MedicalRecordDetails vaccineDetails = buildVaccineDetails();
            MedicalRecord savedRecord = MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord();

            when(petRepositoryPort.existsById(command.getPetId())).thenReturn(true);
            when(detailsFactory.create(command.getDetailsRequest())).thenReturn(vaccineDetails);
            when(medicalRecordRepositoryAdapter.save(any(MedicalRecord.class))).thenReturn(savedRecord);

            // When
            medicalRecordService.createMedicalRecord(command);

            // Then — el orden importa: factory ANTES de save
            var inOrder = inOrder(detailsFactory, medicalRecordRepositoryAdapter);
            inOrder.verify(detailsFactory).create(command.getDetailsRequest());
            inOrder.verify(medicalRecordRepositoryAdapter).save(any(MedicalRecord.class));
        }
    }

    // =========================================================================
    // correctMedicalRecord
    // =========================================================================

    @Nested
    @DisplayName("correctMedicalRecord")
    class CorrectMedicalRecord {

        @Test
        @DisplayName("Debe crear el registro corregido y marcar el original como CORRECTED")
        void shouldCorrectMedicalRecordSuccessfully() {
            // Given
            MedicalRecord original = MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord();
            MedicalRecordDetails correctedDetails = buildCorrectVaccineDetails();
            CorrectMedicalRecordCommand command = aValidCorrectMedicalRecordCommand(original.getId());

            when(medicalRecordRepositoryAdapter.findById(original.getId()))
                    .thenReturn(Optional.of(original));
            when(detailsFactory.create(command.getDetailsRequest()))
                    .thenReturn(correctedDetails);
            when(medicalRecordRepositoryAdapter.save(any(MedicalRecord.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            MedicalRecord result = medicalRecordService.correctMedicalRecord(command);

            // Then
            assertThat(result).isNotNull();

            // Ambos registros deben persistirse
            verify(medicalRecordRepositoryAdapter, times(2)).save(any(MedicalRecord.class));
            verify(domainEventPublisher, atLeastOnce()).publish(any());
        }

        @Test
        @DisplayName("Debe lanzar MedicalRecordNotFoundException si el registro original no existe")
        void shouldThrowWhenOriginalRecordNotFound() {
            // Given
            CorrectMedicalRecordCommand command = aValidCorrectMedicalRecordCommand("nonexistent");
            when(medicalRecordRepositoryAdapter.findById("nonexistent"))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.correctMedicalRecord(command))
                    .isInstanceOf(MedicalRecordNotFoundException.class);

            verify(detailsFactory, never()).create(any());
            verify(medicalRecordRepositoryAdapter, never()).save(any());
        }
    }

    // =========================================================================
    // applyAction
    // =========================================================================

    @Nested
    @DisplayName("applyAction")
    class ApplyAction {

        @Test
        @DisplayName("Debe lanzar MedicalRecordNotFoundException si el registro no existe")
        void shouldThrowWhenRecordNotFound() {
            // Given
            ApplyMedicalRecordActionCommand command = MedicalRecordServiceTestDataBuilder.aValidApplyActionCommand("nonexistent");
            when(medicalRecordRepositoryAdapter.findById("nonexistent"))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.applyAction(command))
                    .isInstanceOf(MedicalRecordNotFoundException.class);

            verify(medicalRecordRepositoryAdapter, never()).save(any());
        }
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    @Nested
    @DisplayName("Consultas")
    class Queries {

        @Test
        @DisplayName("getMedicalRecordById debe retornar el registro si existe")
        void shouldReturnMedicalRecordById() {
            // Given
            MedicalRecord expected = MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord();
            when(medicalRecordRepositoryAdapter.findById(expected.getId()))
                    .thenReturn(Optional.of(expected));

            // When
            MedicalRecord result = medicalRecordService.getMedicalRecordById(expected.getId());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(expected.getId());
            verify(medicalRecordRepositoryAdapter).findById(expected.getId());
        }

        @Test
        @DisplayName("getMedicalRecordById debe lanzar MedicalRecordNotFoundException si no existe")
        void shouldThrowWhenRecordNotFoundById() {
            // Given
            when(medicalRecordRepositoryAdapter.findById("medical_record_001"))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.getMedicalRecordById("medical_record_001"))
                    .isInstanceOf(MedicalRecordNotFoundException.class);
        }

        @Test
        @DisplayName("getMedicalRecordsByPet debe lanzar PetNotFoundException si la mascota no existe")
        void shouldThrowWhenPetNotFoundInGetByPet() {
            // Given
            when(petRepositoryPort.existsById("pet_001")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.getMedicalRecordsByPet("pet_001"))
                    .isInstanceOf(PetNotFoundException.class);

            verify(medicalRecordRepositoryAdapter, never()).findByPetId(any());
        }

        @Test
        @DisplayName("getMedicalRecordsByPet debe retornar la lista si la mascota existe")
        void shouldReturnRecordsByPet() {
            // Given
            List<MedicalRecord> records = List.of(
                    MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord(),
                    MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord()
            );

            String petId = records.getFirst().getPetId();
            when(petRepositoryPort.existsById(petId)).thenReturn(true);
            when(medicalRecordRepositoryAdapter.findByPetId(petId)).thenReturn(records);

            // When
            List<MedicalRecord> result = medicalRecordService.getMedicalRecordsByPet(petId);

            // Then
            assertThat(result).hasSize(2);
            verify(medicalRecordRepositoryAdapter).findByPetId(petId);
        }

        @Test
        @DisplayName("getMedicalRecordsByType debe retornar los registros filtrados por tipo")
        void shouldReturnRecordsByType() {
            // Given
            List<MedicalRecord> records = List.of(MedicalRecordServiceTestDataBuilder.aValidVaccineMedicalRecord());

            String petId = records.getFirst().getPetId();
            when(petRepositoryPort.existsById(petId)).thenReturn(true);
            when(medicalRecordRepositoryAdapter.findByPetIdAndType(petId, MedicalRecordType.VACCINE))
                    .thenReturn(records);

            // When
            List<MedicalRecord> result = medicalRecordService.getMedicalRecordsByType(
                    petId, MedicalRecordType.VACCINE);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getType()).isEqualTo(MedicalRecordType.VACCINE);
        }
    }

    // =========================================================================
    // Helper privado
    // =========================================================================

    private VaccineDetails buildVaccineDetails() {
        return VaccineDetails.create(
                "Rabia",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(1),
                "BATCH-2025-001",
                "VetPharma"
        );
    }

    private VaccineDetails buildCorrectVaccineDetails() {
        return VaccineDetails.create(
                "Rabia",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusMonths(1),
                "BATCH-2025-011",
                "VetPharma2"
        );
    }
}