package com.datavet.clinic.application.service;

import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicService implements ClinicUseCase, ApplicationService {

    private final ClinicRepositoryPort clinicRepositoryPort;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Clinic createClinic(CreateClinicCommand command) {
        if (clinicRepositoryPort.existsByEmail(command.getEmail().getValue())) {
            throw new ClinicAlreadyExistsException("email", command.getEmail().getValue());
        }

        if (clinicRepositoryPort.existsByLegalNumber(command.getLegalNumber())) {
            throw new ClinicAlreadyExistsException("legalNumber", command.getLegalNumber());
        }

        Clinic clinic = Clinic.create(
                command.getClinicName(),
                command.getLegalName(),
                command.getLegalNumber(),
                command.getLegalType(),
                command.getAddress(),
                command.getPhone(),
                command.getEmail(),
                command.getLogoUrl(),
                command.getSchedule()
        );

        publishDomainEvents(clinic);
        return clinicRepositoryPort.save(clinic);
    }

    @Override
    public Clinic createPendingClinic(CreatePendingClinicCommand command) {
        // Verificamos que el email no esté ya registrado
        if (clinicRepositoryPort.existsByEmail(command.getEmail().getValue())) {
            throw new ClinicAlreadyExistsException("email", command.getEmail().getValue());
        }

        // El dominio valida nombre, email y teléfono
        Clinic clinic = Clinic.createPending(
                command.getClinicName(),
                command.getEmail(),
                command.getPhone()
        );

        publishDomainEvents(clinic);
        return clinicRepositoryPort.save(clinic);
    }

    @Override
    public Clinic completeClinicSetup(CompleteClinicSetupCommand command) {
        Clinic clinic = clinicRepositoryPort.findById(command.getClinicId())
                .orElseThrow(() -> new ClinicNotFoundException("Clinic", command.getClinicId()));

        if (clinicRepositoryPort.existsByLegalNumber(command.getLegalNumber())) {
            throw new ClinicAlreadyExistsException("legalNumber", command.getLegalNumber());
        }

        clinic.completeSetup(
                command.getLegalName(),
                command.getLegalNumber(),
                command.getLegalType(),
                command.getAddress(),
                command.getPhone(),
                command.getEmail(),
                command.getLogoUrl(),
                command.getSchedule()
        );

        publishDomainEvents(clinic);
        return clinicRepositoryPort.save(clinic);
    }

    @Override
    public Clinic updateClinic(UpdateClinicCommand command) {
        Clinic existing = clinicRepositoryPort.findById(command.getClinicId())
                .orElseThrow(() -> new ClinicNotFoundException("Clinic", command.getClinicId()));

        if (clinicRepositoryPort.existsByEmailAndIdNot(command.getEmail().getValue(), command.getClinicId())) {
            throw new ClinicAlreadyExistsException("email", command.getEmail().getValue());
        }

        if (clinicRepositoryPort.existsByLegalNumberAndIdNot(command.getLegalNumber(), command.getClinicId())) {
            throw new ClinicAlreadyExistsException("legalNumber", command.getLegalNumber());
        }

        existing.update(
                command.getClinicName(),
                command.getLegalName(),
                command.getLegalNumber(),
                command.getLegalType(),
                command.getAddress(),
                command.getPhone(),
                command.getEmail(),
                command.getLogoUrl(),
                command.getSchedule()
        );

        publishDomainEvents(existing);
        return clinicRepositoryPort.save(existing);
    }

    @Override
    public void deactivateClinic(String id, String reason) {
        Clinic clinic = getClinicById(id);
        clinic.deactivate(reason);
        publishDomainEvents(clinic);
        clinicRepositoryPort.save(clinic);
    }

    @Override
    public Clinic getClinicById(String id) {
        return clinicRepositoryPort.findById(id)
                .orElseThrow(() -> new ClinicNotFoundException("Clinic", id));
    }

    @Override
    public List<Clinic> getAllClinics() {
        return clinicRepositoryPort.findAll();
    }

    private void publishDomainEvents(Clinic clinic) {
        List<DomainEvent> events = clinic.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        clinic.clearDomainEvents();
    }
}