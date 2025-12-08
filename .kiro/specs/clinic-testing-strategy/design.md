# Diseño de Estrategia de Testing para Dominio Clinic

## Overview

Este documento describe la estrategia de testing completa para el dominio Clinic, organizada en capas según la arquitectura hexagonal del sistema. La estrategia se enfoca en garantizar la integridad de datos, prevención de corrupción, y comportamiento correcto en todos los escenarios críticos.

### Principios de Testing

1. **Testing por Capas**: Tests organizados según la arquitectura (Domain, Application, Infrastructure)
2. **Independencia de Shared**: No duplicar tests de Value Objects ya cubiertos en el dominio Shared
3. **Enfoque en Integridad**: Priorizar tests que previenen corrupción de datos
4. **Tests Mínimos pero Completos**: Cubrir todos los casos críticos sin sobre-testear
5. **Tests Realistas**: Usar datos reales, no mocks cuando sea posible para validar funcionalidad real

## Architecture

### Estructura de Tests Propuesta

```
src/test/java/com/datavet/datavet/clinic/
├── domain/
│   ├── model/
│   │   ├── ClinicTest.java (NUEVO)
│   │   └── ClinicDomainEventsTest.java (EXISTENTE - REVISAR)
│   └── exception/
│       └── ClinicExceptionsTest.java (NUEVO)
├── application/
│   ├── service/
│   │   ├── ClinicServiceTest.java (NUEVO)
│   │   └── ClinicServiceExceptionTest.java (EXISTENTE - REVISAR)
│   └── validation/
│       ├── CreateClinicCommandValidatorTest.java (NUEVO)
│       └── UpdateClinicCommandValidatorTest.java (NUEVO)
└── infrastructure/
    ├── persistence/
    │   ├── ClinicRepositoryTest.java (NUEVO)
    │   └── ClinicEntityMappingTest.java (NUEVO)
    └── adapter/
        └── input/
            └── ClinicControllerIntegrationTest.java (EXISTENTE - EXPANDIR)
```

## Components and Interfaces

### 1. Domain Layer Tests

#### 1.1 ClinicTest.java
**Propósito**: Validar la lógica del modelo de dominio Clinic

**Casos de Test**:
- `shouldCreateClinicWithValidData()`: Verifica creación correcta con factory method
- `shouldGenerateCreatedAtAndUpdatedAtOnCreation()`: Valida timestamps automáticos
- `shouldUpdateClinicFieldsCorrectly()`: Verifica actualización de campos
- `shouldUpdateUpdatedAtOnUpdate()`: Valida que updatedAt se actualiza
- `shouldNotModifyCreatedAtOnUpdate()`: Verifica que createdAt permanece inmutable
- `shouldGenerateClinicCreatedEventOnCreation()`: Valida evento de creación
- `shouldGenerateClinicUpdatedEventOnUpdate()`: Valida evento de actualización
- `shouldGenerateClinicDeletedEventOnDelete()`: Valida evento de eliminación
- `shouldClearDomainEventsAfterRetrieval()`: Verifica limpieza de eventos

**Dependencias**: Ninguna (tests unitarios puros)

#### 1.2 ClinicExceptionsTest.java (NUEVO)
**Propósito**: Validar que las excepciones del dominio se crean correctamente

**Casos de Test**:
- `clinicNotFoundExceptionShouldContainClinicId()`: Verifica mensaje con ID
- `clinicAlreadyExistsExceptionShouldContainFieldInfo()`: Verifica mensaje con campo duplicado
- `clinicValidationExceptionShouldContainValidationResult()`: Verifica que contiene ValidationResult
- `clinicValidationExceptionShouldFormatErrorsCorrectly()`: Verifica formato de errores

**Dependencias**: ValidationResult del dominio Shared

### 2. Application Layer Tests

#### 2.1 CreateClinicCommandValidatorTest.java (NUEVO)
**Propósito**: Validar todas las reglas de validación para creación de clínicas

