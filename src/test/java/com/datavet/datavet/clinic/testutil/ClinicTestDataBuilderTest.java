package com.datavet.datavet.clinic.testutil;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that ClinicTestDataBuilder creates valid test data.
 */
class ClinicTestDataBuilderTest {

    @Test
    void shouldCreateValidClinic() {
        // When
        Clinic clinic = ClinicTestDataBuilder.aValidClinic().build();

        // Then
        assertNotNull(clinic);
        assertNotNull(clinic.getClinicName());
        assertNotNull(clinic.getLegalName());
        assertNotNull(clinic.getLegalNumber());
        assertNotNull(clinic.getAddress());
        assertNotNull(clinic.getPhone());
        assertNotNull(clinic.getEmail());
    }

    @Test
    void shouldCreateValidCreateCommand() {
        // When
        CreateClinicCommand command = ClinicTestDataBuilder.aValidCreateCommand();

        // Then
        assertNotNull(command);
        assertNotNull(command.getClinicName());
        assertNotNull(command.getLegalName());
        assertNotNull(command.getLegalNumber());
        assertNotNull(command.getAddress());
        assertNotNull(command.getPhone());
        assertNotNull(command.getEmail());
    }

    @Test
    void shouldCreateValidUpdateCommand() {
        // When
        UpdateClinicCommand command = ClinicTestDataBuilder.aValidUpdateCommand("hola");

        // Then
        assertNotNull(command);
        assertEquals(1L, command.getClinicId());
        assertNotNull(command.getClinicName());
        assertNotNull(command.getLegalName());
        assertNotNull(command.getLegalNumber());
        assertNotNull(command.getAddress());
        assertNotNull(command.getPhone());
        assertNotNull(command.getEmail());
    }

    @Test
    void shouldCreateValidValueObjects() {
        // When
        Address address = ClinicTestDataBuilder.aValidAddress();
        Phone phone = ClinicTestDataBuilder.aValidPhone();
        Email email = ClinicTestDataBuilder.aValidEmail();

        // Then
        assertNotNull(address);
        assertNotNull(phone);
        assertNotNull(email);
    }

    @Test
    void shouldCreateCommandWithCustomEmail() {
        // When
        CreateClinicCommand command = ClinicTestDataBuilder.aCreateCommandWithEmail("custom@clinic.com");

        // Then
        assertNotNull(command);
        assertEquals("custom@clinic.com", command.getEmail().getValue());
    }

    @Test
    void shouldCreateCommandWithCustomLegalNumber() {
        // When
        CreateClinicCommand command = ClinicTestDataBuilder.aCreateCommandWithLegalNumber("99999999");

        // Then
        assertNotNull(command);
        assertEquals("99999999", command.getLegalNumber());
    }

    @Test
    void shouldCreateClinicUsingFactoryMethod() {
        // When
        Clinic clinic = ClinicTestDataBuilder.aValidCreatedClinic();

        // Then
        assertNotNull(clinic);
        assertNotNull(clinic.getCreatedAt());
        assertNotNull(clinic.getUpdatedAt());
        assertFalse(clinic.getDomainEvents().isEmpty(), "Should have ClinicCreatedEvent");
    }
}
