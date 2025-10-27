package com.datavet.datavet.clinic.infrastructure;

import com.datavet.datavet.clinic.infrastructure.adapter.input.ClinicController;
import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.clinic.infrastructure.persistence.repository.JpaClinicRepositoryAdapter;
import com.datavet.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import com.datavet.datavet.shared.application.port.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify proper infrastructure layer boundaries and adapter pattern.
 * Tests that infrastructure layer properly implements adapters and follows hexagonal architecture.
 * Requirements: 2.3, 3.2
 */
class InfrastructureLayerBoundaryTest {

    @Test
    @DisplayName("Controller should be properly annotated as REST controller")
    void clinicController_ShouldBeProperlyAnnotatedRestController() {
        Class<ClinicController> controllerClass = ClinicController.class;
        
        // Check for @RestController annotation
        boolean hasRestControllerAnnotation = Arrays.stream(controllerClass.getAnnotations())
            .anyMatch(annotation -> annotation.annotationType().equals(RestController.class));
        assertTrue(hasRestControllerAnnotation, "Controller should be annotated with @RestController");
        
        // Check for @RequestMapping annotation
        boolean hasRequestMappingAnnotation = Arrays.stream(controllerClass.getAnnotations())
            .anyMatch(annotation -> annotation.annotationType().equals(RequestMapping.class));
        assertTrue(hasRequestMappingAnnotation, "Controller should be annotated with @RequestMapping");
    }

