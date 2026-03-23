package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - createPendingClinic Tests")
class ClinicServiceCreatePendingTest {

    private ClinicService clinicService;

    @Mock
    private ClinicRepositoryPort clinicRepositoryPort;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private Email email;
    private Phone phone;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicService(clinicRepositoryPort, domainEventPublisher);
        email = new Email("clinica@test.com");
        phone = new Phone("+34912345678");
    }

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should create pending clinic and save it when email is not registered")
    void createPendingClinic_WhenEmailNotExists_ShouldCreateAndSave() {
        CreatePendingClinicCommand command = buildCommand();

        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenAnswer(i -> i.getArgument(0));

        Clinic result = clinicService.createPendingClinic(command);

        assertThat(result).isNotNull();
        verify(clinicRepositoryPort).existsByEmail(email);
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should create clinic with PENDING_SETUP status")
    void createPendingClinic_ShouldHavePendingSetupStatus() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        assertThat(captor.getValue().getStatus()).isEqualTo(ClinicStatus.PENDING_SETUP);
    }

    @Test
    @DisplayName("Should only set name, email and phone — legal and address fields must be null")
    void createPendingClinic_ShouldOnlySetBasicFields() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        Clinic saved = captor.getValue();
        assertThat(saved.getClinicName()).isEqualTo("Clínica Test");
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getLegalName()).isNull();
        assertThat(saved.getLegalNumber()).isNull();
        assertThat(saved.getLegalType()).isNull();
        assertThat(saved.getAddress()).isNull();
        assertThat(saved.getSchedule()).isNull();
    }

    @Test
    @DisplayName("Should generate a non-null UUID for the clinic")
    void createPendingClinic_ShouldGenerateId() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        assertThat(captor.getValue().getClinicID()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Should set createdAt on creation")
    void createPendingClinic_ShouldSetCreatedAt() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should publish domain events after creating pending clinic")
    void createPendingClinic_ShouldPublishDomainEvents() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void createPendingClinic_ShouldClearDomainEventsAfterPublishing() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        clinicService.createPendingClinic(command);

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when email is already registered")
    void createPendingClinic_WhenEmailAlreadyExists_ShouldThrow() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> clinicService.createPendingClinic(command))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should not call save when email check throws an unexpected exception")
    void createPendingClinic_WhenEmailCheckThrows_ShouldPropagate() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email))
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> clinicService.createPendingClinic(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");

        verify(clinicRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void createPendingClinic_WhenSaveThrows_ShouldPropagate() {
        CreatePendingClinicCommand command = buildCommand();
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenThrow(new RuntimeException("DB save error"));

        assertThatThrownBy(() -> clinicService.createPendingClinic(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB save error");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private CreatePendingClinicCommand buildCommand() {
        return CreatePendingClinicCommand.builder()
                .clinicName("Clínica Test")
                .email(email)
                .phone(phone)
                .build();
    }
}