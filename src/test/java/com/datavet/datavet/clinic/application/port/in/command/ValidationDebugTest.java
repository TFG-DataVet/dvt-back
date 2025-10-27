package com.datavet.datavet.clinic.application.port.in.command;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class ValidationDebugTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void debugValidationMessages() {
        // Test just clinic name to see the exact message
        CreateClinicCommand blankNameCommand = new CreateClinicCommand(
                "", // blank clinic name
                "Test Legal Name",
                "12345678",
                new Address("123 Test Street", "Test City", "12345"),
                new Phone("+1234567890"),
                new Email("test@example.com"),
                "https://example.com/logo.png"
        );

        Set<ConstraintViolation<CreateClinicCommand>> nameViolations = validator.validate(blankNameCommand);
        System.out.println("Blank clinic name violations: " + nameViolations.size());
        for (ConstraintViolation<CreateClinicCommand> violation : nameViolations) {
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Message: '" + violation.getMessage() + "'");
            System.out.println("Annotation: " + violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
            System.out.println("---");
        }
        
        // Test just legal name
        CreateClinicCommand blankLegalCommand = new CreateClinicCommand(
                "Valid Name",
                "", // blank legal name
                "12345678",
                new Address("123 Test Street", "Test City", "12345"),
                new Phone("+1234567890"),
                new Email("test@example.com"),
                "https://example.com/logo.png"
        );

        Set<ConstraintViolation<CreateClinicCommand>> legalViolations = validator.validate(blankLegalCommand);
        System.out.println("\nBlank legal name violations: " + legalViolations.size());
        for (ConstraintViolation<CreateClinicCommand> violation : legalViolations) {
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Message: '" + violation.getMessage() + "'");
            System.out.println("Annotation: " + violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
            System.out.println("---");
        }
    }
}