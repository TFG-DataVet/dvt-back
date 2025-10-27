# Requirements Document

## Introduction

Este documento define los requisitos para corregir y mejorar el CRUD actual de Clinic en DataVet. El objetivo es asegurar que el controlador siga las mejores prácticas de desarrollo, retornando DTOs apropiados en lugar de entidades directas, implementando validaciones adecuadas y manejo de errores consistente.

## Requirements

### Requirement 1

**User Story:** Como desarrollador del sistema, quiero que todos los endpoints del ClinicController retornen DTOs apropiados en lugar de entidades directas, para que la información sensible no se exponga al cliente y mantener un contrato de API limpio.

#### Acceptance Criteria

1. WHEN se llama al endpoint GET /clinic/{id} THEN el sistema SHALL retornar un ClinicResponse en lugar de la entidad Clinic
2. WHEN se llama al endpoint GET /clinic THEN el sistema SHALL retornar una lista de ClinicResponse en lugar de entidades Clinic
3. WHEN se llama al endpoint PUT /clinic/{id} THEN el sistema SHALL retornar un ClinicResponse en lugar de la entidad Clinic
4. WHEN se actualiza una clínica THEN el sistema SHALL validar que el ID del path coincida con el ID del comando

### Requirement 2

**User Story:** Como desarrollador del sistema, quiero implementar validaciones apropiadas en los DTOs de entrada, para que los datos ingresados sean consistentes y válidos antes de procesarlos.

#### Acceptance Criteria

1. WHEN se envía un CreateClinicCommand THEN el sistema SHALL validar que los campos obligatorios no estén vacíos
2. WHEN se envía un UpdateClinicCommand THEN el sistema SHALL validar que los campos obligatorios no estén vacíos
3. WHEN se envía un email THEN el sistema SHALL validar que tenga formato de email válido
4. WHEN se envía un teléfono THEN el sistema SHALL validar que tenga formato válido
5. IF alguna validación falla THEN el sistema SHALL retornar un error 400 con detalles específicos

### Requirement 3

**User Story:** Como desarrollador del sistema, quiero implementar manejo de errores consistente en el ClinicController, para que los clientes reciban respuestas HTTP apropiadas y mensajes de error informativos.

#### Acceptance Criteria

1. WHEN se busca una clínica que no existe THEN el sistema SHALL retornar error 404 con mensaje descriptivo
2. WHEN ocurre un error de validación THEN el sistema SHALL retornar error 400 con detalles de los campos inválidos
3. WHEN ocurre un error interno THEN el sistema SHALL retornar error 500 sin exponer detalles internos
4. WHEN se intenta crear una clínica con datos duplicados THEN el sistema SHALL retornar error 409 con mensaje apropiado

### Requirement 4

**User Story:** Como desarrollador del sistema, quiero que el código del ClinicController sea limpio y mantenible, para facilitar futuras modificaciones y debugging.

#### Acceptance Criteria

1. WHEN se revisa el código THEN el controlador SHALL usar anotaciones de validación apropiadas
2. WHEN se revisa el código THEN el controlador SHALL tener métodos con responsabilidades claras
3. WHEN se revisa el código THEN el controlador SHALL usar ResponseEntity para control explícito de códigos HTTP
4. WHEN se actualiza una clínica THEN el sistema SHALL eliminar la lógica redundante de reconstrucción del comando