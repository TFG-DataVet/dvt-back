package com.datavet.datavet.architecture;

import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.application.service.ClinicService;
import com.datavet.datavet.clinic.infrastructure.adapter.input.ClinicController;
import com.datavet.datavet.shared.application.port.UseCase;
import com.datavet.datavet.shared.application.port.Repository;
import com.datavet.datavet.shared.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration tests to verify domain architecture and boundaries.
 * Tests the overall structure and dependency rules across the entire clinic domain.
 * Requirements: 2.1, 2.2, 2.3, 3.2
 */
class DomainArchitectureIntegrationTest {

    private static final String BASE_PACKAGE_PATH = "src/main/java/com/datavet/datavet";
    private static final String CLINIC_DOMAIN_PATH = BASE_PACKAGE_PATH + "/clinic";
    private static final String SHARED_DOMAIN_PATH = BASE_PACKAGE_PATH + "/shared";

    @Test
    @DisplayName("Clinic domain should have complete hexagonal architecture structure")
    void clinicDomain_ShouldHaveCompleteHexagonalStructure() {
        // Verify domain layer structure
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/domain/model"),
            "Clinic domain should have domain/model package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/domain/exception"),
            "Clinic domain should have domain/exception package");
        
        // Verify application layer structure
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/application/service"),
            "Clinic domain should have application/service package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/application/dto"),
            "Clinic domain should have application/dto package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/application/port/in"),
            "Clinic domain should have application/port/in package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/application/port/in/command"),
            "Clinic domain should have application/port/in/command package");
        
        // Verify infrastructure layer structure
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/infrastructure/adapter/input"),
            "Clinic domain should have infrastructure/adapter/input package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/infrastructure/persistence/entity"),
            "Clinic domain should have infrastructure/persistence/entity package");
        assertTrue(directoryExists(CLINIC_DOMAIN_PATH + "/infrastructure/persistence/repository"),
            "Clinic domain should have infrastructure/persistence/repository package");
    }

    @Test
    @DisplayName("Shared components should be accessible to clinic domain")
    void sharedComponents_ShouldBeAccessibleToClinicDomain() {
        // Verify shared domain components exist
        assertTrue(directoryExists(SHARED_DOMAIN_PATH + "/domain/exception"),
            "Shared domain exceptions should exist");
        assertTrue(directoryExists(SHARED_DOMAIN_PATH + "/application/port"),
            "Shared application ports should exist");
        
        // Verify clinic domain can access shared components
        assertTrue(DomainException.class.isAssignableFrom(
            com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException.class),
            "Clinic exceptions should extend shared domain exceptions");
        
        assertTrue(UseCase.class.isAssignableFrom(
            com.datavet.datavet.clinic.application.port.in.ClinicUseCase.class),
            "Clinic use cases should extend shared use case interface");
    }

    @Test
    @DisplayName("Domain isolation should be maintained - no cross-domain dependencies")
    void domainIsolation_ShouldBeMaintained() throws IOException {
        // Check that clinic domain files don't import from other potential domains
        List<String> clinicJavaFiles = findJavaFiles(CLINIC_DOMAIN_PATH);
        
        for (String filePath : clinicJavaFiles) {
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            
            for (String line : fileContent) {
                if (line.trim().startsWith("import ")) {
                    // Should not import from other domains (when they exist)
                    assertFalse(line.contains("com.datavet.datavet.pet."),
                        "Clinic domain should not depend on pet domain: " + line + " in " + filePath);
                    assertFalse(line.contains("com.datavet.datavet.appointment."),
                        "Clinic domain should not depend on appointment domain: " + line + " in " + filePath);
                    assertFalse(line.contains("com.datavet.datavet.medical."),
                        "Clinic domain should not depend on medical domain: " + line + " in " + filePath);
                    assertFalse(line.contains("com.datavet.datavet.billing."),
                        "Clinic domain should not depend on billing domain: " + line + " in " + filePath);
                }
            }
        }
    }

    @Test
    @DisplayName("Dependency direction should follow hexagonal architecture rules")
    void dependencyDirection_ShouldFollowHexagonalRules() throws IOException {
        // Domain layer should not depend on application or infrastructure
        List<String> domainFiles = findJavaFiles(CLINIC_DOMAIN_PATH + "/domain");
        for (String filePath : domainFiles) {
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            
            for (String line : fileContent) {
                if (line.trim().startsWith("import ")) {
                    assertFalse(line.contains(".application."),
                        "Domain layer should not depend on application layer: " + line + " in " + filePath);
                    assertFalse(line.contains(".infrastructure."),
                        "Domain layer should not depend on infrastructure layer: " + line + " in " + filePath);
                }
            }
        }
        
        // Application layer should not depend on infrastructure
        List<String> applicationFiles = findJavaFiles(CLINIC_DOMAIN_PATH + "/application");
        for (String filePath : applicationFiles) {
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            
            for (String line : fileContent) {
                if (line.trim().startsWith("import ")) {
                    assertFalse(line.contains(".infrastructure."),
                        "Application layer should not depend on infrastructure layer: " + line + " in " + filePath);
                }
            }
        }
    }

    @Test
    @DisplayName("Package naming should follow established conventions")
    void packageNaming_ShouldFollowConventions() {
        // Verify domain model is in correct package
        assertEquals("com.datavet.datavet.clinic.domain.model",
            Clinic.class.getPackage().getName(),
            "Domain model should follow package naming convention");
        
        // Verify application service is in correct package
        assertEquals("com.datavet.datavet.clinic.application.service",
            ClinicService.class.getPackage().getName(),
            "Application service should follow package naming convention");
        
        // Verify infrastructure controller is in correct package
        assertEquals("com.datavet.datavet.clinic.infrastructure.adapter.input",
            ClinicController.class.getPackage().getName(),
            "Infrastructure controller should follow package naming convention");
    }

    @Test
    @DisplayName("Clinic domain should be completely self-contained")
    void clinicDomain_ShouldBeCompletelyelfContained() {
        // Verify all necessary components exist for a complete domain
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.domain.model.Clinic"),
            "Domain model should exist");
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException"),
            "Domain exceptions should exist");
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.application.service.ClinicService"),
            "Application service should exist");
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.application.port.in.ClinicUseCase"),
            "Use case interface should exist");
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.infrastructure.adapter.input.ClinicController"),
            "Infrastructure controller should exist");
        assertDoesNotThrow(() -> Class.forName("com.datavet.datavet.clinic.infrastructure.persistence.document.ClinicDocument"),
            "Infrastructure document should exist");
    }

    @Test
    @DisplayName("Shared components should provide proper abstractions")
    void sharedComponents_ShouldProvideProperAbstractions() {
        // Verify shared interfaces exist and are properly designed
        assertTrue(UseCase.class.isInterface(), "UseCase should be an interface");
        assertTrue(Repository.class.isInterface(), "Repository should be an interface");
        
        // Verify shared exceptions provide proper hierarchy
        assertTrue(RuntimeException.class.isAssignableFrom(DomainException.class),
            "DomainException should extend RuntimeException");
    }

    // Helper methods
    private boolean directoryExists(String path) {
        return new File(path).exists() && new File(path).isDirectory();
    }

    private List<String> findJavaFiles(String directoryPath) throws IOException {
        Path startPath = Paths.get(directoryPath);
        if (!Files.exists(startPath)) {
            return List.of();
        }
        
        try (Stream<Path> paths = Files.walk(startPath)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .map(Path::toString)
                .toList();
        }
    }
}