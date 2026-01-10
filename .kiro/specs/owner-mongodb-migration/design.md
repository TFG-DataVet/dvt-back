# Design Document: Owner Module MongoDB Migration

## Overview

Esta migración transforma el módulo Owner de una arquitectura basada en PostgreSQL/JPA a MongoDB, manteniendo la arquitectura hexagonal existente. El cambio principal ocurre en la capa de infraestructura (persistencia), mientras que las capas de dominio y aplicación permanecen prácticamente intactas, demostrando la independencia de la arquitectura hexagonal.

### Objetivos

- Migrar la persistencia de Owner de PostgreSQL a MongoDB
- Mantener la arquitectura hexagonal y separación de capas
- Cambiar IDs de Long (auto-increment) a String (ObjectId de MongoDB)
- Adaptar Value Objects (Address, Email, Phone) para MongoDB
- Mantener el módulo Clinic sin cambios (coexistencia temporal de JPA y MongoDB)
- Actualizar tests para usar MongoDB embebido

### Alcance

**Incluido:**
- Módulo Owner completo (domain, application, infrastructure)
- Dependencias Maven
- Configuración de MongoDB
- Tests del módulo Owner
- Value Objects compartidos (adaptación para MongoDB)

**Excluido:**
- Módulo Clinic (permanece con JPA/PostgreSQL)
- Shared domain models (se mantienen genéricos)
- Lógica de negocio (sin cambios)

## Architecture

### Current Architecture (JPA/PostgreSQL)

```
Owner Module
├── Domain Layer
│   ├── Owner (AggregateRoot<Long>)
│   ├── Events (OwnerCreatedEvent, etc.)
│   └── Exceptions
├── Application Layer
│   ├── OwnerService
│   ├── Commands (CreateOwnerCommand, UpdateOwnerCommand)
│   ├── Validators
│   ├── OwnerMapper
│   └── Ports (OwnerRepositoryPort)
└── Infrastructure Layer
    ├── OwnerEntity (JPA @Entity)
    ├── JpaOwnerRepositoryAdapter (extends JpaRepository)
    └── OwnerRepositoryAdapter (implements OwnerRepositoryPort)
```

### Target Architecture (MongoDB)

```
Owner Module
├── Domain Layer (UNCHANGED)
│   ├── Owner (AggregateRoot<String>) ← ID type change
│   ├── Events
│   └── Exceptions
├── Application Layer (MINIMAL CHANGES)
│   ├── OwnerService ← ID type updates
│   ├── Commands ← ID type updates
│   ├── Validators (UNCHANGED)
│   ├── OwnerMapper ← ID type updates
│   └── Ports ← ID type updates
└── Infrastructure Layer (MAJOR CHANGES)
    ├── OwnerDocument (@Document) ← NEW
    ├── MongoOwnerRepositoryAdapter (extends MongoRepository) ← NEW
    └── OwnerRepositoryAdapter ← Updated mappings
```

### Coexistence Strategy

Durante la migración, el sistema soportará ambas bases de datos:

- **Clinic Module**: Continúa usando JPA + PostgreSQL
- **Owner Module**: Usa MongoDB
- **Shared Components**: Se mantienen agnósticos a la tecnología de persistencia

## Components and Interfaces

### 1. Dependencies (pom.xml)

**Cambios:**

```xml
<!-- REMOVE -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- ADD -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- ADD for testing -->
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>
```

**Nota:** Mantener spring-boot-starter-data-jpa temporalmente si Clinic lo requiere, o crear un perfil Maven específico.

### 2. Configuration

#### application.properties

**Cambios:**

```properties
# REMOVE PostgreSQL configuration
# spring.datasource.url=jdbc:postgresql://localhost:5432/clinic_db
# spring.datasource.username=clinicdatavet
# spring.datasource.password=dataVet
# spring.datasource.driver-class-name=org.postgresql.Driver
# spring.jpa.hibernate.ddl-auto=update

# ADD MongoDB configuration
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=datavet_db
spring.data.mongodb.auto-index-creation=true

# Keep logging configuration unchanged
```

#### application-test.properties

```properties
# MongoDB Embedded for tests
spring.data.mongodb.database=datavet_test
de.flapdoodle.mongodb.embedded.version=5.0.5
```

