package com.datavet.clinic.application.port.in.command;

import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreatePendingClinicCommand Validation Tests")
class CreatePendingClinicCommandTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidationWithValidData() {
        CreatePendingClinicCommand command = buildValidCommandWith(b -> b);

        Set<ConstraintViolation<CreatePendingClinicCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con datos válidos");
    }

    // =========================================================================
    // clinicName
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when clinicName is null")
    void shouldFailValidationWhenClinicNameIsNull() {
        CreatePendingClinicCommand command = buildValidCommandWith(b -> b.clinicName(null));

        Set<ConstraintViolation<CreatePendingClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName") &&
                        v.getMessage().equals("El identificador único de la clinica es requerido.")));
    }

    // =========================================================================
    // email
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        CreatePendingClinicCommand command = buildValidCommandWith(b -> b.email(null));

        Set<ConstraintViolation<CreatePendingClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().equals("El email es requerido")));
    }

    @Test
    @DisplayName("Should fail when Email value object rejects invalid format")
    void shouldFailWhenEmailHasInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email("no-es-un-email"),
                "Email debe lanzar excepción con formato inválido");
    }

    @Test
    @DisplayName("Should fail when Email value object rejects blank value")
    void shouldFailWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email(""),
                "Email debe lanzar excepción con valor vacío");
    }

    @Test
    @DisplayName("Should fail when Email value object rejects null value")
    void shouldFailWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email(null),
                "Email debe lanzar excepción con valor nulo");
    }

    // =========================================================================
    // phone
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when phone is null")
    void shouldFailValidationWhenPhoneIsNull() {
        CreatePendingClinicCommand command = buildValidCommandWith(b -> b.phone(null));

        Set<ConstraintViolation<CreatePendingClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("phone") &&
                        v.getMessage().equals("El telefono es requerido")));
    }

    @Test
    @DisplayName("Should fail when Phone value object rejects invalid format")
    void shouldFailWhenPhoneHasInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> new Phone("no-es-un-telefono"),
                "Phone debe lanzar excepción con formato inválido");
    }

    @Test
    @DisplayName("Should fail when Phone value object rejects blank value")
    void shouldFailWhenPhoneIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Phone(""),
                "Phone debe lanzar excepción con valor vacío");
    }

    @Test
    @DisplayName("Should fail when Phone value object rejects null value")
    void shouldFailWhenPhoneIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Phone(null),
                "Phone debe lanzar excepción con valor nulo");
    }

    @Test
    @DisplayName("Should accept valid phone formats")
    void shouldAcceptValidPhoneFormats() {
        String[] validPhones = {"+34912345678", "123-456-7890", "(123) 456-7890", "123 456 7890", "1234567"};

        for (String phoneValue : validPhones) {
            assertDoesNotThrow(() -> new Phone(phoneValue),
                    "Debe aceptar el formato de teléfono: " + phoneValue);
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private CreatePendingClinicCommand buildValidCommandWith(
            java.util.function.UnaryOperator<CreatePendingClinicCommand.CreatePendingClinicCommandBuilder> customizer) {

        Email email = new Email("clinica@test.com");
        Phone phone = new Phone("+34912345678");

        CreatePendingClinicCommand.CreatePendingClinicCommandBuilder builder = CreatePendingClinicCommand.builder()
                .clinicName("Clínica Test")
                .email(email)
                .phone(phone);

        return customizer.apply(builder).build();
    }
}