package com.datavet.datavet.clinic.application.service;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.datavet.clinic.application.service.ClinicService;
import com.datavet.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationError;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ClinicServiceExceptionTest {

    private ClinicService clinicService;

    @Mock
    private ClinicRepositoryPort clinicRepositoryPort;
    
    @Mock
    private com.datavet.datavet.clinic.application.validation.CreateClinicCommandValidator createValidator;
    
    @Mock
    private com.datavet.datavet.clinic.application.validation.UpdateClinicCommandValidator updateValidator;
    
    @Mock
    private DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicService(clinicRepositoryPort, createValidator, updateValidator, domainEventPublisher);
    }

    @Test
    void clinicService_ShouldImplementApplicationServiceInterface() {
        // Test ApplicationService integration
        assertThat(clinicService).isInstanceOf(ApplicationService.class);
    }

    @Test
    void createClinic_WhenValidationFails_ShouldThrowClinicValidationException() {
        // Given
        Address address = new Address("Test Address", "Test City", "12345");
        Phone phone = new Phone("123456789");
        Email email = new Email("test@example.com");
        
        CreateClinicCommand command = new CreateClinicCommand(
                "", // Invalid empty clinic name
                "Test Legal Name",
                "123456789",
                address,
                phone,
                email,
                "http://example.com/logo.png"
        );

        // Mock validation to fail using shared validation framework
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("clinicName", "Clinic name is required");
        when(createValidator.validate(command)).thenReturn(validationResult);

        // When & Then
        assertThatThrownBy(() -> clinicService.createClinic(command))
                .isInstanceOf(ClinicValidationException.class);
        
        // Verify validation was called
        verify(createValidator).validate(command);
    }

    @Test
    void updateClinic_WhenValidationFails_ShouldThrowClinicValidationException() {
        // Given
        Address address = new Address("Updated Address", "Updated City", "12345");
        Phone phone = new Phone("987654321");
        Email email = new Email("updated@example.com");
        
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(null) // Invalid null clinic ID
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("123456789")
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("http://example.com/updated-logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Mock validation to fail using shared validation framework
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("clinicId", "Clinic ID is required");
        when(updateValidator.validate(command)).thenReturn(validationResult);

        // When & Then
        assertThatThrownBy(() -> clinicService.updateClinic(command))
                .isInstanceOf(ClinicValidationException.class);
        
        // Verify validation was called
        verify(updateValidator).validate(command);
    }

    @Test
    void createClinic_WhenValidationPasses_ShouldProceedWithBusinessLogic() {
        // Given
        Address address = new Address("Test Address", "Test City", "12345");
        Phone phone = new Phone("123456789");
        Email email = new Email("test@example.com");
        
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "123456789",
                address,
                phone,
                email,
                "http://example.com/logo.png"
        );

        // Mock validation to pass using shared validation framework
        ValidationResult validationResult = new ValidationResult(); // Empty result = valid
        when(createValidator.validate(command)).thenReturn(validationResult);
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("123456789")).thenReturn(false);
        
        Clinic savedClinic = Clinic.builder()
                .clinicID(1L)
                .clinicName("Test Clinic")
                .legalName("Test Legal Name")
                .legalNumber("123456789")
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("http://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();
        when(clinicRepositoryPort.save(any(Clinic.class))).thenReturn(savedClinic);

        // When
        Clinic result = clinicService.createClinic(command);

        // Then
        assertThat(result).isNotNull();
        verify(createValidator).validate(command);
        verify(clinicRepositoryPort).existsByEmail(email);
        verify(clinicRepositoryPort).existsByLegalNumber("123456789");
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    void repositoryPort_ShouldExtendSharedRepositoryInterface() {
        // Test repository interface integration
        // ClinicRepositoryPort should extend Repository<Clinic, Long>
        assertThat(clinicRepositoryPort).isInstanceOf(com.datavet.datavet.shared.application.port.Repository.class);
    }

    @Test
    void getAllClinics_ShouldUseSharedRepositoryInterface() {
        // Given
        List<Clinic> expectedClinics = Arrays.asList(
                Clinic.builder()
                        .clinicID(1L)
                        .clinicName("Clinic 1")
                        .legalName("Legal Name 1")
                        .legalNumber("123456789")
                        .address(new Address("Address 1", "City 1", "12345"))
                        .phone(new Phone("123456789"))
                        .email(new Email("clinic1@example.com"))
                        .logoUrl("http://example.com/logo1.png")
                        .suscriptionStatus("ACTIVE")
                        .build(),
                Clinic.builder()
                        .clinicID(2L)
                        .clinicName("Clinic 2")
                        .legalName("Legal Name 2")
                        .legalNumber("987654321")
                        .address(new Address("Address 2", "City 2", "54321"))
                        .phone(new Phone("987654321"))
                        .email(new Email("clinic2@example.com"))
                        .logoUrl("http://example.com/logo2.png")
                        .suscriptionStatus("ACTIVE")
                        .build()
        );
        
        when(clinicRepositoryPort.findAll()).thenReturn(expectedClinics);

        // When
        List<Clinic> result = clinicService.getAllClinics();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedClinics);
        verify(clinicRepositoryPort).findAll(); // Method from shared Repository interface
    }

    @Test
    void deleteClinic_ShouldUseSharedRepositoryInterface() {
        // Given
        Long clinicId = 1L;

        // When
        clinicService.deleteClinic(clinicId);

        // Then
        verify(clinicRepositoryPort).deleteById(clinicId); // Method from shared Repository interface
    }

    @Test
    void createClinic_WhenEmailAlreadyExists_ShouldThrowClinicAlreadyExistsException() {
        // Given
        Address address = new Address("Test Address", "Test City", "12345");
        Phone phone = new Phone("123456789");
        Email email = new Email("test@example.com");
        
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "123456789",
                address,
                phone,
                email,
                "http://example.com/logo.png"
        );

        // Mock validation to pass
        when(createValidator.validate(command)).thenReturn(new ValidationResult());
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clinicService.createClinic(command))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessage("Clinic already exists with email: test@example.com");
    }

    @Test
    void createClinic_WhenLegalNumberAlreadyExists_ShouldThrowClinicAlreadyExistsException() {
        // Given
        Address address = new Address("Test Address", "Test City", "12345");
        Phone phone = new Phone("123456789");
        Email email = new Email("test@example.com");
        
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "123456789",
                address,
                phone,
                email,
                "http://example.com/logo.png"
        );

        // Mock validation to pass
        when(createValidator.validate(command)).thenReturn(new ValidationResult());
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("123456789")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clinicService.createClinic(command))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessage("Clinic already exists with legalNumber: 123456789");
    }

    @Test
    void updateClinic_WhenClinicNotFound_ShouldThrowClinicNotFoundException() {
        // Given
        Address address = new Address("Updated Address", "Updated City", "12345");
        Phone phone = new Phone("987654321");
        Email email = new Email("updated@example.com");
        
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(1L)
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("123456789")
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("http://example.com/updated-logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Mock validation to pass
        when(updateValidator.validate(command)).thenReturn(new ValidationResult());
        when(clinicRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.updateClinic(command))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessage("Clinic not found with id: 1");
    }

    @Test
    void updateClinic_WhenEmailAlreadyExistsForDifferentClinic_ShouldThrowClinicAlreadyExistsException() {
        // Given
        Address updatedAddress = new Address("Updated Address", "Updated City", "54321");
        Phone updatedPhone = new Phone("987654321");
        Email updatedEmail = new Email("existing@example.com");
        
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(1L)
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("123456789")
                .address(updatedAddress)
                .phone(updatedPhone)
                .email(updatedEmail)
                .logoUrl("http://example.com/updated-logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        Address originalAddress = new Address("Original Address", "Original City", "12345");
        Phone originalPhone = new Phone("123456789");
        Email originalEmail = new Email("original@example.com");
        
        Clinic existingClinic = Clinic.builder()
                .clinicID(1L)
                .clinicName("Original Clinic")
                .legalName("Original Legal Name")
                .legalNumber("123456789")
                .address(originalAddress)
                .phone(originalPhone)
                .email(originalEmail)
                .logoUrl("http://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Mock validation to pass
        when(updateValidator.validate(command)).thenReturn(new ValidationResult());
        when(clinicRepositoryPort.findById(1L)).thenReturn(Optional.of(existingClinic));
        when(clinicRepositoryPort.existsByEmailAndIdNot(updatedEmail, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clinicService.updateClinic(command))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessage("Clinic already exists with email: existing@example.com");
    }

    @Test
    void getClinicById_WhenClinicNotFound_ShouldThrowClinicNotFoundException() {
        // Given
        Long clinicId = 999L;
        when(clinicRepositoryPort.findById(clinicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clinicService.getClinicById(clinicId))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessage("Clinic not found with id: 999");
    }

    @Test
    void createClinic_WhenNoConflicts_ShouldCreateSuccessfully() {
        // Given
        Address address = new Address("Test Address", "Test City", "12345");
        Phone phone = new Phone("123456789");
        Email email = new Email("test@example.com");
        
        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic",
                "Test Legal Name",
                "123456789",
                address,
                phone,
                email,
                "http://example.com/logo.png"
        );

        Clinic savedClinic = Clinic.builder()
                .clinicID(1L)
                .clinicName("Test Clinic")
                .legalName("Test Legal Name")
                .legalNumber("123456789")
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("http://example.com/logo.png")
                .suscriptionStatus("ACTIVE")
                .build();

        // Mock validation to pass using shared validation framework
        ValidationResult validationResult = new ValidationResult(); // Empty result = valid
        when(createValidator.validate(command)).thenReturn(validationResult);
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("123456789")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenReturn(savedClinic);

        // When
        Clinic result = clinicService.createClinic(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getClinicID()).isEqualTo(1L);
        assertThat(result.getClinicName()).isEqualTo("Test Clinic");
        assertThat(result.getEmail().getValue()).isEqualTo("test@example.com");
        assertThat(result.getSuscriptionStatus()).isEqualTo("ACTIVE");
        
        // Verify shared validation framework was used
        verify(createValidator).validate(command);
        // Verify shared repository interface methods were used
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }
}