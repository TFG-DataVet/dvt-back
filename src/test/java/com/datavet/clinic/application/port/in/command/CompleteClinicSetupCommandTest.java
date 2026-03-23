package com.datavet.clinic.application.port.in.command;

import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompleteClinicSetupCommand Validation Tests")
class CompleteClinicSetupCommandTest {

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
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b);

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con datos válidos");
    }

    @Test
    @DisplayName("Should pass validation when optional fields are null")
    void shouldPassValidationWhenOptionalFieldsAreNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.logoUrl(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con logoUrl nulo");
    }

    // =========================================================================
    // clinicId
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when clinicId is blank")
    void shouldFailValidationWhenClinicIdIsBlank() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.clinicId(""));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicId") &&
                        v.getMessage().equals("El identicador único de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when clinicId is null")
    void shouldFailValidationWhenClinicIdIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.clinicId(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicId")));
    }

    // =========================================================================
    // legalName
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when legal name is blank")
    void shouldFailValidationWhenLegalNameIsBlank() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalName(""));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName") &&
                        v.getMessage().equals("El nombre fiscal de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when legal name is null")
    void shouldFailValidationWhenLegalNameIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalName(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName")));
    }

    @Test
    @DisplayName("Should fail validation when legal name exceeds 150 characters")
    void shouldFailValidationWhenLegalNameExceedsMaxLength() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalName("a".repeat(151)));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName") &&
                        v.getMessage().equals("El nombre fiscal de la clinia no debe de tener mas de 150 caracteres.")));
    }

    // =========================================================================
    // legalNumber
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when legal number is blank")
    void shouldFailValidationWhenLegalNumberIsBlank() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalNumber(""));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber") &&
                        v.getMessage().equals("El numero fiscal es requerido")));
    }

    @Test
    @DisplayName("Should fail validation when legal number is null")
    void shouldFailValidationWhenLegalNumberIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalNumber(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber")));
    }

    @Test
    @DisplayName("Should fail validation when legal number exceeds 50 characters")
    void shouldFailValidationWhenLegalNumberExceedsMaxLength() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalNumber("a".repeat(51)));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber") &&
                        v.getMessage().equals("El numero fiscal no debe de tener mas de 50 caracteres")));
    }

    // =========================================================================
    // legalType
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when legalType is null")
    void shouldFailValidationWhenLegalTypeIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.legalType(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalType") &&
                        v.getMessage().equals("El tipo de persona legal es requerido")));
    }

    // =========================================================================
    // address
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when address is null")
    void shouldFailValidationWhenAddressIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.address(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("address") &&
                        v.getMessage().equals("La direccion es requerida")));
    }

    @Test
    @DisplayName("Should fail when Address value object rejects blank street")
    void shouldFailWhenAddressStreetIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("", "Test City", "12345"),
                "Address debe lanzar excepción con street vacío");
    }

    @Test
    @DisplayName("Should fail when Address value object rejects blank city")
    void shouldFailWhenAddressCityIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("123 Test Street", "", "12345"),
                "Address debe lanzar excepción con city vacía");
    }

    // =========================================================================
    // phone
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when phone is null")
    void shouldFailValidationWhenPhoneIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.phone(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("phone") &&
                        v.getMessage().equals("El telefono es requerido")));
    }

    @Test
    @DisplayName("Should fail when Phone value object rejects invalid format")
    void shouldFailWhenPhoneHasInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> new Phone("invalid-phone"),
                "Phone debe lanzar excepción con formato inválido");
    }

    @Test
    @DisplayName("Should accept valid phone formats")
    void shouldAcceptValidPhoneFormats() {
        String[] validPhones = {"+1234567890", "123-456-7890", "(123) 456-7890", "123 456 7890", "1234567"};

        for (String phoneValue : validPhones) {
            assertDoesNotThrow(() -> new Phone(phoneValue),
                    "Debe aceptar el formato de teléfono: " + phoneValue);
        }
    }

    // =========================================================================
    // email
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.email(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().equals("El email es requerido")));
    }

    @Test
    @DisplayName("Should fail when Email value object rejects invalid format")
    void shouldFailWhenEmailHasInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email("invalid-email"),
                "Email debe lanzar excepción con formato inválido");
    }

    @Test
    @DisplayName("Should fail when Email value object rejects blank value")
    void shouldFailWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email(""),
                "Email debe lanzar excepción con valor vacío");
    }

    // =========================================================================
    // logoUrl
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when logoUrl exceeds 255 characters")
    void shouldFailValidationWhenLogoUrlExceedsMaxLength() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b ->
                b.logoUrl("https://example.com/" + "a".repeat(240)));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("logoUrl")));
    }

    // =========================================================================
    // schedule
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when schedule is null")
    void shouldFailValidationWhenScheduleIsNull() {
        CompleteClinicSetupCommand command = buildValidCommandWith(b -> b.schedule(null));

        Set<ConstraintViolation<CompleteClinicSetupCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("schedule") &&
                        v.getMessage().equals("El horario de la clinica es requerido")));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private CompleteClinicSetupCommand buildValidCommandWith(
            java.util.function.UnaryOperator<CompleteClinicSetupCommand.CompleteClinicSetupCommandBuilder> customizer) {

        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra los fines de semana");

        CompleteClinicSetupCommand.CompleteClinicSetupCommandBuilder builder = CompleteClinicSetupCommand.builder()
                .clinicId("clinic-123")
                .legalName("Test Legal Name")
                .legalNumber("12345678")
                .legalType(LegalType.AUTONOMO)
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("https://example.com/logo.png")
                .schedule(schedule);

        return customizer.apply(builder).build();
    }
}