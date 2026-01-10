package com.datavet.datavet.owner.application.service;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.datavet.owner.domain.exception.OwnerAlreadyExistsException;
import com.datavet.datavet.owner.domain.exception.OwnerNotFoundException;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.testutil.OwnerTestDataBuilder;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepositoryPort ownerRepositoryPort;

    @Mock
    private CreateOwnerCommandValidator createOwnerCommandValidator;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private OwnerService ownerService;

    private String testOwnerId;
    private Owner testOwner;

    @BeforeEach
    void setUp() {
        testOwnerId = new ObjectId().toString();
        testOwner = OwnerTestDataBuilder.buildValidOwner();
    }

    @Test
    void createOwner_WithValidCommand_ShouldReturnOwner() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        when(createOwnerCommandValidator.validate(command)).thenReturn(new ValidationResult());
        when(ownerRepositoryPort.existsByEmail(any())).thenReturn(false);
        when(ownerRepositoryPort.existsByDni(anyString())).thenReturn(false);
        when(ownerRepositoryPort.existsByPhone(any())).thenReturn(false);
        when(ownerRepositoryPort.save(any(Owner.class))).thenReturn(testOwner);

        // When
        Owner result = ownerService.createOwner(command);

        // Then
        assertThat(result).isNotNull();
        verify(ownerRepositoryPort).save(any(Owner.class));
        verify(domainEventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    void createOwner_WithDuplicateEmail_ShouldThrowException() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        when(createOwnerCommandValidator.validate(command)).thenReturn(new ValidationResult());
        when(ownerRepositoryPort.existsByEmail(any())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> ownerService.createOwner(command))
                .isInstanceOf(OwnerAlreadyExistsException.class);
    }

    @Test
    void getOwnerById_WithValidStringId_ShouldReturnOwner() {
        // Given
        when(ownerRepositoryPort.findById(testOwnerId)).thenReturn(Optional.of(testOwner));

        // When
        Owner result = ownerService.getOwnerById(testOwnerId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testOwner);
        verify(ownerRepositoryPort).findById(testOwnerId);
    }

    @Test
    void getOwnerById_WithNonExistentStringId_ShouldThrowException() {
        // Given
        String nonExistentId = new ObjectId().toString();
        when(ownerRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> ownerService.getOwnerById(nonExistentId))
                .isInstanceOf(OwnerNotFoundException.class);
        verify(ownerRepositoryPort).findById(nonExistentId);
    }

    @Test
    void deleteOwner_WithValidStringId_ShouldDeleteOwner() {
        // Given
        when(ownerRepositoryPort.findById(testOwnerId)).thenReturn(Optional.of(testOwner));

        // When
        ownerService.deleteOwner(testOwnerId);

        // Then
        verify(ownerRepositoryPort).findById(testOwnerId);
        verify(ownerRepositoryPort).deleteById(testOwnerId);
        verify(domainEventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    void deleteOwner_WithNonExistentStringId_ShouldThrowException() {
        // Given
        String nonExistentId = new ObjectId().toString();
        when(ownerRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> ownerService.deleteOwner(nonExistentId))
                .isInstanceOf(OwnerNotFoundException.class);
        verify(ownerRepositoryPort).findById(nonExistentId);
        verify(ownerRepositoryPort, never()).deleteById(anyString());
    }

    @Test
    void getAllOwners_ShouldReturnAllOwners() {
        // Given
        List<Owner> owners = List.of(testOwner, OwnerTestDataBuilder.buildValidOwner());
        when(ownerRepositoryPort.findAll()).thenReturn(owners);

        // When
        List<Owner> result = ownerService.getAllOwners();

        // Then
        assertThat(result).hasSize(2);
        verify(ownerRepositoryPort).findAll();
    }
}
