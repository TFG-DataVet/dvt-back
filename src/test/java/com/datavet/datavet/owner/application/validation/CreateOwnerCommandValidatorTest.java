package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOwnerCommandValidatorTest {

    private CreateOwnerCommandValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateOwnerCommandValidator();
    }

    @Test
    void validate_WithValidCommand_ShouldReturnNoErrors() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void validate_WithEmptyOwnerName_ShouldReturnError() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "",
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerName"));
    }

    @Test
    void validate_WithTooLongOwnerName_ShouldReturnError() {
        // Given
        String longName = "a".repeat(101);
        CreateOwnerCommand command = new CreateOwnerCommand(
                longName,
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerName"));
    }

    @Test
    void validate_WithEmptyOwnerLastName_ShouldReturnError() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "",
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerLastName"));
    }

    @Test
    void validate_WithTooLongOwnerLastName_ShouldReturnError() {
        // Given
        String longLastName = "a".repeat(101);
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                longLastName,
                "12345678A",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerLastName"));
    }

    @Test
    void validate_WithEmptyDni_ShouldReturnError() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "",
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerDni"));
    }

    @Test
    void validate_WithTooLongDni_ShouldReturnError() {
        // Given
        String longDni = "a".repeat(101);
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                longDni,
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerDni"));
    }

    @Test
    void validate_WithNullPhone_ShouldReturnError() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "12345678A",
                null,
                new Email("juan@example.com"),
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerPhone"));
    }

    @Test
    void validate_WithNullEmail_ShouldReturnError() {
        // Given
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                "12345678A",
                new Phone("+34612345678"),
                null,
                new Address("Calle Mayor 123", "Madrid", "28001")
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).anyMatch(error -> error.toString().contains("ownerEmail"));
    }
}
