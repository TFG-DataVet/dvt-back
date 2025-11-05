# Domain Development Guidelines

## Overview

This document provides comprehensive guidelines for developing new domains within the DataVet system. The system follows a domain-driven design approach where each business area is organized as an independent domain following hexagonal architecture principles.

## Domain Structure Template

Every domain MUST follow this exact structure:

```
com.datavet.datavet.{domain}/
├── domain/
│   ├── model/               # Domain entities and value objects
│   ├── exception/           # Domain-specific exceptions
│   └── service/            # Domain services (business logic)
├── application/
│   ├── port/
│   │   ├── in/             # Use case interfaces (inbound ports)
│   │   └── out/            # Repository interfaces (outbound ports)
│   ├── service/            # Application services (orchestration)
│   ├── dto/                # Data transfer objects
│   └── mapper/             # Entity-DTO mappers
└── infrastructure/
    ├── adapter/
    │   ├── input/          # Controllers, REST endpoints
    │   └── output/         # Repository implementations
    ├── persistence/
    │   ├── entity/         # JPA entities
    │   └── repository/     # JPA repositories
    └── config/             # Domain-specific configuration
```

## Naming Conventions

### Package Naming
- Domain packages: `com.datavet.datavet.{domain}` (lowercase, singular)
- Examples: `clinic`, `pet`, `appointment`, `medical`, `billing`

### Class Naming
- Domain models: `{Entity}` (e.g., `Pet`, `Appointment`)
- Domain exceptions: `{Entity}{ExceptionType}Exception` (e.g., `PetNotFoundException`)
- Application services: `{Entity}Service` (e.g., `PetService`)
- DTOs: `{Entity}Response`, `{Entity}Request` (e.g., `PetResponse`)
- Commands: `{Action}{Entity}Command` (e.g., `CreatePetCommand`)
- Use cases: `{Entity}UseCase` (e.g., `PetUseCase`)
- Controllers: `{Entity}Controller` (e.g., `PetController`)
- JPA entities: `{Entity}Entity` (e.g., `PetEntity`)
- Repository adapters: `Jpa{Entity}RepositoryAdapter` (e.g., `JpaPetRepositoryAdapter`)

### File Organization
- Each package MUST contain a `package-info.java` file with documentation
- Test files MUST mirror the production structure
- Configuration classes go in the `config` package

## Domain Types

### Core Domains
Independent business capabilities that represent primary business value:
- **Characteristics**: Self-contained, minimal external dependencies
- **Examples**: Clinic, Pet, Appointment, Medical, Billing
- **Dependencies**: Can only depend on shared components, not other domains

### Supporting Domains
Enable and support core domains:
- **Characteristics**: May depend on core domains but not vice versa
- **Examples**: Notification, User, Reporting
- **Dependencies**: Can depend on core domains and shared components

## Implementation Steps for New Domains

### Step 1: Create Package Structure
```bash
# Create the complete package structure
mkdir -p src/main/java/com/datavet/datavet/{domain}/domain/{model,exception,OwnerService}
mkdir -p src/main/java/com/datavet/datavet/{domain}/application/{port/in,port/out,OwnerService,dto,mapper}
mkdir -p src/main/java/com/datavet/datavet/{domain}/infrastructure/{adapter/input,adapter/output,persistence/entity,persistence/repository,config}

# Create corresponding test structure
mkdir -p src/test/java/com/datavet/datavet/{domain}/domain/{model,exception,OwnerService}
mkdir -p src/test/java/com/datavet/datavet/{domain}/application/{port/in,port/out,OwnerService,dto,mapper}
mkdir -p src/test/java/com/datavet/datavet/{domain}/infrastructure/{adapter/input,adapter/output,persistence/entity,persistence/repository,config}
```

### Step 2: Create package-info.java Files
Add documentation for each package explaining its purpose and contents.

### Step 3: Implement Domain Layer
1. **Domain Models**: Create entities and value objects with business logic
2. **Domain Exceptions**: Create domain-specific exception hierarchy
3. **Domain Services**: Implement complex business rules that don't belong to entities

### Step 4: Implement Application Layer
1. **Ports**: Define use case interfaces (in) and repository interfaces (out)
2. **Commands**: Create command objects for use case inputs
3. **DTOs**: Create data transfer objects for external communication
4. **Services**: Implement application services that orchestrate domain logic
5. **Mappers**: Create mappers between domain models and DTOs

### Step 5: Implement Infrastructure Layer
1. **Input Adapters**: Create REST controllers and other input mechanisms
2. **Output Adapters**: Implement repository adapters for data persistence
3. **JPA Entities**: Create persistence entities separate from domain models
4. **Configuration**: Add domain-specific Spring configuration

### Step 6: Create Tests
1. **Unit Tests**: Test domain logic and application services
2. **Integration Tests**: Test infrastructure adapters
3. **Boundary Tests**: Verify domain isolation and dependency rules

## Example: Pet Domain Implementation

