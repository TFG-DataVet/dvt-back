# Design Document

## Overview

Este documento describe el diseño para corregir y mejorar el CRUD de Clinic existente. Las mejoras incluyen el uso consistente de DTOs, implementación de validaciones, manejo de errores robusto y limpieza del código del controlador.

## Architecture

### Current Issues Identified
1. **Inconsistent Response Types**: Algunos endpoints retornan entidades directas mientras otros retornan DTOs
2. **Missing Validations**: Los commands no tienen validaciones de entrada
3. **Poor Error Handling**: No hay manejo consistente de errores HTTP
4. **Code Duplication**: Lógica redundante en el método update
5. **Naming Inconsistencies**: Diferencias entre nombres de campos en commands y entidades

### Proposed Solution
- Estandarizar todos los endpoints para usar ResponseEntity<ClinicResponse>
- Implementar validaciones usando Bean Validation (JSR-303)
- Crear un GlobalExceptionHandler para manejo centralizado de errores
- Limpiar y optimizar el código del controlador
- Corregir inconsistencias de nombres entre DTOs

## Components and Interfaces

### 1. Updated ClinicController
```java
@RestController
@RequestMapping("/clinic")
@RequiredArgsConstructor
@Validated
public class ClinicController {
    // Todos los métodos retornarán ResponseEntity<ClinicResponse> o ResponseEntity<List<ClinicResponse>>
    // Uso de @Valid para validación automática
    // Eliminación de lógica redundante
}
```

### 2. Enhanced Commands with Validation
```java
// CreateClinicCommand con validaciones
@NotBlank String clinicName
@Email String email
@Pattern String phone
// etc.

// UpdateClinicCommand con validaciones similares
```

### 3. Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Manejo de ValidationException -> 400
    // Manejo de EntityNotFoundException -> 404
    // Manejo de DataIntegrityViolationException -> 409
    // Manejo de Exception genérica -> 500
}
```

### 4. Custom Exceptions
```java
public class ClinicNotFoundException extends RuntimeException
public class ClinicAlreadyExistsException extends RuntimeException
```

## Data Models

### Updated Commands Structure
- **CreateClinicCommand**: Corregir typo en `legalNumer` → `legalNumber`
- **UpdateClinicCommand**: Asegurar consistencia con nombres de campos del dominio
- Ambos commands tendrán validaciones apropiadas

### Response DTOs
- **ClinicResponse**: Mantener estructura actual pero asegurar mapeo correcto
- **ErrorResponse**: Nuevo DTO para respuestas de error consistentes

## Error Handling

### HTTP Status Codes Strategy
- **200 OK**: Operaciones exitosas (GET, PUT)
- **201 Created**: Creación exitosa (POST)
- **400 Bad Request**: Errores de validación
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflictos de datos (duplicados)
- **500 Internal Server Error**: Errores internos del servidor

### Error Response Format
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    {
      "field": "email",
      "message": "must be a well-formed email address"
    }
  ],
  "path": "/clinic"
}
```

## Testing Strategy

### Unit Tests
- Validación de DTOs con datos válidos e inválidos
- Mapeo correcto entre entidades y DTOs
- Lógica del controlador con mocks

### Integration Tests
- Endpoints completos con base de datos en memoria
- Validación de códigos de respuesta HTTP
- Validación de formato de respuestas JSON

### Test Cases Priority
1. **Happy Path**: Operaciones CRUD exitosas
2. **Validation Errors**: Datos inválidos en requests
3. **Not Found Scenarios**: Recursos inexistentes
4. **Conflict Scenarios**: Datos duplicados

## Implementation Notes

### Validation Rules
- **clinicName**: Required, max 100 characters
- **legalName**: Required, max 150 characters  
- **email**: Required, valid email format
- **phone**: Optional, valid phone format if provided
- **address**: Required, max 200 characters
- **city**: Required, max 50 characters

### Mapping Considerations
- Asegurar que todos los campos se mapeen correctamente entre Command → Domain → Response
- Manejar campos opcionales apropiadamente
- Considerar campos de auditoría (createdAt, updatedAt) solo en respuestas cuando sea necesario