**Casos de Test**:
- `shouldPassValidationWithValidCommand()`: Comando válido pasa validación
- `shouldFailWhenClinicNameIsNull()`: Rechaza clinicName nulo
- `shouldFailWhenClinicNameIsEmpty()`: Rechaza clinicName vacío
- `shouldFailWhenClinicNameExceedsMaxLength()`: Rechaza clinicName > 100 caracteres
- `shouldFailWhenLegalNameIsNull()`: Rechaza legalName nulo
- `shouldFailWhenLegalNameIsEmpty()`: Rechaza legalName vacío
- `shouldFailWhenLegalNameExceedsMaxLength()`: Rechaza legalName > 150 caracteres
- `shouldFailWhenLegalNumberIsNull()`: Rechaza legalNumber nulo
- `shouldFailWhenLegalNumberIsEmpty()`: Rechaza legalNumber vacío
- `shouldFailWhenLegalNumberExceedsMaxLength()`: Rechaza legalNumber > 50 caracteres
- `shouldFailWhenAddressIsNull()`: Rechaza address nulo
- `shouldFailWhenPhoneIsNull()`: Rechaza phone nulo
- `shouldFailWhenEmailIsNull()`: Rechaza email nulo
- `shouldFailWhenLogoUrlExceedsMaxLength()`: Rechaza logoUrl > 255 caracteres
- `shouldAcceptNullLogoUrl()`: Acepta logoUrl opcional nulo
- `shouldCollectMultipleValidationErrors()`: Acumula múltiples errores

**Dependencias**: CreateClinicCommand, ValidationResult

#### 2.2 UpdateClinicCommandValidatorTest.java (NUEVO)
**Propósito**: Validar todas las reglas de validación para actualización de clínicas

**Casos de Test**:
- `shouldPassValidationWithValidCommand()`: Comando válido pasa validación
- `shouldFailWhenClinicIdIsNull()`: Rechaza clinicId nulo
- `shouldFailWhenClinicIdIsNegative()`: Rechaza clinicId negativo
- `shouldFailWhenClinicIdIsZero()`: Rechaza clinicId cero
- Todos los casos de CreateClinicCommandValidatorTest aplicables
- `shouldFailWhenSubscriptionStatusExceedsMaxLength()`: Rechaza subscriptionStatus > 50 caracteres
- `shouldAcceptNullSubscriptionStatus()`: Acepta subscriptionStatus opcional nulo

**Dependencias**: UpdateClinicCommand, ValidationResult

#### 2.3 ClinicServiceTest.java (NUEVO)
**Propósito**: Validar la lógica de negocio del servicio de aplicación

**Casos de Test**:

**Creación**:
- `shouldCreateClinicSuccessfully()`: Crea clínica con datos válidos
- `shouldThrowExceptionWhenEmailAlreadyExists()`: Previene email duplicado
- `shouldThrowExceptionWhenLegalNumberAlreadyExists()`: Previene legalNumber duplicado
- `shouldThrowValidationExceptionForInvalidCommand()`: Rechaza comando inválido
- `shouldPublishDomainEventsOnCreation()`: Publica eventos antes de guardar
- `shouldClearDomainEventsAfterPublishing()`: Limpia eventos después de publicar
- `shouldSetSubscriptionStatusToActiveOnCreation()`: Establece status ACTIVE por defecto

**Actualización**:
- `shouldUpdateClinicSuccessfully()`: Actualiza clínica con datos válidos
- `shouldThrowExceptionWhenClinicNotFound()`: Rechaza actualización de clínica inexistente
- `shouldThrowExceptionWhenEmailBelongsToAnotherClinic()`: Previene email duplicado en otra clínica
- `shouldAllowSameEmailForSameClinic()`: Permite mantener el mismo email
- `shouldPublishDomainEventsOnUpdate()`: Publica eventos antes de guardar
- `shouldUpdateUpdatedAtTimestamp()`: Actualiza timestamp updatedAt
- `shouldNotModifyCreatedAtTimestamp()`: Mantiene timestamp createdAt

**Consulta**:
- `shouldGetClinicByIdSuccessfully()`: Recupera clínica por ID
- `shouldThrowExceptionWhenClinicNotFoundById()`: Rechaza ID inexistente
- `shouldGetAllClinicsSuccessfully()`: Lista todas las clínicas
- `shouldReturnEmptyListWhenNoClinicsExist()`: Retorna lista vacía sin error

