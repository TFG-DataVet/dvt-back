# Design Document: Complete MongoDB Migration

## Overview

Este diseño documenta la fase final de la migración completa del proyecto DataVet de PostgreSQL/JPA a MongoDB. Los módulos Owner, Clinic y Shared ya han sido migrados exitosamente a usar MongoDB Documents y MongoRepository. Esta fase final se enfoca en:

1. **Limpieza de dependencias**: Eliminar completamente PostgreSQL y JPA del proyecto
2. **Limpieza de código**: Eliminar convertidores JPA obsoletos
3. **Validación de tests**: Asegurar que todos los tests funcionen correctamente con MongoDB
4. **Verificación final**: Confirmar que no existan referencias residuales a JPA/PostgreSQL

### Objetivos

- Eliminar dependencias de PostgreSQL y JPA del pom.xml
- Limpiar configuración de application.properties
- Eliminar convertidores JPA obsoletos (AddressConverter, EmailConverter, PhoneConverter)
- Verificar y corregir todos los tests del proyecto
- Asegurar que la suite completa de tests pase exitosamente
- Confirmar que no existan referencias residuales a JPA/PostgreSQL

### Estado Actual

**Completado:**
- ✅ Owner module migrado a MongoDB (OwnerDocument, MongoOwnerRepositoryAdapter)
- ✅ Clinic module migrado a MongoDB (ClinicDocument, MongoClinicRepositoryAdapter)
- ✅ Shared value objects (Address, Email, Phone) funcionando con MongoDB
- ✅ Configuración de MongoDB agregada a application.properties

**Pendiente:**
- ❌ Eliminar dependencias de PostgreSQL/JPA del pom.xml
- ❌ Limpiar configuración de PostgreSQL de application.properties
- ❌ Eliminar convertidores JPA obsoletos
- ❌ Revisar y corregir tests que puedan estar fallando
- ❌ Verificar que no existan referencias residuales a JPA

## Architecture

### Current State

```
DataVet Project
├── Owner Module (✅ MongoDB)
│   ├── OwnerDocument (@Document)
│   └── MongoOwnerRepositoryAdapter (MongoRepository)
├── Clinic Module (✅ MongoDB)
│   ├── ClinicDocument (@Document)
│   └── MongoClinicRepositoryAdapter (MongoRepository)
├── Shared Module (✅ MongoDB Ready)
│   ├── Value Objects (Address, Email, Phone)
│   └── JPA Converters (❌ OBSOLETE - to be removed)
└── Configuration
    ├── pom.xml (⚠️ Has PostgreSQL/JPA dependencies)
    └── application.properties (⚠️ Has PostgreSQL config)
```

### Target State

```
DataVet Project
├── Owner Module (✅ MongoDB)
│   ├── OwnerDocument (@Document)
│   └── MongoOwnerRepositoryAdapter (MongoRepository)
├── Clinic Module (✅ MongoDB)
│   ├── ClinicDocument (@Document)
│   └── MongoClinicRepositoryAdapter (MongoRepository)
├── Shared Module (✅ MongoDB)
│   └── Value Objects (Address, Email, Phone)
│       └── Auto-serialized by MongoDB
└── Configuration
    ├── pom.xml (✅ Only MongoDB dependencies)
    └── application.properties (✅ Only MongoDB config)
```

## Components and Interfaces

### 1. Dependencies Cleanup (pom.xml)

#### Current Dependencies (to be removed)

```xml
<!-- TO REMOVE -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### Dependencies to Keep

```xml
<!-- KEEP - MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- KEEP - MongoDB Embedded for Tests -->
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>

<!-- KEEP - Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- KEEP - Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- KEEP - Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. Configuration Cleanup (application.properties)

#### Current Configuration

```properties
spring.application.name=datavet

# PostgreSQL Configuration (for Clinic module) - TO REMOVE
spring.datasource.url=jdbc:postgresql://localhost:5432/clinic_db
spring.datasource.username=clinicdatavet
spring.datasource.password=dataVet
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# MongoDB Configuration (for Owner module) - KEEP
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=datavet_db
spring.data.mongodb.auto-index-creation=true

# Logging - KEEP
logging.file.name=logs/datavet.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.level.com.datavet.datavet=INFO
logging.level.com.datavet.datavet.shared.infrastructure.event=DEBUG
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} - %msg%n
```