### 3. OwnerDocument (Infrastructure Layer)

**Ubicación:** `src/main/java/com/datavet/datavet/owner/infrastructure/persistence/entity/OwnerDocument.java`

**Diseño:**

```java
@Document(collection = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDocument {

    @Id
    private String id;  // MongoDB ObjectId as String
    
    @Field("clinic_id")
    private String clinicId;  // Changed to String for consistency
    
    @Field("first_name")
    @NotBlank
    @Size(max = 100)
    private String firstName;
    
    @Field("last_name")
    @NotBlank
    @Size(max = 100)
    private String lastName;
    
    @NotBlank
    @Size(max = 9)
    private String dni;
    
    // Value Objects - MongoDB will serialize automatically
    private Phone phone;
    private Email email;
    private Address address;
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
```

**Decisiones de diseño:**

- **@Document** en lugar de @Entity
- **@Id** sin @GeneratedValue (MongoDB genera ObjectId automáticamente)
- **@Field** para mapear nombres de campos (snake_case en DB, camelCase en Java)
- **Value Objects** se serializan automáticamente (no requieren @Convert)
- **Auditing** usando @CreatedDate y @LastModifiedDate de Spring Data MongoDB
- **No hereda de BaseEntity** (BaseEntity es específico de JPA)

### 4. MongoOwnerRepositoryAdapter (Infrastructure Layer)

**Ubicación:** `src/main/java/com/datavet/datavet/owner/infrastructure/persistence/repository/MongoOwnerRepositoryAdapter.java`

**Diseño:**

```java
public interface MongoOwnerRepositoryAdapter extends MongoRepository<OwnerDocument, String> {
    
    boolean existsByEmail(Email email);
    boolean existsByDni(String dni);
    boolean existsByEmailAndDni(Email email, String dni);
    boolean existsByPhone(Phone phone);
    boolean existsByDniAndIdNot(String dni, String id);
}
```

**Cambios clave:**

- Extiende `MongoRepository<OwnerDocument, String>` en lugar de `JpaRepository<OwnerEntity, Long>`
- ID type cambia de `Long` a `String`
- Método `existsByDniAndOwnerIDNot` se renombra a `existsByDniAndIdNot` (MongoDB usa "id" por convención)

### 5. OwnerRepositoryAdapter (Infrastructure Layer)

**Ubicación:** `src/main/java/com/datavet/datavet/owner/infrastructure/adapter/output/OwnerRepositoryAdapter.java`

**Cambios:**

```java
@Component
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepositoryPort {

    private final MongoOwnerRepositoryAdapter repository;  // Changed

    private OwnerDocument toDocument(Owner owner) {  // Renamed from toEntity
        return OwnerDocument.builder()
                .id(owner.getOwnerID())  // String now
                .clinicId(owner.getClinicID() != null ? owner.getClinicID().toString() : null)
                .firstName(owner.getOwnerName())
                .lastName(owner.getOwnerLastName())
                .dni(owner.getOwnerDni())
                .phone(owner.getOwnerPhone())
                .email(owner.getOwnerEmail())
                .address(owner.getOwnerAddress())
                .build();
    }

    private Owner toDomain(OwnerDocument document) {  // Updated parameter
        return Owner.builder()
                .ownerID(document.getId())  // String
                .clinicID(document.getClinicId() != null ? Long.parseLong(document.getClinicId()) : null)
                .ownerLastName(document.getLastName())
                .ownerName(document.getFirstName())
                .ownerDni(document.getDni())
                .ownerPhone(document.getPhone())
                .ownerEmail(document.getEmail())
                .ownerAddress(document.getAddress())
                .build();
    }

    @Override
    public Owner save(Owner owner) {
        return toDomain(repository.save(toDocument(owner)));
    }

    @Override
    public Optional<Owner> findById(String id) {  // Changed from Long
        return repository.findById(id).map(this::toDomain);
    }

    // ... other methods updated similarly
}
```

### 6. Domain Model Updates

#### Owner (Domain Layer)

**Ubicación:** `src/main/java/com/datavet/datavet/owner/domain/model/Owner.java`

**Cambios:**

