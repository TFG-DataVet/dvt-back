package com.datavet.datavet.clinic.application.service;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.datavet.clinic.application.validation.CreateClinicCommandValidator;
import com.datavet.datavet.clinic.application.validation.UpdateClinicCommandValidator;
import com.datavet.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.testutil.ClinicTestDataBuilder;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClinicService - Creation Operations.
 * Tests the business logic of clinic creation including validation, uniqueness checks,
 * domain event publishing, and exception handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - Creation Operations Tests")
class ClinicServiceTest {

    @Mock
    private ClinicRepositoryPort clinicRepositoryPort;

    @Mock
    private CreateClinicCommandValidator createClinicCommandValidator;

    @Mock
    private UpdateClinicCommandValidator updateClinicCommandValidator;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ClinicService clinicService;

    private CreateClinicCommand validCommand;
    private Clinic validClinic;

    @BeforeEach
    void setUp() {
        validCommand = ClinicTestDataBuilder.aValidCreateCommand();
        validClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId("hola");
    }

    @Test
    @DisplayName("Should create clinic successfully with valid data")
    void shouldCreateClinicSuccessfully() {
        // Given: Valid command passes validation and no duplicates exist
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber(validCommand.getLegalNumber()))
                .thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenReturn(validClinic);

        // When: Creating a clinic
        Clinic result = clinicService.createClinic(validCommand);

        // Then: Clinic is created and saved
        assertThat(result).isNotNull();
        assertThat(result.getClinicID()).isEqualTo("hola");
        assertThat(result.getClinicName()).isEqualTo(validCommand.getClinicName());
        assertThat(result.getLegalName()).isEqualTo(validCommand.getLegalName());
        assertThat(result.getLegalNumber()).isEqualTo(validCommand.getLegalNumber());
        assertThat(result.getEmail()).isEqualTo(validCommand.getEmail());
        assertThat(result.getSuscriptionStatus()).isEqualTo("ACTIVE");

        // Verify interactions
        verify(createClinicCommandValidator).validate(validCommand);
        verify(clinicRepositoryPort).existsByEmail(validCommand.getEmail());
        verify(clinicRepositoryPort).existsByLegalNumber(validCommand.getLegalNumber());
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given: Valid command but email already exists
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(true);

        // When/Then: Creating clinic throws ClinicAlreadyExistsException
        assertThatThrownBy(() -> clinicService.createClinic(validCommand))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("email")
                .hasMessageContaining(validCommand.getEmail().getValue());

