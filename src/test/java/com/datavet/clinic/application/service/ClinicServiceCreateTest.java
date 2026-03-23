package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - createClinic (admin) Tests")
class ClinicServiceCreateTest {

    private ClinicService clinicService;

    @Mock
    private ClinicRepositoryPort clinicRepositoryPort;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private Email email;
    private Phone phone;
    private Address address;
    private ClinicSchedule schedule;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicService(clinicRepositoryPort, domainEventPublisher);
        email    = new Email("clinica@test.com");
        phone    = new Phone("+34912345678");
        address  = new Address("Calle Test 1", "Madrid", "28001");
        schedule = ClinicSchedule.of("Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra fines de semana");
    }

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should create and save clinic when no conflicts exist")
    void createClinic_WhenNoConflicts_ShouldCreateAndSave() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        Clinic result = clinicService.createClinic(buildCommand());

        assertThat(result).isNotNull();
        verify(clinicRepositoryPort).existsByEmail(email);
        verify(clinicRepositoryPort).existsByLegalNumber("12345678A");
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should create clinic with ACTIVE status directly")
    void createClinic_ShouldHaveActiveStatus() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        assertThat(captor.getValue().getStatus()).isEqualTo(ClinicStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should persist all fields from the command")
    void createClinic_ShouldPersistAllCommandFields() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        Clinic saved = captor.getValue();
        assertThat(saved.getClinicName()).isEqualTo("Clínica Test");
        assertThat(saved.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(saved.getLegalNumber()).isEqualTo("12345678A");
        assertThat(saved.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(saved.getAddress()).isEqualTo(address);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getSchedule()).isEqualTo(schedule);
    }

    @Test
    @DisplayName("Should generate a non-null UUID for the clinic")
    void createClinic_ShouldGenerateId() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        assertThat(captor.getValue().getClinicID()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Should set createdAt on creation")
    void createClinic_ShouldSetCreatedAt() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should publish domain events after creating clinic")
    void createClinic_ShouldPublishDomainEvents() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void createClinic_ShouldClearDomainEventsAfterPublishing() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.createClinic(buildCommand());

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when email already exists")
    void createClinic_WhenEmailAlreadyExists_ShouldThrow() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> clinicService.createClinic(buildCommand()))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when legalNumber already exists")
    void createClinic_WhenLegalNumberAlreadyExists_ShouldThrow() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(true);

        assertThatThrownBy(() -> clinicService.createClinic(buildCommand()))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("legalNumber");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should not check legalNumber when email check already fails")
    void createClinic_WhenEmailFails_ShouldNotCheckLegalNumber() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> clinicService.createClinic(buildCommand()))
                .isInstanceOf(ClinicAlreadyExistsException.class);

        verify(clinicRepositoryPort, never()).existsByLegalNumber(any());
    }

    @Test
    @DisplayName("Should propagate exception when email check throws unexpectedly")
    void createClinic_WhenEmailCheckThrows_ShouldPropagate() {
        when(clinicRepositoryPort.existsByEmail(email))
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> clinicService.createClinic(buildCommand()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");

        verify(clinicRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void createClinic_WhenSaveThrows_ShouldPropagate() {
        when(clinicRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenThrow(new RuntimeException("DB save error"));

        assertThatThrownBy(() -> clinicService.createClinic(buildCommand()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB save error");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private CreateClinicCommand buildCommand() {
        return CreateClinicCommand.builder()
                .clinicName("Clínica Test")
                .legalName("Clínica Test S.L.")
                .legalNumber("12345678A")
                .legalType(LegalType.AUTONOMO)
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("https://example.com/logo.png")
                .schedule(schedule)
                .build();
    }
}