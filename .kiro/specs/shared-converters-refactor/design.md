# Design Document

## Overview

Esta refactorización mueve los convertidores JPA de value objects desde el módulo específico de Clinic hacia el módulo Shared, creando una arquitectura más limpia y reutilizable. Los convertidores (AddressConverter, EmailConverter, PhoneConverter) serán relocalizados manteniendo su funcionalidad exacta pero haciéndolos disponibles para todos los dominios.

## Architecture

### Current State
```
clinic/
└── infrastructure/
    └── persistence/
        └── converter/
            ├── AddressConverter.java
            ├── EmailConverter.java
            └── PhoneConverter.java
```

### Target State
```
shared/
└── infrastructure/
    └── persistence/
        └── converter/
            ├── AddressConverter.java
            ├── EmailConverter.java
            └── PhoneConverter.java
```

### Impact Analysis
- **Clinic Module**: Remover archivos de converter y actualizar imports
- **Owner Module**: Podrá usar los converters para sus entidades
- **Future Domains**: Tendrán acceso automático a los converters
- **Shared Module**: Se convierte en el punto central para converters JPA

## Components and Interfaces

### AddressConverter
- **Location**: `com.datavet.datavet.shared.infrastructure.persistence.converter.AddressConverter`
- **Functionality**: Convierte Address value object a/desde JSON string
- **Dependencies**: Address value object, Jackson ObjectMapper
- **Annotations**: `@Converter(autoApply = true)`, `@Slf4j`

### EmailConverter  
- **Location**: `com.datavet.datavet.shared.infrastructure.persistence.converter.EmailConverter`
- **Functionality**: Convierte Email value object a/desde String
- **Dependencies**: Email value object
- **Annotations**: `@Converter(autoApply = true)`

### PhoneConverter
- **Location**: `com.datavet.datavet.shared.infrastructure.persistence.converter.PhoneConverter`  
- **Functionality**: Convierte Phone value object a/desde String
- **Dependencies**: Phone value object
- **Annotations**: `@Converter(autoApply = true)`

## Data Models

Los convertidores trabajan con los siguientes value objects del shared domain:

### Address Value Object
```java
// Existing in shared.domain.valueobject.Address
- street: String
- city: String  
- postalCode: String (optional)
```

### Email Value Object
```java
// Existing in shared.domain.valueobject.Email
- value: String
```

### Phone Value Object
```java
// Existing in shared.domain.valueobject.Phone
- value: String
```

## Migration Strategy

### Phase 1: Create Shared Converters
1. Crear directorio `shared/infrastructure/persistence/converter/`
2. Copiar archivos de converter con nuevos package declarations
3. Verificar que mantienen toda la funcionalidad

### Phase 2: Update References
1. Actualizar imports en `ClinicEntity.java`
2. Verificar que JPA encuentra los converters automáticamente
3. Ejecutar tests para validar funcionalidad

### Phase 3: Cleanup
1. Eliminar archivos originales de clinic/converter/
2. Verificar que no quedan referencias rotas
3. Validar que tests siguen pasando

## Error Handling

### Converter Discovery
- **Issue**: JPA podría no encontrar converters en nueva ubicación
- **Solution**: Los converters mantienen `@Converter(autoApply = true)` que permite auto-discovery
- **Validation**: Verificar que entidades siguen funcionando correctamente

### Import Resolution
- **Issue**: Clases que usan los converters podrían tener imports rotos
- **Solution**: Actualizar sistemáticamente todos los imports
- **Validation**: Compilación exitosa sin errores

### Backward Compatibility
- **Issue**: Datos existentes en BD podrían no ser compatibles
- **Solution**: Los converters mantienen la misma lógica de conversión
- **Validation**: Tests de persistencia existentes deben pasar

## Testing Strategy

### Unit Tests
- Verificar que converters funcionan individualmente en nueva ubicación
- Validar conversión bidireccional (to/from database)
- Probar casos edge (null values, empty strings)

### Integration Tests  
- Verificar que ClinicEntity sigue funcionando con converters movidos
- Probar operaciones CRUD completas
- Validar que JPA encuentra y aplica converters automáticamente

### Regression Tests
- Ejecutar todos los tests existentes de Clinic
- Verificar que no hay degradación de funcionalidad
- Confirmar que persistencia funciona igual que antes

## Implementation Considerations

### Package Structure
- Mantener consistencia con estructura existente de shared
- Seguir convenciones de naming establecidas
- Preservar organización lógica de componentes

### JPA Configuration
- Los converters con `autoApply = true` se aplican automáticamente
- No requiere configuración adicional en entities
- JPA escanea automáticamente el classpath para converters

### Dependencies
- Converters dependen solo de value objects de shared domain
- No introducen dependencias circulares
- Mantienen bajo acoplamiento con otros módulos