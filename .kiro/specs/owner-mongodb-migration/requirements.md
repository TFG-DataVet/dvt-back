# Requirements Document

## Introduction

Esta especificación define la migración del módulo Owner de PostgreSQL/JPA a MongoDB. La migración incluye la actualización de dependencias, eliminación de referencias a PostgreSQL, conversión de entidades JPA a documentos MongoDB, y adaptación de repositorios para usar Spring Data MongoDB. El módulo Clinic permanecerá sin cambios en esta fase.

## Glossary

- **Owner Module**: Módulo del sistema que gestiona la información de propietarios de mascotas
- **OwnerEntity**: Clase de persistencia actual que usa JPA para PostgreSQL
- **OwnerDocument**: Nueva clase de persistencia que usará MongoDB
- **MongoDB Repository**: Repositorio de Spring Data MongoDB que reemplazará JPA Repository
- **Value Object Converters**: Convertidores para Address, Email y Phone que deben adaptarse a MongoDB
- **Application Layer**: Capa de aplicación que no debe verse afectada por cambios de persistencia
- **Domain Layer**: Capa de dominio que permanece independiente de la infraestructura

## Requirements

### Requirement 1

**User Story:** Como desarrollador, quiero actualizar las dependencias del proyecto para soportar MongoDB, de manera que pueda usar Spring Data MongoDB en lugar de JPA.

#### Acceptance Criteria

1. WHEN se actualiza el archivo pom.xml, THE System SHALL incluir la dependencia spring-boot-starter-data-mongodb
2. WHEN se actualiza el archivo pom.xml, THE System SHALL eliminar la dependencia postgresql
3. WHEN se actualiza el archivo pom.xml, THE System SHALL mantener spring-boot-starter-validation para validaciones
4. WHEN se actualiza el archivo pom.xml, THE System SHALL mantener las dependencias de testing existentes
5. WHERE se requiere testing con base de datos embebida, THE System SHALL incluir de-flapdoodle-embed-mongo para tests

### Requirement 2

**User Story:** Como desarrollador, quiero eliminar todas las referencias a PostgreSQL en la configuración, de manera que el sistema use exclusivamente MongoDB.

#### Acceptance Criteria

1. WHEN se actualiza application.properties, THE System SHALL eliminar todas las propiedades spring.datasource relacionadas con PostgreSQL
2. WHEN se actualiza application.properties, THE System SHALL eliminar la propiedad spring.jpa.hibernate.ddl-auto
3. WHEN se actualiza application.properties, THE System SHALL agregar spring.data.mongodb.uri con la configuración de conexión a MongoDB
4. WHEN se actualiza application.properties, THE System SHALL agregar spring.data.mongodb.database con el nombre de la base de datos
5. IF existen archivos de configuración de test, THEN THE System SHALL actualizar application-test.properties con configuración de MongoDB embebido

### Requirement 3

**User Story:** Como desarrollador, quiero convertir OwnerEntity de una entidad JPA a un documento MongoDB, de manera que pueda persistir datos en MongoDB.

#### Acceptance Criteria

1. WHEN se refactoriza OwnerEntity, THE System SHALL renombrar la clase a OwnerDocument
2. WHEN se refactoriza OwnerDocument, THE System SHALL reemplazar @Entity con @Document(collection = "owners")
3. WHEN se refactoriza OwnerDocument, THE System SHALL reemplazar @Id @GeneratedValue con @Id de tipo String para MongoDB ObjectId
4. WHEN se refactoriza OwnerDocument, THE System SHALL eliminar @Table, @Column y anotaciones JPA
5. WHEN se refactoriza OwnerDocument, THE System SHALL usar @Field para mapear nombres de campos cuando sea necesario
6. WHEN se refactoriza OwnerDocument, THE System SHALL mantener los campos clinicID, firstName, lastName, dni, phone, email y address
7. WHEN se refactoriza OwnerDocument, THE System SHALL eliminar la herencia de BaseEntity si esta es específica de JPA

### Requirement 4

**User Story:** Como desarrollador, quiero adaptar los convertidores de Value Objects para MongoDB, de manera que Address, Email y Phone se persistan correctamente.

#### Acceptance Criteria

