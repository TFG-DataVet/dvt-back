# Diseño de la Guía para Crear Nuevos Dominios

## Visión General

La guía será un documento completo que explique paso a paso cómo crear un nuevo dominio en el proyecto DataVet, siguiendo la arquitectura hexagonal (puertos y adaptadores) ya establecida. La guía usará el dominio "Dueño" (Owner) como ejemplo práctico, mostrando cada archivo que debe crearse y cómo conectar las diferentes capas.

## Arquitectura de la Guía

### Estructura del Documento

La guía seguirá una estructura pedagógica que va de lo conceptual a lo práctico:

1. **Introducción y Conceptos**: Explicación simple de la arquitectura
2. **Preparación**: Qué necesitas antes de empezar
3. **Implementación por Capas**: Paso a paso desde el dominio hacia afuera
4. **Integración y Pruebas**: Cómo conectar todo y verificar que funciona
5. **Checklist y Troubleshooting**: Verificación final y solución de problemas

### Principios de Diseño

- **Lenguaje Sencillo**: Evitar jerga técnica innecesaria
- **Ejemplos Concretos**: Cada concepto acompañado de código real
- **Progresión Lógica**: Cada paso construye sobre el anterior
- **Verificación Continua**: Checkpoints para validar el progreso
- **Reutilización**: Énfasis en usar componentes compartidos existentes

## Componentes de la Guía

### 1. Capa de Dominio (Domain Layer)

#### 1.1 Modelo de Dominio
- **Archivo**: `Owner.java`
- **Propósito**: Entidad principal que representa un dueño de mascota
- **Características**:
  - Extiende `AggregateRoot<Long>`
  - Implementa `Entity<Long>`
  - Usa value objects compartidos (Email, Phone, Address)
  - Incluye métodos de factory (create)
  - Maneja eventos de dominio
  - Validaciones de negocio

#### 1.2 Eventos de Dominio
- **Archivos**: `OwnerCreatedEvent.java`, `OwnerUpdatedEvent.java`, `OwnerDeletedEvent.java`
- **Propósito**: Notificar cambios importantes en el dominio
- **Características**:
  - Implementan `DomainEvent`
  - Contienen información relevante del evento
  - Se publican automáticamente por el agregado

#### 1.3 Excepciones de Dominio
- **Archivos**: `OwnerNotFoundException.java`, `OwnerAlreadyExistsException.java`, `OwnerValidationException.java`
- **Propósito**: Manejar errores específicos del dominio
- **Características**:
  - Extienden excepciones base del shared
  - Proporcionan mensajes específicos
  - Se mapean a códigos HTTP apropiados

### 2. Capa de Aplicación (Application Layer)

#### 2.1 Puertos de Entrada (Input Ports)
- **Archivo**: `OwnerUseCase.java`
- **Propósito**: Define qué operaciones puede realizar el dominio
- **Características**:
  - Interfaz que extiende `UseCase`
  - Métodos para CRUD básico
  - Operaciones específicas del negocio

#### 2.2 Comandos
- **Archivos**: `CreateOwnerCommand.java`, `UpdateOwnerCommand.java`
- **Propósito**: Encapsular datos de entrada para operaciones
- **Características**:
  - Objetos inmutables (Value objects)
  - Validaciones básicas con anotaciones
  - Usan value objects compartidos

#### 2.3 Validadores
- **Archivos**: `CreateOwnerCommandValidator.java`, `UpdateOwnerCommandValidator.java`
- **Propósito**: Validar comandos usando el framework compartido
- **Características**:
  - Implementan `Validator<T>`
  - Usan `ValidationResult` compartido
  - Validaciones de negocio específicas

#### 2.4 Puertos de Salida (Output Ports)
- **Archivo**: `OwnerRepositoryPort.java`
- **Propósito**: Define cómo el dominio accede a datos
- **Características**:
  - Interfaz que extiende `Repository<Owner, Long>`
  - Métodos de consulta específicos
  - Operaciones de persistencia

#### 2.5 Servicios de Aplicación
- **Archivo**: `OwnerService.java`
- **Propósito**: Orquestar operaciones del dominio
- **Características**:
  - Implementa `OwnerUseCase` y `ApplicationService`
  - Maneja transacciones
  - Publica eventos de dominio
  - Coordina validaciones

#### 2.6 DTOs y Mappers
- **Archivos**: `OwnerResponse.java`, `OwnerMapper.java`
- **Propósito**: Transformar datos entre capas
- **Características**:
  - DTOs inmutables para respuestas
  - Mappers estáticos para conversiones
  - Separación clara entre capas

### 3. Capa de Infraestructura (Infrastructure Layer)

#### 3.1 Adaptadores de Entrada (Input Adapters)
- **Archivo**: `OwnerController.java`
- **Propósito**: Exponer APIs REST
- **Características**:
  - Controlador Spring Boot
  - Mapeo de URLs
  - Validación de requests
  - Manejo de respuestas HTTP

#### 3.2 DTOs de Request
- **Archivos**: `CreateOwnerRequest.java`, `UpdateOwnerRequest.java`
- **Propósito**: Recibir datos del cliente
- **Características**:
  - Validaciones con Bean Validation
  - Campos específicos de la API
  - Conversión a comandos

