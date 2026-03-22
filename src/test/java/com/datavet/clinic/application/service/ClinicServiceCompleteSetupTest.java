package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.clinic.domain.exception.ClinicValidationException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - completeClinicSetup Tests")
class ClinicServiceCompleteSetupTest {

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
    @DisplayName("Should complete setup and save clinic when clinic is in PENDING_SETUP")
    void completeSetup_WhenPendingClinicExists_ShouldCompleteAndSave() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        Clinic result = clinicService.completeClinicSetup(buildCommand("clinic-1"));

        assertThat(result).isNotNull();
        verify(clinicRepositoryPort).findById("clinic-1");
        verify(clinicRepositoryPort).existsByLegalNumber("12345678A");
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should transition clinic status from PENDING_SETUP to ACTIVE")
    void completeSetup_ShouldTransitionStatusToActive() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.completeClinicSetup(buildCommand("clinic-1"));

        assertThat(captor.getValue().getStatus()).isEqualTo(ClinicStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should fill all legal and address fields after setup")
    void completeSetup_ShouldFillAllFields() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.completeClinicSetup(buildCommand("clinic-1"));

        Clinic saved = captor.getValue();
        assertThat(saved.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(saved.getLegalNumber()).isEqualTo("12345678A");
        assertThat(saved.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(saved.getAddress()).isEqualTo(address);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getSchedule()).isEqualTo(schedule);
    }

    @Test
    @DisplayName("Should set updatedAt when completing setup")
    void completeSetup_ShouldSetUpdatedAt() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.completeClinicSetup(buildCommand("clinic-1"));

        assertThat(captor.getValue().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should publish domain events after completing setup")
    void completeSetup_ShouldPublishDomainEvents() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        clinicService.completeClinicSetup(buildCommand("clinic-1"));

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void completeSetup_ShouldClearDomainEventsAfterPublishing() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.completeClinicSetup(buildCommand("clinic-1"));

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicNotFoundException when clinic does not exist")
    void completeSetup_WhenClinicNotFound_ShouldThrow() {
        when(clinicRepositoryPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicService.completeClinicSetup(buildCommand("no-existe")))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessageContaining("no-existe");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when legalNumber already exists")
    void completeSetup_WhenLegalNumberAlreadyExists_ShouldThrow() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(true);

        assertThatThrownBy(() -> clinicService.completeClinicSetup(buildCommand("clinic-1")))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("legalNumber");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicValidationException when clinic is not in PENDING_SETUP status")
    void completeSetup_WhenClinicIsNotPending_ShouldThrow() {
        // Una clínica ya activa no puede volver a completar el setup
        Clinic activeClinic = Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);

        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(activeClinic));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);

        assertThatThrownBy(() -> clinicService.completeClinicSetup(buildCommand("clinic-1")))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("PENDING_SETUP");

        verify(clinicRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void completeSetup_WhenSaveThrows_ShouldPropagate() {
        Clinic pending = buildPendingClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(pending));
        when(clinicRepositoryPort.existsByLegalNumber("12345678A")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class)))
                .thenThrow(new RuntimeException("DB save error"));

        assertThatThrownBy(() -> clinicService.completeClinicSetup(buildCommand("clinic-1")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB save error");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Clinic buildPendingClinic() {
        return Clinic.createPending("Clínica Test", email, phone);
    }

    private CompleteClinicSetupCommand buildCommand(String clinicId) {
        return CompleteClinicSetupCommand.builder()
                .clinicId(clinicId)
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