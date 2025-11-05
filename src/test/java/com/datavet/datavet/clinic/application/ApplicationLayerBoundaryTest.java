package com.datavet.datavet.clinic.application;

import com.datavet.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.application.service.ClinicService;
import com.datavet.datavet.shared.application.port.UseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify proper application layer boundaries and hexagonal architecture.
 * Tests that application layer properly separates concerns and follows port-adapter pattern.
 * Requirements: 2.2, 3.2
 */
class ApplicationLayerBoundaryTest {

    @Test
    @DisplayName("Use case interface should extend shared UseCase marker interface")
    void clinicUseCase_ShouldExtendSharedUseCaseInterface() {
        assertTrue(UseCase.class.isAssignableFrom(ClinicUseCase.class),
            "Clinic use case should extend shared UseCase interface");
    }

    @Test
    @DisplayName("Use case interface should define business operations without implementation details")
    void clinicUseCase_ShouldDefineBusinessOperationsOnly() {
        Class<ClinicUseCase> useCaseClass = ClinicUseCase.class;
        
        // Verify it's an interface
        assertTrue(useCaseClass.isInterface(), "Use case should be an interface");
        
        // Check that all methods are abstract (no default implementations)
        Method[] methods = useCaseClass.getDeclaredMethods();
        assertTrue(methods.length > 0, "Use case should define business operations");
        
        Arrays.stream(methods)
            .forEach(method -> {
                assertTrue(Modifier.isAbstract(method.getModifiers()),
                    "Use case methods should be abstract: " + method.getName());
            });
        
        // Verify method names represent business operations
        boolean hasCreateOperation = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("createClinic"));
        boolean hasUpdateOperation = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("updateClinic"));
        boolean hasGetOperation = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("getClinicById"));
        
        assertTrue(hasCreateOperation, "Use case should define create operation");
        assertTrue(hasUpdateOperation, "Use case should define update operation");
        assertTrue(hasGetOperation, "Use case should define get operation");
    }

    @Test
    @DisplayName("Application OwnerService should implement use case interface")
    void clinicService_ShouldImplementUseCaseInterface() {
        assertTrue(ClinicUseCase.class.isAssignableFrom(ClinicService.class),
            "Application OwnerService should implement use case interface");
    }

    @Test
    @DisplayName("Command objects should be in correct package and follow naming convention")
    void commandObjects_ShouldFollowConventions() {
        // Verify CreateClinicCommand package
        assertEquals("com.datavet.datavet.clinic.application.port.in.command",
            CreateClinicCommand.class.getPackage().getName(),
            "Commands should be in application.port.in.command package");
        
        // Verify UpdateClinicCommand package
        assertEquals("com.datavet.datavet.clinic.application.port.in.command",
            UpdateClinicCommand.class.getPackage().getName(),
            "Commands should be in application.port.in.command package");
        
        // Verify naming convention
        assertTrue(CreateClinicCommand.class.getSimpleName().endsWith("Command"),
            "Command classes should end with 'Command'");
        assertTrue(UpdateClinicCommand.class.getSimpleName().endsWith("Command"),
            "Command classes should end with 'Command'");
    }

    @Test
    @DisplayName("Command objects should be immutable data structures")
    void commandObjects_ShouldBeImmutableDataStructures() {
        Class<CreateClinicCommand> createCommandClass = CreateClinicCommand.class;
        
        // Check that all fields are private and final (immutability)
        Arrays.stream(createCommandClass.getDeclaredFields())
            .forEach(field -> {
                assertTrue(Modifier.isPrivate(field.getModifiers()),
                    "Command fields should be private: " + field.getName());
                assertTrue(Modifier.isFinal(field.getModifiers()),
                    "Command fields should be final for immutability: " + field.getName());
            });
    }

    @Test
    @DisplayName("Application layer should not depend on infrastructure layer")
    void applicationLayer_ShouldNotDependOnInfrastructure() {
        Class<ClinicService> serviceClass = ClinicService.class;
        
        // Check constructor parameters and field types
        Arrays.stream(serviceClass.getDeclaredFields())
            .forEach(field -> {
                String fieldTypeName = field.getType().getName();
                assertFalse(fieldTypeName.contains(".infrastructure."),
                    "Application layer should not depend on infrastructure: " + fieldTypeName);
            });
        
        // Check method parameters and return types
        Arrays.stream(serviceClass.getDeclaredMethods())
            .forEach(method -> {
                // Check return type
                String returnTypeName = method.getReturnType().getName();
                assertFalse(returnTypeName.contains(".infrastructure."),
                    "Application methods should not return infrastructure types: " + returnTypeName);
                
                // Check parameter types
                Arrays.stream(method.getParameterTypes())
                    .forEach(paramType -> {
                        String paramTypeName = paramType.getName();
                        assertFalse(paramTypeName.contains(".infrastructure."),
                            "Application methods should not use infrastructure parameters: " + paramTypeName);
                    });
            });
    }

    @Test
    @DisplayName("Application OwnerService should be in correct package")
    void applicationService_ShouldBeInCorrectPackage() {
        assertEquals("com.datavet.datavet.clinic.application.OwnerService",
            ClinicService.class.getPackage().getName(),
            "Application OwnerService should be in application.OwnerService package");
    }

    @Test
    @DisplayName("Use case interface should be in correct package")
    void useCaseInterface_ShouldBeInCorrectPackage() {
        assertEquals("com.datavet.datavet.clinic.application.port.in",
            ClinicUseCase.class.getPackage().getName(),
            "Use case interface should be in application.port.in package");
    }
}