    @Test
    @DisplayName("Controller should depend on use case interface, not implementation")
    void clinicController_ShouldDependOnUseCaseInterface() {
        Class<ClinicController> controllerClass = ClinicController.class;
        
        // Find the use case field
        Field useCaseField = Arrays.stream(controllerClass.getDeclaredFields())
            .filter(field -> ClinicUseCase.class.isAssignableFrom(field.getType()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(useCaseField, "Controller should have a use case dependency");
        assertEquals(ClinicUseCase.class, useCaseField.getType(),
            "Controller should depend on use case interface, not implementation");
    }

    @Test
    @DisplayName("Controller should be in correct package")
    void clinicController_ShouldBeInCorrectPackage() {
        assertEquals("com.datavet.datavet.clinic.infrastructure.adapter.input",
            ClinicController.class.getPackage().getName(),
            "Controller should be in infrastructure.adapter.input package");
    }

    @Test
    @DisplayName("JPA entity should be in correct package")
    void clinicEntity_ShouldBeInCorrectPackage() {
        assertEquals("com.datavet.datavet.clinic.infrastructure.persistence.entity",
            ClinicEntity.class.getPackage().getName(),
            "JPA entity should be in infrastructure.persistence.entity package");
    }

    @Test
    @DisplayName("Repository adapter should be in correct package")
    void repositoryAdapter_ShouldBeInCorrectPackage() {
        assertEquals("com.datavet.datavet.clinic.infrastructure.persistence.repository",
            JpaClinicRepositoryAdapter.class.getPackage().getName(),
            "Repository adapter should be in infrastructure.persistence.repository package");
    }

    @Test
    @DisplayName("Controller methods should handle HTTP operations")
    void controllerMethods_ShouldHandleHttpOperations() {
        Class<ClinicController> controllerClass = ClinicController.class;
        Method[] methods = controllerClass.getDeclaredMethods();
        
        // Check that controller has methods for CRUD operations
        boolean hasCreateMethod = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("create"));
        boolean hasUpdateMethod = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("update"));
        boolean hasGetByIdMethod = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("getById"));
        boolean hasGetAllMethod = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("getAll"));
        boolean hasDeleteMethod = Arrays.stream(methods)
            .anyMatch(method -> method.getName().equals("delete"));
        
        assertTrue(hasCreateMethod, "Controller should have create method");
        assertTrue(hasUpdateMethod, "Controller should have update method");
        assertTrue(hasGetByIdMethod, "Controller should have getById method");
        assertTrue(hasGetAllMethod, "Controller should have getAll method");
        assertTrue(hasDeleteMethod, "Controller should have delete method");
    }

    @Test
    @DisplayName("Infrastructure layer should not expose domain objects directly")
    void infrastructureLayer_ShouldNotExposeDomainObjectsDirectly() {
        Class<ClinicController> controllerClass = ClinicController.class;
        
        // Check that controller methods don't directly expose domain objects in public API
        // They should use DTOs/Response objects instead
        Arrays.stream(controllerClass.getDeclaredMethods())
            .filter(method -> method.getName().equals("create") || 
                             method.getName().equals("update") ||
                             method.getName().equals("getById") ||
                             method.getName().equals("getAll"))
            .forEach(method -> {
                String returnTypeName = method.getReturnType().getName();
                // Should return ResponseEntity with DTO, not domain object directly
                assertTrue(returnTypeName.contains("ResponseEntity"),
                    "Controller methods should return ResponseEntity: " + method.getName());
            });
    }

    @Test
    @DisplayName("Infrastructure components should follow naming conventions")
    void infrastructureComponents_ShouldFollowNamingConventions() {
        // Controller naming
        assertTrue(ClinicController.class.getSimpleName().endsWith("Controller"),
            "Controller classes should end with 'Controller'");
        
        // Entity naming
        assertTrue(ClinicEntity.class.getSimpleName().endsWith("Entity"),
            "JPA entity classes should end with 'Entity'");
        
        // Repository adapter naming
        assertTrue(JpaClinicRepositoryAdapter.class.getSimpleName().contains("Repository"),
            "Repository classes should contain 'Repository' in name");
        assertTrue(JpaClinicRepositoryAdapter.class.getSimpleName().endsWith("Adapter"),
            "Repository adapter classes should end with 'Adapter'");
    }

    @Test
    @DisplayName("Infrastructure layer should properly separate input and output adapters")
    void infrastructureLayer_ShouldSeparateInputAndOutputAdapters() {
        // Input adapter (Controller) package
        String controllerPackage = ClinicController.class.getPackage().getName();
        assertTrue(controllerPackage.contains(".adapter.input"),
            "Input adapters should be in adapter.input package");
        
        // Output adapter (Repository) package  
        String repositoryPackage = JpaClinicRepositoryAdapter.class.getPackage().getName();
        assertTrue(repositoryPackage.contains(".persistence.repository"),
            "Output adapters should be in persistence.repository package");
        
        // Persistence entity package
        String entityPackage = ClinicEntity.class.getPackage().getName();
        assertTrue(entityPackage.contains(".persistence.entity"),
            "Persistence entities should be in persistence.entity package");
    }

    @Test
    @DisplayName("ClinicEntity should extend BaseEntity for shared infrastructure")
    void clinicEntity_ShouldExtendBaseEntity() {
        assertTrue(BaseEntity.class.isAssignableFrom(ClinicEntity.class),
            "ClinicEntity should extend BaseEntity from shared infrastructure");
    }

    @Test
    @DisplayName("Repository adapter should implement shared Repository interface")
    void repositoryAdapter_ShouldImplementSharedRepositoryInterface() {
        assertTrue(Repository.class.isAssignableFrom(JpaClinicRepositoryAdapter.class),
            "JpaClinicRepositoryAdapter should implement shared Repository interface");
    }

    @Test
    @DisplayName("ClinicEntity should have proper JPA annotations for value objects")
    void clinicEntity_ShouldHaveProperJpaAnnotationsForValueObjects() {
        Class<ClinicEntity> entityClass = ClinicEntity.class;
        
        // Check for @Convert annotations on value object fields
        Field[] fields = entityClass.getDeclaredFields();
        
        boolean hasAddressConverter = Arrays.stream(fields)
            .filter(field -> field.getName().equals("address"))
            .anyMatch(field -> Arrays.stream(field.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("Convert")));
        
        boolean hasEmailConverter = Arrays.stream(fields)
            .filter(field -> field.getName().equals("email"))
            .anyMatch(field -> Arrays.stream(field.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("Convert")));
        
        boolean hasPhoneConverter = Arrays.stream(fields)
            .filter(field -> field.getName().equals("phone"))
            .anyMatch(field -> Arrays.stream(field.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("Convert")));
        
        assertTrue(hasAddressConverter, "Address field should have @Convert annotation");
        assertTrue(hasEmailConverter, "Email field should have @Convert annotation");
        assertTrue(hasPhoneConverter, "Phone field should have @Convert annotation");
    }

    @Test
    @DisplayName("Infrastructure layer should use shared value objects")
    void infrastructureLayer_ShouldUseSharedValueObjects() {
        Class<ClinicEntity> entityClass = ClinicEntity.class;
        
        // Check that entity uses shared value objects
        Field[] fields = entityClass.getDeclaredFields();
        
        boolean hasAddressField = Arrays.stream(fields)
            .anyMatch(field -> field.getName().equals("address") && 
                      field.getType().getName().equals("com.datavet.datavet.shared.domain.valueobject.Address"));
        
        boolean hasEmailField = Arrays.stream(fields)
            .anyMatch(field -> field.getName().equals("email") && 
                      field.getType().getName().equals("com.datavet.datavet.shared.domain.valueobject.Email"));
        
        boolean hasPhoneField = Arrays.stream(fields)
            .anyMatch(field -> field.getName().equals("phone") && 
                      field.getType().getName().equals("com.datavet.datavet.shared.domain.valueobject.Phone"));
        
        assertTrue(hasAddressField, "ClinicEntity should use shared Address value object");
        assertTrue(hasEmailField, "ClinicEntity should use shared Email value object");
        assertTrue(hasPhoneField, "ClinicEntity should use shared Phone value object");
    }
}