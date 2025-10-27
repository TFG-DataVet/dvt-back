# Design Document - Clinic Shared Integration

## Overview

Este documento de diseño describe la integración completa de los componentes compartidos (shared components) en el dominio clinic del sistema DataVet. El objetivo es transformar la implementación actual del clinic para que utilice completamente la infraestructura compartida, estableciendo un patrón de referencia para futuros dominios.

La integración incluye la adopción de clases base, interfaces compartidas, value objects, excepciones base y patrones de infraestructura comunes, manteniendo la funcionalidad existente mientras se estandariza la implementación.

## Architecture

### Current State Analysis

El dominio clinic actualmente tiene una implementación funcional pero no integrada con los shared components:

**Domain Layer**:
- `Clinic.java`: Entidad de dominio independiente sin herencia de clases base
- `ClinicNotFoundException.java`: Ya extiende `EntityNotFoundException` (parcialmente integrado)
- `ClinicAlreadyExistsException.java`: Ya extiende `EntityAlreadyExistsException` (parcialmente integrado)

**Application Layer**:
- `ClinicService.java`: Servicio de aplicación sin implementar `ApplicationService`
- Ports y commands sin usar interfaces base compartidas

**Infrastructure Layer**:
- `ClinicEntity.java`: Entidad JPA sin extender `BaseEntity`
- Repository adapter sin usar `Repository<T, ID>` interface

### Target Architecture

La nueva arquitectura integrará completamente los shared components:**Dom
ain Layer Integration**:
- `Clinic.java` → Implementar `Entity<Long>` interface y usar value objects compartidos
- Excepciones ya integradas con base classes
- Agregar soporte para domain events usando `AggregateRoot<Long>`

**Application Layer Integration**:
- `ClinicService.java` → Implementar `ApplicationService` interface
- Repository ports → Extender `Repository<Clinic, Long>`
- Agregar validación usando shared validation framework

**Infrastructure Layer Integration**:
- `ClinicEntity.java` → Extender `BaseEntity` para audit fields
- Repository adapter → Implementar `Repository<Clinic, Long>`
- Usar `GlobalExceptionHandler` y `DatabaseConfig` compartidos

## Components and Interfaces

### Domain Model Integration

**Clinic Entity Enhancement**:
```java
public class Clinic extends AggregateRoot<Long> implements Entity<Long> {
    private Long clinicID;
    private String clinicName;
    private String legalName;
    private String legalNumber;
    private Address address;  // Using shared Address value object
    private Email email;      // Using shared Email value object
    private Phone phone;      // Using shared Phone value object
    // ... other fields
}
```

**Value Objects Integration**:
- Reemplazar campos primitivos de dirección con `Address` value object
- Reemplazar campo email String con `Email` value object
- Reemplazar campo phone String con `Phone` value object

### Application Layer Integration

**Service Enhancement**:
```java
@Service
public class ClinicService implements ClinicUseCase, ApplicationService {
    // Implementation using shared patterns
}
```

**Repository Port Enhancement**:
```java
public interface ClinicRepositoryPort extends Repository<Clinic, Long> {
    // Domain-specific methods
    boolean existsByEmail(Email email);
    boolean existsByLegalNumber(String legalNumber);
}
```

### Infrastructure Layer Integration

**Entity Enhancement**:
```java
@Entity
@Table(name = "clinics")
public class ClinicEntity extends BaseEntity {
    // JPA entity with audit fields from BaseEntity
}
```

**Repository Adapter Enhancement**:
```java
@Repository
public class JpaClinicRepositoryAdapter implements ClinicRepositoryPort {
    // Implementation using shared Repository interface
}
```

## Data Models

### Value Objects Integration

**Address Integration**:
- Combinar campos `address`, `city`, `codePostal` en un solo `Address` value object
- Mantener validación y formateo consistente
- Aprovechar métodos utilitarios de `Address`

**Email Integration**:
- Reemplazar validación manual con `Email` value object
- Usar validación incorporada y formateo estándar

**Phone Integration**:
- Reemplazar regex pattern con `Phone` value object
- Estandarizar formato y validación de números telefónicos

### Domain Events Integration

**Clinic Events**:
- `ClinicCreatedEvent`: Evento cuando se crea una nueva clínica
- `ClinicUpdatedEvent`: Evento cuando se actualiza información de clínica
- `ClinicDeletedEvent`: Evento cuando se elimina una clínica

## Error Handling

### Exception Integration Status

Las excepciones ya están parcialmente integradas:
- `ClinicNotFoundException` extiende `EntityNotFoundException` ✓
- `ClinicAlreadyExistsException` extiende `EntityAlreadyExistsException` ✓

**Enhancement Needed**:
- Aprovechar constructores base para mensajes consistentes
- Usar `GlobalExceptionHandler` para manejo centralizado

## Testing Strategy

### Integration Testing Approach

**Domain Layer Tests**:
- Verificar uso correcto de value objects
- Validar domain events functionality
- Probar business rules con shared components

**Application Layer Tests**:
- Verificar integración con `ApplicationService`
- Probar validación usando shared framework
- Validar manejo de excepciones base

**Infrastructure Layer Tests**:
- Verificar audit fields de `BaseEntity`
- Probar repository implementation con shared interface
- Validar configuración compartida

## Implementation Strategy

### Phase 1: Domain Model Integration
1. Integrar value objects (Address, Email, Phone)
2. Implementar Entity interface
3. Agregar AggregateRoot functionality para domain events

### Phase 2: Application Layer Integration  
1. Implementar ApplicationService interface
2. Actualizar repository ports con shared Repository interface
3. Integrar shared validation framework

### Phase 3: Infrastructure Layer Integration
1. Extender BaseEntity en ClinicEntity
2. Implementar shared Repository interface en adapter
3. Configurar uso de shared infrastructure components

### Phase 4: Testing and Documentation
1. Actualizar tests para nueva integración
2. Documentar patrones como template
3. Crear ejemplos de uso para futuros dominios

## Design Decisions and Rationales

### 1. Gradual Integration Approach
**Decision**: Integrar shared components de forma incremental por capas
**Rationale**: Minimiza riesgo, permite validación paso a paso, mantiene funcionalidad

### 2. Value Objects Priority
**Decision**: Priorizar integración de value objects (Address, Email, Phone)
**Rationale**: Mayor impacto en consistencia de datos, reutilización inmediata

### 3. Preserve Existing API
**Decision**: Mantener API externa sin cambios durante integración
**Rationale**: Evita breaking changes, permite integración transparente

### 4. Template Documentation
**Decision**: Documentar cada patrón integrado como template
**Rationale**: Facilita replicación en futuros dominios, estandariza desarrollo

Esta integración establecerá clinic como el template de referencia para implementar shared components en todos los futuros dominios del sistema DataVet.