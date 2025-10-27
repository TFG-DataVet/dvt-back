# Design Document - Project Restructure

## Overview

This design document outlines the restructuring of the DataVet system from a single-domain architecture to a multi-domain architecture organized by business areas. The restructure will transform the current monolithic structure into a scalable, domain-driven design that supports parallel development and clear separation of concerns.

The restructure will maintain full backward compatibility with existing Clinic functionality while establishing patterns for future domain development.

## Architecture

### Current State Analysis

The existing system follows a single-domain hexagonal architecture with all components under the `datavet` package:
- Domain layer: `domain/model`, `domain/exception`
- Application layer: `application/service`, `application/dto`, `application/port`
- Infrastructure layer: `infrastructure/adapter`, `infrastructure/persistence`, `infrastructure/config`

### Target Architecture

The new architecture will organize code by business domains, each following hexagonal architecture principles:

```
com.datavet.datavet/
├── shared/                    # Shared components across domains
│   ├── domain/               # Common domain concepts
│   ├── application/          # Shared application services
│   └── infrastructure/       # Common infrastructure
├── clinic/                   # Clinic Management Domain (Core)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── pet/                      # Pet Management Domain (Core)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── appointment/              # Appointment Scheduling Domain (Core)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── medical/                  # Medical Records Domain (Core)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── billing/                  # Billing & Payments Domain (Core)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── notification/             # Notification Domain (Support)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
└── user/                     # User Management Domain (Support)
    ├── domain/
    ├── application/
    └── infrastructure/
```

### Domain Classification

**Core Domains** (Independent business capabilities):
- **Clinic**: Clinic management and configuration
- **Pet**: Pet registration and profile management
- **Appointment**: Scheduling and appointment management
- **Medical**: Medical records and treatment history
- **Billing**: Invoicing, payments, and financial tracking

**Supporting Domains** (Enable core domains):
- **Notification**: Email, SMS, and push notifications
- **User**: Authentication, authorization, and user management

### Dependency Rules

1. **Core domains** are independent of each other
2. **Supporting domains** may depend on core domains but not vice versa
3. **Shared components** can be used by any domain
4. **Cross-domain communication** happens through well-defined interfaces

## Components and Interfaces

### Domain Structure Template

Each domain follows the same internal structure:

```
{domain}/
├── domain/
│   ├── model/               # Domain entities and value objects
│   ├── exception/           # Domain-specific exceptions
│   └── service/            # Domain services (business logic)
├── application/
│   ├── port/
│   │   ├── in/             # Use case interfaces
│   │   └── out/            # Repository interfaces
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

### Shared Components

**Shared Domain**:
- Common value objects (Address, Money, etc.)
- Base domain events
- Common domain exceptions

**Shared Application**:
- Cross-cutting concerns (logging, validation)
- Common DTOs for integration
- Event publishing mechanisms

**Shared Infrastructure**:
- Database configuration
- Security configuration
- Global exception handling
- Common utilities

### Migration Strategy for Clinic Domain

The existing Clinic functionality will be moved to the new `clinic` domain structure:

**Current → Target Mapping**:
- `domain/model/Clinic.java` → `clinic/domain/model/Clinic.java`
- `domain/exception/Clinic*.java` → `clinic/domain/exception/Clinic*.java`
- `application/service/ClinicService.java` → `clinic/application/service/ClinicService.java`
- `application/dto/ClinicResponse.java` → `clinic/application/dto/ClinicResponse.java`
- `application/port/in/ClinicUseCase.java` → `clinic/application/port/in/ClinicUseCase.java`
- `application/port/in/command/Clinic*.java` → `clinic/application/port/in/command/Clinic*.java`
- `infrastructure/adapter/input/ClinicController.java` → `clinic/infrastructure/adapter/input/ClinicController.java`
- `infrastructure/persistence/entity/ClinicEntity.java` → `clinic/infrastructure/persistence/entity/ClinicEntity.java`
- `infrastructure/persistence/repository/JpaClinicRepositoryAdapter.java` → `clinic/infrastructure/persistence/repository/JpaClinicRepositoryAdapter.java`

## Data Models

### Domain Boundaries

Each domain will own its data models and be responsible for their consistency:

**Clinic Domain**:
- Clinic entity with basic information
- Clinic configuration and settings
- Clinic-specific business rules

**Future Domain Models** (for reference):
- **Pet Domain**: Pet, Owner, Breed, Species
- **Appointment Domain**: Appointment, TimeSlot, Schedule
- **Medical Domain**: MedicalRecord, Treatment, Diagnosis, Prescription
- **Billing Domain**: Invoice, Payment, BillingAccount
- **User Domain**: User, Role, Permission
- **Notification Domain**: NotificationTemplate, NotificationLog

### Cross-Domain References

Domains will reference other domains through IDs only, maintaining loose coupling:
- Pet references Clinic via `clinicId`
- Appointment references Pet via `petId` and Clinic via `clinicId`
- Medical records reference Pet via `petId`

## Error Handling

### Domain-Specific Exceptions

Each domain maintains its own exception hierarchy:
- `{Domain}NotFoundException`
- `{Domain}AlreadyExistsException`
- `{Domain}ValidationException`
- `{Domain}BusinessRuleException`

### Global Exception Handling

The shared infrastructure will provide:
- Global exception handler for common HTTP responses
- Logging and monitoring for cross-domain errors
- Standardized error response format

### Migration Considerations

Existing Clinic exceptions will be moved to the clinic domain while maintaining the same behavior and API contracts.

## Testing Strategy

### Domain-Level Testing

Each domain will have its own test structure mirroring the production code:

```
{domain}/
├── domain/
│   └── model/              # Unit tests for domain logic
├── application/
│   └── service/            # Unit tests for application services
└── infrastructure/
    └── adapter/            # Integration tests for adapters
```

### Cross-Domain Testing

- Integration tests will verify domain boundaries are respected
- Contract tests will ensure stable interfaces between domains
- End-to-end tests will validate complete business workflows

### Migration Testing

- All existing Clinic tests will be moved to the clinic domain
- Tests will be updated to reflect new package structure
- Regression tests will ensure no functionality is lost during migration

## Implementation Phases

### Phase 1: Foundation Setup
- Create shared components structure
- Establish domain template and conventions
- Set up build configuration for multi-domain structure

### Phase 2: Clinic Domain Migration
- Move existing Clinic code to new domain structure
- Update all imports and references
- Ensure all tests pass with new structure

### Phase 3: Domain Expansion Preparation
- Finalize shared component interfaces
- Document domain development guidelines
- Prepare for future domain additions

## Design Decisions and Rationales

### 1. Domain-First Organization
**Decision**: Organize by business domain rather than technical layers
**Rationale**: Enables parallel development, clearer business alignment, and easier maintenance as the system grows

### 2. Hexagonal Architecture per Domain
**Decision**: Each domain follows complete hexagonal architecture
**Rationale**: Maintains consistency, enables independent testing, and preserves architectural benefits at domain level

### 3. Shared Components Strategy
**Decision**: Extract common functionality to shared packages
**Rationale**: Reduces duplication while maintaining domain independence, enables consistent cross-cutting concerns

### 4. Backward Compatibility Priority
**Decision**: Maintain existing Clinic API and behavior during migration
**Rationale**: Ensures zero downtime and risk during restructure, allows gradual adoption of new patterns

### 5. Core vs Supporting Domain Classification
**Decision**: Distinguish between core business domains and supporting capabilities
**Rationale**: Clarifies dependency rules, prioritizes development efforts, and maintains clean architecture boundaries

This design provides a scalable foundation for the DataVet system while preserving existing functionality and establishing clear patterns for future development.