```java
public class Owner extends AggregateRoot<String> implements Entity<String> {  // Changed from Long

    private String ownerID;  // Changed from Long
    private String clinicID;  // Changed from Long for consistency
    
    // ... rest remains the same
    
    @Override
    public String getId() {  // Changed return type
        return this.ownerID;
    }
    
    public static Owner create(
            String ownerID,  // Changed from Long
            String clinicID,  // Changed from Long
            // ... other parameters
    ) {
        // ... implementation
    }
}
```

### 7. Application Layer Updates

#### OwnerRepositoryPort

**Cambios:**

```java
public interface OwnerRepositoryPort {
    Owner save(Owner owner);
    Optional<Owner> findById(String id);  // Changed from Long
    List<Owner> findAll();
    void deleteById(String id);  // Changed from Long
    boolean existsById(String id);  // Changed from Long
    boolean existsByEmail(Email email);
    boolean existsByDni(String dni);
    boolean existsByPhone(Phone phone);
    boolean existsByDniAndOwnerIdNot(String dni, String id);  // Changed from Long
}
```

#### Commands

**CreateOwnerCommand:** No requiere cambios (no tiene ID)

**UpdateOwnerCommand:**

```java
public class UpdateOwnerCommand {
    private String ownerID;  // Changed from Long
    // ... rest unchanged
}
```

#### OwnerService

**Cambios:**

```java
@Service
@RequiredArgsConstructor
public class OwnerService implements OwnerUseCase, ApplicationService {
    
    // ... dependencies unchanged
    
    @Override
    public void deleteOwner(String id) {  // Changed from Long
        Owner owner = getOwnerById(id);
        // ... rest unchanged
    }

    @Override
    public Owner getOwnerById(String id) {  // Changed from Long
        return ownerRepositoryPort.findById(id)
            .orElseThrow(() -> new OwnerNotFoundException(id));
    }
    
    // ... other methods unchanged
}
```

#### OwnerMapper

**Cambios mínimos:**

```java
public class OwnerMapper {
    public static OwnerResponse toResponse(Owner owner) {
        // ownerID is now String, but OwnerResponse handles it
        return new OwnerResponse(
                owner.getOwnerID(),  // String now
                // ... rest unchanged
        );
    }
}
```

### 8. Value Objects Configuration

#### MongoDB Configuration Class

**Ubicación:** `src/main/java/com/datavet/datavet/shared/infrastructure/config/MongoConfig.java`

**Diseño:**

```java
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    
    // MongoDB will automatically serialize Value Objects as embedded documents
    // No custom converters needed unless special serialization is required
    
    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        // Add custom converters if needed
        return new MongoCustomConversions(converters);
    }
}
```

**Nota:** MongoDB serializa automáticamente objetos Java como documentos embebidos. Los Value Objects (Address, Email, Phone) funcionarán sin convertidores personalizados, a menos que se requiera un formato específico.

### 9. Controller Layer

**Ubicación:** `src/main/java/com/datavet/datavet/owner/infrastructure/adapter/input/OwnerController.java`

**Cambios:**

```java
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    
    private final OwnerUseCase ownerUseCase;
    
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwner(@PathVariable String id) {  // Changed from Long
        // ... implementation
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable String id) {  // Changed from Long
        // ... implementation
    }
    
    // ... other endpoints
}
```

### 10. DTOs and Responses

#### OwnerResponse

**Cambios:**

```java
public record OwnerResponse(
        String ownerID,  // Changed from Long
        String ownerName,
        String ownerLastName,
        String ownerDni,
        Phone ownerPhone,
        Email ownerEmail,
        AddressDto ownerAddress
) {
    // ... nested AddressDto unchanged
}
```

## Data Models

### MongoDB Document Structure

```json
{
  "_id": "507f1f77bcf86cd799439011",
  "clinic_id": "507f191e810c19729de860ea",
  "first_name": "Juan",
  "last_name": "Pérez",
  "dni": "12345678A",
  "phone": {
    "value": "+34612345678"
  },
  "email": {
    "value": "juan.perez@example.com"
  },
  "address": {
    "street": "Calle Mayor 123",
    "city": "Madrid",
    "postalCode": "28001"
  },
  "created_at": "2025-12-05T10:30:00",
  "updated_at": "2025-12-05T10:30:00"
}
```

### Domain Model (Unchanged Logic)

