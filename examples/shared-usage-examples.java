// ============================================================================
// EJEMPLO REAL: Cómo se usan las capas compartidas (shared)
// ============================================================================

// 1. ✅ EXCEPCIONES COMPARTIDAS
// En lugar de crear excepciones desde cero, heredas de las compartidas

// Shared (base):
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
}

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityType, Object id) {
        super(entityType + " with id " + id + " not found");
    }
}

// Clinic domain (usa shared):
public class ClinicNotFoundException extends EntityNotFoundException {
    public ClinicNotFoundException(Long clinicId) {
        super("Clinic", clinicId);  // ← Usa la lógica compartida
    }
}

// Pet domain (usa shared):
public class PetNotFoundException extends EntityNotFoundException {
    public PetNotFoundException(Long petId) {
        super("Pet", petId);  // ← Misma lógica, diferente dominio
    }
}

// ============================================================================
// 2. ✅ INTERFACES COMPARTIDAS
// Todos los casos de uso implementan la misma interface base

// Shared:
public interface UseCase {
    // Marker interface - identifica que es un caso de uso
}

// Clinic domain:
public interface ClinicUseCase extends UseCase {  // ← Hereda de shared
    Clinic createClinic(CreateClinicCommand command);
    Clinic getClinicById(Long id);
}

// Pet domain:
public interface PetUseCase extends UseCase {  // ← Hereda de shared
    Pet createPet(CreatePetCommand command);
    Pet getPetById(Long id);
}

// ============================================================================
// 3. ✅ VALUE OBJECTS COMPARTIDOS
// Objetos de valor que se usan en múltiples dominios

// Shared:
public class Email {
    private final String value;
    
    public Email(String email) {
        if (!isValid(email)) throw new IllegalArgumentException("Invalid email");
        this.value = email;
    }
    
    private boolean isValid(String email) {
        return email.contains("@") && email.length() > 5;
    }
}

// Clinic domain:
public class Clinic {
    private Email email;  // ← Usa Email compartido
    
    public Clinic(String emailStr) {
        this.email = new Email(emailStr);  // ← Validación automática
    }
}

// Owner domain:
public class Owner {
    private Email email;  // ← Mismo Email compartido
    
    public Owner(String emailStr) {
        this.email = new Email(emailStr);  // ← Misma validación
    }
}

// ============================================================================
// 4. ✅ MAPPERS COMPARTIDOS
// Interface base para todos los mappers

// Shared:
public interface Mapper<DOMAIN, DTO> {
    DTO toDto(DOMAIN domain);
    DOMAIN toDomain(DTO dto);
}

// Clinic domain:
@Component
public class ClinicMapper implements Mapper<Clinic, ClinicResponse> {
    public ClinicResponse toDto(Clinic clinic) {
        return ClinicResponse.builder()
            .id(clinic.getId())
            .name(clinic.getName())
            .build();
    }
}

// ============================================================================
// 5. ✅ CONFIGURACIÓN COMPARTIDA
// Configuraciones que aplican a todos los dominios

// Shared:
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)  // ← Maneja TODAS las excepciones de este tipo
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(ex.getMessage()));
    }
}

// Resultado: Cuando ClinicNotFoundException o PetNotFoundException se lanzan,
// automáticamente se manejan con el mismo handler compartido

// ============================================================================
// 6. ✅ REPOSITORIOS COMPARTIDOS
// Interface base para todos los repositorios

// Shared:
public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    T save(T entity);
    void deleteById(ID id);
    List<T> findAll();
}

// Clinic domain:
public interface ClinicRepositoryPort extends Repository<Clinic, Long> {
    // Métodos específicos de Clinic
    boolean existsByEmail(String email);
    Optional<Clinic> findByLegalNumber(String legalNumber);
}

// Pet domain:
public interface PetRepositoryPort extends Repository<Pet, Long> {
    // Métodos específicos de Pet
    List<Pet> findByOwnerId(Long ownerId);
    List<Pet> findByClinicId(Long clinicId);
}