#### Target Configuration

```properties
spring.application.name=datavet

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=datavet_db
spring.data.mongodb.auto-index-creation=true

# Logging
logging.file.name=logs/datavet.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.level.com.datavet.datavet=INFO
logging.level.com.datavet.datavet.shared.infrastructure.event=DEBUG
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} - %msg%n
```

### 3. JPA Converters Removal

#### Files to Remove

```
src/main/java/com/datavet/datavet/shared/infrastructure/util/converter/
├── AddressConverter.java (DELETE)
├── EmailConverter.java (DELETE)
└── PhoneConverter.java (DELETE)

src/test/java/com/datavet/datavet/shared/infrastructure/persistence/converter/
└── SharedConverterAutoDiscoveryTest.java (DELETE)
```

#### Why Remove?

MongoDB serializa automáticamente los Value Objects como documentos embebidos sin necesidad de convertidores personalizados. Los convertidores JPA (`AttributeConverter`) son específicos de JPA y no se usan con MongoDB.

**Ejemplo de serialización automática en MongoDB:**

```java
// Value Object
public class Address {
    private String street;
    private String city;
    private String postalCode;
}

// Document
@Document(collection = "owners")
public class OwnerDocument {
    @Id
    private String id;
    
    private Address address;  // MongoDB serializa automáticamente
}
```

**Resultado en MongoDB:**

```json
{
  "_id": "507f1f77bcf86cd799439011",
  "address": {
    "street": "Calle Mayor 123",
    "city": "Madrid",
    "postalCode": "28001"
  }
}
```

### 4. Test Strategy

#### Test Categories

**1. Unit Tests**
- Tests de servicios (OwnerService, ClinicService)
- Tests de validadores (CreateOwnerCommandValidator, etc.)
- Tests de mappers (OwnerMapper, ClinicMapper)
- Tests de modelos de dominio (Owner, Clinic)
- Tests de Value Objects (Address, Email, Phone)

**2. Integration Tests**
- Tests de repositorios (MongoOwnerRepositoryAdapter, MongoClinicRepositoryAdapter)
- Tests de controladores (OwnerController, ClinicController)
- Tests de arquitectura (DomainArchitectureIntegrationTest)

**3. Test Data Builders**
- OwnerTestDataBuilder
- ClinicTestDataBuilder

#### Common Test Issues and Fixes

**Issue 1: ID Type Mismatch**

```java
// ❌ OLD (PostgreSQL/JPA)
Long ownerId = 1L;
Owner owner = Owner.create(1L, 10L, ...);

// ✅ NEW (MongoDB)
String ownerId = new ObjectId().toString();
Owner owner = Owner.create(new ObjectId().toString(), new ObjectId().toString(), ...);
```

**Issue 2: Repository Test Configuration**

```java
// ❌ OLD (JPA)
@DataJpaTest
class OwnerRepositoryTest {
    @Autowired
    private JpaOwnerRepositoryAdapter repository;
}

// ✅ NEW (MongoDB)
@DataMongoTest
class MongoOwnerRepositoryAdapterTest {
    @Autowired
    private MongoOwnerRepositoryAdapter repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();  // Clean between tests
    }
}
```

**Issue 3: Integration Test Configuration**

```java
// ✅ MongoDB Integration Test
@SpringBootTest
@AutoConfigureMockMvc
class OwnerControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private MongoOwnerRepositoryAdapter repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    void shouldCreateOwner() throws Exception {
        // Test implementation
    }
}
```

**Issue 4: Test Data Builders**

