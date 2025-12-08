# Implementation Plan: Owner Module MongoDB Migration

- [x] 1. Update project dependencies and configuration
  - Update pom.xml to add spring-boot-starter-data-mongodb dependency
  - Update pom.xml to add de.flapdoodle.embed.mongo for testing
  - Keep spring-boot-starter-data-jpa temporarily for Clinic module compatibility
  - Update application.properties to add MongoDB configuration (uri, database name)
  - Remove PostgreSQL-specific configuration from application.properties
  - Update application-test.properties with MongoDB embedded configuration
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 2. Create MongoDB configuration and infrastructure setup
  - Create MongoConfig class in shared.infrastructure.config package
  - Add @EnableMongoAuditing annotation for automatic timestamp management
  - Configure MongoCustomConversions bean if custom converters are needed
  - _Requirements: 4.2, 4.3_

- [x] 3. Update domain model to use String IDs
  - [x] 3.1 Update Owner domain model
    - Change ownerID field type from Long to String
    - Change clinicID field type from Long to String
    - Update getId() method return type to String
    - Update create() factory method parameters to accept String IDs
    - Update all domain events to use String IDs
    - _Requirements: 7.1_
  
  - [x] 3.2 Update domain exceptions
    - Update OwnerNotFoundException to accept String parameter
    - Update exception messages to handle String IDs
    - _Requirements: 7.1_

- [x] 4. Create MongoDB document and repository
  - [x] 4.1 Create OwnerDocument class
    - Create OwnerDocument.java in owner.infrastructure.persistence.entity package
    - Add @Document(collection = "owners") annotation
    - Add @Id field of type String for MongoDB ObjectId
    - Add @Field annotations for snake_case field mapping (clinic_id, first_name, last_name, created_at, updated_at)
    - Include all fields: id, clinicId, firstName, lastName, dni, phone, email, address
    - Add @CreatedDate and @LastModifiedDate for auditing
    - Add validation annotations (@NotBlank, @Size)
    - Add Lombok annotations (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder)
    - Add compound indexes for email, dni, phone, and clinicId
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_
  
  - [x] 4.2 Create MongoOwnerRepositoryAdapter interface
    - Create MongoOwnerRepositoryAdapter.java in owner.infrastructure.persistence.repository package
    - Extend MongoRepository<OwnerDocument, String>
    - Add query methods: existsByEmail, existsByDni, existsByEmailAndDni, existsByPhone
    - Add existsByDniAndIdNot method (renamed from existsByDniAndOwnerIDNot)
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 5. Update repository adapter and ports
  - [x] 5.1 Update OwnerRepositoryPort interface
    - Change all ID parameters from Long to String in method signatures
    - Update findById, deleteById, existsById, existsByDniAndOwnerIdNot methods
    - _Requirements: 7.2_
  
  - [x] 5.2 Update OwnerRepositoryAdapter implementation
    - Inject MongoOwnerRepositoryAdapter instead of JpaOwnerRepositoryAdapter
    - Rename toEntity method to toDocument
    - Update toDocument method to map Owner to OwnerDocument with String IDs
    - Update toDomain method to map OwnerDocument to Owner
    - Handle clinicID conversion between String and Long if needed
    - Update all method implementations to use String IDs
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [x] 6. Update application layer
  - [x] 6.1 Update commands
    - Update UpdateOwnerCommand to use String for ownerID field
    - Verify CreateOwnerCommand doesn't need changes (no ID field)
    - _Requirements: 7.2_
  
  - [x] 6.2 Update OwnerService
    - Update deleteOwner method parameter from Long to String
    - Update getOwnerById method parameter from Long to String
    - Update all method calls to use String IDs
    - _Requirements: 7.3_
  
  - [x] 6.3 Update OwnerMapper
    - Verify toResponse method handles String IDs correctly
    - Update OwnerResponse if needed to use String for ownerID
    - _Requirements: 7.4_

- [x] 7. Update infrastructure input adapters
  - [x] 7.1 Update OwnerController
    - Change @PathVariable type from Long to String in getOwner method
    - Change @PathVariable type from Long to String in deleteOwner method
    - Update all endpoint methods to use String IDs
    - _Requirements: 7.2_
  
  - [x] 7.2 Update DTOs
    - Update CreateOwnerRequest if it contains ID references
    - Update UpdateOwnerRequest to use String for ownerID
    - Update OwnerResponse to use String for ownerID field
    - _Requirements: 7.2_

- [x] 8. Update test infrastructure
  - [x] 8.1 Update test configuration
    - Verify application-test.properties has MongoDB embedded configuration
    - Create test MongoDB configuration class if needed
    - _Requirements: 8.1_
  
  - [x] 8.2 Update OwnerTestDataBuilder
    - Update buildValidOwner to generate ObjectId strings for IDs
    - Use new ObjectId().toString() for ownerID and clinicID
    - Update all test data creation methods to use String IDs
    - _Requirements: 8.4_

- [x] 9. Update repository tests
  - [x] 9.1 Update MongoOwnerRepositoryAdapter tests
    - Rename test class if it references JPA
    - Add @DataMongoTest annotation
    - Update test setup to clear MongoDB collections
    - Update all test data to use String IDs with valid ObjectId format
    - Update assertions to work with String IDs
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 10. Update service and application layer tests
  - [x] 10.1 Update OwnerService tests
    - Update mock data to use String IDs
    - Update method call verifications to expect String parameters
    - Update test assertions for String IDs
    - _Requirements: 8.3, 8.4_
  
  - [x] 10.2 Update command validator tests
    - Update test data to use String IDs where applicable
    - Verify validation logic still works with String IDs
    - _Requirements: 8.3, 8.4_

- [ ] 11. Update controller integration tests
  - [x] 11.1 Update OwnerController integration tests
    - Add @SpringBootTest annotation if not present
    - Update test data to use valid ObjectId strings
    - Update URL paths in MockMvc calls to use String IDs
    - Update response assertions to expect String IDs
    - Add MongoDB cleanup in @BeforeEach or @AfterEach
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 12. Verify Clinic module remains unaffected
  - Verify ClinicEntity still uses JPA annotations
  - Verify ClinicRepository still extends JpaRepository
  - Verify Clinic tests still pass without modifications
  - Verify no shared infrastructure changes break Clinic module
  - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [ ] 13. Run full test suite and fix any issues
  - Execute mvn clean test to run all tests
  - Fix any compilation errors related to ID type changes
  - Fix any test failures in Owner module
  - Verify Clinic module tests still pass
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 14. Manual verification and cleanup
  - Start application and verify MongoDB connection
  - Test Owner CRUD operations via REST endpoints
  - Verify domain events are published correctly
  - Check application logs for any MongoDB-related errors
  - Remove old OwnerEntity.java file
  - Remove old JpaOwnerRepositoryAdapter.java file
  - Update any documentation referencing PostgreSQL for Owner module
  - _Requirements: 3.7, 5.1, 6.5, 9.1_
