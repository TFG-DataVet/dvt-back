# Implementation Plan

- [x] 1. Integrate shared value objects in Clinic domain model
  - Replace primitive address fields with Address value object from shared components
  - Replace email String field with Email value object for consistent validation
  - Replace phone String field with Phone value object for standardized formatting
  - Update Clinic constructor and builder to use value objects
  - _Requirements: 1.3, 1.4_

- [x] 1.1 Integrate Address value object in Clinic model
  - Modify Clinic.java to use Address value object instead of separate address, city, codePostal fields
  - Update constructor and builder methods to accept Address parameter
  - Ensure backward compatibility in data mapping
  - _Requirements: 1.3_

- [x] 1.2 Integrate Email value object in Clinic model
  - Replace email String field with Email value object in Clinic.java
  - Update validation to use Email's built-in validation
  - Modify constructor and builder to use Email parameter
  - _Requirements: 1.3_

- [x] 1.3 Integrate Phone value object in Clinic model
  - Replace phone String field with Phone value object in Clinic.java
  - Remove manual regex validation in favor of Phone's validation
  - Update constructor and builder to use Phone parameter
  - _Requirements: 1.3_

- [x] 2. Implement Entity interface and AggregateRoot functionality
  - Make Clinic implement Entity<Long> interface from shared domain
  - Extend AggregateRoot<Long> to support domain events
  - Add domain event publishing for clinic lifecycle events
  - _Requirements: 1.1, 1.2_

- [x] 2.1 Implement Entity interface in Clinic
  - Make Clinic class implement Entity<Long> interface
  - Ensure getId() method returns proper Long identifier
  - Update class structure to comply with Entity contract
  - _Requirements: 1.1_

- [x] 2.2 Extend AggregateRoot for domain events
  - Make Clinic extend AggregateRoot<Long> base class
  - Add domain event creation for clinic creation, update, and deletion
  - Implement proper event publishing in business methods
  - _Requirements: 1.2_

- [x] 2.3 Create domain events for Clinic lifecycle
  - Create ClinicCreatedEvent, ClinicUpdatedEvent, and ClinicDeletedEvent classes
  - Implement DomainEvent interface in each event class
  - Add event data and proper event construction
  - _Requirements: 1.2_

- [x] 3. Integrate ApplicationService interface in ClinicService
  - Make ClinicService implement ApplicationService marker interface
  - Ensure service follows shared application service patterns
  - Update service to use shared validation framework
  - _Requirements: 3.1, 3.3_

- [x] 3.1 Implement ApplicationService interface
  - Make ClinicService implement ApplicationService interface from shared application
  - Verify service structure follows shared application service patterns
  - _Requirements: 3.1_

- [x] 3.2 Update repository port to extend shared Repository interface
  - Make ClinicRepositoryPort extend Repository<Clinic, Long> interface
  - Update method signatures to match shared repository patterns
  - Ensure domain-specific methods are preserved
  - _Requirements: 3.2_

- [x] 3.3 Integrate shared validation framework
  - Update ClinicService to use shared validation utilities
  - Replace manual validation with shared Validator interface
  - Create ValidationResult handling for clinic operations
  - _Requirements: 3.3_

- [x] 4. Integrate shared infrastructure components
  - Make ClinicEntity extend BaseEntity for audit fields
  - Update JpaClinicRepositoryAdapter to implement shared Repository interface
  - Ensure proper use of shared database configuration
  - _Requirements: 4.1, 4.2, 4.4_

- [x] 4.1 Extend BaseEntity in ClinicEntity
  - Make ClinicEntity extend BaseEntity from shared infrastructure
  - Remove manual createdAt and updatedAt fields in favor of BaseEntity audit fields
  - Update JPA mappings to work with BaseEntity structure
  - _Requirements: 4.1_

- [x] 4.2 Implement shared Repository interface in adapter
  - Make JpaClinicRepositoryAdapter implement Repository<Clinic, Long> interface
  - Update method implementations to match shared repository contract
  - Ensure proper mapping between domain Clinic and ClinicEntity
  - _Requirements: 4.2_

- [x] 4.3 Update entity mapping for value objects
  - Modify ClinicEntity to properly map Address, Email, and Phone value objects
  - Create appropriate JPA converters or embedded mappings for value objects
  - Ensure database schema compatibility with value object integration
  - _Requirements: 4.1, 1.3_

- [x] 5. Update application layer to handle value objects
  - Modify CreateClinicCommand and UpdateClinicCommand to use value objects
  - Update ClinicResponse DTO to properly serialize value objects
  - Ensure ClinicMapper handles value object conversion correctly
  - _Requirements: 3.2, 1.3_

- [x] 5.1 Update command objects for value objects
  - Modify CreateClinicCommand to accept Address, Email, and Phone parameters
  - Update UpdateClinicCommand to use value objects instead of primitive fields
  - Ensure command validation works with value object constraints
  - _Requirements: 3.2, 1.3_

- [x] 5.2 Update DTO and mapper for value objects
  - Modify ClinicResponse to properly expose value object data
  - Update ClinicMapper to convert between domain value objects and DTOs
  - Ensure JSON serialization works correctly for value objects
  - _Requirements: 3.2, 1.3_

- [x] 6. Update infrastructure layer for value object persistence
  - Create JPA converters for Address, Email, and Phone value objects
  - Update database schema if needed for value object storage
  - Ensure proper serialization and deserialization of value objects
  - _Requirements: 4.1, 4.3_

- [x] 6.1 Create JPA converters for value objects
  - Implement AttributeConverter for Address value object
  - Create AttributeConverter for Email value object
  - Implement AttributeConverter for Phone value object
  - _Requirements: 4.1_

- [x] 6.2 Update ClinicEntity JPA mappings
  - Add @Convert annotations for value object fields in ClinicEntity
  - Ensure proper column mappings for converted value objects
  - Test database persistence and retrieval of value objects
  - _Requirements: 4.1_

- [x] 7. Update tests for shared component integration
  - Modify existing clinic tests to work with value objects and shared components
  - Add tests for domain events functionality
  - Create integration tests for shared infrastructure usage
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 7.1 Update domain model tests
  - Modify Clinic domain tests to use value objects
  - Add tests for Entity interface implementation
  - Test AggregateRoot functionality and domain events
  - _Requirements: 5.1_

- [x] 7.2 Update application service tests
  - Update ClinicService tests for ApplicationService integration
  - Test shared validation framework usage
  - Verify repository interface integration
  - _Requirements: 5.2_

- [x] 7.3 Update infrastructure tests
  - Modify ClinicEntity tests for BaseEntity integration
  - Test JPA converters for value objects
  - Verify shared repository implementation
  - _Requirements: 5.3_