```java
// ✅ MongoDB Test Data Builder
public class OwnerTestDataBuilder {
    
    public static Owner buildValidOwner() {
        return Owner.create(
                new ObjectId().toString(),  // ownerID
                new ObjectId().toString(),  // clinicID
                "Juan",
                "Pérez",
                "12345678A",
                Phone.of("+34612345678"),
                Email.of("juan@example.com"),
                Address.of("Calle Mayor 123", "Madrid", "28001")
        );
    }
    
    public static OwnerDocument buildValidOwnerDocument() {
        return OwnerDocument.builder()
                .id(new ObjectId().toString())
                .clinicId(new ObjectId().toString())
                .firstName("Juan")
                .lastName("Pérez")
                .dni("12345678A")
                .phone(Phone.of("+34612345678"))
                .email(Email.of("juan@example.com"))
                .address(Address.of("Calle Mayor 123", "Madrid", "28001"))
                .build();
    }
}
```

## Data Models

### MongoDB Serialization of Value Objects

MongoDB serializa automáticamente los Value Objects sin necesidad de convertidores:

**Address Value Object:**

```java
public class Address {
    private String street;
    private String city;
    private String postalCode;
    
    // Constructor, getters, equals, hashCode
}
```

**MongoDB Document:**

```json
{
  "address": {
    "street": "Calle Mayor 123",
    "city": "Madrid",
    "postalCode": "28001"
  }
}
```

**Email Value Object:**

```java
public class Email {
    private String value;
    
    // Constructor, getters, validation, equals, hashCode
}
```

**MongoDB Document:**

```json
{
  "email": {
    "value": "juan@example.com"
  }
}
```

**Phone Value Object:**

```java
public class Phone {
    private String value;
    
    // Constructor, getters, validation, equals, hashCode
}
```

**MongoDB Document:**

