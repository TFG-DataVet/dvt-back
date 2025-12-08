# Requirements Document

## Introduction

Este documento define los requisitos de testing para el dominio Clinic del sistema DataVet. El objetivo es establecer una cobertura de pruebas completa que garantice la integridad de datos, prevención de corrupción, y comportamiento correcto en todos los escenarios críticos del dominio Clinic.

**Nota:** Los Value Objects (Address, Email, Phone) están en el dominio Shared y ya tienen cobertura de tests completa en `ValueObjectsIntegrationTest.java`. Los tests de Clinic se enfocarán en la lógica específica del dominio y en cómo se integran estos Value Objects.

## Glossary

- **Clinic_Domain**: El agregado raíz que representa una clínica veterinaria en el sistema, incluyendo su modelo de dominio, servicios de aplicación, validadores, y capa de persistencia
- **Value_Objects**: Objetos inmutables (Address, Email, Phone) que encapsulan validación y lógica de negocio
- **Domain_Events**: Eventos que representan cambios significativos en el estado del dominio (ClinicCreatedEvent, ClinicUpdatedEvent, ClinicDeletedEvent)
- **Command_Validators**: Componentes que validan comandos antes de su ejecución (CreateClinicCommandValidator, UpdateClinicCommandValidator)
- **Repository_Port**: Interfaz que define operaciones de persistencia para el dominio Clinic
- **Data_Integrity**: Garantía de que los datos permanecen consistentes, válidos y sin corrupción durante todo su ciclo de vida

## Requirements

### Requirement 1: Validación de Datos de Entrada

**User Story:** Como desarrollador del sistema, quiero que todos los datos de entrada sean validados exhaustivamente, para que no se permita la creación o actualización de clínicas con datos inválidos o corruptos

#### Acceptance Criteria

1. WHEN THE Clinic_Domain recibe un CreateClinicCommand con clinicName vacío o nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
2. WHEN THE Clinic_Domain recibe un CreateClinicCommand con clinicName que excede 100 caracteres, THEN THE Command_Validators SHALL rechazar el comando indicando el límite excedido
3. WHEN THE Clinic_Domain recibe un CreateClinicCommand con legalName vacío o nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
4. WHEN THE Clinic_Domain recibe un CreateClinicCommand con legalName que excede 150 caracteres, THEN THE Command_Validators SHALL rechazar el comando indicando el límite excedido
5. WHEN THE Clinic_Domain recibe un CreateClinicCommand con legalNumber vacío o nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
6. WHEN THE Clinic_Domain recibe un CreateClinicCommand con legalNumber que excede 50 caracteres, THEN THE Command_Validators SHALL rechazar el comando indicando el límite excedido
7. WHEN THE Clinic_Domain recibe un CreateClinicCommand con Address nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
8. WHEN THE Clinic_Domain recibe un CreateClinicCommand con Phone nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
9. WHEN THE Clinic_Domain recibe un CreateClinicCommand con Email nulo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
10. WHEN THE Clinic_Domain recibe un CreateClinicCommand con logoUrl que excede 255 caracteres, THEN THE Command_Validators SHALL rechazar el comando indicando el límite excedido
11. WHEN THE Clinic_Domain recibe un CreateClinicCommand con Value Objects válidos creados desde el Controller, THEN THE Command_Validators SHALL aceptar el comando sin errores adicionales

### Requirement 2: Prevención de Duplicados

**User Story:** Como administrador del sistema, quiero que el sistema prevenga la creación de clínicas duplicadas, para que no existan múltiples registros con el mismo email o número legal

#### Acceptance Criteria

1. WHEN THE Clinic_Domain recibe un CreateClinicCommand con un email que ya existe en el sistema, THEN THE Clinic_Domain SHALL lanzar ClinicAlreadyExistsException con el campo "email"
2. WHEN THE Clinic_Domain recibe un CreateClinicCommand con un legalNumber que ya existe en el sistema, THEN THE Clinic_Domain SHALL lanzar ClinicAlreadyExistsException con el campo "legalNumber"
3. WHEN THE Clinic_Domain recibe un UpdateClinicCommand con un email que pertenece a otra clínica, THEN THE Clinic_Domain SHALL lanzar ClinicAlreadyExistsException con el campo "email"
4. WHEN THE Clinic_Domain recibe un UpdateClinicCommand con el mismo email de la clínica actual, THEN THE Clinic_Domain SHALL permitir la actualización sin error
5. WHEN THE Repository_Port verifica existencia de email, THEN THE Repository_Port SHALL retornar true solo si el email existe en otra clínica