**Eliminación**:
- `shouldDeleteClinicSuccessfully()`: Elimina clínica existente
- `shouldThrowExceptionWhenDeletingNonExistentClinic()`: Rechaza eliminación de clínica inexistente
- `shouldPublishDomainEventsOnDeletion()`: Publica eventos antes de eliminar

**Dependencias**: Mock de ClinicRepositoryPort, Mock de DomainEventPublisher, Validators reales

### 3. Infrastructure Layer Tests

#### 3.1 ClinicRepositoryTest.java (NUEVO)
**Propósito**: Validar operaciones de persistencia con base de datos real (H2 en memoria)

**Casos de Test**:

**Persistencia Básica**:
- `shouldSaveClinicAndGenerateId()`: Guarda clínica y genera ID automático
- `shouldFindClinicById()`: Recupera clínica por ID
- `shouldReturnEmptyWhenClinicNotFound()`: Retorna Optional.empty() para ID inexistente
- `shouldFindAllClinics()`: Lista todas las clínicas
- `shouldDeleteClinicById()`: Elimina clínica por ID

**Value Objects**:
- `shouldPersistAndRetrieveAddressCorrectly()`: Persiste y recupera Address sin pérdida
- `shouldPersistAndRetrieveEmailCorrectly()`: Persiste y recupera Email sin pérdida
- `shouldPersistAndRetrievePhoneCorrectly()`: Persiste y recupera Phone sin pérdida
- `shouldHandleNullOptionalFields()`: Maneja campos opcionales nulos (logoUrl)

**Consultas de Unicidad**:
- `shouldReturnTrueWhenEmailExists()`: Detecta email existente
- `shouldReturnFalseWhenEmailDoesNotExist()`: Detecta email no existente
- `shouldReturnTrueWhenLegalNumberExists()`: Detecta legalNumber existente
- `shouldReturnFalseWhenLegalNumberDoesNotExist()`: Detecta legalNumber no existente
- `shouldExcludeCurrentClinicWhenCheckingEmailUniqueness()`: Excluye clínica actual en verificación de email
- `shouldExcludeCurrentClinicWhenCheckingLegalNumberUniqueness()`: Excluye clínica actual en verificación de legalNumber

**Actualización**:
- `shouldUpdateAllFieldsCorrectly()`: Actualiza todos los campos
- `shouldUpdateValueObjectsCorrectly()`: Actualiza Value Objects correctamente
- `shouldMaintainIdAfterUpdate()`: Mantiene ID después de actualización

**Dependencias**: Spring Data JPA, H2 Database, @DataJpaTest

#### 3.2 ClinicEntityMappingTest.java (NUEVO)
**Propósito**: Validar conversión entre Clinic (domain) y ClinicEntity (persistence)

**Casos de Test**:
- `shouldMapDomainModelToEntity()`: Convierte Clinic a ClinicEntity correctamente
- `shouldMapEntityToDomainModel()`: Convierte ClinicEntity a Clinic correctamente
- `shouldPreserveAllFieldsDuringMapping()`: No pierde datos en conversión
- `shouldMapValueObjectsCorrectly()`: Convierte Value Objects correctamente
- `shouldHandleNullOptionalFieldsInMapping()`: Maneja campos opcionales nulos

**Dependencias**: Ninguna (tests unitarios de mapeo)

#### 3.3 ClinicControllerIntegrationTest.java (EXISTENTE - EXPANDIR)
**Propósito**: Validar endpoints REST end-to-end

**Casos de Test a Agregar/Verificar**:

**POST /clinic (Creación)**:
- `shouldCreateClinicWithValidData()`: Crea clínica y retorna 201
- `shouldReturnCreatedClinicWithGeneratedId()`: Retorna clínica con ID generado
- `shouldReturn400WhenCreatingWithInvalidData()`: Rechaza datos inválidos con 400
- `shouldReturn409WhenEmailAlreadyExists()`: Rechaza email duplicado con 409
- `shouldReturn409WhenLegalNumberAlreadyExists()`: Rechaza legalNumber duplicado con 409
- `shouldValidateValueObjectsOnCreation()`: Valida formato de Email, Phone, Address

