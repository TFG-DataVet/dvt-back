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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UpdateClinicCommand Validation Tests")
class UpdateClinicCommandTest {

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b);

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con datos válidos");
    }

    @Test
    @DisplayName("Should pass validation when optional fields are null")
    void shouldPassValidationWhenOptionalFieldsAreNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.logoUrl(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertTrue(violations.isEmpty(), "No debe haber errores con logoUrl y suscriptionStatus nulos");
    }

    // =========================================================================
    // clinicId
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when clinicId is null")
    void shouldFailValidationWhenClinicIdIsNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.clinicId(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicId") &&
                        v.getMessage().equals("El identificador único de la clinica es requerido.")));
    }

    // =========================================================================
    // clinicName
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when clinic name is blank")
    void shouldFailValidationWhenClinicNameIsBlank() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.clinicName(""));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName") &&
                        v.getMessage().equals("El nombre de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when clinic name is null")
    void shouldFailValidationWhenClinicNameIsNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.clinicName(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("clinicName")));
    }

    @Test
    @DisplayName("Should fail validation when clinic name exceeds 100 characters")
    void shouldFailValidationWhenClinicNameExceedsMaxLength() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.clinicName("a".repeat(101)));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalName(""));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName") &&
                        v.getMessage().equals("El nombre fiscal de la clinica es requerido.")));
    }

    @Test
    @DisplayName("Should fail validation when legal name is null")
    void shouldFailValidationWhenLegalNameIsNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalName(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalName")));
    }

    @Test
    @DisplayName("Should fail validation when legal name exceeds 150 characters")
    void shouldFailValidationWhenLegalNameExceedsMaxLength() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalName("a".repeat(151)));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalNumber(""));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber") &&
                        v.getMessage().equals("El numero fiscal es requerido")));
    }

    @Test
    @DisplayName("Should fail validation when legal number is null")
    void shouldFailValidationWhenLegalNumberIsNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalNumber(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("legalNumber")));
    }

    @Test
    @DisplayName("Should fail validation when legal number exceeds 50 characters")
    void shouldFailValidationWhenLegalNumberExceedsMaxLength() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalNumber("a".repeat(51)));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.legalType(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.address(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("address") &&
                        v.getMessage().equals("La direccion es requerida")));
    }

    @Test
    @DisplayName("Should fail when Address value object rejects blank street")
    void shouldFailWhenAddressStreetIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("", "Updated City", "54321"),
                "Address debe lanzar excepción con street vacío");
    }

    @Test
    @DisplayName("Should fail when Address value object rejects blank city")
    void shouldFailWhenAddressCityIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("456 Updated Street", "", "54321"),
                "Address debe lanzar excepción con city vacía");
    }

    // =========================================================================
    // phone
    // =========================================================================

    @Test
    @DisplayName("Should fail validation when phone is null")
    void shouldFailValidationWhenPhoneIsNull() {
        UpdateClinicCommand command = buildValidCommandWith(b -> b.phone(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.email(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b ->
                b.logoUrl("https://example.com/" + "a".repeat(240)));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

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
        UpdateClinicCommand command = buildValidCommandWith(b -> b.schedule(null));

        Set<ConstraintViolation<UpdateClinicCommand>> violations = validator.validate(command);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("schedule") &&
                        v.getMessage().equals("El horario de la clinica es requerido")));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private UpdateClinicCommand buildValidCommandWith(
            java.util.function.UnaryOperator<UpdateClinicCommand.UpdateClinicCommandBuilder> customizer) {

        Address address = new Address("456 Updated Street", "Updated City", "54321");
        Phone phone = new Phone("+0987654321");
        Email email = new Email("updated@example.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra los fines de semana");

        UpdateClinicCommand.UpdateClinicCommandBuilder builder = UpdateClinicCommand.builder()
                .clinicId("clinic-123")
                .clinicName("Updated Clinic")
                .legalName("Updated Legal Name")
                .legalNumber("87654321")
                .legalType(LegalType.AUTONOMO)
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl("https://example.com/updated-logo.png")
                .schedule(schedule);

        return customizer.apply(builder).build();
    }
}