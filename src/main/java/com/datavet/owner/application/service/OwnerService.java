package com.datavet.owner.application.service;

import com.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.owner.application.validation.UpdateOwnerCommandValidator;
import com.datavet.owner.domain.exception.OwnerAlreadyExistsException;
import com.datavet.owner.domain.exception.OwnerNotFoundException;
import com.datavet.owner.domain.model.Owner;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.exception.email.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService implements OwnerUseCase, ApplicationService {

    private final OwnerRepositoryPort ownerRepositoryPort;
    private final CreateOwnerCommandValidator createOwnerCommandValidator;
    private final DomainEventPublisher domainEventPublisher;
    private final UpdateOwnerCommandValidator updateClinicCommandValidator;


    @Override
    public Owner createOwner(CreateOwnerCommand command) {

        // Check for duplicate email (value object is already created in command)
        if ( ownerRepositoryPort.existsByEmail(command.getOwnerEmail().getValue())){
            throw new OwnerAlreadyExistsException("email", command.getOwnerEmail().getValue());
        }

        // Check exists dni
        if (ownerRepositoryPort.existsByDocumentNumber(command.getOwnerDni().getDocumentNumber())) {
            throw new OwnerAlreadyExistsException("dni", command.getOwnerDni().getDocumentNumber());
        }

        // Check for phone
        if(ownerRepositoryPort.existsByPhone(command.getOwnerPhone())) {
            throw new OwnerAlreadyExistsException("phone", command.getOwnerPhone().getValue());
        }

        // Use Factory method to create owner with domain events
        Owner owner = Owner.create(
                command.getClinidId(),
                command.getOwnerName(),
                command.getOwnerLastName(),
                command.getOwnerDni(),
                command.getOwnerPhone(),
                command.getOwnerEmail(),
                command.getOwnerAddress(),
                command.getUrl(),
                command.isAcceptTermsAndCond()
        );

        // Publish domain events BEFORE saving (While ew still have them)
        publishDomainEvent(owner);

        Owner savedOwner = ownerRepositoryPort.save(owner);

        return savedOwner;
    }

    @Override
    public Owner updateOwner(UpdateOwnerCommand command) {
        Owner existing = ownerRepositoryPort.findById(command.getOwnerID())
                .orElseThrow(() -> new OwnerNotFoundException(command.getOwnerID()));

        ownerRepositoryPort.findByEmail(command.getOwnerEmail().getValue())
                .ifPresent(ownerWithSameEmail -> {

                    if (!ownerWithSameEmail.getOwnerId().equals(command.getOwnerID()))
                        throw new EmailAlreadyExistsException("Owner", command.getOwnerEmail().getValue());
                });

        existing.update(
                command.getOwnerName(),
                command.getOwnerLastName(),
                command.getOwnerEmail(),
                command.getOwnerDni(),
                command.getOwnerAddress(),
                command.getOwnerPhone(),
                command.getUrl()
        );

        publishDomainEvent(existing);

        Owner savedOwner = ownerRepositoryPort.save(existing);

        return savedOwner;
    }

    @Override
    public void deleteOwner(String id) {
        Owner owner = getOwnerById(id);
        owner.delete();
        publishDomainEvent(owner);

        ownerRepositoryPort.deleteById(id);
    }

    @Override
    public Owner getOwnerById(String id) {
        return ownerRepositoryPort.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException(id));
    }

    @Override
    public List<Owner> getAllOwners() {
        return ownerRepositoryPort.findAll();
    }

    private void publishDomainEvent(Owner owner) {
        List<DomainEvent> events = owner.getDomainEvents();
        for (DomainEvent event : events) {
            domainEventPublisher.publish(event);
        }
        owner.clearDomainEvents();
    }
}
