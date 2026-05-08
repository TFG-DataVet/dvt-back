package com.datavet.clinic.application.port.in.command;

import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateClinicCommand Validation Tests")
class CreateClinicCommandTest {

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
        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                List.of("Lunes - Viernes"), LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra los fines de semana");

        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic", "Test Legal Name", "12345678",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty());
        assertEquals("Test Clinic", command.getClinicName());
    }

    @Test
    @DisplayName("Should pass validation when optional fields are null")
    void shouldPassValidationWhenOptionalFieldsAreNull() {
        Address address = new Address("123 Test Street", "Test City", null); // postalCode opcional
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                List.of("Lunes - Viernes"), LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra los fines de semana");

        CreateClinicCommand command = new CreateClinicCommand(
                "Test Clinic", "Test Legal Name", "12345678",
                LegalType.AUTONOMO, address, phone, email,
                null, // logoUrl es opcional
                schedule);

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con logoUrl nulo");
    }

    // =========================================================================
    // clinicName
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when clinic name is blank")
    void shouldFailValidationWhenClinicNameIsBlank() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.clinicName(""));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName") &&
                        v.getMessage().equals("El nombre de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when clinic name is null")
    void shouldFailValidationWhenClinicNameIsNull() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.clinicName(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName")));
    }

    @Test
    @DisplayName("Should fail validation when clinic name exceeds 100 characters")
    void shouldFailValidationWhenClinicNameExceedsMaxLength() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.clinicName("a".repeat(101)));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName") &&
                        v.getMessage().equals("El nombre de la clinica no debe de tener mas de 100 caracteres.")));
    }

    // =========================================================================
    // legalName
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when legal name is blank")
    void shouldFailValidationWhenLegalNameIsBlank() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalName(""));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName") &&
                        v.getMessage().equals("El nombre fiscal de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when legal name is null")
    void shouldFailValidationWhenLegalNameIsNull() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalName(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName")));
    }

    @Test
    @DisplayName("Should fail validation when legal name exceeds 150 characters")
    void shouldFailValidationWhenLegalNameExceedsMaxLength() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalName("a".repeat(151)));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalNumber(""));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber") &&
                        v.getMessage().equals("El numero fiscal es requerido")));
    }

    @Test
    @DisplayName("Should fail validation when legal number is null")
    void shouldFailValidationWhenLegalNumberIsNull() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalNumber(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber")));
    }

    @Test
    @DisplayName("Should fail validation when legal number exceeds 50 characters")
    void shouldFailValidationWhenLegalNumberExceedsMaxLength() {
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalNumber("a".repeat(51)));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.legalType(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.address(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.phone(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.email(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.logoUrl("https://example.com/" + "a".repeat(240)));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

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
        CreateClinicCommand command = buildValidCommandWith(b -> b.schedule(null));

        Set<ConstraintViolation<CreateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("schedule")));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    /**
     * Construye un command válido por defecto y aplica una customización sobre el builder,
     * permitiendo invalidar un solo campo por test.
     */
    private CreateClinicCommand buildValidCommandWith(
            java.util.function.UnaryOperator<CreateClinicCommand.CreateClinicCommandBuilder> customizer) {

        Address address = new Address("123 Test Street", "Test City", "12345");
        Phone phone = new Phone("+1234567890");
        Email email = new Email("test@example.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                List.of("Lunes - Viernes"), LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra los fines de semana");

        CreateClinicCommand.CreateClinicCommandBuilder builder = CreateClinicCommand.builder()
                .clinicName("Test Clinic")
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