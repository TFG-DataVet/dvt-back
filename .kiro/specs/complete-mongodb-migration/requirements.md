# Requirements Document

## Introduction

Esta especificación define la finalización de la migración completa del proyecto DataVet de PostgreSQL/JPA a MongoDB. Los módulos Owner, Clinic y Shared ya han sido migrados a usar MongoDB Documents y MongoRepository. Esta fase final se enfoca en eliminar completamente las dependencias de PostgreSQL/JPA y asegurar que todos los tests del proyecto funcionen correctamente con MongoDB.

## Glossary

- **DataVet System**: Sistema de gestión veterinaria que incluye módulos Owner, Clinic y Shared
- **MongoDB**: Base de datos NoSQL utilizada para persistencia de datos
- **PostgreSQL**: Base de datos relacional que será completamente eliminada del proyecto
- **JPA**: Java Persistence API que será eliminada ya que no se usa con MongoDB
- **Flapdoodle**: MongoDB embebido utilizado para tests
- **Test Suite**: Conjunto completo de tests unitarios e integración del proyecto
- **application.properties**: Archivo de configuración principal de Spring Boot
- **pom.xml**: Archivo de configuración de dependencias Maven

## Requirements

### Requirement 1

**User Story:** Como desarrollador, quiero eliminar completamente las dependencias de PostgreSQL y JPA del proyecto, de manera que el sistema use exclusivamente MongoDB.

#### Acceptance Criteria

1. WHEN se actualiza el archivo pom.xml, THE System SHALL eliminar la dependencia spring-boot-starter-data-jpa
2. WHEN se actualiza el archivo pom.xml, THE System SHALL eliminar la dependencia postgresql
3. WHEN se actualiza el archivo pom.xml, THE System SHALL mantener únicamente spring-boot-starter-data-mongodb
4. WHEN se actualiza el archivo pom.xml, THE System SHALL mantener de-flapdoodle-embed-mongo para tests
5. WHEN se actualiza application.properties, THE System SHALL eliminar todas las propiedades spring.datasource relacionadas con PostgreSQL
6. WHEN se actualiza application.properties, THE System SHALL eliminar la propiedad spring.jpa.hibernate.ddl-auto
7. WHEN se actualiza application.properties, THE System SHALL mantener únicamente la configuración de MongoDB

### Requirement 2

**User Story:** Como desarrollador, quiero verificar que todos los tests unitarios del proyecto funcionen correctamente con MongoDB, de manera que pueda confiar en la suite de tests.

#### Acceptance Criteria

1. WHEN se ejecutan tests unitarios del módulo Owner, THE System SHALL ejecutar todos los tests sin errores
2. WHEN se ejecutan tests unitarios del módulo Clinic, THE System SHALL ejecutar todos los tests sin errores
3. WHEN se ejecutan tests unitarios del módulo Shared, THE System SHALL ejecutar todos los tests sin errores
4. WHEN se ejecutan tests de validación de comandos, THE System SHALL validar correctamente usando String IDs
5. WHEN se ejecutan tests de servicios, THE System SHALL usar mocks correctamente con String IDs
6. WHEN se ejecutan tests de mappers, THE System SHALL convertir correctamente entre domain y DTOs con String IDs

### Requirement 3

**User Story:** Como desarrollador, quiero verificar que todos los tests de integración del proyecto funcionen correctamente con MongoDB embebido, de manera que pueda validar la integración completa del sistema.

#### Acceptance Criteria

1. WHEN se ejecutan tests de repositorio, THE System SHALL usar @DataMongoTest para configuración de MongoDB
2. WHEN se ejecutan tests de repositorio, THE System SHALL limpiar la base de datos entre tests
3. WHEN se ejecutan tests de controladores, THE System SHALL usar @SpringBootTest con MongoDB embebido
4. WHEN se ejecutan tests de integración, THE System SHALL generar IDs válidos usando ObjectId
5. WHEN se ejecutan tests de integración, THE System SHALL verificar operaciones CRUD completas
6. WHEN se ejecutan tests de arquitectura, THE System SHALL validar las capas sin referencias a JPA

### Requirement 4

**User Story:** Como desarrollador, quiero verificar que los tests de Value Objects funcionen correctamente con MongoDB, de manera que la serialización y deserialización sea correcta.

#### Acceptance Criteria

1. WHEN se ejecutan tests de Address, THE System SHALL serializar y deserializar correctamente en MongoDB
2. WHEN se ejecutan tests de Email, THE System SHALL serializar y deserializar correctamente en MongoDB
3. WHEN se ejecutan tests de Phone, THE System SHALL serializar y deserializar correctamente en MongoDB
4. WHEN se ejecutan tests de conversión, THE System SHALL validar que los convertidores MongoDB funcionen correctamente

### Requirement 5

**User Story:** Como desarrollador, quiero verificar que los TestDataBuilders generen datos válidos para MongoDB, de manera que los tests usen datos consistentes.

#### Acceptance Criteria

1. WHEN se usa OwnerTestDataBuilder, THE System SHALL generar IDs válidos usando ObjectId
2. WHEN se usa ClinicTestDataBuilder, THE System SHALL generar IDs válidos usando ObjectId
3. WHEN se generan datos de prueba, THE System SHALL crear Value Objects válidos
4. WHEN se generan datos de prueba, THE System SHALL crear documentos MongoDB válidos

### Requirement 6

**User Story:** Como desarrollador, quiero ejecutar la suite completa de tests del proyecto, de manera que pueda confirmar que la migración a MongoDB está completa y funcional.

#### Acceptance Criteria

1. WHEN se ejecuta mvn clean test, THE System SHALL compilar el proyecto sin errores
2. WHEN se ejecuta mvn clean test, THE System SHALL ejecutar todos los tests sin fallos
3. WHEN se ejecuta mvn clean test, THE System SHALL mostrar un reporte de cobertura de tests
4. WHEN se ejecuta la aplicación, THE System SHALL conectarse exitosamente a MongoDB
5. WHEN se ejecuta la aplicación, THE System SHALL crear índices automáticamente en MongoDB

### Requirement 7

**User Story:** Como desarrollador, quiero eliminar los convertidores JPA que ya no son necesarios con MongoDB, de manera que el código esté limpio y sin dependencias obsoletas.

#### Acceptance Criteria

1. WHEN se eliminan convertidores JPA, THE System SHALL eliminar AddressConverter de shared.infrastructure.util.converter
2. WHEN se eliminan convertidores JPA, THE System SHALL eliminar EmailConverter de shared.infrastructure.util.converter
3. WHEN se eliminan convertidores JPA, THE System SHALL eliminar PhoneConverter de shared.infrastructure.util.converter
4. WHEN se eliminan tests obsoletos, THE System SHALL eliminar SharedConverterAutoDiscoveryTest
5. WHEN se usan Value Objects con MongoDB, THE System SHALL serializar automáticamente sin convertidores personalizados

### Requirement 8

**User Story:** Como desarrollador, quiero verificar que no existan referencias residuales a JPA o PostgreSQL en el código, de manera que el proyecto esté completamente limpio.

#### Acceptance Criteria

1. WHEN se buscan imports de JPA, THE System SHALL no encontrar ninguna referencia en código de producción
2. WHEN se buscan anotaciones JPA, THE System SHALL no encontrar @Entity, @Table, @Column en código de producción
3. WHEN se buscan referencias a PostgreSQL, THE System SHALL no encontrar ninguna en código de producción
4. WHEN se revisa la configuración, THE System SHALL contener únicamente configuración de MongoDB
5. WHEN se revisan los tests, THE System SHALL usar únicamente anotaciones de MongoDB para tests de persistencia