1. WHEN se usan Value Objects en OwnerDocument, THE System SHALL permitir que MongoDB serialice automáticamente los Value Objects
2. IF se requieren convertidores personalizados, THEN THE System SHALL crear convertidores que implementen Converter de Spring Data MongoDB
3. WHEN se configuran convertidores, THE System SHALL registrarlos en una clase de configuración MongoDB
4. WHEN se persisten Value Objects, THE System SHALL mantener la estructura de datos y validaciones existentes

### Requirement 5

**User Story:** Como desarrollador, quiero migrar JpaOwnerRepositoryAdapter a un repositorio MongoDB, de manera que las operaciones de persistencia funcionen con MongoDB.

#### Acceptance Criteria

1. WHEN se refactoriza el repositorio, THE System SHALL renombrar JpaOwnerRepositoryAdapter a MongoOwnerRepositoryAdapter
2. WHEN se refactoriza el repositorio, THE System SHALL extender MongoRepository en lugar de JpaRepository
3. WHEN se refactoriza el repositorio, THE System SHALL cambiar el tipo de ID de Long a String
4. WHEN se refactoriza el repositorio, THE System SHALL mantener los métodos de consulta existsByEmail, existsByDni, existsByEmailAndDni, existsByPhone, existsByDniAndOwnerIDNot
5. WHEN se usan métodos de consulta, THE System SHALL usar la nomenclatura de query methods de Spring Data MongoDB
6. WHEN se actualiza el repositorio, THE System SHALL actualizar el campo ownerID a tipo String en las consultas

### Requirement 6

**User Story:** Como desarrollador, quiero actualizar el adaptador de salida del módulo Owner, de manera que use el nuevo repositorio MongoDB.

#### Acceptance Criteria

1. WHEN se actualiza el adaptador de salida, THE System SHALL inyectar MongoOwnerRepositoryAdapter en lugar de JpaOwnerRepositoryAdapter
2. WHEN se actualiza el adaptador de salida, THE System SHALL usar OwnerDocument en lugar de OwnerEntity
3. WHEN se actualiza el adaptador de salida, THE System SHALL mantener la lógica de mapeo entre Owner (dominio) y OwnerDocument
4. WHEN se actualiza el adaptador de salida, THE System SHALL manejar IDs de tipo String en lugar de Long
5. WHEN se persisten datos, THE System SHALL permitir que MongoDB genere automáticamente los ObjectId

### Requirement 7

**User Story:** Como desarrollador, quiero actualizar el modelo de dominio Owner si es necesario, de manera que sea compatible con IDs de tipo String.

#### Acceptance Criteria

1. WHEN se revisa el modelo de dominio Owner, THE System SHALL cambiar el tipo de ownerID de Long a String si está presente
2. WHEN se actualizan comandos y DTOs, THE System SHALL cambiar referencias de Long a String para IDs
3. WHEN se actualiza la capa de aplicación, THE System SHALL mantener toda la lógica de negocio sin cambios
4. WHEN se actualizan mappers, THE System SHALL adaptar conversiones de ID de Long a String

### Requirement 8

**User Story:** Como desarrollador, quiero actualizar los tests del módulo Owner, de manera que funcionen con MongoDB embebido.

#### Acceptance Criteria

1. WHEN se ejecutan tests de integración, THE System SHALL usar MongoDB embebido (Flapdoodle)
2. WHEN se configuran tests, THE System SHALL usar @DataMongoTest para tests de repositorio
3. WHEN se actualizan tests, THE System SHALL cambiar referencias de OwnerEntity a OwnerDocument
4. WHEN se actualizan tests, THE System SHALL cambiar tipos de ID de Long a String
5. WHEN se ejecutan tests, THE System SHALL limpiar la base de datos entre tests

### Requirement 9

**User Story:** Como desarrollador, quiero asegurar que el módulo Clinic no se vea afectado, de manera que solo Owner use MongoDB en esta fase.

#### Acceptance Criteria

1. WHEN se realizan cambios, THE System SHALL mantener todas las clases del módulo Clinic sin modificaciones
2. WHEN se actualiza BaseEntity, THE System SHALL verificar que Clinic no dependa de cambios específicos de MongoDB
3. WHEN se ejecuta la aplicación, THE System SHALL permitir que Clinic continúe usando JPA/PostgreSQL
4. IF hay clases compartidas afectadas, THEN THE System SHALL crear versiones específicas para Owner sin afectar Clinic