**PUT /clinic/{id} (Actualización)**:
- `shouldUpdateClinicWithValidData()`: Actualiza clínica y retorna 200
- `shouldReturn404WhenUpdatingNonExistentClinic()`: Rechaza ID inexistente con 404
- `shouldReturn400WhenUpdatingWithInvalidData()`: Rechaza datos inválidos con 400
- `shouldReturn409WhenUpdatingWithDuplicateEmail()`: Rechaza email duplicado con 409
- `shouldAllowUpdatingWithSameEmail()`: Permite mantener mismo email

**GET /clinic/{id} (Consulta por ID)**:
- `shouldGetClinicById()`: Recupera clínica y retorna 200
- `shouldReturn404WhenClinicNotFound()`: Rechaza ID inexistente con 404
- `shouldReturnAllFieldsIncludingValueObjects()`: Retorna todos los campos correctamente

**GET /clinic (Listar todas)**:
- `shouldGetAllClinics()`: Lista todas las clínicas y retorna 200
- `shouldReturnEmptyArrayWhenNoClinics()`: Retorna array vacío sin error

**DELETE /clinic/{id} (Eliminación)**:
- `shouldDeleteClinicSuccessfully()`: Elimina clínica y retorna 204
- `shouldReturn404WhenDeletingNonExistentClinic()`: Rechaza ID inexistente con 404
- `shouldNotBeAbleToGetDeletedClinic()`: Verifica que clínica eliminada no existe

**Dependencias**: @SpringBootTest, MockMvc, Base de datos H2

## Data Models

### Test Data Builders

Para facilitar la creación de datos de test, se crearán builders:

```java
public class ClinicTestDataBuilder {
    public static Clinic.ClinicBuilder aValidClinic() {
        return Clinic.builder()
            .clinicName("Test Clinic")
            .legalName("Test Clinic Legal Name")
            .legalNumber("12345678")
            .address(new Address("123 Main St", "Springfield", "12345"))
            .phone(new Phone("+1234567890"))
            .email(new Email("test@clinic.com"))
            .logoUrl("https://example.com/logo.png")
            .suscriptionStatus("ACTIVE");
    }
    
    public static CreateClinicCommand aValidCreateCommand() {
        return new CreateClinicCommand(
            "Test Clinic",
            "Test Clinic Legal Name",
            "12345678",
            new Address("123 Main St", "Springfield", "12345"),
            new Phone("+1234567890"),
            new Email("test@clinic.com"),
            "https://example.com/logo.png"
        );
    }
    
    public static UpdateClinicCommand aValidUpdateCommand(Long clinicId) {
        return UpdateClinicCommand.builder()
            .clinicId(clinicId)
            .clinicName("Updated Clinic")
            .legalName("Updated Legal Name")
            .legalNumber("87654321")
            .address(new Address("456 Oak St", "Shelbyville", "54321"))
            .phone(new Phone("+0987654321"))
            .email(new Email("updated@clinic.com"))
            .logoUrl("https://example.com/new-logo.png")
            .suscriptionStatus("ACTIVE")
            .build();
    }
}
```

## Error Handling

### Estrategia de Manejo de Errores en Tests

1. **Excepciones de Dominio**: Validar que se lanzan con mensajes correctos
2. **Excepciones de Validación**: Verificar que contienen todos los errores
3. **Respuestas HTTP**: Validar códigos de estado y estructura de ErrorResponse
4. **Rollback de Transacciones**: Verificar que errores no dejan datos corruptos

### Códigos HTTP Esperados

- `200 OK`: Operaciones exitosas (GET, PUT)
- `201 Created`: Creación exitosa (POST)
- `204 No Content`: Eliminación exitosa (DELETE)
- `400 Bad Request`: Datos inválidos
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Violación de restricciones de unicidad

## Testing Strategy

### Niveles de Testing