```json
{
  "phone": {
    "value": "+34612345678"
  }
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property Reflection

After reviewing all testable criteria from the prework analysis, I've identified the following properties and examples. Many criteria are about test execution success or configuration, which are validation steps rather than testable properties. The properties below focus on functional correctness that can be verified through automated tests.

**Redundancy Analysis:**
- Properties 4.1, 4.2, 4.3 (Value Object serialization) can be combined into a single comprehensive property
- Properties 5.1 and 5.2 (TestDataBuilder ID generation) can be combined
- Configuration verification examples (1.1-1.7, 7.1-7.4, 8.1-8.3) are kept separate as they verify different aspects

### Correctness Properties

**Property 1: Value Object MongoDB Round-Trip**
*For any* valid Value Object (Address, Email, or Phone), serializing to MongoDB and deserializing back should produce an equivalent Value Object with the same field values.
**Validates: Requirements 4.1, 4.2, 4.3**

**Property 2: Command Validation with String IDs**
*For any* command with a String ID field, validation should correctly accept valid ObjectId strings and reject invalid ID formats.
**Validates: Requirements 2.4**

**Property 3: Mapper ID Preservation**
*For any* domain object with a String ID, mapping to DTO and back to domain should preserve the exact ID value.
**Validates: Requirements 2.6**

**Property 4: ObjectId Generation Validity**
*For any* generated test data ID, the ID string should be a valid MongoDB ObjectId format (24 hexadecimal characters).
**Validates: Requirements 3.4, 5.1, 5.2**

**Property 5: Test Data Value Object Validity**
*For any* Value Object generated by test data builders, the Value Object should pass all its validation rules.
**Validates: Requirements 5.3**

**Property 6: Test Data Document Completeness**
*For any* MongoDB document generated by test data builders, the document should have all required fields populated with valid values.
**Validates: Requirements 5.4**

### Configuration Verification Examples

These are specific examples that verify configuration correctness:

**Example 1: pom.xml JPA Dependency Removal**
Verify that pom.xml does not contain `spring-boot-starter-data-jpa` dependency.
**Validates: Requirements 1.1**

**Example 2: pom.xml PostgreSQL Dependency Removal**
Verify that pom.xml does not contain `postgresql` dependency.
**Validates: Requirements 1.2**

**Example 3: pom.xml MongoDB Dependency Present**
Verify that pom.xml contains `spring-boot-starter-data-mongodb` dependency.
**Validates: Requirements 1.3**

**Example 4: pom.xml Flapdoodle Dependency Present**
Verify that pom.xml contains `de.flapdoodle.embed.mongo` dependency with test scope.
**Validates: Requirements 1.4**

**Example 5: application.properties PostgreSQL Config Removal**
Verify that application.properties does not contain any `spring.datasource` properties.
**Validates: Requirements 1.5**

**Example 6: application.properties JPA Config Removal**
Verify that application.properties does not contain `spring.jpa.hibernate.ddl-auto` property.
**Validates: Requirements 1.6**

**Example 7: application.properties MongoDB Config Present**
Verify that application.properties contains MongoDB configuration (`spring.data.mongodb.uri` and `spring.data.mongodb.database`).
**Validates: Requirements 1.7**

**Example 8: JPA Converter Files Removed**
Verify that AddressConverter.java, EmailConverter.java, and PhoneConverter.java do not exist in the codebase.
**Validates: Requirements 7.1, 7.2, 7.3**

**Example 9: Obsolete Test Removed**
Verify that SharedConverterAutoDiscoveryTest.java does not exist in the codebase.
**Validates: Requirements 7.4**

**Example 10: No JPA Imports in Production Code**
Verify that no files in `src/main/java` contain imports from `jakarta.persistence` package.
**Validates: Requirements 8.1**

**Example 11: No JPA Annotations in Production Code**
Verify that no files in `src/main/java` contain JPA annotations (`@Entity`, `@Table`, `@Column`).
**Validates: Requirements 8.2**

**Example 12: No PostgreSQL References in Production Code**
Verify that no files in `src/main/java` contain references to `postgresql` or `org.postgresql`.
**Validates: Requirements 8.3**

**Example 13: MongoDB Test Annotations**
Verify that repository tests use `@DataMongoTest` annotation instead of `@DataJpaTest`.
**Validates: Requirements 8.5**

**Example 14: MongoDB Index Creation**
Verify that when the application starts, MongoDB automatically creates the defined indexes for Owner and Clinic collections.
**Validates: Requirements 6.5**

**Example 15: Architecture Layer Validation**
Verify that architecture tests validate layer dependencies without any references to JPA-specific rules.
**Validates: Requirements 3.6**

## Error Handling

### Compilation Errors

**Potential Issue:** Missing JPA dependencies after removal

**Solution:** Ensure all code has been migrated to MongoDB before removing JPA dependencies. Search for any remaining JPA imports or annotations.

```bash
# Search for JPA references
grep -r "jakarta.persistence" src/main/java/
grep -r "@Entity\|@Table\|@Column" src/main/java/
```

### Test Failures

**Potential Issue:** Tests failing due to ID type mismatches

**Solution:** Update all test data to use String IDs with valid ObjectId format:

```java
// ❌ OLD
Long id = 1L;

// ✅ NEW
String id = new ObjectId().toString();
```

**Potential Issue:** Repository tests failing due to incorrect configuration

**Solution:** Ensure repository tests use `@DataMongoTest` and clean data between tests:

```java
@DataMongoTest
class MongoOwnerRepositoryAdapterTest {
    
    @Autowired
    private MongoOwnerRepositoryAdapter repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
}
```

### Runtime Errors

**Potential Issue:** MongoDB connection failures

**Solution:** Verify MongoDB is running and configuration is correct:

```bash
# Check MongoDB is running
mongosh --eval "db.adminCommand('ping')"