        // Verify: Repository save was never called
        verify(clinicRepositoryPort, never()).save(any(Clinic.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when legal number already exists")
    void shouldThrowExceptionWhenLegalNumberAlreadyExists() {
        // Given: Valid command but legal number already exists
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber(validCommand.getLegalNumber()))
                .thenReturn(true);

        // When/Then: Creating clinic throws ClinicAlreadyExistsException
        assertThatThrownBy(() -> clinicService.createClinic(validCommand))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("legalNumber")
                .hasMessageContaining(validCommand.getLegalNumber());

        // Verify: Repository save was never called
        verify(clinicRepositoryPort, never()).save(any(Clinic.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should throw validation exception for invalid command")
    void shouldThrowValidationExceptionForInvalidCommand() {
        // Given: Command fails validation
        ValidationResult invalidResult = new ValidationResult();
        invalidResult.addError("clinicName", "Clinic name is required");
        invalidResult.addError("email", "Email is required");
        
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(invalidResult);

        // When/Then: Creating clinic throws ClinicValidationException
        assertThatThrownBy(() -> clinicService.createClinic(validCommand))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("validation failed");

        // Verify: No repository or event publisher interactions
        verify(clinicRepositoryPort, never()).existsByEmail(any(Email.class));
        verify(clinicRepositoryPort, never()).existsByLegalNumber(anyString());
        verify(clinicRepositoryPort, never()).save(any(Clinic.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should publish domain events on creation")
    void shouldPublishDomainEventsOnCreation() {
        // Given: Valid command and successful creation
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber(validCommand.getLegalNumber()))
                .thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenReturn(validClinic);

        // When: Creating a clinic
        clinicService.createClinic(validCommand);

        // Then: Domain events are published
        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void shouldClearDomainEventsAfterPublishing() {
        // Given: Valid command and successful creation
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber(validCommand.getLegalNumber()))
                .thenReturn(false);
        
        // Capture the clinic being saved
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(clinicCaptor.capture()))
                .thenAnswer(invocation -> {
                    Clinic savedClinic = invocation.getArgument(0);
                    // Verify events are cleared before saving
                    assertThat(savedClinic.getDomainEvents()).isEmpty();
                    return validClinic;
                });

        // When: Creating a clinic
        clinicService.createClinic(validCommand);

        // Then: Events were published and cleared
        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
        
        // Verify the saved clinic has no events
        Clinic savedClinic = clinicCaptor.getValue();
        assertThat(savedClinic.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should set subscription status to ACTIVE on creation")
    void shouldSetSubscriptionStatusToActiveOnCreation() {
        // Given: Valid command
        ValidationResult validResult = new ValidationResult();
        when(createClinicCommandValidator.validate(validCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.existsByEmail(validCommand.getEmail()))
                .thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber(validCommand.getLegalNumber()))
                .thenReturn(false);
        
        // Capture the clinic being saved to verify subscription status
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(clinicCaptor.capture()))
                .thenReturn(validClinic);

        // When: Creating a clinic
        clinicService.createClinic(validCommand);

        // Then: Subscription status is set to ACTIVE
        Clinic savedClinic = clinicCaptor.getValue();
        assertThat(savedClinic.getSuscriptionStatus()).isEqualTo("ACTIVE");
    }

    // ==================== UPDATE OPERATION TESTS ====================

    @Test
    @DisplayName("Should update clinic successfully with valid data")
    void shouldUpdateClinicSuccessfully() {
        // Given: Existing clinic and valid update command
        String clinicId = "hola";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId(clinicId);
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.aValidUpdateCommand(clinicId);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Updating the clinic
        Clinic result = clinicService.updateClinic(updateCommand);

        // Then: Clinic is updated with new values
        assertThat(result).isNotNull();
        assertThat(result.getClinicID()).isEqualTo(clinicId);
        assertThat(result.getClinicName()).isEqualTo(updateCommand.getClinicName());
        assertThat(result.getLegalName()).isEqualTo(updateCommand.getLegalName());
        assertThat(result.getLegalNumber()).isEqualTo(updateCommand.getLegalNumber());
        assertThat(result.getEmail()).isEqualTo(updateCommand.getEmail());
        assertThat(result.getAddress()).isEqualTo(updateCommand.getAddress());
        assertThat(result.getPhone()).isEqualTo(updateCommand.getPhone());
        assertThat(result.getLogoUrl()).isEqualTo(updateCommand.getLogoUrl());
        assertThat(result.getSuscriptionStatus()).isEqualTo(updateCommand.getSuscriptionStatus());

        // Verify interactions
        verify(updateClinicCommandValidator).validate(updateCommand);
        verify(clinicRepositoryPort).findById(clinicId);
        verify(clinicRepositoryPort).existsByEmailAndIdNot(updateCommand.getEmail(), clinicId);
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should throw exception when clinic not found during update")
    void shouldThrowExceptionWhenClinicNotFound() {
        // Given: Update command for non-existent clinic
        String nonExistentId = "hola2";
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.aValidUpdateCommand(nonExistentId);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // When/Then: Updating non-existent clinic throws ClinicNotFoundException
        assertThatThrownBy(() -> clinicService.updateClinic(updateCommand))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());

        // Verify: Repository save was never called
        verify(clinicRepositoryPort, never()).existsByEmailAndIdNot(any(Email.class), anyString());
        verify(clinicRepositoryPort, never()).save(any(Clinic.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when email belongs to another clinic")
    void shouldThrowExceptionWhenEmailBelongsToAnotherClinic() {
        // Given: Update command with email that belongs to another clinic
        String clinicId = "hola";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId(clinicId);
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.anUpdateCommandWithEmail(
                clinicId, "another@clinic.com");
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(true); // Email exists for another clinic

        // When/Then: Updating with duplicate email throws ClinicAlreadyExistsException
        assertThatThrownBy(() -> clinicService.updateClinic(updateCommand))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("email")
                .hasMessageContaining(updateCommand.getEmail().getValue());

        // Verify: Repository save was never called
        verify(clinicRepositoryPort, never()).save(any(Clinic.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should allow updating with same email for same clinic")
    void shouldAllowSameEmailForSameClinic() {
        // Given: Update command with the same email as the existing clinic
        String clinicId = "hola";
        String sameEmail = "test@clinic.com";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithEmail(clinicId, sameEmail);
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.anUpdateCommandWithEmail(clinicId, sameEmail);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(false); // Email doesn't exist for other clinics
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Updating with same email
        Clinic result = clinicService.updateClinic(updateCommand);

        // Then: Update succeeds
        assertThat(result).isNotNull();
        assertThat(result.getEmail().getValue()).isEqualTo(sameEmail);

        // Verify: Save was called
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should publish domain events on update")
    void shouldPublishDomainEventsOnUpdate() {
        // Given: Existing clinic and valid update command
        String clinicId = "hola";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId(clinicId);
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.aValidUpdateCommand(clinicId);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Updating the clinic
        clinicService.updateClinic(updateCommand);

        // Then: Domain events are published
        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should update updatedAt timestamp on update")
    void shouldUpdateUpdatedAtTimestamp() {
        // Given: Existing clinic with original timestamps
        String clinicId = "hola";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId(clinicId);
        LocalDateTime originalUpdatedAt = existingClinic.getUpdatedAt();
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.aValidUpdateCommand(clinicId);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(false);
        
        // Capture the clinic being saved
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(clinicCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Updating the clinic
        clinicService.updateClinic(updateCommand);

        // Then: updatedAt timestamp is updated
        Clinic savedClinic = clinicCaptor.getValue();
        assertThat(savedClinic.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should not modify createdAt timestamp on update")
    void shouldNotModifyCreatedAtTimestamp() {
        // Given: Existing clinic with original timestamps
        String clinicId = "hola";
        Clinic existingClinic = ClinicTestDataBuilder.aValidCreatedClinicWithId(clinicId);
        LocalDateTime originalCreatedAt = existingClinic.getCreatedAt();
        UpdateClinicCommand updateCommand = ClinicTestDataBuilder.aValidUpdateCommand(clinicId);
        
        ValidationResult validResult = new ValidationResult();
        when(updateClinicCommandValidator.validate(updateCommand))
                .thenReturn(validResult);
        when(clinicRepositoryPort.findById(clinicId))
                .thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updateCommand.getEmail(), clinicId))
                .thenReturn(false);
        
        // Capture the clinic being saved
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(clinicCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Updating the clinic
        clinicService.updateClinic(updateCommand);

        // Then: createdAt timestamp remains unchanged
        Clinic savedClinic = clinicCaptor.getValue();
        assertThat(savedClinic.getCreatedAt()).isEqualTo(originalCreatedAt);
    }
}