### Requirement 3: Gestión de Domain Events

**User Story:** Como desarrollador del sistema, quiero que todos los cambios significativos en el dominio Clinic generen eventos de dominio, para que otros componentes puedan reaccionar a estos cambios

#### Acceptance Criteria

1. WHEN THE Clinic_Domain crea una nueva clínica, THEN THE Clinic_Domain SHALL generar un ClinicCreatedEvent con clinicId, clinicName y legalName
2. WHEN THE Clinic_Domain actualiza una clínica existente, THEN THE Clinic_Domain SHALL generar un ClinicUpdatedEvent con clinicId y clinicName actualizado
3. WHEN THE Clinic_Domain elimina una clínica, THEN THE Clinic_Domain SHALL generar un ClinicDeletedEvent con clinicId y clinicName
4. WHEN THE Clinic_Domain publica Domain_Events, THEN THE Domain_Events SHALL ser publicados antes de persistir los cambios en la base de datos
5. WHEN THE Clinic_Domain publica Domain_Events, THEN THE Clinic_Domain SHALL limpiar la lista de eventos después de publicarlos

### Requirement 4: Manejo de Excepciones

**User Story:** Como desarrollador del sistema, quiero que todas las operaciones del dominio Clinic manejen excepciones de forma consistente, para que los errores sean predecibles y manejables

#### Acceptance Criteria

1. WHEN THE Clinic_Domain busca una clínica por ID que no existe, THEN THE Clinic_Domain SHALL lanzar ClinicNotFoundException con el ID específico
2. WHEN THE Clinic_Domain actualiza una clínica que no existe, THEN THE Clinic_Domain SHALL lanzar ClinicNotFoundException antes de intentar la actualización
3. WHEN THE Clinic_Domain elimina una clínica que no existe, THEN THE Clinic_Domain SHALL lanzar ClinicNotFoundException antes de intentar la eliminación
4. WHEN THE Command_Validators detecta errores de validación, THEN THE Command_Validators SHALL lanzar ClinicValidationException con todos los errores encontrados
5. WHEN THE ClinicValidationException es lanzada, THEN THE ClinicValidationException SHALL contener un ValidationResult con detalles de cada campo inválido

### Requirement 5: Integridad de Persistencia

**User Story:** Como desarrollador del sistema, quiero que la capa de persistencia mantenga la integridad de los datos, para que no haya pérdida o corrupción de información durante operaciones CRUD

#### Acceptance Criteria

1. WHEN THE Repository_Port guarda una nueva clínica, THEN THE Repository_Port SHALL retornar la clínica con un ID generado automáticamente
2. WHEN THE Repository_Port guarda una clínica con Value Objects (Address, Email, Phone), THEN THE Repository_Port SHALL persistir correctamente usando los converters del dominio Shared
3. WHEN THE Repository_Port recupera una clínica, THEN THE Repository_Port SHALL reconstruir correctamente todos los Value Objects desde la base de datos sin pérdida de información
4. WHEN THE Repository_Port actualiza una clínica, THEN THE Repository_Port SHALL sobrescribir todos los campos con los nuevos valores sin pérdida de datos
5. WHEN THE Repository_Port elimina una clínica, THEN THE Repository_Port SHALL remover completamente el registro de la base de datos
6. WHEN THE Repository_Port persiste y recupera Value Objects, THEN THE Value_Objects SHALL mantener su formato y validez original (validación delegada a Shared)

### Requirement 6: Validación de Actualización de Datos

**User Story:** Como desarrollador del sistema, quiero que las actualizaciones de clínicas validen todos los campos igual que en la creación, para que no se puedan introducir datos inválidos mediante actualizaciones

#### Acceptance Criteria