# Verify connection string in application.properties
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=datavet_db
```

**Potential Issue:** Value Objects not serializing correctly

**Solution:** Ensure Value Objects have proper constructors and getters. MongoDB requires a no-args constructor (can be private) and getters for serialization.

## Testing Strategy

### Unit Testing

**Focus Areas:**
- Command validators with String IDs
- Service layer with String IDs
- Mappers with String IDs
- Domain models
- Value Objects

**Approach:**
- Use mocks for dependencies
- Test with valid ObjectId strings
- Verify behavior with String IDs

### Integration Testing

**Focus Areas:**
- Repository operations (CRUD)
- Controller endpoints
- Value Object serialization
- MongoDB index creation

**Approach:**
- Use `@DataMongoTest` for repository tests
- Use `@SpringBootTest` for controller tests
- Clean database between tests
- Use embedded MongoDB (Flapdoodle)

### Property-Based Testing

**Properties to Test:**

1. **Value Object Round-Trip:** For any valid Value Object, MongoDB serialization and deserialization should preserve all fields
2. **ID Validation:** For any String ID, validation should correctly identify valid ObjectId format
3. **Mapper Preservation:** For any domain object, mapping to DTO and back should preserve the ID
4. **Test Data Validity:** For any generated test data, IDs should be valid ObjectIds and Value Objects should pass validation

**Testing Framework:** JUnit 5 with custom property-based test utilities or jqwik library

### Test Execution Plan

**Phase 1: Unit Tests**
```bash
mvn test -Dtest="*Test" -DfailIfNoTests=false
```

**Phase 2: Integration Tests**
```bash
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
```

**Phase 3: Full Suite**
```bash
mvn clean test
```

**Phase 4: Verification**
```bash
# Verify no JPA references
grep -r "jakarta.persistence" src/main/java/
grep -r "@Entity\|@Table\|@Column" src/main/java/

# Verify MongoDB configuration
grep "spring.data.mongodb" src/main/resources/application.properties
```

## Validation Checklist

### Pre-Migration Validation

- [x] Owner module using MongoDB
- [x] Clinic module using MongoDB
- [x] Shared Value Objects working with MongoDB
- [x] MongoDB configuration present in application.properties

### Migration Tasks

- [ ] Remove PostgreSQL dependency from pom.xml
- [ ] Remove JPA dependency from pom.xml
- [ ] Remove PostgreSQL configuration from application.properties
- [ ] Remove JPA configuration from application.properties
- [ ] Delete JPA converters (AddressConverter, EmailConverter, PhoneConverter)
- [ ] Delete SharedConverterAutoDiscoveryTest
- [ ] Fix any failing unit tests
- [ ] Fix any failing integration tests
- [ ] Verify no JPA imports in production code
- [ ] Verify no JPA annotations in production code
- [ ] Run full test suite successfully

### Post-Migration Validation

- [ ] All tests passing (mvn clean test)
- [ ] Application starts successfully
- [ ] MongoDB connection working
- [ ] MongoDB indexes created automatically
- [ ] No JPA references in codebase
- [ ] No PostgreSQL references in codebase
- [ ] Value Objects serializing correctly
- [ ] CRUD operations working correctly

## Implementation Notes

### Order of Operations

1. **First:** Verify current state (all modules migrated)
2. **Second:** Update configuration files (pom.xml, application.properties)
3. **Third:** Remove obsolete code (JPA converters, obsolete tests)
4. **Fourth:** Fix failing tests
5. **Fifth:** Run full test suite
6. **Sixth:** Verify no residual references
7. **Last:** Manual verification with running application

### Risk Mitigation

**Risk:** Breaking existing functionality

**Mitigation:** 
- Run tests after each change
- Keep git commits small and focused
- Test manually after all changes

**Risk:** Missing JPA references

**Mitigation:**
- Use grep to search for JPA imports and annotations
- Review all compilation errors carefully
- Check test failures for JPA-related issues

**Risk:** Test failures due to MongoDB configuration

**Mitigation:**
- Ensure Flapdoodle dependency is present
- Verify test configuration uses @DataMongoTest
- Clean database between tests

## Success Criteria

The migration is complete when:

1. ✅ pom.xml contains only MongoDB dependencies (no JPA, no PostgreSQL)
2. ✅ application.properties contains only MongoDB configuration
3. ✅ No JPA converters exist in the codebase
4. ✅ No obsolete tests exist
5. ✅ All unit tests pass
6. ✅ All integration tests pass
7. ✅ No JPA imports or annotations in production code
8. ✅ No PostgreSQL references in codebase
9. ✅ Application starts and connects to MongoDB successfully
10. ✅ MongoDB indexes are created automatically
11. ✅ Value Objects serialize/deserialize correctly
12. ✅ CRUD operations work correctly through REST API