```java
Owner {
    ownerID: String
    clinicID: String
    ownerName: String
    ownerLastName: String
    ownerDni: String
    ownerPhone: Phone
    ownerEmail: Email
    ownerAddress: Address
    createdAt: LocalDateTime
    updatedAt: LocalDateTime
}
```

## Error Handling

### Exception Updates

**OwnerNotFoundException:**

```java
public class OwnerNotFoundException extends EntityNotFoundException {
    public OwnerNotFoundException(String id) {  // Changed from Long
        super("Owner", id);
    }
}
```

**Otras excepciones:** No requieren cambios (no dependen del tipo de ID)

### MongoDB-Specific Errors

Agregar manejo para:

- `MongoException`: Errores de conexión
- `DuplicateKeyException`: Violaciones de índices únicos
- `OptimisticLockingFailureException`: Conflictos de concurrencia

**Implementación en GlobalExceptionHandler:**

```java
@ExceptionHandler(MongoException.class)
public ResponseEntity<ErrorResponse> handleMongoException(MongoException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("Database error", ex.getMessage()));
}
```

## Testing Strategy

### Unit Tests

**Cambios mínimos:**
- Actualizar tipos de ID de `Long` a `String`
- Usar ObjectId válidos en datos de prueba: `new ObjectId().toString()`

### Integration Tests

#### Repository Tests

**Configuración:**

```java
@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MongoOwnerRepositoryAdapterTest {
    
    @Autowired
    private MongoOwnerRepositoryAdapter repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    void shouldSaveAndRetrieveOwner() {
        // Test implementation
    }
}
```

#### Controller Tests

**Cambios:**

```java
@SpringBootTest
@AutoConfigureMockMvc
class OwnerControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldGetOwnerById() throws Exception {
        String ownerId = new ObjectId().toString();
        
        mockMvc.perform(get("/api/owners/" + ownerId))
                .andExpect(status().isOk());
    }
}
```

### Test Data Builders

**OwnerTestDataBuilder:**

```java
public class OwnerTestDataBuilder {
    
    public static Owner buildValidOwner() {
        return Owner.create(
                new ObjectId().toString(),  // Changed from 1L
                new ObjectId().toString(),  // Changed from 10L
                "Juan",
                "Pérez",
                "12345678A",
                Phone.of("+34612345678"),
                Email.of("juan@example.com"),
                Address.of("Calle Mayor 123", "Madrid", "28001")
        );
    }
}
```

## Migration Considerations

### Data Migration

**Estrategia:**

1. **Exportar datos de PostgreSQL:**
   ```sql
   COPY owner TO '/tmp/owners.csv' CSV HEADER;
   ```

2. **Transformar IDs:**
   - Crear script para convertir IDs numéricos a ObjectIds
   - Mantener mapeo de IDs antiguos a nuevos

3. **Importar a MongoDB:**
   ```javascript
   db.owners.insertMany([...])
   ```

### Rollback Strategy

- Mantener PostgreSQL activo durante período de prueba
- Implementar sincronización bidireccional temporal si es necesario
- Validar integridad de datos antes de desactivar PostgreSQL

### Performance Considerations

**Índices MongoDB:**

```java
@Document(collection = "owners")
@CompoundIndexes({
    @CompoundIndex(name = "email_idx", def = "{'email.value': 1}", unique = true),
    @CompoundIndex(name = "dni_idx", def = "{'dni': 1}", unique = true),
    @CompoundIndex(name = "phone_idx", def = "{'phone.value': 1}", unique = true),
    @CompoundIndex(name = "clinic_idx", def = "{'clinic_id': 1}")
})
public class OwnerDocument {
    // ...
}
```

## Validation

### Pre-Migration Checklist

- [ ] Todas las dependencias actualizadas
- [ ] Configuración de MongoDB validada
- [ ] Tests unitarios pasando
- [ ] Tests de integración pasando
- [ ] Índices MongoDB creados
- [ ] Datos migrados y validados

### Post-Migration Validation

- [ ] Endpoints REST funcionando correctamente
- [ ] Validaciones de negocio funcionando
- [ ] Eventos de dominio publicándose
- [ ] Performance aceptable
- [ ] Logs sin errores
- [ ] Módulo Clinic no afectado
