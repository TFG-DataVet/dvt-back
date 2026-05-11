package com.datavet.appointment.application.service;

import com.datavet.appointment.application.port.in.AppointmentUseCase;
import com.datavet.appointment.application.port.in.command.CancelAppointmentCommand;
import com.datavet.appointment.application.port.in.command.CreateAppointmentCommand;
import com.datavet.appointment.application.port.in.command.UpdateAppointmentStatusCommand;
import com.datavet.appointment.application.port.out.AppointmentEmailPort;
import com.datavet.appointment.application.port.out.AppointmentRepositoryPort;
import com.datavet.appointment.domain.exception.AppointmentNotFoundException;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.model.PetSnapshot;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService implements AppointmentUseCase, ApplicationService {

    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final AppointmentEmailPort      appointmentEmailPort;
    private final DomainEventPublisher      domainEventPublisher;

    @Override
    @Transactional
    public Appointment create(CreateAppointmentCommand command) {

        PetSnapshot petSnapshot = null;
        if (!command.isEmergency()) {
            petSnapshot = PetSnapshot.of(
                    command.getPetId(),
                    command.getPetName(),
                    command.getPetSpecies()
            );
        }

        Appointment appointment = Appointment.create(
                command.getClinicId(),
                command.isEmergency(),
                command.getType(),
                command.getScheduledAt(),
                command.getOwnerId(),
                command.getOwnerName(),
                command.getOwnerEmail(),
                command.getOwnerPhone(),
                petSnapshot,
                command.getCreationEmployeeId(),
                command.getMedicalEmployeeId(),
                command.getNotes(),
                command.getProductIds(),
                command.getSource()
        );

        publishDomainEvents(appointment);
        Appointment saved = appointmentRepositoryPort.save(appointment);

        if (!saved.isEmergency() && saved.getOwnerEmail() != null) {
            try {
                appointmentEmailPort.sendAppointmentCreatedEmail(
                        saved.getOwnerEmail(),
                        saved.getOwnerName(),
                        saved
                );
            } catch (Exception e) {
                log.warn("No se pudo enviar email de confirmación de cita {}: {}", saved.getId(), e.getMessage());
            }
        }

        return saved;
    }

    @Override
    @Transactional
    public Appointment updateStatus(UpdateAppointmentStatusCommand command) {

        Appointment appointment = appointmentRepositoryPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(command.getAppointmentId()));

        if (!appointment.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La cita no pertenece a tu clínica");
        }

        if (command.getMedicalEmployeeId() != null && !command.getMedicalEmployeeId().isBlank()) {
            appointment.assignMedicalEmployee(command.getMedicalEmployeeId());
        }

        appointment.advanceStatus(command.getNewStatus());

        publishDomainEvents(appointment);
        return appointmentRepositoryPort.save(appointment);
    }

    @Override
    @Transactional
    public void cancel(CancelAppointmentCommand command) {

        Appointment appointment = appointmentRepositoryPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(command.getAppointmentId()));

        if (!appointment.getClinicId().equals(command.getClinicId())) {
            throw new AccessDeniedException("La cita no pertenece a tu clínica");
        }

        appointment.cancel(command.getReason());

        publishDomainEvents(appointment);
        appointmentRepositoryPort.save(appointment);
    }

    @Override
    public Appointment getById(String appointmentId, String clinicId) {
        Appointment appointment = appointmentRepositoryPort.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (!appointment.getClinicId().equals(clinicId)) {
            throw new AccessDeniedException("La cita no pertenece a tu clínica");
        }

        return appointment;
    }

    @Override
    public List<Appointment> getByClinic(
            String clinicId,
            LocalDate date,
            AppointmentStatus status,
            AppointmentType type,
            String ownerId) {

        return appointmentRepositoryPort.findByClinicIdWithFilters(
                clinicId, date, status, type, ownerId);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void publishDomainEvents(Appointment appointment) {
        List<DomainEvent> events = appointment.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        appointment.clearDomainEvents();
    }
}