#### 3.3 Persistencia
- **Archivos**: `OwnerEntity.java`, `JpaOwnerRepository.java`, `JpaOwnerRepositoryAdapter.java`
- **Propósito**: Manejar persistencia en base de datos
- **Características**:
  - Entidad JPA que extiende `BaseEntity`
  - Convertidores para value objects
  - Repositorio JPA con consultas personalizadas
  - Adaptador que implementa el puerto de salida

#### 3.4 Convertidores JPA
- **Archivos**: Reutilizar convertidores existentes (`AddressConverter.java`, `EmailConverter.java`, `PhoneConverter.java`)
- **Propósito**: Convertir value objects a/desde base de datos
- **Características**:
  - Implementan `AttributeConverter`
  - Manejan serialización/deserialización
  - Reutilizan lógica compartida

## Flujo de Datos

### Flujo de Creación (POST)
1. **Request** → `CreateOwnerRequest` (Infrastructure)
2. **Validation** → Bean Validation + Custom Validation
3. **Command** → `CreateOwnerCommand` (Application)
4. **Service** → `OwnerService.createOwner()` (Application)
5. **Domain** → `Owner.create()` + Domain Events
6. **Repository** → `OwnerRepositoryPort.save()` (Application → Infrastructure)
7. **Response** → `OwnerResponse` via `OwnerMapper` (Application)

### Flujo de Consulta (GET)
1. **Request** → Path Parameter (Infrastructure)
2. **Service** → `OwnerService.getOwnerById()` (Application)
3. **Repository** → `OwnerRepositoryPort.findById()` (Application → Infrastructure)
4. **Response** → `OwnerResponse` via `OwnerMapper` (Application)

## Estrategia de Testing

### Tests de Dominio
- **Archivo**: `OwnerTest.java`
- **Propósito**: Validar lógica de negocio
- **Cobertura**: Factory methods, eventos, validaciones

### Tests de Aplicación
- **Archivo**: `OwnerServiceTest.java`
- **Propósito**: Validar orquestación
- **Cobertura**: Casos de uso, manejo de errores, eventos

### Tests de Infraestructura
- **Archivos**: `OwnerControllerTest.java`, `OwnerRepositoryTest.java`
- **Propósito**: Validar integración
- **Cobertura**: APIs REST, persistencia, conversiones

## Manejo de Errores

### Estrategia de Excepciones
- **Dominio**: Excepciones específicas del negocio
- **Aplicación**: Validación de comandos y orquestación
- **Infraestructura**: Errores de persistencia y comunicación
- **Global**: `GlobalExceptionHandler` maneja todas las excepciones

### Códigos de Respuesta HTTP
- `201 Created`: Creación exitosa
- `200 OK`: Consulta/actualización exitosa
- `404 Not Found`: Entidad no encontrada
- `400 Bad Request`: Validación fallida
- `409 Conflict`: Entidad ya existe
- `500 Internal Server Error`: Error del sistema

## Configuración y Dependencias

### Dependencias Spring
- `@Service`: Servicios de aplicación
- `@Repository`: Adaptadores de repositorio
- `@RestController`: Controladores REST
- `@Component`: Validadores y otros componentes

### Configuración de Base de Datos
- Tablas con naming convention consistente
- Índices para consultas frecuentes
- Constraints para integridad referencial
- Migraciones con Flyway (si aplica)

## Patrones de Implementación

### Reutilización de Componentes Compartidos
- **Value Objects**: Email, Phone, Address
- **Base Classes**: AggregateRoot, Entity, BaseEntity
- **Validation Framework**: ValidationResult, Validator
- **Exception Hierarchy**: EntityNotFoundException, etc.
- **Event System**: DomainEvent, DomainEventPublisher

### Convenciones de Naming
- **Packages**: `com.datavet.datavet.owner.*`
- **Classes**: `Owner`, `OwnerService`, `OwnerController`
- **Tables**: `owner` (singular, lowercase)
- **Columns**: `owner_id`, `first_name` (snake_case)

## Checklist de Implementación

### Preparación
- [ ] Definir el modelo de dominio y sus propiedades
- [ ] Identificar value objects a reutilizar
- [ ] Planificar la estructura de base de datos

### Capa de Dominio
- [ ] Crear modelo de dominio con eventos
- [ ] Implementar excepciones específicas
- [ ] Definir factory methods y validaciones

### Capa de Aplicación
- [ ] Crear puerto de entrada (UseCase)
- [ ] Implementar comandos con validaciones
- [ ] Crear validadores personalizados
- [ ] Definir puerto de salida (Repository)
- [ ] Implementar servicio de aplicación
- [ ] Crear DTOs y mappers

### Capa de Infraestructura
- [ ] Crear entidad JPA
- [ ] Implementar repositorio JPA
- [ ] Crear adaptador de repositorio
- [ ] Implementar controlador REST
- [ ] Crear DTOs de request/response

### Integración y Pruebas
- [ ] Configurar dependencias Spring
- [ ] Crear migraciones de base de datos
- [ ] Implementar tests unitarios
- [ ] Implementar tests de integración
- [ ] Probar APIs con Postman/curl

Esta estructura garantiza que cada desarrollador pueda seguir un proceso sistemático y consistente para crear nuevos dominios, aprovechando al máximo la infraestructura compartida existente.