package com.datavet.datavet.owner.application.service;

import com.datavet.datavet.owner.application.port.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.datavet.owner.domain.exception.OwnerAlreadyExistsException;
import com.datavet.datavet.owner.domain.exception.OwnerValidationException;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService implements OwnerUseCase, ApplicationService {

    private final OwnerRepositoryPort ownerRepositoryPort;
    private final CreateOwnerCommandValidator createOwnerCommandValidator;
//    private final UpdateOwnerCommandValidation updateOwnerCommandValidation;
    private final DomainEventPublisher domainEventPublisher;


    @Override
    public Owner createOwner(CreateOwnerCommand command) {
        // Validate command using shared validation framework
        ValidationResult validationResult = createOwnerCommandValidator.validate(command);
        if ( validationResult.hasErrors()) {
            throw new OwnerValidationException(validationResult);
        }

        // Check for duplicate email (value object is already created in command)
        if ( ownerRepositoryPort.existsByEmail(command.getOwnerEmail())){
            throw new OwnerAlreadyExistsException("email", command.getOwnerEmail().getValue());
        }

        // Check exists dni
        if (ownerRepositoryPort.existsByDni(command.getOwnerDni())) {
            throw new OwnerAlreadyExistsException("dni", command.getOwnerDni());
        }

        // Check for phone
        if(ownerRepositoryPort.existsByPhone(command.getOwnerPhone())) {
            throw new OwnerAlreadyExistsException("phone", command.getOwnerPhone().getValue());
        }

        // Use Factory method to create owner with domain events
        Owner owner = Owner.create(
                null,
                1L,
                command.getOwnerName(),
                command.getOwnerLastName(),
                command.getOwnerDni(),
                command.getOwnerPhone(),
                command.getOwnerEmail(),
                command.getOwnerPassword(),
                command.getOwnerAddress()
        );

        // Publish domain events BEFORE sabing (While ew still have them)
        publishDomainEvent(owner);

        Owner savedOwner = ownerRepositoryPort.save(owner);

        return savedOwner;
    }

    @Override
    public Owner updateOwner(UpdateOwnerCommand command) {
        return null;
    }

    @Override
    public void deleteOwner(Long id) {

    }

    @Override
    public Owner getOwnerById(Long id) {
        return null;
    }

    @Override
    public List<Owner> getAllOwners() {
        return List.of();
    }

    private void publishDomainEvent(Owner owner) {
        List<DomainEvent> events = owner.getDomainEvents();
        for (DomainEvent event : events) {
            domainEventPublisher.publish(event);
        }
        owner.clearDomainEvents();
    }
}