#### 1. Unit Tests (Capa Domain y Application)
- **Alcance**: Clases individuales sin dependencias externas
- **Herramientas**: JUnit 5, Mockito para mocks mínimos
- **Velocidad**: Muy rápida
- **Cobertura**: Lógica de negocio, validaciones, eventos de dominio

#### 2. Integration Tests (Capa Infrastructure - Repository)
- **Alcance**: Interacción con base de datos
- **Herramientas**: @DataJpaTest, H2 in-memory database
- **Velocidad**: Rápida
- **Cobertura**: Persistencia, converters, consultas

#### 3. End-to-End Tests (Controller)
- **Alcance**: Flujo completo desde HTTP hasta base de datos
- **Herramientas**: @SpringBootTest, MockMvc, H2 in-memory database
- **Velocidad**: Moderada
- **Cobertura**: Integración completa, serialización JSON, manejo de errores HTTP

### Priorización de Tests

**Alta Prioridad** (Prevención de Corrupción de Datos):
1. Validación de comandos (CreateClinicCommandValidatorTest, UpdateClinicCommandValidatorTest)
2. Prevención de duplicados (ClinicServiceTest - casos de duplicados)
3. Persistencia de Value Objects (ClinicRepositoryTest - casos de Value Objects)
4. Integridad de actualización (ClinicServiceTest - casos de actualización)

**Media Prioridad** (Funcionalidad Correcta):
1. Operaciones CRUD básicas (ClinicServiceTest, ClinicRepositoryTest)
2. Domain Events (ClinicTest, ClinicServiceTest)
3. Manejo de excepciones (ClinicExceptionsTest, ClinicServiceTest)

**Baja Prioridad** (Casos Edge):
1. Campos opcionales nulos
2. Listas vacías
3. Formato de mensajes de error

### Configuración de Tests

#### application-test.properties
```properties
# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Logging
logging.level.com.datavet.datavet=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Convenciones de Naming

- **Test Classes**: `{ClassName}Test.java` para unit tests, `{ClassName}IntegrationTest.java` para integration tests
- **Test Methods**: `should{ExpectedBehavior}When{Condition}()` o `should{ExpectedBehavior}()`
- **Given-When-Then**: Usar comentarios para estructurar tests complejos

### Assertions y Verificaciones

1. **Datos Persistidos**: Verificar que todos los campos se guardan correctamente
2. **Value Objects**: Verificar que se reconstruyen correctamente desde DB
3. **Timestamps**: Verificar createdAt y updatedAt
4. **Domain Events**: Verificar que se generan, publican y limpian
5. **Excepciones**: Verificar tipo, mensaje y datos contenidos

## Implementation Notes

### Tests Existentes a Revisar

1. **ClinicDomainEventsTest.java**: Verificar que cubre todos los eventos (Created, Updated, Deleted)
2. **ClinicServiceExceptionTest.java**: Verificar que cubre todas las excepciones
3. **ClinicControllerIntegrationTest.java**: Expandir con casos de error y validación

### Tests Nuevos a Crear

1. **ClinicTest.java**: Tests unitarios del modelo de dominio
2. **ClinicExceptionsTest.java**: Tests de excepciones
3. **CreateClinicCommandValidatorTest.java**: Tests de validación de creación
4. **UpdateClinicCommandValidatorTest.java**: Tests de validación de actualización
5. **ClinicServiceTest.java**: Tests del servicio de aplicación
6. **ClinicRepositoryTest.java**: Tests de persistencia
7. **ClinicEntityMappingTest.java**: Tests de mapeo entre capas

### Dependencias de Testing

```xml
<!-- Ya incluidas en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Consideraciones Especiales

1. **Value Objects del Shared**: No testear validación de Email, Phone, Address en tests de Clinic (ya cubiertos)
2. **Converters del Shared**: Verificar que funcionan correctamente con datos de Clinic
3. **Domain Events**: Verificar que se publican ANTES de persistir (importante para consistencia)
4. **Transacciones**: Tests de repository deben usar @Transactional para rollback automático
5. **Datos de Test**: Usar builders para crear datos consistentes y reutilizables
