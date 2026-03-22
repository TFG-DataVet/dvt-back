package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.in.command.UpdateClinicCommand;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - updateClinic & deactivateClinic Tests")
class ClinicServiceUpdateTest {

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
    // updateClinic — happy path
    // =========================================================================

    @Test
    @DisplayName("Should update and save clinic when no conflicts exist")
    void updateClinic_WhenNoConflicts_ShouldUpdateAndSave() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        Clinic result = clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        assertThat(result).isNotNull();
        assertThat(result.getClinicName()).isEqualTo("Clínica Actualizada");
        assertThat(result.getLegalName()).isEqualTo("Clínica Actualizada S.L.");
        assertThat(result.getLegalNumber()).isEqualTo("12345678A");
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getPhone()).isEqualTo(phone);
        assertThat(result.getLogoUrl()).isEqualTo("https://example.com/logo-nuevo.png");

        verify(clinicRepositoryPort).findById("clinic-1");
        verify(clinicRepositoryPort).existsByEmailAndIdNot(email, "clinic-1");
        verify(clinicRepositoryPort).existsByLegalNumberAndIdNot("12345678A", "clinic-1");
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should update updatedAt timestamp on update")
    void updateClinic_ShouldUpdateUpdatedAt() {
        Clinic existing = buildActiveClinic();
        LocalDateTime originalUpdatedAt = existing.getUpdatedAt();

        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        assertThat(captor.getValue().getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should not modify createdAt on update")
    void updateClinic_ShouldNotModifyCreatedAt() {
        Clinic existing = buildActiveClinic();
        LocalDateTime originalCreatedAt = existing.getCreatedAt();

        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        assertThat(captor.getValue().getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should publish domain events on update")
    void updateClinic_ShouldPublishDomainEvents() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing on update")
    void updateClinic_ShouldClearDomainEventsAfterPublishing() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should allow updating with the same email as the current clinic")
    void updateClinic_WhenSameEmail_ShouldSucceed() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        Clinic result = clinicService.updateClinic(buildUpdateCommand("clinic-1"));

        assertThat(result).isNotNull();
        verify(clinicRepositoryPort).save(any(Clinic.class));
    }

    // =========================================================================
    // updateClinic — excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicNotFoundException when clinic does not exist")
    void updateClinic_WhenClinicNotFound_ShouldThrow() {
        when(clinicRepositoryPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicService.updateClinic(buildUpdateCommand("no-existe")))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessageContaining("no-existe");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when email belongs to another clinic")
    void updateClinic_WhenEmailBelongsToAnotherClinic_ShouldThrow() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(true);

        assertThatThrownBy(() -> clinicService.updateClinic(buildUpdateCommand("clinic-1")))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicAlreadyExistsException when legalNumber belongs to another clinic")
    void updateClinic_WhenLegalNumberBelongsToAnotherClinic_ShouldThrow() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(true);

        assertThatThrownBy(() -> clinicService.updateClinic(buildUpdateCommand("clinic-1")))
                .isInstanceOf(ClinicAlreadyExistsException.class)
                .hasMessageContaining("legalNumber");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicValidationException when trying to update a DEACTIVATED clinic")
    void updateClinic_WhenClinicIsDeactivated_ShouldThrow() {
        Clinic active = buildActiveClinic();
        active.deactivate("Cierre temporal");
        // Limpiamos los eventos del deactivate para no interferir con el verify posterior
        active.clearDomainEvents();

        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(active));
        when(clinicRepositoryPort.existsByEmailAndIdNot(email, "clinic-1")).thenReturn(false);
        when(clinicRepositoryPort.existsByLegalNumberAndIdNot("12345678A", "clinic-1")).thenReturn(false);

        assertThatThrownBy(() -> clinicService.updateClinic(buildUpdateCommand("clinic-1")))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("desactivada");

        verify(clinicRepositoryPort, never()).save(any());
    }

    // =========================================================================
    // deactivateClinic — happy path
    // =========================================================================

    @Test
    @DisplayName("Should deactivate and save clinic when it is active")
    void deactivateClinic_WhenClinicIsActive_ShouldDeactivateAndSave() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));
        when(clinicRepositoryPort.save(any(Clinic.class))).thenAnswer(i -> i.getArgument(0));

        clinicService.deactivateClinic("clinic-1", "Cierre temporal");

        verify(clinicRepositoryPort).findById("clinic-1");
        verify(clinicRepositoryPort).save(any(Clinic.class));
        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should transition clinic status to DEACTIVATED")
    void deactivateClinic_ShouldSetDeactivatedStatus() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.deactivateClinic("clinic-1", "Cierre temporal");

        assertThat(captor.getValue().getStatus()).isEqualTo(ClinicStatus.DEACTIVATED);
    }

    @Test
    @DisplayName("Should set updatedAt when deactivating")
    void deactivateClinic_ShouldSetUpdatedAt() {
        Clinic existing = buildActiveClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));

        ArgumentCaptor<Clinic> captor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        clinicService.deactivateClinic("clinic-1", "Cierre temporal");

        assertThat(captor.getValue().getUpdatedAt()).isNotNull();
    }

    // =========================================================================
    // deactivateClinic — excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicNotFoundException when clinic does not exist")
    void deactivateClinic_WhenClinicNotFound_ShouldThrow() {
        when(clinicRepositoryPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicService.deactivateClinic("no-existe", "razón"))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessageContaining("no-existe");

        verify(clinicRepositoryPort, never()).save(any());
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw ClinicValidationException when clinic is already deactivated")
    void deactivateClinic_WhenAlreadyDeactivated_ShouldThrow() {
        Clinic existing = buildActiveClinic();
        existing.deactivate("Primera desactivación");
        existing.clearDomainEvents();

        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> clinicService.deactivateClinic("clinic-1", "Segunda desactivación"))
                .isInstanceOf(ClinicValidationException.class)
                .hasMessageContaining("desactivada");

        verify(clinicRepositoryPort, never()).save(any());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Clinic buildActiveClinic() {
        return Clinic.create(
                "Clínica Test",
                "Clínica Test S.L.",
                "12345678A",
                LegalType.AUTONOMO,
                address,
                phone,
                email,
                "https://example.com/logo.png",
                schedule
        );
    }

    private UpdateClinicCommand buildUpdateCommand(String clinicId) {
        return UpdateClinicCommand.builder()
                .clinicId(clinicId)
                .clinicName("Clínica Actualizada")
                .legalName("Clínica Actualizada S.L.")
                .legalNumber("12345678A")
                .legalType(LegalType.AUTONOMO)
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("https://example.com/logo-nuevo.png")
                .schedule(schedule)
                .build();
    }
}