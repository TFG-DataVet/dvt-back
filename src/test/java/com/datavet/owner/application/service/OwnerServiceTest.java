package com.datavet.owner.application.service;

import com.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.owner.domain.exception.OwnerAlreadyExistsException;
import com.datavet.owner.domain.exception.OwnerNotFoundException;
import com.datavet.owner.domain.model.Owner;
import com.datavet.owner.testutil.OwnerTestDataBuilder;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.validation.ValidationResult;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private static final String DEFAULT_OWNER_NAME = "Juan";
    private static final String DEFAULT_OWNER_LAST_NAME = "Pérez";
    private static final String DEFAULT_STREET = "Calle Mayor 123";
    private static final String DEFAULT_CITY = "Madrid";
    private static final String DEFAULT_POSTAL_CODE = "28001";
    private static final String DEFAULT_PHONE = "+34612345678";
    private static final String DEFAULT_EMAIL = "juan.perez@example.com";

    @BeforeEach
    void setUp() {
        testOwnerId = new ObjectId().toString();
        testOwner = OwnerTestDataBuilder.aValidOwner();
    }

    @Test
    void createOwner_WithValidCommand_ShouldReturnOwner() {
        // Given

        CreateOwnerCommand command =
                CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();

        when(createOwnerCommandValidator.validate(command)).thenReturn(new ValidationResult());
        when(ownerRepositoryPort.existsByEmail(any())).thenReturn(false);
        when(ownerRepositoryPort.existsByDocumentNumber(anyString())).thenReturn(false);
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
        CreateOwnerCommand command = CreateOwnerCommand.builder()
                .clinidId(UUID.randomUUID().toString())
                .ownerName(DEFAULT_OWNER_NAME)
                .ownerLastName(DEFAULT_OWNER_LAST_NAME)
                .ownerDni(aValidDocument())
                .ownerPhone(aValidPhone())
                .ownerEmail(aValidEmail())
                .ownerAddress(aValidAddress())
                .url("Esto es una web")
                .acceptTermsAndCond(true)
                .build();

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
        List<Owner> owners = List.of(testOwner, OwnerTestDataBuilder.aValidOwner());
        when(ownerRepositoryPort.findAll()).thenReturn(owners);

        // When
        List<Owner> result = ownerService.getAllOwners();

        // Then
        assertThat(result).hasSize(2);
        verify(ownerRepositoryPort).findAll();
    }

    /**
     * Creates a valid Address with default test data.
     */
    public static Address aValidAddress() {
        return new Address(DEFAULT_STREET, DEFAULT_CITY, DEFAULT_POSTAL_CODE);
    }

    public static DocumentId aValidDocument() {
        return DocumentId.of("DNI", "23402587H");
    }
    /**
     * Creates a valid Phone with default test data.
     */
    public static Phone aValidPhone() {
        return new Phone(DEFAULT_PHONE);
    }

    /**
     * Creates a valid Email with default test data.
     */
    public static Email aValidEmail() {
        return new Email(DEFAULT_EMAIL);
    }
}
