package com.datavet.datavet.pet.application.service;

import com.datavet.datavet.pet.application.factory.MedicalRecordDetailsFactory;
import com.datavet.datavet.pet.application.port.in.MedicalRecordUseCase;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.ApplyMedicalRecordActionCommand;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.CorrectMedicalRecordCommand;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.CreateMedicalRecordCommand;
import com.datavet.datavet.pet.application.port.out.PetRepositoryPort;
import com.datavet.datavet.pet.domain.exception.MedicalRecordNotFoundException;
import com.datavet.datavet.pet.domain.exception.PetNotFoundException;
import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.infrastructure.adapter.output.MedicalRecordRepositoryPort;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordService implements MedicalRecordUseCase, ApplicationService {

    private final MedicalRecordRepositoryPort medicalRecordRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;
    private final MedicalRecordDetailsFactory detailsFactory;   // ← nueva dependencia
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public MedicalRecord createMedicalRecord(CreateMedicalRecordCommand command) {
        // Validación de contexto
        if (!petRepositoryPort.existsById(command.getPetId())) {
            throw new PetNotFoundException(command.getPetId());
        }

        // El service construye el details a partir del request tipado
        MedicalRecordDetails details = detailsFactory.create(command.getDetailsRequest());

        // El dominio valida type == details.getType(), details.validate(), etc.
        MedicalRecord record = MedicalRecord.create(
                command.getPetId(),
                command.getClinicId(),
                command.getType(),
                command.getVeterinarianId(),
                command.getNotes(),
                details
        );

        publishDomainEvents(record);
        return medicalRecordRepositoryPort.save(record);
    }

    @Override
    @Transactional
    public MedicalRecord correctMedicalRecord(CorrectMedicalRecordCommand command) {
        MedicalRecord original = medicalRecordRepositoryPort
                .findById(command.getOriginalRecordId())
                .orElseThrow(() -> new MedicalRecordNotFoundException(command.getOriginalRecordId()));

        // El service construye los details corregidos también a través de la factory
        MedicalRecordDetails correctedDetails = detailsFactory.create(command.getDetailsRequest());

        MedicalRecord corrected = MedicalRecord.createCorrectionOf(
                original, correctedDetails, command.getVeterinarianId(), command.getReason()
        );

        original.markAsCorrected(corrected, command.getReason());

        publishDomainEvents(corrected);
        publishDomainEvents(original);

        medicalRecordRepositoryPort.save(original);
        return medicalRecordRepositoryPort.save(corrected);
    }

    @Override
    public MedicalRecord applyMedicalRecordAction(ApplyMedicalRecordActionCommand command) {
        return null;
    }

    @Transactional
    @Override
    public MedicalRecord applyAction(ApplyMedicalRecordActionCommand command) {
        MedicalRecord record = medicalRecordRepositoryPort
                .findById(command.getMedicalRecordId())
                .orElseThrow(() -> new MedicalRecordNotFoundException(command.getMedicalRecordId()));

        // El dominio lanza UnsupportedOperationException si el tipo no soporta estados
        // El dominio lanza IllegalStateException si la acción no es válida para el estado actual
        record.applyAction(command.getAction(), command.getVeterinarianId());

        publishDomainEvents(record);

        return medicalRecordRepositoryPort.save(record);
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    @Override
    public MedicalRecord getMedicalRecordById(String medicalRecordId) {
        return medicalRecordRepositoryPort.findById(medicalRecordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(medicalRecordId));
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByPet(String petId) {
        if (!petRepositoryPort.existsById(petId)) {
            throw new PetNotFoundException(petId);
        }
        return medicalRecordRepositoryPort.findByPetId(petId);
    }

    @Override
    public List<MedicalRecord> getMeicalRecordsByType(String petId, MedicalRecordType type) {
        return List.of();
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByType(String petId, MedicalRecordType type) {
        if (!petRepositoryPort.existsById(petId)) {
            throw new PetNotFoundException(petId);
        }
        return medicalRecordRepositoryPort.findByPetIdAndType(petId, type);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private void publishDomainEvents(MedicalRecord record) {
        List<DomainEvent> events = record.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        record.clearDomainEvents();
    }

}