### Domain Model Example
```java
package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.valueobject.Email;

public class Pet extends AggregateRoot<PetId> {
    private final String name;
    private final Species species;
    private final Breed breed;
    private final OwnerId ownerId;
    private final ClinicId clinicId;
    
    // Constructor, getters, business methods
}
```

### Use Case Interface Example
```java
package com.datavet.datavet.pet.application.port.in;

import com.datavet.datavet.pet.application.port.in.command.CreatePetCommand;
import com.datavet.datavet.pet.application.dto.PetResponse;

public interface PetUseCase {
    PetResponse createPet(CreatePetCommand command);
    PetResponse findPetById(PetId petId);
    List<PetResponse> findPetsByClinic(ClinicId clinicId);
}
```

### Controller Example
```java
package com.datavet.datavet.pet.infrastructure.adapter.input;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetUseCase petUseCase;
    
    @PostMapping
    public ResponseEntity<PetResponse> createPet(@RequestBody CreatePetRequest request) {
        CreatePetCommand command = CreatePetCommand.builder()
            .name(request.getName())
            .speciesId(request.getSpeciesId())
            .build();
        
        PetResponse response = petUseCase.createPet(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

## Cross-Domain Communication

### Rules
1. **No Direct Dependencies**: Domains cannot directly import classes from other domains
2. **ID-Only References**: Use only IDs to reference entities from other domains
3. **Event-Driven Communication**: Use domain events for cross-domain notifications
4. **Shared Interfaces**: Use shared interfaces for cross-domain contracts

### Example: Cross-Domain Reference
```java
// CORRECT: Reference by ID only
public class Appointment {
    private final PetId petId;        // Reference to Pet domain
    private final ClinicId clinicId;  // Reference to Clinic domain
}

// INCORRECT: Direct domain reference
public class Appointment {
    private final Pet pet;            // Direct dependency on Pet domain
}
```

## Shared Components Usage

### When to Use Shared Components
- Common value objects (Address, Money, Email, Phone)
- Base domain classes (Entity, AggregateRoot, DomainEvent)
- Cross-cutting concerns (validation, logging, security)
- Common exceptions (EntityNotFoundException, ValidationException)

### When NOT to Use Shared Components
- Domain-specific business logic
- Domain-specific data models
- Domain-specific validation rules

## Testing Guidelines

### Domain Tests
```java
// Test domain logic in isolation
class PetTest {
    @Test
    void shouldCreateValidPet() {
        // Test domain model behavior
    }
}
```

### Application Tests
```java
// Test application OwnerService orchestration
class PetServiceTest {
    @Test
    void shouldCreatePetSuccessfully() {
        // Test use case implementation
    }
}
```

### Integration Tests
```java
// Test infrastructure adapters
@SpringBootTest
class PetControllerIntegrationTest {
    @Test
    void shouldCreatePetViaRestApi() {
        // Test complete flow through REST API
    }
}
```

### Boundary Tests
```java
// Verify domain isolation
class PetDomainBoundaryTest {
    @Test
    void domainShouldNotDependOnOtherDomains() {
        // Verify no unwanted dependencies
    }
}
```

## Common Patterns

### Repository Pattern
```java
// Domain interface (outbound port)
public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(PetId id);
}

// Infrastructure implementation
@Repository
public class JpaPetRepositoryAdapter implements PetRepository {
    // JPA implementation
}
```

### Command Pattern
```java
// Command object for use case input
@Builder
public class CreatePetCommand {
    private final String name;
    private final SpeciesId speciesId;
    private final ClinicId clinicId;
}
```

### Mapper Pattern
```java
// Map between domain and DTO
@Component
public class PetMapper implements Mapper<Pet, PetResponse> {
    public PetResponse toResponse(Pet pet) {
        return PetResponse.builder()
            .id(pet.getId().getValue())
            .name(pet.getName())
            .build();
    }
}
```

## Best Practices

### Domain Layer
- Keep domain models rich with business logic
- Use value objects for primitive obsession
- Implement domain events for important business events
- Keep domain layer pure (no framework dependencies)

### Application Layer
- Keep application services thin (orchestration only)
- Use commands for complex input validation
- Implement proper error handling and validation
- Use mappers to convert between layers

### Infrastructure Layer
- Keep JPA entities separate from domain models
- Use proper transaction boundaries
- Implement proper error handling for external systems
- Use configuration classes for Spring setup

### General
- Follow SOLID principles
- Write comprehensive tests
- Document complex business rules
- Use meaningful names for classes and methods
- Keep packages focused and cohesive

## Migration Checklist

When adding a new domain, ensure:

- [ ] Package structure follows the template exactly
- [ ] All package-info.java files are created with proper documentation
- [ ] Domain models are rich and contain business logic
- [ ] Application services are thin and focus on orchestration
- [ ] Infrastructure adapters properly isolate external concerns
- [ ] Tests cover all layers and verify domain boundaries
- [ ] No direct dependencies on other domains exist
- [ ] Shared components are used appropriately
- [ ] Naming conventions are followed consistently
- [ ] Documentation is updated to reflect the new domain

This template ensures consistency across all domains and maintains the architectural integrity of the DataVet system.