1. WHEN THE Clinic_Domain recibe un UpdateClinicCommand con clinicId nulo o negativo, THEN THE Command_Validators SHALL rechazar el comando con un mensaje de error específico
2. WHEN THE Clinic_Domain recibe un UpdateClinicCommand con campos inválidos, THEN THE Command_Validators SHALL aplicar las mismas reglas de validación que CreateClinicCommand
3. WHEN THE Clinic_Domain recibe un UpdateClinicCommand con subscriptionStatus que excede 50 caracteres, THEN THE Command_Validators SHALL rechazar el comando indicando el límite excedido
4. WHEN THE Clinic_Domain actualiza una clínica, THEN THE Clinic_Domain SHALL actualizar el campo updatedAt con la fecha y hora actual
5. WHEN THE Clinic_Domain actualiza una clínica, THEN THE Clinic_Domain SHALL mantener el campo createdAt sin modificaciones

### Requirement 7: Conversión entre Capas

**User Story:** Como desarrollador del sistema, quiero que las conversiones entre DTOs, Commands, Domain Models y Entities sean correctas y completas, para que no haya pérdida de información entre capas

#### Acceptance Criteria

1. WHEN THE Clinic_Domain convierte un CreateClinicRequest a CreateClinicCommand, THEN THE Clinic_Domain SHALL crear correctamente los Value Objects (Address, Email, Phone) desde los campos del request
2. WHEN THE Clinic_Domain convierte un Clinic domain model a ClinicResponse, THEN THE Clinic_Domain SHALL extraer correctamente los valores de los Value Objects
3. WHEN THE Repository_Port convierte un Clinic domain model a ClinicEntity, THEN THE Repository_Port SHALL mapear correctamente todos los campos incluyendo Value Objects
4. WHEN THE Repository_Port convierte un ClinicEntity a Clinic domain model, THEN THE Repository_Port SHALL reconstruir correctamente todos los Value Objects
5. WHEN THE Clinic_Domain realiza conversiones entre capas, THEN THE Clinic_Domain SHALL preservar todos los datos sin pérdida de información

### Requirement 8: Operaciones de Consulta

**User Story:** Como usuario del sistema, quiero poder consultar clínicas por ID o listar todas las clínicas, para que pueda acceder a la información de las clínicas registradas

#### Acceptance Criteria

1. WHEN THE Clinic_Domain busca una clínica por ID válido, THEN THE Clinic_Domain SHALL retornar la clínica con todos sus datos completos
2. WHEN THE Clinic_Domain lista todas las clínicas, THEN THE Clinic_Domain SHALL retornar una lista con todas las clínicas registradas en el sistema
3. WHEN THE Clinic_Domain lista todas las clínicas y no hay clínicas registradas, THEN THE Clinic_Domain SHALL retornar una lista vacía sin lanzar excepciones
4. WHEN THE Repository_Port ejecuta consultas, THEN THE Repository_Port SHALL retornar datos consistentes con el estado actual de la base de datos
5. WHEN THE Clinic_Domain retorna clínicas, THEN THE Clinic_Domain SHALL incluir todos los Value Objects correctamente inicializados

### Requirement 9: Integración End-to-End

**User Story:** Como desarrollador del sistema, quiero que las operaciones completas desde el controller hasta la base de datos funcionen correctamente, para que el sistema sea confiable en producción

#### Acceptance Criteria

1. WHEN THE Clinic_Domain recibe una petición HTTP POST válida para crear una clínica, THEN THE Clinic_Domain SHALL crear la clínica y retornar HTTP 201 con los datos de la clínica creada
2. WHEN THE Clinic_Domain recibe una petición HTTP PUT válida para actualizar una clínica, THEN THE Clinic_Domain SHALL actualizar la clínica y retornar HTTP 200 con los datos actualizados
3. WHEN THE Clinic_Domain recibe una petición HTTP GET para obtener una clínica por ID, THEN THE Clinic_Domain SHALL retornar HTTP 200 con los datos de la clínica
4. WHEN THE Clinic_Domain recibe una petición HTTP DELETE para eliminar una clínica, THEN THE Clinic_Domain SHALL eliminar la clínica y retornar HTTP 204
5. WHEN THE Clinic_Domain recibe una petición HTTP con datos inválidos, THEN THE Clinic_Domain SHALL retornar HTTP 400 con detalles de los errores de validación
6. WHEN THE Clinic_Domain recibe una petición HTTP para una clínica que no existe, THEN THE Clinic_Domain SHALL retornar HTTP 404 con mensaje de error apropiado
7. WHEN THE Clinic_Domain recibe una petición HTTP que viola restricciones de unicidad, THEN THE Clinic_Domain SHALL retornar HTTP 409 con mensaje de error apropiado
