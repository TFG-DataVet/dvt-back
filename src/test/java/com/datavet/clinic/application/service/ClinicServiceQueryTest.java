package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicService - Queries Tests")
class ClinicServiceQueryTest {

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
    // getClinicById — happy path
    // =========================================================================

    @Test
    @DisplayName("Should return clinic when it exists")
    void getClinicById_WhenClinicExists_ShouldReturnClinic() {
        Clinic existing = buildClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));

        Clinic result = clinicService.getClinicById("clinic-1");

        assertThat(result).isNotNull();
        assertThat(result.getClinicName()).isEqualTo("Clínica Test");
        verify(clinicRepositoryPort).findById("clinic-1");
    }

    @Test
    @DisplayName("Should return clinic with all its fields intact")
    void getClinicById_ShouldReturnClinicWithAllFields() {
        Clinic existing = buildClinic();
        when(clinicRepositoryPort.findById("clinic-1")).thenReturn(Optional.of(existing));

        Clinic result = clinicService.getClinicById("clinic-1");

        assertThat(result.getClinicName()).isEqualTo("Clínica Test");
        assertThat(result.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(result.getLegalNumber()).isEqualTo("12345678A");
        assertThat(result.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPhone()).isEqualTo(phone);
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getSchedule()).isEqualTo(schedule);
    }

    // =========================================================================
    // getClinicById — excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw ClinicNotFoundException when clinic does not exist")
    void getClinicById_WhenClinicNotFound_ShouldThrow() {
        when(clinicRepositoryPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicService.getClinicById("no-existe"))
                .isInstanceOf(ClinicNotFoundException.class)
                .hasMessageContaining("no-existe");
    }

    @Test
    @DisplayName("Should propagate exception when repository throws unexpectedly")
    void getClinicById_WhenRepositoryThrows_ShouldPropagate() {
        when(clinicRepositoryPort.findById("clinic-1"))
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> clinicService.getClinicById("clinic-1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    // =========================================================================
    // getAllClinics — happy path
    // =========================================================================

    @Test
    @DisplayName("Should return all clinics from repository")
    void getAllClinics_ShouldReturnAllClinics() {
        List<Clinic> expected = List.of(buildClinic(), buildClinic());
        when(clinicRepositoryPort.findAll()).thenReturn(expected);

        List<Clinic> result = clinicService.getAllClinics();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expected);
        verify(clinicRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clinics exist")
    void getAllClinics_WhenNoClinicsExist_ShouldReturnEmptyList() {
        when(clinicRepositoryPort.findAll()).thenReturn(List.of());

        List<Clinic> result = clinicService.getAllClinics();

        assertThat(result).isEmpty();
        verify(clinicRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should not call any other repository method besides findAll")
    void getAllClinics_ShouldOnlyCallFindAll() {
        when(clinicRepositoryPort.findAll()).thenReturn(List.of());

        clinicService.getAllClinics();

        verify(clinicRepositoryPort).findAll();
        verifyNoMoreInteractions(clinicRepositoryPort);
    }

    // =========================================================================
    // getAllClinics — excepciones
    // =========================================================================

    @Test
    @DisplayName("Should propagate exception when repository throws unexpectedly")
    void getAllClinics_WhenRepositoryThrows_ShouldPropagate() {
        when(clinicRepositoryPort.findAll())
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> clinicService.getAllClinics())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Clinic buildClinic() {
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
}