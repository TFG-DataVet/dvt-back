# Implementation Plan: Complete MongoDB Migration

- [x] 1. Update project dependencies in pom.xml
  - Remove spring-boot-starter-data-jpa dependency
  - Remove postgresql dependency
  - Verify spring-boot-starter-data-mongodb dependency exists
  - Verify de.flapdoodle.embed.mongo dependency exists with test scope
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [x] 2. Clean application.properties configuration
  - Remove all spring.datasource properties (url, username, password, driver-class-name)
  - Remove spring.jpa.hibernate.ddl-auto property
  - Verify spring.data.mongodb configuration exists (uri, database, auto-index-creation)
  - _Requirements: 1.5, 1.6, 1.7_

- [x] 3. Remove obsolete JPA converters and tests
  - Delete AddressConverter.java from shared.infrastructure.util.converter
  - Delete EmailConverter.java from shared.infrastructure.util.converter
  - Delete PhoneConverter.java from shared.infrastructure.util.converter
  - Delete SharedConverterAutoDiscoveryTest.java from shared.infrastructure.persistence.converter
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 4. Verify and fix Owner module tests
  - [ ] 4.1 Review and fix OwnerService unit tests
    - Verify tests use String IDs with valid ObjectId format
    - Verify mock configurations work with String IDs
    - Run tests and fix any failures
    - _Requirements: 2.1, 2.4, 2.5_
  
  - [ ] 4.2 Review and fix Owner command validator tests
    - Verify CreateOwnerCommandValidatorTest works correctly
    - Verify validation logic handles String IDs
    - Run tests and fix any failures
    - _Requirements: 2.1, 2.4_
  
  - [ ] 4.3 Review and fix Owner mapper tests
    - Verify OwnerMapper tests preserve String IDs correctly
    - Run tests and fix any failures
    - _Requirements: 2.1, 2.6_
  
  - [ ] 4.4 Review and fix Owner repository integration tests
    - Verify MongoOwnerRepositoryAdapterTest uses @DataMongoTest
    - Verify tests clean database between executions
    - Verify tests use valid ObjectId strings
    - Run tests and fix any failures
    - _Requirements: 3.1, 3.2, 3.4, 3.5_
  
  - [ ] 4.5 Review and fix Owner controller integration tests
    - Verify OwnerControllerIntegrationTest uses @SpringBootTest
    - Verify tests use valid ObjectId strings in URLs
    - Verify tests clean database between executions
    - Run tests and fix any failures
    - _Requirements: 3.3, 3.4, 3.5_
  
  - [ ] 4.6 Review and fix OwnerTestDataBuilder
    - Verify buildValidOwner generates valid ObjectId strings
    - Verify all test data methods use new ObjectId().toString()
    - _Requirements: 5.1, 5.3, 5.4_

- [ ] 5. Verify and fix Clinic module tests
  - [ ] 5.1 Review and fix ClinicService unit tests
    - Verify tests use String IDs with valid ObjectId format
    - Verify mock configurations work with String IDs
    - Run tests and fix any failures
    - _Requirements: 2.2, 2.4, 2.5_
  
  - [ ] 5.2 Review and fix Clinic command validator tests
    - Verify CreateClinicCommandValidatorTest works correctly
    - Verify UpdateClinicCommandValidatorTest works correctly
    - Verify validation logic handles String IDs
    - Run tests and fix any failures
    - _Requirements: 2.2, 2.4_
  
  - [ ] 5.3 Review and fix Clinic mapper tests
    - Verify ClinicMapper tests preserve String IDs correctly
    - Run tests and fix any failures
    - _Requirements: 2.2, 2.6_
  
  - [ ] 5.4 Review and fix Clinic repository integration tests
    - Verify MongoClinicRepositoryAdapterTest uses @DataMongoTest
    - Verify tests clean database between executions
    - Verify tests use valid ObjectId strings
    - Run tests and fix any failures
    - _Requirements: 3.1, 3.2, 3.4, 3.5_
  
  - [ ] 5.5 Review and fix Clinic controller integration tests
    - Verify ClinicControllerIntegrationTest uses @SpringBootTest
    - Verify tests use valid ObjectId strings in URLs
    - Verify tests clean database between executions
    - Run tests and fix any failures
    - _Requirements: 3.3, 3.4, 3.5_
  
  - [ ] 5.6 Review and fix ClinicTestDataBuilder
    - Verify buildValidClinic generates valid ObjectId strings
    - Verify all test data methods use new ObjectId().toString()
    - _Requirements: 5.2, 5.3, 5.4_

- [ ] 6. Verify and fix Shared module tests
  - [ ] 6.1 Review and fix Value Object tests
    - Verify ValueObjectsIntegrationTest works with MongoDB serialization
    - Test Address serialization/deserialization
    - Test Email serialization/deserialization
    - Test Phone serialization/deserialization
    - Run tests and fix any failures
    - _Requirements: 2.3, 4.1, 4.2, 4.3_
  
  - [ ] 6.2 Review and fix GlobalExceptionHandler tests
    - Verify exception handling works correctly
    - Run tests and fix any failures
    - _Requirements: 2.3_

- [ ] 7. Review and fix architecture tests
  - Verify DomainArchitectureIntegrationTest has no JPA-specific rules
  - Verify architecture tests validate layer dependencies correctly
  - Run tests and fix any failures
  - _Requirements: 3.6_

- [ ] 8. Run full test suite and verify
  - Execute mvn clean test to run all tests
  - Verify all tests pass without errors
  - Review test output for any warnings
  - _Requirements: 6.1, 6.2_

- [ ] 9. Verify no JPA references in production code
  - Search for jakarta.persistence imports in src/main/java
  - Search for @Entity, @Table, @Column annotations in src/main/java
  - Search for postgresql references in src/main/java
  - Verify no matches found
  - _Requirements: 8.1, 8.2, 8.3_

- [ ] 10. Verify no JPA references in test code
  - Search for @DataJpaTest in src/test/java
  - Verify all repository tests use @DataMongoTest instead
  - _Requirements: 8.5_

- [ ] 11. Manual verification with running application
  - Start MongoDB locally (mongod)
  - Start the application (mvn spring-boot:run)
  - Verify application connects to MongoDB successfully
  - Verify MongoDB indexes are created automatically
  - Test Owner CRUD operations via REST API
  - Test Clinic CRUD operations via REST API
  - Verify Value Objects serialize correctly in responses
  - _Requirements: 6.4, 6.5_

- [ ] 12. Final validation and cleanup
  - Review all changes made
  - Verify all requirements are met
  - Update documentation if needed
  - Commit changes with descriptive message
  - _Requirements: All_
