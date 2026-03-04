package com.datavet.datavet.architecture;

import com.datavet.datavet.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetDomainArchitectureIntegrationTest {

    private static final String BASE_PACKAGE_PATH ="src/main/java/com/datavet/datavet";
    private static final String PET_DOMAIN_PATH = BASE_PACKAGE_PATH + "/pet";
    private static final String SHARED_DOMAIN_PATH = BASE_PACKAGE_PATH + "/shared";

    @Test
    @DisplayName("Pet domain should hace complete hexagonal architecture structure")
    void petDomain_ShouldHaveCompleteHexagonalStructure() {
        // Verify domain layer structure
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/domain/model"),
                "Pet domain should have domain/model package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/domain/exception"),
                "Pet domain should have domain/exception package");

        // Verify application layer structure
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/application/service"),
                "Pet domain should have application/service package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/application/dto"),
                "Pet domain should have application/dto package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/application/port/in"),
                "Pet domain should have application/port/in package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/application/port/in/command"),
                "Pet domain should have application/port/in/command package");

        // Verify infrastructure layer structure
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/infrastructure/adapter/input"),
                "Pet domain should have infrastructure/adapter/input package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/infrastructure/persistence/document"),
                "Pet domain should have infrastructure/persistence/entity package");
        assertTrue(directoryExists(PET_DOMAIN_PATH + "/infrastructure/persistence/repository"),
                "Pet domain should have infrastructure/persistence/repository package");
    }

    @Test
    @DisplayName("Shared components should be accessible to clinic domain")
    void sharedComponents_ShouldBeAccessibleToPetDomain() {
        // Verify shared domain components exist
        assertTrue(directoryExists(SHARED_DOMAIN_PATH + "/domain/exception"),
                "Shared domain exceptions should exist");
        assertTrue(directoryExists(SHARED_DOMAIN_PATH + "/application/port"),
                "Shared application ports should exist");

        // Verify clinic domain can access shared components
        assertTrue(DomainException.class.isAssignableFrom(
                        com.datavet.datavet.pet.domain.exception.PetNotFoundException.class),
                "Pet exceptions should extend shared domain exceptions");
//
//        assertTrue(UseCase.class.isAssignableFrom(
//                        com.datavet.datavet.pet.application.port.in.ClinicUseCase.class),
//                "Clinic use cases should extend shared use case interface");
    }


    private boolean directoryExists(String path) {
        return new File(path).exists() && new File(path).isDirectory();
    }
}
