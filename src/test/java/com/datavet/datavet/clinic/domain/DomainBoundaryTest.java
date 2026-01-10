package com.datavet.datavet.clinic.domain;

import com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.domain.exception.DomainException;
import com.datavet.datavet.shared.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify proper domain separation and dependency rules.
 * Tests that clinic domain is properly isolated and follows hexagonal architecture.
 * Requirements: 2.1, 2.2, 2.3, 3.2
 */
class DomainBoundaryTest {

    @Test
    @DisplayName("Clinic domain model should not depend on infrastructure or application layers")
    void clinicDomainModel_ShouldNotDependOnInfrastructureOrApplication() {
        Class<Clinic> clinicClass = Clinic.class;
        
        // Get all imports by checking field types, method parameters, etc.
        Arrays.stream(clinicClass.getDeclaredFields())
            .forEach(field -> {
                String fieldTypeName = field.getType().getName();
                assertFalse(fieldTypeName.contains(".infrastructure."), 
                    "Domain model should not depend on infrastructure layer: " + fieldTypeName);
                assertFalse(fieldTypeName.contains(".application."), 
                    "Domain model should not depend on application layer: " + fieldTypeName);
            });
        
        // Check that domain model only uses allowed types (standard Java types, validation annotations, and shared domain components)
        Arrays.stream(clinicClass.getDeclaredFields())
            .forEach(field -> {
                String fieldTypeName = field.getType().getName();
                assertTrue(
                    fieldTypeName.startsWith("java.") || 
                    fieldTypeName.startsWith("jakarta.validation.") ||
                    fieldTypeName.startsWith("com.datavet.datavet.shared.domain.") ||
                    fieldTypeName.equals("java.lang.String") ||
                    fieldTypeName.equals("java.lang.Long") ||
                    fieldTypeName.equals("long") ||
                    fieldTypeName.equals("java.time.LocalDateTime"),
                    "Domain model should only use standard types, validation annotations, and shared domain components: " + fieldTypeName
                );
            });
    }

    @Test
    @DisplayName("Clinic domain exceptions should extend shared domain exceptions")
    void clinicDomainExceptions_ShouldExtendSharedDomainExceptions() {
        // Test that ClinicNotFoundException extends the shared EntityNotFoundException
        assertTrue(EntityNotFoundException.class.isAssignableFrom(ClinicNotFoundException.class),
            "Clinic domain exceptions should extend shared domain exceptions");
        
        // Test that shared EntityNotFoundException extends DomainException
        assertTrue(DomainException.class.isAssignableFrom(EntityNotFoundException.class),
            "Shared exceptions should extend base DomainException");
        
        // Test that ClinicNotFoundException is properly part of the domain exception hierarchy
        assertTrue(DomainException.class.isAssignableFrom(ClinicNotFoundException.class),
            "Clinic exceptions should be part of domain exception hierarchy");
    }

    @Test
    @DisplayName("Domain model should be immutable and follow domain-driven design principles")
    void clinicDomainModel_ShouldFollowDomainDrivenDesignPrinciples() {
        Class<Clinic> clinicClass = Clinic.class;
        
        // Check that the class is not final (allows for proxying in frameworks)
        assertFalse(Modifier.isFinal(clinicClass.getModifiers()),
            "Domain model should not be final to allow framework proxying");
        
        // Check that all fields are private (encapsulation)
        Arrays.stream(clinicClass.getDeclaredFields())
            .forEach(field -> {
                assertTrue(Modifier.isPrivate(field.getModifiers()),
                    "All domain model fields should be private: " + field.getName());
            });
        
        // Verify the class has proper builder pattern (indicated by @Builder annotation or builder method)
        boolean hasBuilderAnnotation = Arrays.stream(clinicClass.getAnnotations())
            .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("Builder"));
        
        // Alternative: check if builder method exists
        boolean hasBuilderMethod = false;
        try {
            clinicClass.getMethod("builder");
            hasBuilderMethod = true;
        } catch (NoSuchMethodException e) {
            // Method doesn't exist
        }
        
        assertTrue(hasBuilderAnnotation || hasBuilderMethod, 
            "Domain model should use builder pattern for construction");
    }

    @Test
    @DisplayName("Domain package structure should follow hexagonal architecture")
    void domainPackageStructure_ShouldFollowHexagonalArchitecture() {
        // Verify that domain model is in the correct package
        assertEquals("com.datavet.datavet.clinic.domain.model", 
            Clinic.class.getPackage().getName(),
            "Domain model should be in domain.model package");
        
        // Verify that domain exception is in the correct package
        assertEquals("com.datavet.datavet.clinic.domain.exception", 
            ClinicNotFoundException.class.getPackage().getName(),
            "Domain exceptions should be in domain.exception package");
    }

    @Test
    @DisplayName("Domain exceptions should provide meaningful error messages")
    void domainExceptions_ShouldProvideMeaningfulErrorMessages() {
        // Test ClinicNotFoundException with ID
        String testId = "123L";
        ClinicNotFoundException exception = new ClinicNotFoundException(testId);
        
        assertNotNull(exception.getMessage(), "Exception should have a message");
        assertTrue(exception.getMessage().contains("Clinic"), 
            "Exception message should mention the entity type");
        assertTrue(exception.getMessage().contains(testId.toString()), 
            "Exception message should contain the ID");
        
        // Test ClinicNotFoundException with custom message
        String customMessage = "Custom clinic not found message";
        ClinicNotFoundException customException = new ClinicNotFoundException(customMessage);
        assertEquals(customMessage, customException.getMessage(),
            "Exception should preserve custom message");
    }
}