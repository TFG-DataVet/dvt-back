# Guía para Crear un Nuevo Dominio - Ejemplo: Dueño (Owner)

## Tabla de Contenidos

1. [Introducción y Conceptos](#1-introducción-y-conceptos)
   - [¿Qué es un Dominio?](#qué-es-un-dominio)
   - [Arquitectura Hexagonal Explicada](#arquitectura-hexagonal-explicada)
   - [Glosario de Términos](#glosario-de-términos)

2. [Preparación y Prerrequisitos](#2-preparación-y-prerrequisitos)
   - [Herramientas Necesarias](#herramientas-necesarias)
   - [Estructura del Proyecto](#estructura-del-proyecto)
   - [Navegando el Código Existente](#navegando-el-código-existente)

3. [Capa de Dominio (Domain Layer)](#3-capa-de-dominio-domain-layer)
   - [Modelo de Dominio](#modelo-de-dominio)
   - [Eventos de Dominio](#eventos-de-dominio)
   - [Excepciones de Dominio](#excepciones-de-dominio)

4. [Capa de Aplicación (Application Layer)](#4-capa-de-aplicación-application-layer)
   - [Puertos y Casos de Uso](#puertos-y-casos-de-uso)
   - [Comandos y Validaciones](#comandos-y-validaciones)
   - [Servicios de Aplicación](#servicios-de-aplicación)
   - [DTOs y Mappers](#dtos-y-mappers)

5. [Capa de Infraestructura (Infrastructure Layer)](#5-capa-de-infraestructura-infrastructure-layer)
   - [Controladores REST](#controladores-rest)
   - [Persistencia JPA](#persistencia-jpa)
   - [DTOs de Request](#dtos-de-request)

6. [Integración y Configuración](#6-integración-y-configuración)
   - [Configuración Spring](#configuración-spring)
   - [Migraciones de Base de Datos](#migraciones-de-base-de-datos)

7. [Testing y Validación](#7-testing-y-validación)
   - [Tests Unitarios](#tests-unitarios)
   - [Tests de Integración](#tests-de-integración)
   - [Herramientas de Testing](#herramientas-de-testing)

8. [Mejores Prácticas y Troubleshooting](#8-mejores-prácticas-y-troubleshooting)
   - [Patrones de Diseño](#patrones-de-diseño)
   - [Convenciones de Naming](#convenciones-de-naming)
   - [Solución de Problemas Comunes](#solución-de-problemas-comunes)

9. [Checklists y Verificación](#9-checklists-y-verificación)
   - [Checklist por Capa](#checklist-por-capa)
   - [Comandos de Verificación](#comandos-de-verificación)

---

## 1. Introducción y Conceptos

### ¿Qué es un Dominio?

Un **dominio** en nuestro proyecto representa una área específica del negocio veterinario. Por ejemplo:
- **Clínica**: Información sobre las clínicas veterinarias
- **Dueño**: Información sobre los propietarios de las mascotas
- **Mascota**: Información sobre los animales
- **Cita**: Información sobre las consultas veterinarias

Cada dominio es independiente pero puede comunicarse con otros a través de interfaces bien definidas.

### Arquitectura Hexagonal Explicada

Nuestro proyecto usa la **Arquitectura Hexagonal** (también conocida como Puertos y Adaptadores). Esta arquitectura nos ayuda a crear código más limpio, testeable y mantenible.

#### ¿Cómo funciona?

Imagina que cada dominio es como una casa con tres pisos, donde el piso más importante (el dominio) está en el centro y protegido:

```
                    ┌─────────────────────────────────────┐
                    │         INFRASTRUCTURE              │
                    │                                     │
    ┌─────────────┐ │  ┌─────────────┐ ┌─────────────┐   │ ┌─────────────┐
    │   REST API  │◄┼─►│ Controller  │ │ Repository  │◄──┼─┤  Database   │
    │   Client    │ │  │             │ │  Adapter    │   │ │             │
    └─────────────┘ │  └─────────────┘ └─────────────┘   │ └─────────────┘
                    │         ▲               ▲           │
                    └─────────┼───────────────┼───────────┘
                              │               │
                    ┌─────────┼───────────────┼───────────┐
                    │         ▼               ▼           │
                    │         APPLICATION                 │
                    │                                     │
                    │  ┌─────────────┐ ┌─────────────┐   │
                    │  │   Service   │ │ Validation  │   │
                    │  │             │ │             │   │
                    │  └─────────────┘ └─────────────┘   │
                    │         ▲               ▲           │
                    └─────────┼───────────────┼───────────┘
                              │               │
                    ┌─────────┼───────────────┼───────────┐
                    │         ▼               ▼           │
                    │            DOMAIN                   │
                    │     (Reglas de Negocio)             │
                    │                                     │
                    │  ┌─────────────┐ ┌─────────────┐   │
                    │  │    Model    │ │   Events    │   │
                    │  │   (Owner)   │ │             │   │
                    │  └─────────────┘ └─────────────┘   │
                    └─────────────────────────────────────┘
```

#### Las Tres Capas Explicadas

**1. 🏛️ DOMAIN (Dominio) - El Corazón**
- **¿Qué es?**: Las reglas de negocio más importantes
- **¿Qué contiene?**: Modelos (Owner), eventos, excepciones
- **¿Por qué es importante?**: No depende de nada externo, es puro negocio
- **Ejemplo**: "Un dueño debe tener un email válido"

**2. 🔧 APPLICATION (Aplicación) - El Coordinador**
- **¿Qué es?**: Orquesta las operaciones del dominio
- **¿Qué contiene?**: Servicios, comandos, validadores, puertos
- **¿Por qué es importante?**: Conecta el dominio con el mundo exterior
- **Ejemplo**: "Cuando creo un dueño, valido los datos y publico un evento"

**3. 🌐 INFRASTRUCTURE (Infraestructura) - Las Conexiones**
- **¿Qué es?**: Todo lo que conecta con sistemas externos
- **¿Qué contiene?**: Controladores REST, repositorios JPA, base de datos
- **¿Por qué es importante?**: Permite que la aplicación funcione en el mundo real
- **Ejemplo**: "Guardo el dueño en PostgreSQL y expongo una API REST"

#### Flujo de Datos - Un Ejemplo Práctico

Cuando un cliente quiere crear un nuevo dueño, esto es lo que pasa:

```
1. Cliente → POST /owners (REST API)
2. OwnerController → recibe CreateOwnerRequest
3. OwnerController → convierte a CreateOwnerCommand
4. OwnerService → valida el comando
5. Owner.create() → crea el modelo de dominio
6. OwnerCreatedEvent → se publica automáticamente
7. OwnerRepository → guarda en base de datos
8. OwnerResponse → se devuelve al cliente
```

#### ¿Por qué esta estructura?

**✅ Ventajas:**
- **Separación clara**: Cada capa tiene una responsabilidad específica
- **Fácil testing**: Puedes probar cada capa por separado
- **Flexibilidad**: Puedes cambiar la base de datos sin afectar las reglas de negocio
- **Mantenibilidad**: El código está organizado y es fácil de entender
- **Reutilización**: Los componentes compartidos se usan en todos los dominios

**🎯 Principios clave:**
- **Dependencias hacia adentro**: Las capas externas dependen de las internas, nunca al revés
- **Puertos y Adaptadores**: Interfaces definen qué se puede hacer, implementaciones definen cómo
- **Inversión de dependencias**: El dominio no conoce la infraestructura

#### Comparación con Arquitecturas Tradicionales

**❌ Arquitectura en Capas Tradicional:**
```
Controller → Service → Repository → Database
     ↓         ↓          ↓
   Difícil de testear y cambiar
```

**✅ Arquitectura Hexagonal:**
```
    Infrastructure
         ↓
    Application ←→ Ports
         ↓
      Domain
```
- Cada capa se puede testear independientemente
- Fácil cambiar implementaciones (ej: de MySQL a PostgreSQL)
- El dominio está protegido de cambios externos

#### ¿Cuándo usar cada capa?

**🏛️ Usa DOMAIN cuando:**
- Defines reglas de negocio ("Un dueño debe tener email válido")
- Creas modelos principales (`Owner`, `Pet`)
- Manejas eventos importantes (`OwnerCreated`)

**🔧 Usa APPLICATION cuando:**
- Coordinas operaciones (`OwnerService`)
- Validas comandos (`CreateOwnerCommandValidator`)
- Defines qué puede hacer el sistema (`OwnerUseCase`)

**🌐 Usa INFRASTRUCTURE cuando:**
- Conectas con base de datos (`OwnerEntity`, `JpaOwnerRepository`)
- Expones APIs REST (`OwnerController`)
- Integras con sistemas externos

### Glosario de Términos

#### 🏗️ Conceptos de Arquitectura

| Término | Definición | Ejemplo en el Proyecto |
|---------|------------|------------------------|
| **Dominio** | Área específica del negocio veterinario | `clinic`, `owner`, `pet` |
| **Agregado** | La entidad principal que agrupa información relacionada | `Owner` (agrupa nombre, email, teléfono) |
| **Value Object** | Objetos inmutables que representan valores | `Email`, `Phone`, `Address` |
| **Entity** | Objeto con identidad única que puede cambiar | `Owner` con ID único |

#### 🔌 Puertos y Adaptadores

| Término | Definición | Ejemplo en el Proyecto |
|---------|------------|------------------------|
| **Puerto (Port)** | Interface que define qué operaciones se pueden hacer | `OwnerUseCase` (crear, buscar, actualizar) |
| **Adaptador (Adapter)** | Implementación concreta de un puerto | `OwnerService` implementa `OwnerUseCase` |
| **Puerto de Entrada** | Interface para operaciones que vienen del exterior | `OwnerUseCase` (desde REST API) |
| **Puerto de Salida** | Interface para operaciones hacia sistemas externos | `OwnerRepositoryPort` (hacia base de datos) |

#### 📦 Objetos de Transferencia

| Término | Definición | Ejemplo en el Proyecto |
|---------|------------|------------------------|
| **Comando** | Objeto inmutable con datos para realizar una acción | `CreateOwnerCommand` |
| **DTO** | Objeto para transferir datos entre capas | `OwnerResponse`, `CreateOwnerRequest` |
| **Request DTO** | DTO que recibe datos del cliente | `CreateOwnerRequest` (desde REST API) |
| **Response DTO** | DTO que envía datos al cliente | `OwnerResponse` (hacia REST API) |

#### 🎯 Patrones de Dominio

| Término | Definición | Ejemplo en el Proyecto |
|---------|------------|------------------------|
| **Evento de Dominio** | Notificación de que algo importante ocurrió | `OwnerCreatedEvent` |
| **Factory Method** | Método estático para crear objetos | `Owner.create()` |
| **Repository** | Patrón para acceso a datos | `OwnerRepositoryPort` |
| **Mapper** | Convierte objetos entre diferentes capas | `OwnerMapper` |

#### 🛠️ Componentes Técnicos

| Término | Definición | Ejemplo en el Proyecto |
|---------|------------|------------------------|
| **Service** | Orquesta operaciones del dominio | `OwnerService` |
| **Controller** | Maneja requests HTTP | `OwnerController` |
| **Entity (JPA)** | Clase que se mapea a tabla de base de datos | `OwnerEntity` |
| **Validator** | Valida datos usando reglas de negocio | `CreateOwnerCommandValidator` |

#### 🔧 Herramientas y Frameworks

| Término | Definición | Uso en el Proyecto |
|---------|------------|-------------------|
| **Spring Boot** | Framework para aplicaciones Java | Base de toda la aplicación |
| **JPA** | API para persistencia en Java | Mapeo objeto-relacional |
| **Bean Validation** | Validación declarativa con anotaciones | `@NotNull`, `@Email`, etc. |
| **Maven** | Herramienta de gestión de proyectos | Compilación y dependencias |

#### 📋 Convenciones del Proyecto

| Término | Convención | Ejemplo |
|---------|------------|---------|
| **Package Naming** | `com.datavet.datavet.[dominio].[capa]` | `com.datavet.datavet.owner.domain` |
| **Class Naming** | `[Dominio][Propósito]` | `OwnerService`, `OwnerController` |
| **Table Naming** | Singular, lowercase | `owner`, `clinic` |
| **Column Naming** | Snake_case | `first_name`, `email_address` |

#### 🎨 Anotaciones Importantes

| Anotación | Propósito | Dónde se usa |
|-----------|-----------|--------------|
| `@Service` | Marca un servicio de aplicación | `OwnerService` |
| `@RestController` | Marca un controlador REST | `OwnerController` |
| `@Entity` | Marca una entidad JPA | `OwnerEntity` |
| `@Component` | Marca un componente Spring | Validadores, mappers |
| `@Valid` | Activa validación de Bean Validation | Parámetros de métodos |

---

## 2. Preparación y Prerrequisitos

### Herramientas Necesarias

#### 🛠️ Software Requerido

Antes de empezar, asegúrate de tener instalado:

| Herramienta | Versión Mínima | Propósito | Verificación |
|-------------|----------------|-----------|--------------|
| **Java JDK** | 17+ | Lenguaje de programación | `java -version` |
| **Maven** | 3.8+ | Gestión de dependencias y compilación | `mvn -version` |
| **IDE** | Cualquiera | Desarrollo de código | IntelliJ IDEA, Eclipse, VS Code |
| **Git** | 2.0+ | Control de versiones | `git --version` |

#### 🗄️ Base de Datos (Opcional para desarrollo)

El proyecto está configurado para usar **H2** en memoria por defecto, pero puedes configurar:

| Base de Datos | Uso Recomendado | Configuración |
|---------------|-----------------|---------------|
| **H2** | Desarrollo local | Ya configurada (por defecto) |
| **PostgreSQL** | Producción | Modificar `application.properties` |
| **MySQL** | Alternativa | Agregar dependencia y configurar |

#### 🧪 Herramientas de Testing

| Herramienta | Propósito | Instalación |
|-------------|-----------|-------------|
| **Postman** | Probar APIs REST | [Descargar](https://www.postman.com/downloads/) |
| **curl** | Probar APIs desde terminal | Incluido en la mayoría de sistemas |
| **HTTPie** | Alternativa moderna a curl | `pip install httpie` |

#### ✅ Verificación del Entorno

Ejecuta estos comandos para verificar que todo está listo:

```bash
# Verificar Java
java -version
# Debería mostrar: openjdk version "17.x.x" o superior

# Verificar Maven
mvn -version
# Debería mostrar: Apache Maven 3.8.x o superior

# Compilar el proyecto
mvn clean compile
# Debería compilar sin errores

# Ejecutar tests
mvn test
# Debería pasar todos los tests existentes
```

### Estructura del Proyecto

#### 📁 Organización General

El proyecto DataVet sigue una estructura modular donde cada dominio es independiente pero comparte componentes comunes:

```
datavet/
├── src/main/java/com/datavet/datavet/
│   ├── DatavetApplication.java      ← Punto de entrada de la aplicación
│   ├── shared/                      ← 🔧 Componentes reutilizables
│   │   ├── domain/                  ← Clases base para todos los dominios
│   │   ├── application/             ← Interfaces y servicios compartidos
│   │   └── infrastructure/          ← Configuración global y utilidades
│   ├── clinic/                      ← 🏥 Dominio existente (ejemplo)
│   └── owner/                       ← 👤 Tu nuevo dominio (a crear)
├── src/main/resources/
│   └── application.properties       ← Configuración de la aplicación
├── src/test/java/                   ← Tests organizados igual que src/main
├── docs/                            ← Documentación del proyecto
├── examples/                        ← Ejemplos y comparaciones
└── pom.xml                          ← Configuración de Maven
```

#### 🏗️ Estructura de un Dominio

Cada dominio sigue exactamente la misma estructura. Aquí está el patrón que debes seguir:

```
src/main/java/com/datavet/datavet/[DOMINIO]/
├── domain/                          ← 🏛️ CAPA DE DOMINIO
│   ├── model/                       ← Entidades principales del negocio
│   │   ├── [Dominio].java          ← Ej: Owner.java, Pet.java
│   │   └── package-info.java       ← Documentación del paquete
│   ├── event/                       ← Eventos que publica el dominio
│   │   ├── [Dominio]CreatedEvent.java
│   │   ├── [Dominio]UpdatedEvent.java
│   │   └── [Dominio]DeletedEvent.java
│   ├── exception/                   ← Excepciones específicas del dominio
│   │   ├── [Dominio]NotFoundException.java
│   │   ├── [Dominio]AlreadyExistsException.java
│   │   └── [Dominio]ValidationException.java
│   └── service/                     ← Servicios de dominio (si son necesarios)
├── application/                     ← 🔧 CAPA DE APLICACIÓN
│   ├── port/                        ← Interfaces (contratos)
│   │   ├── in/                      ← Puertos de entrada (lo que puede hacer)
│   │   │   ├── [Dominio]UseCase.java
│   │   │   └── command/             ← Comandos de entrada
│   │   │       ├── Create[Dominio]Command.java
│   │   │       └── Update[Dominio]Command.java
│   │   └── out/                     ← Puertos de salida (lo que necesita)
│   │       └── [Dominio]RepositoryPort.java
│   ├── service/                     ← Servicios de aplicación (orquestadores)
│   │   └── [Dominio]Service.java
│   ├── dto/                         ← Objetos de transferencia de datos
│   │   └── [Dominio]Response.java
│   ├── mapper/                      ← Conversores entre capas
│   │   └── [Dominio]Mapper.java
│   └── validation/                  ← Validadores de comandos
│       ├── Create[Dominio]CommandValidator.java
│       └── Update[Dominio]CommandValidator.java
└── infrastructure/                  ← 🌐 CAPA DE INFRAESTRUCTURA
    ├── adapter/                     ← Adaptadores (implementaciones)
    │   ├── input/                   ← Adaptadores de entrada (REST, etc.)
    │   │   ├── [Dominio]Controller.java
    │   │   └── dto/                 ← DTOs específicos de la API
    │   │       ├── Create[Dominio]Request.java
    │   │       └── Update[Dominio]Request.java
    │   └── output/                  ← Adaptadores de salida (DB, etc.)
    │       └── [Dominio]RepositoryAdapter.java
    ├── persistence/                 ← Persistencia en base de datos
    │   ├── entity/                  ← Entidades JPA
    │   │   └── [Dominio]Entity.java
    │   ├── repository/              ← Repositorios JPA
    │   │   └── Jpa[Dominio]Repository.java
    │   └── converter/               ← Convertidores JPA (si son necesarios)
    └── config/                      ← Configuración específica del dominio
```

#### 🔧 Componentes Compartidos (Shared)

El directorio `shared` contiene todo lo que pueden reutilizar los dominios:

```
src/main/java/com/datavet/datavet/shared/
├── domain/                          ← Clases base para el dominio
│   ├── model/
│   │   ├── AggregateRoot.java      ← Clase base para entidades principales
│   │   └── Entity.java             ← Clase base para todas las entidades
│   ├── event/
│   │   ├── DomainEvent.java        ← Interface para eventos
│   │   └── DomainEventPublisher.java ← Publicador de eventos
│   ├── exception/                   ← Excepciones base
│   │   ├── EntityNotFoundException.java
│   │   ├── EntityAlreadyExistsException.java
│   │   └── DomainException.java
│   ├── validation/                  ← Framework de validación
│   │   ├── ValidationResult.java
│   │   └── ValidationError.java
│   └── valueobject/                 ← Value Objects reutilizables
│       ├── Email.java              ← ✅ Reutilizar siempre
│       ├── Phone.java              ← ✅ Reutilizar siempre
│       └── Address.java            ← ✅ Reutilizar siempre
├── application/                     ← Interfaces y servicios base
│   ├── port/
│   │   ├── Repository.java         ← Interface base para repositorios
│   │   └── UseCase.java            ← Interface base para casos de uso
│   ├── service/
│   │   └── ApplicationService.java ← Clase base para servicios
│   ├── validation/
│   │   └── Validator.java          ← Interface para validadores
│   └── mapper/
│       └── Mapper.java             ← Interface base para mappers
└── infrastructure/                  ← Configuración y utilidades globales
    ├── config/
    │   ├── DatabaseConfig.java     ← Configuración de base de datos
    │   └── GlobalExceptionHandler.java ← Manejo global de errores
    ├── persistence/
    │   ├── BaseEntity.java         ← Clase base para entidades JPA
    │   └── BaseRepository.java     ← Repositorio base con operaciones comunes
    └── event/
        └── LoggingDomainEventPublisher.java ← Implementación del publicador
```

#### 📋 Convenciones de Naming

| Tipo de Archivo | Patrón | Ejemplo |
|-----------------|--------|---------|
| **Modelo de Dominio** | `[Dominio].java` | `Owner.java` |
| **Evento** | `[Dominio][Acción]Event.java` | `OwnerCreatedEvent.java` |
| **Excepción** | `[Dominio][Tipo]Exception.java` | `OwnerNotFoundException.java` |
| **Comando** | `[Acción][Dominio]Command.java` | `CreateOwnerCommand.java` |
| **UseCase** | `[Dominio]UseCase.java` | `OwnerUseCase.java` |
| **Service** | `[Dominio]Service.java` | `OwnerService.java` |
| **Controller** | `[Dominio]Controller.java` | `OwnerController.java` |
| **Entity JPA** | `[Dominio]Entity.java` | `OwnerEntity.java` |
| **Repository** | `Jpa[Dominio]Repository.java` | `JpaOwnerRepository.java` |

### Navegando el Código Existente

#### 🏥 Estudiando el Dominio Clínica

El dominio **Clínica** ya está completamente implementado y es tu mejor referencia. Aquí te muestro los archivos clave que debes estudiar:

#### 🏛️ Capa de Dominio - Ejemplos Reales

```bash
# 1. Modelo principal - Estudia cómo se estructura
src/main/java/com/datavet/datavet/clinic/domain/model/Clinic.java

# 2. Eventos de dominio - Ve cómo se publican
src/main/java/com/datavet/datavet/clinic/domain/event/ClinicCreatedEvent.java
src/main/java/com/datavet/datavet/clinic/domain/event/ClinicUpdatedEvent.java

# 3. Excepciones específicas - Aprende el patrón
src/main/java/com/datavet/datavet/clinic/domain/exception/ClinicNotFoundException.java
src/main/java/com/datavet/datavet/clinic/domain/exception/ClinicAlreadyExistsException.java
```

#### 🔧 Capa de Aplicación - Ejemplos Reales

```bash
# 1. Puerto de entrada - Define qué puede hacer el dominio
src/main/java/com/datavet/datavet/clinic/application/port/in/ClinicUseCase.java

# 2. Comandos - Cómo se estructuran los datos de entrada
src/main/java/com/datavet/datavet/clinic/application/port/in/command/CreateClinicCommand.java
src/main/java/com/datavet/datavet/clinic/application/port/in/command/UpdateClinicCommand.java

# 3. Servicio de aplicación - El orquestador principal
src/main/java/com/datavet/datavet/clinic/application/service/ClinicService.java

# 4. Validadores - Cómo validar comandos
src/main/java/com/datavet/datavet/clinic/application/validation/CreateClinicCommandValidator.java

# 5. DTOs y Mappers - Cómo convertir entre capas
src/main/java/com/datavet/datavet/clinic/application/dto/ClinicResponse.java
src/main/java/com/datavet/datavet/clinic/application/mapper/ClinicMapper.java
```

#### 🌐 Capa de Infraestructura - Ejemplos Reales

```bash
# 1. Controlador REST - Cómo exponer APIs
src/main/java/com/datavet/datavet/clinic/infrastructure/adapter/input/ClinicController.java

# 2. DTOs de Request - Cómo recibir datos del cliente
src/main/java/com/datavet/datavet/clinic/infrastructure/adapter/input/dto/CreateClinicRequest.java

# 3. Entidad JPA - Cómo mapear a base de datos
src/main/java/com/datavet/datavet/clinic/infrastructure/persistence/entity/ClinicEntity.java

# 4. Repositorio JPA - Cómo acceder a datos
src/main/java/com/datavet/datavet/clinic/infrastructure/persistence/repository/JpaClinicRepositoryAdapter.java

# 5. Convertidores - Cómo manejar Value Objects en JPA
src/main/java/com/datavet/datavet/clinic/infrastructure/persistence/converter/EmailConverter.java
src/main/java/com/datavet/datavet/clinic/infrastructure/persistence/converter/PhoneConverter.java
src/main/java/com/datavet/datavet/clinic/infrastructure/persistence/converter/AddressConverter.java
```

#### 🧪 Tests - Aprende las Mejores Prácticas

```bash
# Tests de dominio
src/test/java/com/datavet/datavet/clinic/domain/model/ClinicDomainEventsTest.java

# Tests de aplicación
src/test/java/com/datavet/datavet/clinic/application/service/ClinicServiceExceptionTest.java

# Tests de infraestructura
src/test/java/com/datavet/datavet/clinic/infrastructure/adapter/input/ClinicControllerIntegrationTest.java
src/test/java/com/datavet/datavet/clinic/infrastructure/persistence/repository/ClinicRepositoryIntegrationTest.java
```

#### 🔍 Cómo Explorar el Código

**1. Empieza por el Modelo de Dominio**
```bash
# Abre este archivo y estudia:
# - Cómo extiende AggregateRoot
# - Cómo usa Value Objects (Email, Phone, Address)
# - Cómo implementa factory methods
# - Cómo publica eventos
src/main/java/com/datavet/datavet/clinic/domain/model/Clinic.java
```

**2. Sigue con el Servicio de Aplicación**
```bash
# Estudia cómo:
# - Implementa el UseCase
# - Orquesta las operaciones
# - Maneja validaciones
# - Publica eventos
src/main/java/com/datavet/datavet/clinic/application/service/ClinicService.java
```

**3. Termina con el Controlador**
```bash
# Ve cómo:
# - Expone endpoints REST
# - Valida requests
# - Convierte DTOs
# - Maneja respuestas HTTP
src/main/java/com/datavet/datavet/clinic/infrastructure/adapter/input/ClinicController.java
```

#### 🎯 Puntos Clave a Observar

Cuando estudies el código existente, presta especial atención a:

**✅ Patrones que DEBES seguir:**
- Uso de `AggregateRoot` y `Entity` del shared
- Reutilización de Value Objects (`Email`, `Phone`, `Address`)
- Implementación de factory methods (`create()`)
- Publicación automática de eventos de dominio
- Validación usando el framework compartido
- Separación clara entre DTOs de diferentes capas

**❌ Errores que DEBES evitar:**
- No crear Value Objects cuando ya existen en shared
- No usar las clases base del shared
- Mezclar lógica de diferentes capas
- No seguir las convenciones de naming
- No implementar validaciones adecuadas

#### 🚀 Comandos Útiles para Explorar

```bash
# Ver la estructura completa del proyecto
find src -name "*.java" | head -20

# Buscar todos los archivos de un dominio específico
find src -path "*clinic*" -name "*.java"

# Ver todos los Value Objects disponibles
ls src/main/java/com/datavet/datavet/shared/domain/valueobject/

# Ver todas las clases base disponibles
ls src/main/java/com/datavet/datavet/shared/domain/model/

# Compilar y ver si hay errores
mvn clean compile

# Ejecutar tests específicos de un dominio
mvn test -Dtest="*Clinic*"
```

Con esta preparación, ya tienes todo lo necesario para empezar a crear tu nuevo dominio. En las siguientes secciones, te guiaré paso a paso para implementar cada componente.

---

## 3. Capa de Dominio (Domain Layer)

La **capa de dominio** es el corazón de tu aplicación. Aquí viven las reglas de negocio más importantes y los conceptos centrales del mundo veterinario. Esta capa debe ser completamente independiente de tecnologías externas como bases de datos o APIs.

### Modelo de Dominio

El modelo de dominio representa las entidades principales de tu negocio. En nuestro ejemplo, crearemos el modelo **Owner** (Dueño) que representa a los propietarios de las mascotas.

#### 🏗️ Creando el Modelo Owner

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/domain/model/Owner.java
```

**Código completo del modelo:**

```java
package com.datavet.datavet.owner.domain.model;

import com.datavet.datavet.owner.domain.event.OwnerCreatedEvent;
import com.datavet.datavet.owner.domain.event.OwnerDeletedEvent;
import com.datavet.datavet.owner.domain.event.OwnerUpdatedEvent;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Entity;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Agregado Owner que representa a un propietario de mascotas.
 * 
 * Este modelo encapsula toda la información y comportamientos relacionados
 * con los dueños de mascotas en el sistema veterinario.
 * 
 * Características principales:
 * - Extiende AggregateRoot para manejar eventos de dominio
 * - Implementa Entity para tener identidad única
 * - Usa Value Objects compartidos (Email, Phone, Address)
 * - Incluye factory methods para creación controlada
 * - Publica eventos de dominio automáticamente
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends AggregateRoot<Long> implements Entity<Long> {
    
    private Long ownerId;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String firstName;
    
    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String lastName;
    
    @NotNull(message = "El email es obligatorio")
    private Email email;
    
    private Phone phone;
    
    private Address address;
    
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    private String identificationNumber; // DNI, NIE, etc.
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes; // Notas adicionales sobre el dueño
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Retorna el identificador único de esta entidad Owner.
     * Implementación requerida por la interfaz Entity<Long>.
     */
    @Override
    public Long getId() {
        return this.ownerId;
    }

    /**
     * Factory method para crear un nuevo Owner.
     * 
     * Este método es la forma recomendada de crear nuevos dueños porque:
     * - Garantiza que se publique el evento OwnerCreatedEvent
     * - Establece automáticamente las fechas de creación y actualización
     * - Valida que los datos mínimos estén presentes
     * 
     * @param ownerId ID único del dueño
     * @param firstName Nombre del dueño
     * @param lastName Apellido del dueño
     * @param email Email del dueño (obligatorio)
     * @param phone Teléfono del dueño (opcional)
     * @param address Dirección del dueño (opcional)
     * @param identificationNumber Número de identificación (opcional)
     * @param notes Notas adicionales (opcional)
     * @return Nueva instancia de Owner con evento de creación publicado
     */
    public static Owner create(Long ownerId, String firstName, String lastName, 
                              Email email, Phone phone, Address address, 
                              String identificationNumber, String notes) {
        
        // Validaciones de negocio específicas
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del dueño es obligatorio");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del dueño es obligatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("El email del dueño es obligatorio");
        }
        
        Owner owner = Owner.builder()
                .ownerId(ownerId)
                .firstName(firstName.trim())
                .lastName(lastName.trim())
                .email(email)
                .phone(phone)
                .address(address)
                .identificationNumber(identificationNumber != null ? identificationNumber.trim() : null)
                .notes(notes != null ? notes.trim() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Publicar evento de dominio
        owner.addDomainEvent(OwnerCreatedEvent.of(ownerId, firstName, lastName, email));
        
        return owner;
    }

    /**
     * Actualiza la información del dueño y publica un evento de actualización.
     * 
     * @param firstName Nuevo nombre
     * @param lastName Nuevo apellido
     * @param email Nuevo email
     * @param phone Nuevo teléfono
     * @param address Nueva dirección
     * @param identificationNumber Nuevo número de identificación
     * @param notes Nuevas notas
     */
    public void update(String firstName, String lastName, Email email, 
                      Phone phone, Address address, String identificationNumber, String notes) {
        
        // Validaciones de negocio
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del dueño es obligatorio");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del dueño es obligatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("El email del dueño es obligatorio");
        }
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.identificationNumber = identificationNumber != null ? identificationNumber.trim() : null;
        this.notes = notes != null ? notes.trim() : null;
        this.updatedAt = LocalDateTime.now();
        
        // Publicar evento de dominio
        addDomainEvent(OwnerUpdatedEvent.of(this.ownerId, this.firstName, this.lastName));
    }

    /**
     * Marca el dueño para eliminación y publica un evento de eliminación.
     * 
     * Nota: En un sistema real, probablemente implementarías "soft delete"
     * en lugar de eliminación física para mantener el historial.
     */
    public void delete() {
        addDomainEvent(OwnerDeletedEvent.of(this.ownerId, this.firstName, this.lastName));
    }
    
    /**
     * Retorna el nombre completo del dueño.
     * Método de conveniencia para mostrar información.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Verifica si el dueño tiene información de contacto completa.
     * Útil para validaciones de negocio.
     */
    public boolean hasCompleteContactInfo() {
        return email != null && phone != null && address != null;
    }
}
```

#### 🔍 Explicación Detallada del Código

**1. Anotaciones de Lombok:**
```java
@Getter                                    // Genera getters automáticamente
@Builder                                   // Permite usar patrón Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Constructor privado para Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)   // Constructor sin parámetros privado
```

**2. Herencia y Interfaces:**
```java
public class Owner extends AggregateRoot<Long> implements Entity<Long>
```
- `AggregateRoot<Long>`: Proporciona funcionalidad para manejar eventos de dominio
- `Entity<Long>`: Marca que esta clase tiene identidad única (ID de tipo Long)

**3. Value Objects Reutilizados:**
```java
private Email email;      // ✅ Reutiliza validación de email del shared
private Phone phone;      // ✅ Reutiliza validación de teléfono del shared  
private Address address;  // ✅ Reutiliza validación de dirección del shared
```

**4. Factory Method Pattern:**
```java
public static Owner create(...)  // ✅ Forma controlada de crear instancias
```
- Garantiza que siempre se publique el evento `OwnerCreatedEvent`
- Aplica validaciones de negocio antes de crear la instancia
- Establece fechas automáticamente

**5. Eventos de Dominio:**
```java
owner.addDomainEvent(OwnerCreatedEvent.of(...));  // ✅ Publica evento automáticamente
```

#### 📁 Estructura de Carpetas

Después de crear el modelo, tu estructura debería verse así:

```
src/main/java/com/datavet/datavet/owner/
└── domain/
    └── model/
        ├── Owner.java           ← ✅ Tu nuevo modelo
        └── package-info.java    ← Documentación del paquete
```

#### ✅ Checklist - Modelo de Dominio

- [ ] ✅ Extiende `AggregateRoot<Long>`
- [ ] ✅ Implementa `Entity<Long>`
- [ ] ✅ Usa Value Objects del shared (`Email`, `Phone`, `Address`)
- [ ] ✅ Incluye factory method `create()`
- [ ] ✅ Publica eventos de dominio
- [ ] ✅ Tiene validaciones de negocio
- [ ] ✅ Usa anotaciones de validación Jakarta
- [ ] ✅ Incluye métodos de conveniencia (`getFullName()`)

#### 🚀 Próximo Paso

Una vez que hayas creado el modelo `Owner.java`, el siguiente paso es crear los **eventos de dominio** que este modelo publica. Estos eventos notificarán al resto del sistema cuando ocurran cambios importantes en los dueños.

### Eventos de Dominio

Los **eventos de dominio** son notificaciones que se publican cuando ocurre algo importante en tu dominio. Son fundamentales para mantener la consistencia entre diferentes partes del sistema y para implementar funcionalidades como auditoría, notificaciones, o sincronización con otros dominios.

#### 🎯 ¿Cuándo usar Eventos de Dominio?

**✅ Usa eventos cuando:**
- Se crea una nueva entidad importante (`OwnerCreatedEvent`)
- Se actualiza información crítica (`OwnerUpdatedEvent`)
- Se elimina una entidad (`OwnerDeletedEvent`)
- Ocurre un cambio que otros dominios necesitan saber

**❌ No uses eventos para:**
- Cambios menores que no afectan a otros sistemas
- Operaciones de consulta (GET)
- Validaciones simples

#### 🏗️ Creando los Eventos del Owner

**Ubicación de los archivos:**
```
src/main/java/com/datavet/datavet/owner/domain/event/
├── OwnerCreatedEvent.java
├── OwnerUpdatedEvent.java
└── OwnerDeletedEvent.java
```

#### 1. OwnerCreatedEvent

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/event/OwnerCreatedEvent.java`

```java
package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.valueobject.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica cuando se crea un nuevo dueño.
 * 
 * Este evento contiene la información esencial del dueño recién creado
 * y puede ser usado por otros sistemas para:
 * - Enviar emails de bienvenida
 * - Crear perfiles en sistemas externos
 * - Registrar auditoría
 * - Sincronizar con otros dominios
 */
@Getter
@RequiredArgsConstructor
public class OwnerCreatedEvent implements DomainEvent {
    
    private final Long ownerId;
    private final String firstName;
    private final String lastName;
    private final Email email;
    private final LocalDateTime occurredOn;
    
    /**
     * Factory method para crear el evento con timestamp automático.
     * 
     * @param ownerId ID del dueño creado
     * @param firstName Nombre del dueño
     * @param lastName Apellido del dueño
     * @param email Email del dueño
     * @return Nueva instancia del evento
     */
    public static OwnerCreatedEvent of(Long ownerId, String firstName, String lastName, Email email) {
        return new OwnerCreatedEvent(ownerId, firstName, lastName, email, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
    
    /**
     * Retorna el nombre completo del dueño para logging y notificaciones.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("OwnerCreatedEvent{ownerId=%d, fullName='%s', email='%s', occurredOn=%s}", 
                ownerId, getFullName(), email.getValue(), occurredOn);
    }
}
```

#### 2. OwnerUpdatedEvent

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/event/OwnerUpdatedEvent.java`

```java
package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica cuando se actualiza un dueño existente.
 * 
 * Este evento se usa para:
 * - Notificar cambios a sistemas externos
 * - Mantener sincronización de datos
 * - Registrar auditoría de cambios
 * - Invalidar cachés relacionados
 */
@Getter
@RequiredArgsConstructor
public class OwnerUpdatedEvent implements DomainEvent {
    
    private final Long ownerId;
    private final String firstName;
    private final String lastName;
    private final LocalDateTime occurredOn;
    
    /**
     * Factory method para crear el evento con timestamp automático.
     * 
     * @param ownerId ID del dueño actualizado
     * @param firstName Nombre actualizado del dueño
     * @param lastName Apellido actualizado del dueño
     * @return Nueva instancia del evento
     */
    public static OwnerUpdatedEvent of(Long ownerId, String firstName, String lastName) {
        return new OwnerUpdatedEvent(ownerId, firstName, lastName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
    
    /**
     * Retorna el nombre completo del dueño para logging y notificaciones.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("OwnerUpdatedEvent{ownerId=%d, fullName='%s', occurredOn=%s}", 
                ownerId, getFullName(), occurredOn);
    }
}
```

#### 3. OwnerDeletedEvent

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/event/OwnerDeletedEvent.java`

```java
package com.datavet.datavet.owner.domain.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica cuando se elimina un dueño.
 * 
 * Este evento es crítico para:
 * - Limpiar datos relacionados en otros sistemas
 * - Notificar a sistemas de facturación
 * - Registrar auditoría de eliminaciones
 * - Manejar cascadas de eliminación (ej: mascotas huérfanas)
 */
@Getter
@RequiredArgsConstructor
public class OwnerDeletedEvent implements DomainEvent {
    
    private final Long ownerId;
    private final String firstName;
    private final String lastName;
    private final LocalDateTime occurredOn;
    
    /**
     * Factory method para crear el evento con timestamp automático.
     * 
     * @param ownerId ID del dueño eliminado
     * @param firstName Nombre del dueño eliminado
     * @param lastName Apellido del dueño eliminado
     * @return Nueva instancia del evento
     */
    public static OwnerDeletedEvent of(Long ownerId, String firstName, String lastName) {
        return new OwnerDeletedEvent(ownerId, firstName, lastName, LocalDateTime.now());
    }
    
    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
    
    /**
     * Retorna el nombre completo del dueño para logging y auditoría.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("OwnerDeletedEvent{ownerId=%d, fullName='%s', occurredOn=%s}", 
                ownerId, getFullName(), occurredOn);
    }
}
```

#### 🔍 Explicación Detallada de los Eventos

**1. Implementación de DomainEvent:**
```java
public class OwnerCreatedEvent implements DomainEvent
```
- Todos los eventos deben implementar la interfaz `DomainEvent` del shared
- Esto garantiza que tengan un timestamp (`occurredOn()`)

**2. Patrón Inmutable:**
```java
@RequiredArgsConstructor  // Constructor con todos los campos final
private final Long ownerId;  // Campos inmutables
```
- Los eventos son inmutables una vez creados
- Esto previene modificaciones accidentales

**3. Factory Methods:**
```java
public static OwnerCreatedEvent of(...)  // ✅ Crea con timestamp automático
```
- Simplifican la creación del evento
- Establecen automáticamente el timestamp

**4. Información Relevante:**
```java
private final Long ownerId;     // ✅ ID para identificar la entidad
private final String firstName; // ✅ Datos útiles para notificaciones
private final Email email;      // ✅ Solo en eventos de creación
```

#### 🔄 Cómo se Integran con el Sistema de Eventos

**1. Publicación Automática:**
```java
// En el modelo Owner.java
owner.addDomainEvent(OwnerCreatedEvent.of(ownerId, firstName, lastName, email));
```

**2. El AggregateRoot maneja la colección:**
```java
// Heredado de AggregateRoot
public List<DomainEvent> getDomainEvents()  // Obtiene eventos pendientes
public void clearDomainEvents()             // Limpia después de publicar
```

**3. El servicio de aplicación los publica:**
```java
// En OwnerService.java (próxima sección)
Owner owner = Owner.create(...);
ownerRepository.save(owner);
eventPublisher.publishEvents(owner.getDomainEvents());  // ✅ Publica eventos
owner.clearDomainEvents();  // ✅ Limpia eventos publicados
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
└── domain/
    ├── model/
    │   ├── Owner.java
    │   └── package-info.java
    └── event/                           ← ✅ Nueva carpeta
        ├── OwnerCreatedEvent.java      ← ✅ Evento de creación
        ├── OwnerUpdatedEvent.java      ← ✅ Evento de actualización
        ├── OwnerDeletedEvent.java      ← ✅ Evento de eliminación
        └── package-info.java           ← Documentación del paquete
```

#### 🎯 Casos de Uso Reales de los Eventos

**OwnerCreatedEvent puede disparar:**
- 📧 Envío de email de bienvenida
- 📊 Actualización de métricas de nuevos clientes
- 🔄 Sincronización con CRM externo
- 📝 Registro en sistema de auditoría

**OwnerUpdatedEvent puede disparar:**
- 🔄 Actualización de cachés
- 📧 Notificación de cambios importantes
- 📊 Actualización de reportes
- 🔄 Sincronización con sistemas externos

**OwnerDeletedEvent puede disparar:**
- 🗑️ Limpieza de datos relacionados
- 📧 Notificación a administradores
- 📊 Actualización de métricas
- ⚠️ Verificación de mascotas huérfanas

#### ✅ Checklist - Eventos de Dominio

- [ ] ✅ Implementan `DomainEvent`
- [ ] ✅ Son inmutables (campos `final`)
- [ ] ✅ Tienen factory methods con `of()`
- [ ] ✅ Incluyen timestamp automático
- [ ] ✅ Contienen información relevante
- [ ] ✅ Tienen métodos `toString()` informativos
- [ ] ✅ Se publican desde el modelo de dominio
- [ ] ✅ Siguen convenciones de naming

#### 🚀 Próximo Paso

Con los eventos implementados, el siguiente paso es crear las **excepciones de dominio** que el modelo `Owner` puede lanzar cuando ocurren errores específicos del negocio.

### Excepciones de Dominio

Las **excepciones de dominio** representan errores específicos que pueden ocurrir en tu dominio de negocio. Son fundamentales para manejar casos de error de manera consistente y proporcionar mensajes claros tanto a desarrolladores como a usuarios finales.

#### 🎯 ¿Cuándo crear Excepciones de Dominio?

**✅ Crea excepciones cuando:**
- Una entidad no se encuentra (`OwnerNotFoundException`)
- Se intenta crear algo que ya existe (`OwnerAlreadyExistsException`)
- Los datos no cumplen reglas de negocio (`OwnerValidationException`)
- Ocurre un error específico del dominio

**❌ No crees excepciones para:**
- Errores técnicos (base de datos, red)
- Validaciones simples de formato (usa Value Objects)
- Errores que ya maneja el framework

#### 🏗️ Creando las Excepciones del Owner

**Ubicación de los archivos:**
```
src/main/java/com/datavet/datavet/owner/domain/exception/
├── OwnerNotFoundException.java
├── OwnerAlreadyExistsException.java
├── OwnerValidationException.java
└── package-info.java
```

#### 1. OwnerNotFoundException

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/exception/OwnerNotFoundException.java`

```java
package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityNotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un dueño en el sistema.
 * 
 * Esta excepción se usa cuando:
 * - Se busca un dueño por ID y no existe
 * - Se busca un dueño por email y no existe
 * - Se intenta actualizar un dueño que no existe
 * 
 * Resultado HTTP: 404 Not Found
 */
public class OwnerNotFoundException extends EntityNotFoundException {
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje descriptivo del error
     */
    public OwnerNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original del error
     */
    public OwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor para búsqueda por ID.
     * Genera automáticamente el mensaje: "Owner not found with id: 123"
     * 
     * @param ownerId ID del dueño que no se encontró
     */
    public OwnerNotFoundException(Long ownerId) {
        super("Owner", ownerId);
    }
    
    /**
     * Constructor para búsqueda por campo específico.
     * Genera automáticamente el mensaje: "Owner not found with email: john@example.com"
     * 
     * @param fieldName Nombre del campo usado en la búsqueda
     * @param fieldValue Valor del campo usado en la búsqueda
     */
    public OwnerNotFoundException(String fieldName, String fieldValue) {
        super("Owner", fieldName, fieldValue);
    }
    
    /**
     * Factory method para búsqueda por email.
     * Método de conveniencia para el caso más común.
     * 
     * @param email Email del dueño que no se encontró
     * @return Nueva instancia de la excepción
     */
    public static OwnerNotFoundException byEmail(String email) {
        return new OwnerNotFoundException("email", email);
    }
    
    /**
     * Factory method para búsqueda por número de identificación.
     * 
     * @param identificationNumber Número de identificación del dueño
     * @return Nueva instancia de la excepción
     */
    public static OwnerNotFoundException byIdentificationNumber(String identificationNumber) {
        return new OwnerNotFoundException("identification number", identificationNumber);
    }
}
```

#### 2. OwnerAlreadyExistsException

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/exception/OwnerAlreadyExistsException.java`

```java
package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.shared.domain.exception.EntityAlreadyExistsException;

/**
 * Excepción lanzada cuando se intenta crear un dueño que ya existe.
 * 
 * Esta excepción se usa cuando:
 * - Se intenta crear un dueño con un email que ya existe
 * - Se intenta crear un dueño con un número de identificación que ya existe
 * - Se detecta duplicación en campos únicos
 * 
 * Resultado HTTP: 409 Conflict
 */
public class OwnerAlreadyExistsException extends EntityAlreadyExistsException {
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje descriptivo del error
     */
    public OwnerAlreadyExistsException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original del error
     */
    public OwnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor para duplicación por campo específico.
     * Genera automáticamente el mensaje: "Owner already exists with email: john@example.com"
     * 
     * @param fieldName Nombre del campo duplicado
     * @param fieldValue Valor del campo duplicado
     */
    public OwnerAlreadyExistsException(String fieldName, String fieldValue) {
        super("Owner", fieldName, fieldValue);
    }
    
    /**
     * Factory method para duplicación por email.
     * Método de conveniencia para el caso más común.
     * 
     * @param email Email duplicado
     * @return Nueva instancia de la excepción
     */
    public static OwnerAlreadyExistsException withEmail(String email) {
        return new OwnerAlreadyExistsException("email", email);
    }
    
    /**
     * Factory method para duplicación por número de identificación.
     * 
     * @param identificationNumber Número de identificación duplicado
     * @return Nueva instancia de la excepción
     */
    public static OwnerAlreadyExistsException withIdentificationNumber(String identificationNumber) {
        return new OwnerAlreadyExistsException("identification number", identificationNumber);
    }
    
    /**
     * Factory method para duplicación por múltiples campos.
     * Útil cuando la duplicación se detecta por combinación de campos.
     * 
     * @param firstName Nombre del dueño
     * @param lastName Apellido del dueño
     * @param email Email del dueño
     * @return Nueva instancia de la excepción
     */
    public static OwnerAlreadyExistsException withFullInfo(String firstName, String lastName, String email) {
        return new OwnerAlreadyExistsException(
            String.format("Owner already exists with name '%s %s' and email '%s'", 
                         firstName, lastName, email)
        );
    }
}
```

#### 3. OwnerValidationException

**Archivo:** `src/main/java/com/datavet/datavet/owner/domain/exception/OwnerValidationException.java`

```java
package com.datavet.datavet.owner.domain.exception;

import com.datavet.datavet.shared.domain.exception.DomainException;
import com.datavet.datavet.shared.domain.validation.ValidationResult;

/**
 * Excepción lanzada cuando la validación de un dueño falla.
 * 
 * Esta excepción se usa cuando:
 * - Los datos del dueño no cumplen las reglas de negocio
 * - Fallan validaciones complejas que involucran múltiples campos
 * - Se detectan inconsistencias en los datos
 * 
 * Resultado HTTP: 400 Bad Request
 */
public class OwnerValidationException extends DomainException {
    
    private final ValidationResult validationResult;
    
    /**
     * Constructor con resultado de validación del framework compartido.
     * 
     * @param validationResult Resultado de la validación con errores detallados
     */
    public OwnerValidationException(ValidationResult validationResult) {
        super("Owner validation failed: " + formatErrors(validationResult));
        this.validationResult = validationResult;
    }
    
    /**
     * Constructor con mensaje simple.
     * Útil para validaciones específicas sin usar el framework.
     * 
     * @param message Mensaje descriptivo del error de validación
     */
    public OwnerValidationException(String message) {
        super("Owner validation failed: " + message);
        this.validationResult = null;
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original del error
     */
    public OwnerValidationException(String message, Throwable cause) {
        super("Owner validation failed: " + message, cause);
        this.validationResult = null;
    }
    
    /**
     * Retorna el resultado de validación si está disponible.
     * 
     * @return ValidationResult con errores detallados, o null si no está disponible
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }
    
    /**
     * Verifica si la excepción tiene errores de validación detallados.
     * 
     * @return true si tiene ValidationResult, false en caso contrario
     */
    public boolean hasValidationErrors() {
        return validationResult != null && !validationResult.isValid();
    }
    
    /**
     * Factory method para validación de email duplicado.
     * 
     * @param email Email que está duplicado
     * @return Nueva instancia de la excepción
     */
    public static OwnerValidationException duplicateEmail(String email) {
        return new OwnerValidationException(
            String.format("Email '%s' is already registered by another owner", email)
        );
    }
    
    /**
     * Factory method para validación de datos incompletos.
     * 
     * @return Nueva instancia de la excepción
     */
    public static OwnerValidationException incompleteContactInfo() {
        return new OwnerValidationException(
            "Owner must have complete contact information (email, phone, and address)"
        );
    }
    
    /**
     * Factory method para validación de nombre inválido.
     * 
     * @param firstName Nombre proporcionado
     * @param lastName Apellido proporcionado
     * @return Nueva instancia de la excepción
     */
    public static OwnerValidationException invalidName(String firstName, String lastName) {
        return new OwnerValidationException(
            String.format("Invalid name format: '%s %s'. Names must contain only letters and spaces", 
                         firstName, lastName)
        );
    }
    
    /**
     * Formatea los errores de validación en un string legible.
     * 
     * @param result Resultado de validación
     * @return String con errores formateados
     */
    private static String formatErrors(ValidationResult result) {
        if (result == null || result.isValid()) {
            return "Unknown validation error";
        }
        
        return result.getErrors().stream()
                .map(error -> error.getField() + ": " + error.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown validation error");
    }
}
```

#### 🔍 Explicación Detallada de las Excepciones

**1. Herencia de Excepciones Base:**
```java
public class OwnerNotFoundException extends EntityNotFoundException
```
- Reutiliza lógica común del shared
- Garantiza consistencia en mensajes de error
- Se mapea automáticamente a códigos HTTP correctos

**2. Múltiples Constructores:**
```java
public OwnerNotFoundException(Long ownerId)                    // Por ID
public OwnerNotFoundException(String fieldName, String value) // Por campo
public OwnerNotFoundException(String message)                 // Personalizado
```
- Flexibilidad para diferentes casos de uso
- Mensajes automáticos vs personalizados

**3. Factory Methods:**
```java
public static OwnerNotFoundException byEmail(String email)  // ✅ Método de conveniencia
public static OwnerAlreadyExistsException withEmail(String email)  // ✅ Más legible
```
- Hacen el código más legible
- Encapsulan lógica de creación

**4. Integración con Framework de Validación:**
```java
private final ValidationResult validationResult;  // ✅ Errores detallados
public boolean hasValidationErrors()              // ✅ Verificación fácil
```

#### 🎯 Cómo Usar las Excepciones

**En el Modelo de Dominio:**
```java
// En Owner.java
public static Owner create(...) {
    if (email == null) {
        throw new OwnerValidationException("Email is required");
    }
    // ...
}
```

**En Servicios de Aplicación:**
```java
// En OwnerService.java
public Owner findById(Long id) {
    return ownerRepository.findById(id)
        .orElseThrow(() -> new OwnerNotFoundException(id));
}

public Owner create(CreateOwnerCommand command) {
    if (ownerRepository.existsByEmail(command.getEmail())) {
        throw OwnerAlreadyExistsException.withEmail(command.getEmail().getValue());
    }
    // ...
}
```

**En Controladores (manejo automático):**
```java
// El GlobalExceptionHandler mapea automáticamente:
// OwnerNotFoundException → 404 Not Found
// OwnerAlreadyExistsException → 409 Conflict  
// OwnerValidationException → 400 Bad Request
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
└── domain/
    ├── model/
    │   ├── Owner.java
    │   └── package-info.java
    ├── event/
    │   ├── OwnerCreatedEvent.java
    │   ├── OwnerUpdatedEvent.java
    │   ├── OwnerDeletedEvent.java
    │   └── package-info.java
    └── exception/                              ← ✅ Nueva carpeta
        ├── OwnerNotFoundException.java        ← ✅ Error 404
        ├── OwnerAlreadyExistsException.java   ← ✅ Error 409
        ├── OwnerValidationException.java      ← ✅ Error 400
        └── package-info.java                  ← Documentación del paquete
```

#### 🎯 Mapeo a Códigos HTTP

| Excepción | Código HTTP | Cuándo Usar |
|-----------|-------------|-------------|
| `OwnerNotFoundException` | 404 Not Found | Dueño no existe |
| `OwnerAlreadyExistsException` | 409 Conflict | Email/ID duplicado |
| `OwnerValidationException` | 400 Bad Request | Datos inválidos |

#### ✅ Checklist - Excepciones de Dominio

- [ ] ✅ Extienden excepciones base del shared
- [ ] ✅ Tienen múltiples constructores para flexibilidad
- [ ] ✅ Incluyen factory methods para casos comunes
- [ ] ✅ Proporcionan mensajes descriptivos
- [ ] ✅ Se integran con el framework de validación
- [ ] ✅ Siguen convenciones de naming
- [ ] ✅ Se mapean a códigos HTTP apropiados

#### 🚀 Próximo Paso

¡Felicidades! Has completado la **capa de dominio** del Owner. Ahora tienes:

✅ **Modelo de dominio** (`Owner.java`) con eventos y validaciones  
✅ **Eventos de dominio** para notificar cambios importantes  
✅ **Excepciones específicas** para manejar errores del negocio  

El siguiente paso es implementar la **capa de aplicación**, donde crearás los servicios que orquestan las operaciones del dominio y los conectan con el mundo exterior.

---

## 4. Capa de Aplicación (Application Layer)

La **capa de aplicación** es el coordinador de tu dominio. Su trabajo es orquestar las operaciones del dominio, manejar validaciones, coordinar transacciones y conectar el dominio con el mundo exterior. Esta capa no contiene lógica de negocio (esa está en el dominio), sino que coordina y organiza las operaciones.

### Puertos y Casos de Uso

Los **puertos** son interfaces que definen qué operaciones puede realizar tu dominio. Son contratos que especifican las capacidades del sistema sin revelar cómo se implementan. Los **casos de uso** representan las acciones que los usuarios pueden realizar en tu sistema.

#### 🎯 ¿Qué son los Puertos?

**Puertos de Entrada (Input Ports):**
- Definen qué puede hacer tu dominio
- Son interfaces implementadas por servicios de aplicación
- Representan casos de uso del negocio
- Ejemplo: `OwnerUseCase` define crear, buscar, actualizar dueños

**Puertos de Salida (Output Ports):**
- Definen qué necesita tu dominio del exterior
- Son interfaces implementadas por adaptadores de infraestructura
- Representan dependencias externas
- Ejemplo: `OwnerRepositoryPort` define cómo acceder a datos

#### 🏗️ Creando el Puerto de Entrada - OwnerUseCase

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/port/in/OwnerUseCase.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.port.in;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.shared.application.port.UseCase;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada que define todas las operaciones disponibles para el dominio Owner.
 * 
 * Esta interfaz representa el contrato de lo que puede hacer el sistema con los dueños.
 * Es implementada por OwnerService y define los casos de uso principales:
 * 
 * - Crear nuevos dueños
 * - Buscar dueños existentes
 * - Actualizar información de dueños
 * - Eliminar dueños
 * - Listar dueños con filtros
 * 
 * Principios importantes:
 * - Solo define QUÉ se puede hacer, no CÓMO
 * - Usa objetos de comando para operaciones complejas
 * - Retorna DTOs, no entidades de dominio
 * - Es independiente de la tecnología (no sabe de REST, JPA, etc.)
 */
public interface OwnerUseCase extends UseCase {
    
    // ========== OPERACIONES DE CREACIÓN ==========
    
    /**
     * Crea un nuevo dueño en el sistema.
     * 
     * Este caso de uso:
     * 1. Valida el comando de entrada
     * 2. Verifica que no exista un dueño con el mismo email
     * 3. Crea el modelo de dominio Owner
     * 4. Publica el evento OwnerCreatedEvent
     * 5. Persiste el dueño en el repositorio
     * 6. Retorna la respuesta con los datos del dueño creado
     * 
     * @param command Comando con los datos del dueño a crear
     * @return OwnerResponse con los datos del dueño creado
     * @throws OwnerAlreadyExistsException si ya existe un dueño con el mismo email
     * @throws OwnerValidationException si los datos del comando son inválidos
     */
    OwnerResponse createOwner(CreateOwnerCommand command);
    
    // ========== OPERACIONES DE CONSULTA ==========
    
    /**
     * Busca un dueño por su ID único.
     * 
     * @param ownerId ID único del dueño
     * @return Optional con el dueño encontrado, o empty si no existe
     */
    Optional<OwnerResponse> findOwnerById(Long ownerId);
    
    /**
     * Busca un dueño por su ID único y lanza excepción si no existe.
     * Método de conveniencia para casos donde el dueño debe existir.
     * 
     * @param ownerId ID único del dueño
     * @return OwnerResponse con los datos del dueño
     * @throws OwnerNotFoundException si el dueño no existe
     */
    OwnerResponse getOwnerById(Long ownerId);
    
    /**
     * Busca un dueño por su email.
     * 
     * @param email Email del dueño a buscar
     * @return Optional con el dueño encontrado, o empty si no existe
     */
    Optional<OwnerResponse> findOwnerByEmail(String email);
    
    /**
     * Busca dueños por nombre (búsqueda parcial, case-insensitive).
     * Útil para funcionalidades de autocompletado o búsqueda.
     * 
     * @param name Nombre o parte del nombre a buscar
     * @return Lista de dueños que coinciden con el criterio
     */
    List<OwnerResponse> findOwnersByName(String name);
    
    /**
     * Busca un dueño por su número de identificación.
     * 
     * @param identificationNumber Número de identificación del dueño
     * @return Optional con el dueño encontrado, o empty si no existe
     */
    Optional<OwnerResponse> findOwnerByIdentificationNumber(String identificationNumber);
    
    /**
     * Obtiene todos los dueños del sistema.
     * Nota: En un sistema real, esto debería tener paginación.
     * 
     * @return Lista con todos los dueños
     */
    List<OwnerResponse> getAllOwners();
    
    /**
     * Obtiene dueños con paginación.
     * Versión más eficiente para sistemas con muchos dueños.
     * 
     * @param page Número de página (empezando en 0)
     * @param size Tamaño de página
     * @return Lista paginada de dueños
     */
    List<OwnerResponse> getOwners(int page, int size);
    
    // ========== OPERACIONES DE ACTUALIZACIÓN ==========
    
    /**
     * Actualiza la información de un dueño existente.
     * 
     * Este caso de uso:
     * 1. Valida el comando de entrada
     * 2. Verifica que el dueño existe
     * 3. Verifica que el nuevo email no esté en uso por otro dueño
     * 4. Actualiza el modelo de dominio
     * 5. Publica el evento OwnerUpdatedEvent
     * 6. Persiste los cambios
     * 7. Retorna la respuesta con los datos actualizados
     * 
     * @param ownerId ID del dueño a actualizar
     * @param command Comando con los nuevos datos
     * @return OwnerResponse con los datos actualizados
     * @throws OwnerNotFoundException si el dueño no existe
     * @throws OwnerAlreadyExistsException si el nuevo email ya está en uso
     * @throws OwnerValidationException si los datos del comando son inválidos
     */
    OwnerResponse updateOwner(Long ownerId, UpdateOwnerCommand command);
    
    // ========== OPERACIONES DE ELIMINACIÓN ==========
    
    /**
     * Elimina un dueño del sistema.
     * 
     * Este caso de uso:
     * 1. Verifica que el dueño existe
     * 2. Verifica que el dueño no tenga mascotas asociadas (regla de negocio)
     * 3. Marca el dueño para eliminación
     * 4. Publica el evento OwnerDeletedEvent
     * 5. Elimina el dueño del repositorio
     * 
     * @param ownerId ID del dueño a eliminar
     * @throws OwnerNotFoundException si el dueño no existe
     * @throws OwnerValidationException si el dueño tiene mascotas asociadas
     */
    void deleteOwner(Long ownerId);
    
    // ========== OPERACIONES DE VERIFICACIÓN ==========
    
    /**
     * Verifica si existe un dueño con el email especificado.
     * Útil para validaciones antes de crear o actualizar.
     * 
     * @param email Email a verificar
     * @return true si existe un dueño con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un dueño con el número de identificación especificado.
     * 
     * @param identificationNumber Número de identificación a verificar
     * @return true si existe un dueño con ese número, false en caso contrario
     */
    boolean existsByIdentificationNumber(String identificationNumber);
    
    /**
     * Cuenta el total de dueños en el sistema.
     * Útil para estadísticas y reportes.
     * 
     * @return Número total de dueños
     */
    long countOwners();
}
```

#### 🏗️ Creando el Puerto de Salida - OwnerRepositoryPort

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/port/out/OwnerRepositoryPort.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.port.out;

import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.port.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define cómo el dominio Owner accede a la persistencia.
 * 
 * Esta interfaz especifica qué operaciones de datos necesita el dominio,
 * sin saber cómo se implementan (podría ser JPA, MongoDB, archivos, etc.).
 * 
 * Es implementada por adaptadores de infraestructura como JpaOwnerRepositoryAdapter.
 * 
 * Principios importantes:
 * - Define QUÉ datos necesita el dominio, no CÓMO obtenerlos
 * - Trabaja con entidades de dominio (Owner), no DTOs
 * - Es independiente de la tecnología de persistencia
 * - Extiende Repository<Owner, Long> para operaciones básicas
 */
public interface OwnerRepositoryPort extends Repository<Owner, Long> {
    
    // ========== OPERACIONES BÁSICAS HEREDADAS ==========
    // Estas operaciones vienen de Repository<Owner, Long>:
    // - save(Owner owner): Owner
    // - findById(Long id): Optional<Owner>
    // - findAll(): List<Owner>
    // - deleteById(Long id): void
    // - existsById(Long id): boolean
    // - count(): long
    
    // ========== CONSULTAS ESPECÍFICAS DEL DOMINIO ==========
    
    /**
     * Busca un dueño por su email.
     * El email es único en el sistema, por lo que retorna Optional.
     * 
     * @param email Email del dueño a buscar
     * @return Optional con el dueño encontrado, o empty si no existe
     */
    Optional<Owner> findByEmail(String email);
    
    /**
     * Busca dueños cuyo nombre o apellido contenga el texto especificado.
     * Búsqueda case-insensitive para mejorar la experiencia del usuario.
     * 
     * @param name Texto a buscar en nombre o apellido
     * @return Lista de dueños que coinciden con el criterio
     */
    List<Owner> findByNameContaining(String name);
    
    /**
     * Busca un dueño por su número de identificación.
     * El número de identificación es único cuando está presente.
     * 
     * @param identificationNumber Número de identificación del dueño
     * @return Optional con el dueño encontrado, o empty si no existe
     */
    Optional<Owner> findByIdentificationNumber(String identificationNumber);
    
    /**
     * Busca dueños por ciudad en su dirección.
     * Útil para reportes geográficos o campañas locales.
     * 
     * @param city Ciudad a buscar
     * @return Lista de dueños en la ciudad especificada
     */
    List<Owner> findByAddressCity(String city);
    
    // ========== OPERACIONES DE VERIFICACIÓN ==========
    
    /**
     * Verifica si existe un dueño con el email especificado.
     * Más eficiente que findByEmail cuando solo necesitas verificar existencia.
     * 
     * @param email Email a verificar
     * @return true si existe un dueño con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un dueño con el número de identificación especificado.
     * 
     * @param identificationNumber Número de identificación a verificar
     * @return true si existe un dueño con ese número, false en caso contrario
     */
    boolean existsByIdentificationNumber(String identificationNumber);
    
    /**
     * Verifica si existe otro dueño (diferente al ID especificado) con el email dado.
     * Útil para validaciones de actualización donde el dueño puede mantener su email actual.
     * 
     * @param email Email a verificar
     * @param excludeOwnerId ID del dueño a excluir de la búsqueda
     * @return true si existe otro dueño con ese email, false en caso contrario
     */
    boolean existsByEmailAndIdNot(String email, Long excludeOwnerId);
    
    /**
     * Verifica si existe otro dueño con el número de identificación dado.
     * Similar a existsByEmailAndIdNot pero para número de identificación.
     * 
     * @param identificationNumber Número de identificación a verificar
     * @param excludeOwnerId ID del dueño a excluir de la búsqueda
     * @return true si existe otro dueño con ese número, false en caso contrario
     */
    boolean existsByIdentificationNumberAndIdNot(String identificationNumber, Long excludeOwnerId);
    
    // ========== OPERACIONES DE PAGINACIÓN ==========
    
    /**
     * Obtiene dueños con paginación.
     * Implementación eficiente para sistemas con muchos registros.
     * 
     * @param page Número de página (empezando en 0)
     * @param size Tamaño de página
     * @return Lista paginada de dueños
     */
    List<Owner> findAll(int page, int size);
    
    /**
     * Busca dueños por nombre con paginación.
     * Combina búsqueda por nombre con paginación.
     * 
     * @param name Texto a buscar en nombre o apellido
     * @param page Número de página (empezando en 0)
     * @param size Tamaño de página
     * @return Lista paginada de dueños que coinciden con el criterio
     */
    List<Owner> findByNameContaining(String name, int page, int size);
    
    // ========== OPERACIONES DE ESTADÍSTICAS ==========
    
    /**
     * Cuenta dueños por ciudad.
     * Útil para reportes de distribución geográfica.
     * 
     * @param city Ciudad a contar
     * @return Número de dueños en la ciudad especificada
     */
    long countByAddressCity(String city);
    
    /**
     * Cuenta dueños que tienen información de contacto completa.
     * Útil para métricas de calidad de datos.
     * 
     * @return Número de dueños con email, teléfono y dirección
     */
    long countByCompleteContactInfo();
}
```

#### 🔍 Explicación Detallada de los Puertos

**1. Separación de Responsabilidades:**
```java
// Puerto de ENTRADA - Define capacidades del sistema
public interface OwnerUseCase extends UseCase

// Puerto de SALIDA - Define necesidades del sistema  
public interface OwnerRepositoryPort extends Repository<Owner, Long>
```

**2. Independencia Tecnológica:**
```java
// ✅ El puerto no sabe si usa JPA, MongoDB, o archivos
Optional<Owner> findByEmail(String email);

// ❌ Esto sería dependiente de JPA
@Query("SELECT o FROM OwnerEntity o WHERE o.email = ?1")
Optional<OwnerEntity> findByEmail(String email);
```

**3. Contratos Claros:**
```java
// ✅ Especifica exactamente qué hace y qué puede fallar
/**
 * @throws OwnerNotFoundException si el dueño no existe
 * @throws OwnerAlreadyExistsException si el email ya está en uso
 */
OwnerResponse createOwner(CreateOwnerCommand command);
```

**4. Reutilización de Interfaces Base:**
```java
public interface OwnerUseCase extends UseCase           // ✅ Marca como caso de uso
public interface OwnerRepositoryPort extends Repository // ✅ Operaciones CRUD básicas
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
│   ├── model/
│   ├── event/
│   └── exception/
└── application/                                    ← ✅ Nueva capa
    └── port/                                       ← ✅ Puertos (interfaces)
        ├── in/                                     ← ✅ Puertos de entrada
        │   ├── OwnerUseCase.java                  ← ✅ Define capacidades
        │   └── command/                           ← Comandos (próxima sección)
        └── out/                                    ← ✅ Puertos de salida
            └── OwnerRepositoryPort.java           ← ✅ Define necesidades
```

#### 🎯 Patrón Puertos y Adaptadores en Acción

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REST Client   │───▶│ OwnerController │───▶│  OwnerService   │
│                 │    │   (Adapter)     │    │ (implements     │
└─────────────────┘    └─────────────────┘    │  OwnerUseCase)  │
                                              └─────────┬───────┘
                                                        │
                                                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Database     │◀───│ JpaOwnerRepo    │◀───│OwnerRepository  │
│                 │    │   (Adapter)     │    │     Port        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

**Flujo de datos:**
1. **Cliente REST** → llama al **Adaptador de Entrada** (Controller)
2. **Controller** → llama al **Puerto de Entrada** (UseCase)
3. **Service** → implementa el **Puerto de Entrada**
4. **Service** → usa el **Puerto de Salida** (RepositoryPort)
5. **Adapter** → implementa el **Puerto de Salida**
6. **Adapter** → accede a la **Base de Datos**

#### ✅ Checklist - Puertos y Casos de Uso

- [ ] ✅ Puerto de entrada extiende `UseCase`
- [ ] ✅ Puerto de salida extiende `Repository<Owner, Long>`
- [ ] ✅ Métodos tienen documentación clara con `@throws`
- [ ] ✅ Operaciones cubren todos los casos de uso (CRUD + búsquedas)
- [ ] ✅ Interfaces son independientes de tecnología
- [ ] ✅ Nombres de métodos son descriptivos y consistentes
- [ ] ✅ Incluyen operaciones de verificación (`exists...`)
- [ ] ✅ Consideran paginación para listas grandes

#### 🚀 Próximo Paso

Con los puertos definidos, el siguiente paso es crear los **comandos** que encapsulan los datos de entrada y los **validadores** que garantizan que estos datos cumplan las reglas de negocio.

### Comandos y Validaciones

Los **comandos** son objetos inmutables que encapsulan toda la información necesaria para realizar una operación específica. Son el mecanismo principal para transferir datos desde las capas externas (como controladores REST) hacia la capa de aplicación. Los **validadores** garantizan que estos comandos cumplan con las reglas de negocio antes de ser procesados.

#### 🎯 ¿Qué son los Comandos?

**Características de los Comandos:**
- **Inmutables**: Una vez creados, no pueden modificarse
- **Específicos**: Cada operación tiene su propio comando
- **Validables**: Contienen toda la información necesaria para validar
- **Independientes**: No dependen de tecnologías externas

**Tipos de Comandos:**
- **CreateOwnerCommand**: Para crear nuevos dueños
- **UpdateOwnerCommand**: Para actualizar dueños existentes
- **DeleteOwnerCommand**: Para eliminar dueños (si es necesario)

#### 🏗️ Creando CreateOwnerCommand

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/port/in/command/CreateOwnerCommand.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.port.in.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Comando para crear un nuevo dueño en el sistema.
 * 
 * Este comando encapsula toda la información necesaria para crear un dueño
 * y se usa como parámetro en OwnerUseCase.createOwner().
 * 
 * Características:
 * - Inmutable (todos los campos son final)
 * - Validable (usa Bean Validation y validadores personalizados)
 * - Usa Value Objects del shared para tipos complejos
 * - Incluye validaciones básicas con anotaciones
 * 
 * Flujo típico:
 * 1. Se crea desde un DTO de request (CreateOwnerRequest)
 * 2. Se valida usando CreateOwnerCommandValidator
 * 3. Se pasa al servicio de aplicación
 * 4. Se convierte a modelo de dominio (Owner)
 */
@Getter
@Builder
@RequiredArgsConstructor
public class CreateOwnerCommand {
    
    /**
     * Nombre del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private final String firstName;
    
    /**
     * Apellido del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private final String lastName;
    
    /**
     * Email del dueño.
     * Obligatorio, debe ser un Value Object Email válido.
     * El Value Object Email ya incluye validación de formato.
     */
    @NotNull(message = "El email es obligatorio")
    @Valid
    private final Email email;
    
    /**
     * Teléfono del dueño.
     * Opcional, pero si se proporciona debe ser un Value Object Phone válido.
     */
    @Valid
    private final Phone phone;
    
    /**
     * Dirección del dueño.
     * Opcional, pero si se proporciona debe ser un Value Object Address válido.
     */
    @Valid
    private final Address address;
    
    /**
     * Número de identificación del dueño (DNI, NIE, pasaporte, etc.).
     * Opcional, máximo 20 caracteres.
     */
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    private final String identificationNumber;
    
    /**
     * Notas adicionales sobre el dueño.
     * Opcional, máximo 500 caracteres.
     */
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private final String notes;
    
    /**
     * Retorna el nombre completo del dueño.
     * Método de conveniencia para logging y mensajes.
     * 
     * @return Nombre completo en formato "Nombre Apellido"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Verifica si el comando incluye información de contacto completa.
     * Útil para validaciones de negocio.
     * 
     * @return true si tiene email, teléfono y dirección, false en caso contrario
     */
    public boolean hasCompleteContactInfo() {
        return email != null && phone != null && address != null;
    }
    
    /**
     * Verifica si el comando incluye número de identificación.
     * 
     * @return true si tiene número de identificación, false en caso contrario
     */
    public boolean hasIdentificationNumber() {
        return identificationNumber != null && !identificationNumber.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("CreateOwnerCommand{fullName='%s', email='%s', hasPhone=%s, hasAddress=%s}", 
                getFullName(), 
                email != null ? email.getValue() : "null",
                phone != null,
                address != null);
    }
}
```

#### 🏗️ Creando UpdateOwnerCommand

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/port/in/command/UpdateOwnerCommand.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.port.in.command;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Comando para actualizar un dueño existente en el sistema.
 * 
 * Este comando encapsula toda la información necesaria para actualizar un dueño
 * y se usa como parámetro en OwnerUseCase.updateOwner().
 * 
 * Diferencias con CreateOwnerCommand:
 * - No incluye ID (se pasa por separado en el método)
 * - Puede incluir validaciones específicas de actualización
 * - Permite validar que el nuevo email no esté en uso por otro dueño
 * 
 * Flujo típico:
 * 1. Se crea desde un DTO de request (UpdateOwnerRequest)
 * 2. Se valida usando UpdateOwnerCommandValidator
 * 3. Se pasa al servicio de aplicación junto con el ID
 * 4. Se usa para actualizar el modelo de dominio existente
 */
@Getter
@Builder
@RequiredArgsConstructor
public class UpdateOwnerCommand {
    
    /**
     * Nombre actualizado del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private final String firstName;
    
    /**
     * Apellido actualizado del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private final String lastName;
    
    /**
     * Email actualizado del dueño.
     * Obligatorio, debe ser un Value Object Email válido.
     * Se validará que no esté en uso por otro dueño.
     */
    @NotNull(message = "El email es obligatorio")
    @Valid
    private final Email email;
    
    /**
     * Teléfono actualizado del dueño.
     * Opcional, puede ser null para eliminar el teléfono existente.
     */
    @Valid
    private final Phone phone;
    
    /**
     * Dirección actualizada del dueño.
     * Opcional, puede ser null para eliminar la dirección existente.
     */
    @Valid
    private final Address address;
    
    /**
     * Número de identificación actualizado del dueño.
     * Opcional, puede ser null o vacío para eliminar el número existente.
     */
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    private final String identificationNumber;
    
    /**
     * Notas actualizadas sobre el dueño.
     * Opcional, puede ser null o vacío para eliminar las notas existentes.
     */
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private final String notes;
    
    /**
     * Retorna el nombre completo actualizado del dueño.
     * 
     * @return Nombre completo en formato "Nombre Apellido"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Verifica si la actualización incluye información de contacto completa.
     * 
     * @return true si tiene email, teléfono y dirección, false en caso contrario
     */
    public boolean hasCompleteContactInfo() {
        return email != null && phone != null && address != null;
    }
    
    /**
     * Verifica si la actualización incluye número de identificación.
     * 
     * @return true si tiene número de identificación, false en caso contrario
     */
    public boolean hasIdentificationNumber() {
        return identificationNumber != null && !identificationNumber.trim().isEmpty();
    }
    
    /**
     * Verifica si la actualización elimina el teléfono.
     * 
     * @return true si el teléfono es null, false en caso contrario
     */
    public boolean removesPhone() {
        return phone == null;
    }
    
    /**
     * Verifica si la actualización elimina la dirección.
     * 
     * @return true si la dirección es null, false en caso contrario
     */
    public boolean removesAddress() {
        return address == null;
    }
    
    @Override
    public String toString() {
        return String.format("UpdateOwnerCommand{fullName='%s', email='%s', hasPhone=%s, hasAddress=%s}", 
                getFullName(), 
                email != null ? email.getValue() : "null",
                phone != null,
                address != null);
    }
}
```

#### 🏗️ Creando CreateOwnerCommandValidator

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/validation/CreateOwnerCommandValidator.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.shared.application.validation.Validator;
import com.datavet.datavet.shared.domain.validation.ValidationError;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador para CreateOwnerCommand que implementa reglas de negocio específicas.
 * 
 * Este validador complementa las validaciones básicas de Bean Validation
 * con reglas de negocio más complejas que requieren acceso a datos o
 * lógica específica del dominio.
 * 
 * Validaciones implementadas:
 * - Email único en el sistema
 * - Número de identificación único (si se proporciona)
 * - Formato válido de nombres (solo letras y espacios)
 * - Consistencia entre campos relacionados
 * 
 * Se ejecuta después de las validaciones de Bean Validation.
 */
@Component
@RequiredArgsConstructor
public class CreateOwnerCommandValidator implements Validator<CreateOwnerCommand> {
    
    private final OwnerRepositoryPort ownerRepository;
    
    // Patrón para validar nombres: solo letras, espacios, acentos y guiones
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s-]+$");
    
    /**
     * Valida un comando de creación de dueño.
     * 
     * @param command Comando a validar
     * @return ValidationResult con errores encontrados (si los hay)
     */
    @Override
    public ValidationResult validate(CreateOwnerCommand command) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Validar que el comando no sea null
        if (command == null) {
            errors.add(ValidationError.of("command", "El comando no puede ser null"));
            return ValidationResult.withErrors(errors);
        }
        
        // Validar unicidad del email
        validateEmailUniqueness(command, errors);
        
        // Validar unicidad del número de identificación
        validateIdentificationNumberUniqueness(command, errors);
        
        // Validar formato de nombres
        validateNameFormat(command, errors);
        
        // Validar consistencia de datos
        validateDataConsistency(command, errors);
        
        // Validar reglas de negocio específicas
        validateBusinessRules(command, errors);
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.withErrors(errors);
    }
    
    /**
     * Valida que el email no esté en uso por otro dueño.
     */
    private void validateEmailUniqueness(CreateOwnerCommand command, List<ValidationError> errors) {
        if (command.getEmail() != null) {
            String emailValue = command.getEmail().getValue();
            if (ownerRepository.existsByEmail(emailValue)) {
                errors.add(ValidationError.of("email", 
                    String.format("Ya existe un dueño registrado con el email '%s'", emailValue)));
            }
        }
    }
    
    /**
     * Valida que el número de identificación no esté en uso (si se proporciona).
     */
    private void validateIdentificationNumberUniqueness(CreateOwnerCommand command, List<ValidationError> errors) {
        if (command.hasIdentificationNumber()) {
            String idNumber = command.getIdentificationNumber();
            if (ownerRepository.existsByIdentificationNumber(idNumber)) {
                errors.add(ValidationError.of("identificationNumber", 
                    String.format("Ya existe un dueño registrado con el número de identificación '%s'", idNumber)));
            }
        }
    }
    
    /**
     * Valida que los nombres tengan formato válido (solo letras, espacios, acentos).
     */
    private void validateNameFormat(CreateOwnerCommand command, List<ValidationError> errors) {
        // Validar nombre
        if (command.getFirstName() != null && !NAME_PATTERN.matcher(command.getFirstName()).matches()) {
            errors.add(ValidationError.of("firstName", 
                "El nombre solo puede contener letras, espacios y guiones"));
        }
        
        // Validar apellido
        if (command.getLastName() != null && !NAME_PATTERN.matcher(command.getLastName()).matches()) {
            errors.add(ValidationError.of("lastName", 
                "El apellido solo puede contener letras, espacios y guiones"));
        }
    }
    
    /**
     * Valida consistencia entre campos relacionados.
     */
    private void validateDataConsistency(CreateOwnerCommand command, List<ValidationError> errors) {
        // Si se proporciona dirección, debe tener al menos ciudad
        if (command.getAddress() != null) {
            if (command.getAddress().getCity() == null || command.getAddress().getCity().trim().isEmpty()) {
                errors.add(ValidationError.of("address.city", 
                    "Si se proporciona dirección, la ciudad es obligatoria"));
            }
        }
        
        // Si se proporciona teléfono, debe ser válido para el país de la dirección
        if (command.getPhone() != null && command.getAddress() != null) {
            validatePhoneCountryConsistency(command, errors);
        }
    }
    
    /**
     * Valida que el teléfono sea consistente con el país de la dirección.
     */
    private void validatePhoneCountryConsistency(CreateOwnerCommand command, List<ValidationError> errors) {
        // Esta es una validación de ejemplo - en un sistema real podrías
        // validar que el código de país del teléfono coincida con el país de la dirección
        String phoneCountry = command.getPhone().getCountryCode();
        String addressCountry = command.getAddress().getCountry();
        
        if (phoneCountry != null && addressCountry != null && 
            !phoneCountry.equalsIgnoreCase(addressCountry)) {
            errors.add(ValidationError.of("phone", 
                String.format("El código de país del teléfono (%s) no coincide con el país de la dirección (%s)", 
                    phoneCountry, addressCountry)));
        }
    }
    
    /**
     * Valida reglas de negocio específicas del dominio.
     */
    private void validateBusinessRules(CreateOwnerCommand command, List<ValidationError> errors) {
        // Regla de negocio: Si el dueño tiene más de 65 años (inferido por algún campo),
        // debe tener información de contacto completa
        // Esta es una regla de ejemplo - adaptarla según las necesidades reales
        
        // Regla de negocio: Ciertos tipos de identificación requieren formato específico
        validateIdentificationNumberFormat(command, errors);
        
        // Regla de negocio: Nombres muy cortos o muy largos pueden ser sospechosos
        validateNameLength(command, errors);
    }
    
    /**
     * Valida formato específico del número de identificación según el tipo.
     */
    private void validateIdentificationNumberFormat(CreateOwnerCommand command, List<ValidationError> errors) {
        if (command.hasIdentificationNumber()) {
            String idNumber = command.getIdentificationNumber();
            
            // Ejemplo: DNI español debe tener 8 dígitos + 1 letra
            if (idNumber.matches("\\d{8}[A-Za-z]")) {
                // Validar letra del DNI español
                if (!isValidSpanishDNI(idNumber)) {
                    errors.add(ValidationError.of("identificationNumber", 
                        "El DNI español no tiene una letra válida"));
                }
            }
            // Ejemplo: NIE español debe empezar por X, Y o Z
            else if (idNumber.matches("[XYZ]\\d{7}[A-Za-z]")) {
                // Validar NIE español
                if (!isValidSpanishNIE(idNumber)) {
                    errors.add(ValidationError.of("identificationNumber", 
                        "El NIE español no tiene un formato válido"));
                }
            }
        }
    }
    
    /**
     * Valida longitud razonable de nombres.
     */
    private void validateNameLength(CreateOwnerCommand command, List<ValidationError> errors) {
        // Nombres muy cortos pueden ser errores de tipeo
        if (command.getFirstName() != null && command.getFirstName().trim().length() < 2) {
            errors.add(ValidationError.of("firstName", 
                "El nombre debe tener al menos 2 caracteres"));
        }
        
        if (command.getLastName() != null && command.getLastName().trim().length() < 2) {
            errors.add(ValidationError.of("lastName", 
                "El apellido debe tener al menos 2 caracteres"));
        }
    }
    
    /**
     * Valida DNI español usando el algoritmo oficial.
     * Implementación simplificada - en un sistema real usarías una librería especializada.
     */
    private boolean isValidSpanishDNI(String dni) {
        if (dni == null || !dni.matches("\\d{8}[A-Za-z]")) {
            return false;
        }
        
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        int number = Integer.parseInt(dni.substring(0, 8));
        char expectedLetter = letters.charAt(number % 23);
        char actualLetter = Character.toUpperCase(dni.charAt(8));
        
        return expectedLetter == actualLetter;
    }
    
    /**
     * Valida NIE español.
     * Implementación simplificada - en un sistema real usarías una librería especializada.
     */
    private boolean isValidSpanishNIE(String nie) {
        if (nie == null || !nie.matches("[XYZ]\\d{7}[A-Za-z]")) {
            return false;
        }
        
        // Convertir primera letra a número
        char firstChar = nie.charAt(0);
        String numberPart = switch (firstChar) {
            case 'X' -> "0" + nie.substring(1, 8);
            case 'Y' -> "1" + nie.substring(1, 8);
            case 'Z' -> "2" + nie.substring(1, 8);
            default -> nie.substring(1, 8);
        };
        
        // Aplicar el mismo algoritmo que el DNI
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        int number = Integer.parseInt(numberPart);
        char expectedLetter = letters.charAt(number % 23);
        char actualLetter = Character.toUpperCase(nie.charAt(8));
        
        return expectedLetter == actualLetter;
    }
}
```

#### 🏗️ Creando UpdateOwnerCommandValidator

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/validation/UpdateOwnerCommandValidator.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.shared.application.validation.Validator;
import com.datavet.datavet.shared.domain.validation.ValidationError;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador para UpdateOwnerCommand que implementa reglas de negocio específicas.
 * 
 * Este validador es similar a CreateOwnerCommandValidator pero con diferencias clave:
 * - Permite que el dueño mantenga su email actual
 * - Permite que el dueño mantenga su número de identificación actual
 * - Valida que el dueño a actualizar existe
 * - Considera el ID del dueño en validaciones de unicidad
 * 
 * Se ejecuta después de las validaciones de Bean Validation.
 */
@Component
@RequiredArgsConstructor
public class UpdateOwnerCommandValidator implements Validator<UpdateOwnerCommand> {
    
    private final OwnerRepositoryPort ownerRepository;
    
    // Patrón para validar nombres: solo letras, espacios, acentos y guiones
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s-]+$");
    
    /**
     * Valida un comando de actualización de dueño.
     * 
     * @param command Comando a validar
     * @return ValidationResult con errores encontrados (si los hay)
     */
    @Override
    public ValidationResult validate(UpdateOwnerCommand command) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Validar que el comando no sea null
        if (command == null) {
            errors.add(ValidationError.of("command", "El comando no puede ser null"));
            return ValidationResult.withErrors(errors);
        }
        
        // Validar formato de nombres
        validateNameFormat(command, errors);
        
        // Validar consistencia de datos
        validateDataConsistency(command, errors);
        
        // Validar reglas de negocio específicas
        validateBusinessRules(command, errors);
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.withErrors(errors);
    }
    
    /**
     * Valida un comando de actualización con el ID del dueño.
     * Este método adicional permite validaciones que requieren el ID.
     * 
     * @param ownerId ID del dueño a actualizar
     * @param command Comando a validar
     * @return ValidationResult con errores encontrados (si los hay)
     */
    public ValidationResult validate(Long ownerId, UpdateOwnerCommand command) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Validar que el ID no sea null
        if (ownerId == null) {
            errors.add(ValidationError.of("ownerId", "El ID del dueño no puede ser null"));
            return ValidationResult.withErrors(errors);
        }
        
        // Ejecutar validaciones básicas del comando
        ValidationResult basicValidation = validate(command);
        if (!basicValidation.isValid()) {
            errors.addAll(basicValidation.getErrors());
        }
        
        // Validar que el dueño existe
        validateOwnerExists(ownerId, errors);
        
        // Validar unicidad del email (excluyendo el dueño actual)
        validateEmailUniqueness(ownerId, command, errors);
        
        // Validar unicidad del número de identificación (excluyendo el dueño actual)
        validateIdentificationNumberUniqueness(ownerId, command, errors);
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.withErrors(errors);
    }
    
    /**
     * Valida que el dueño a actualizar existe en el sistema.
     */
    private void validateOwnerExists(Long ownerId, List<ValidationError> errors) {
        if (!ownerRepository.existsById(ownerId)) {
            errors.add(ValidationError.of("ownerId", 
                String.format("No existe un dueño con ID %d", ownerId)));
        }
    }
    
    /**
     * Valida que el email no esté en uso por otro dueño (excluyendo el actual).
     */
    private void validateEmailUniqueness(Long ownerId, UpdateOwnerCommand command, List<ValidationError> errors) {
        if (command.getEmail() != null) {
            String emailValue = command.getEmail().getValue();
            if (ownerRepository.existsByEmailAndIdNot(emailValue, ownerId)) {
                errors.add(ValidationError.of("email", 
                    String.format("Ya existe otro dueño registrado con el email '%s'", emailValue)));
            }
        }
    }
    
    /**
     * Valida que el número de identificación no esté en uso por otro dueño (si se proporciona).
     */
    private void validateIdentificationNumberUniqueness(Long ownerId, UpdateOwnerCommand command, List<ValidationError> errors) {
        if (command.hasIdentificationNumber()) {
            String idNumber = command.getIdentificationNumber();
            if (ownerRepository.existsByIdentificationNumberAndIdNot(idNumber, ownerId)) {
                errors.add(ValidationError.of("identificationNumber", 
                    String.format("Ya existe otro dueño registrado con el número de identificación '%s'", idNumber)));
            }
        }
    }
    
    /**
     * Valida que los nombres tengan formato válido (solo letras, espacios, acentos).
     */
    private void validateNameFormat(UpdateOwnerCommand command, List<ValidationError> errors) {
        // Validar nombre
        if (command.getFirstName() != null && !NAME_PATTERN.matcher(command.getFirstName()).matches()) {
            errors.add(ValidationError.of("firstName", 
                "El nombre solo puede contener letras, espacios y guiones"));
        }
        
        // Validar apellido
        if (command.getLastName() != null && !NAME_PATTERN.matcher(command.getLastName()).matches()) {
            errors.add(ValidationError.of("lastName", 
                "El apellido solo puede contener letras, espacios y guiones"));
        }
    }
    
    /**
     * Valida consistencia entre campos relacionados.
     */
    private void validateDataConsistency(UpdateOwnerCommand command, List<ValidationError> errors) {
        // Si se proporciona dirección, debe tener al menos ciudad
        if (command.getAddress() != null) {
            if (command.getAddress().getCity() == null || command.getAddress().getCity().trim().isEmpty()) {
                errors.add(ValidationError.of("address.city", 
                    "Si se proporciona dirección, la ciudad es obligatoria"));
            }
        }
        
        // Si se proporciona teléfono, debe ser válido para el país de la dirección
        if (command.getPhone() != null && command.getAddress() != null) {
            validatePhoneCountryConsistency(command, errors);
        }
        
        // Validar que no se eliminen campos críticos sin reemplazo
        validateCriticalFieldRemoval(command, errors);
    }
    
    /**
     * Valida que el teléfono sea consistente con el país de la dirección.
     */
    private void validatePhoneCountryConsistency(UpdateOwnerCommand command, List<ValidationError> errors) {
        String phoneCountry = command.getPhone().getCountryCode();
        String addressCountry = command.getAddress().getCountry();
        
        if (phoneCountry != null && addressCountry != null && 
            !phoneCountry.equalsIgnoreCase(addressCountry)) {
            errors.add(ValidationError.of("phone", 
                String.format("El código de país del teléfono (%s) no coincide con el país de la dirección (%s)", 
                    phoneCountry, addressCountry)));
        }
    }
    
    /**
     * Valida que no se eliminen campos críticos sin proporcionar alternativas.
     */
    private void validateCriticalFieldRemoval(UpdateOwnerCommand command, List<ValidationError> errors) {
        // Si se elimina el teléfono, debe haber una dirección válida para contacto
        if (command.removesPhone() && command.removesAddress()) {
            errors.add(ValidationError.of("contactInfo", 
                "No se puede eliminar tanto el teléfono como la dirección. Debe mantener al menos una forma de contacto"));
        }
    }
    
    /**
     * Valida reglas de negocio específicas del dominio.
     */
    private void validateBusinessRules(UpdateOwnerCommand command, List<ValidationError> errors) {
        // Validar formato específico del número de identificación
        validateIdentificationNumberFormat(command, errors);
        
        // Validar longitud razonable de nombres
        validateNameLength(command, errors);
        
        // Validar cambios que podrían afectar a mascotas asociadas
        validateImpactOnPets(command, errors);
    }
    
    /**
     * Valida formato específico del número de identificación según el tipo.
     */
    private void validateIdentificationNumberFormat(UpdateOwnerCommand command, List<ValidationError> errors) {
        if (command.hasIdentificationNumber()) {
            String idNumber = command.getIdentificationNumber();
            
            // Reutilizar la misma lógica que CreateOwnerCommandValidator
            if (idNumber.matches("\\d{8}[A-Za-z]") && !isValidSpanishDNI(idNumber)) {
                errors.add(ValidationError.of("identificationNumber", 
                    "El DNI español no tiene una letra válida"));
            } else if (idNumber.matches("[XYZ]\\d{7}[A-Za-z]") && !isValidSpanishNIE(idNumber)) {
                errors.add(ValidationError.of("identificationNumber", 
                    "El NIE español no tiene un formato válido"));
            }
        }
    }
    
    /**
     * Valida longitud razonable de nombres.
     */
    private void validateNameLength(UpdateOwnerCommand command, List<ValidationError> errors) {
        if (command.getFirstName() != null && command.getFirstName().trim().length() < 2) {
            errors.add(ValidationError.of("firstName", 
                "El nombre debe tener al menos 2 caracteres"));
        }
        
        if (command.getLastName() != null && command.getLastName().trim().length() < 2) {
            errors.add(ValidationError.of("lastName", 
                "El apellido debe tener al menos 2 caracteres"));
        }
    }
    
    /**
     * Valida cambios que podrían afectar a mascotas asociadas.
     * En un sistema real, podrías verificar si el cambio de nombre o contacto
     * afecta a citas programadas, historiales médicos, etc.
     */
    private void validateImpactOnPets(UpdateOwnerCommand command, List<ValidationError> errors) {
        // Esta es una validación de ejemplo - en un sistema real podrías:
        // 1. Verificar si el dueño tiene mascotas
        // 2. Verificar si hay citas programadas
        // 3. Validar que los cambios no afecten procesos en curso
        
        // Por ahora, solo agregamos una validación de ejemplo
        if (command.removesPhone() && command.removesAddress()) {
            errors.add(ValidationError.of("contactInfo", 
                "Si el dueño tiene mascotas registradas, debe mantener al menos una forma de contacto"));
        }
    }
    
    // Métodos auxiliares reutilizados de CreateOwnerCommandValidator
    private boolean isValidSpanishDNI(String dni) {
        if (dni == null || !dni.matches("\\d{8}[A-Za-z]")) {
            return false;
        }
        
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        int number = Integer.parseInt(dni.substring(0, 8));
        char expectedLetter = letters.charAt(number % 23);
        char actualLetter = Character.toUpperCase(dni.charAt(8));
        
        return expectedLetter == actualLetter;
    }
    
    private boolean isValidSpanishNIE(String nie) {
        if (nie == null || !nie.matches("[XYZ]\\d{7}[A-Za-z]")) {
            return false;
        }
        
        char firstChar = nie.charAt(0);
        String numberPart = switch (firstChar) {
            case 'X' -> "0" + nie.substring(1, 8);
            case 'Y' -> "1" + nie.substring(1, 8);
            case 'Z' -> "2" + nie.substring(1, 8);
            default -> nie.substring(1, 8);
        };
        
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        int number = Integer.parseInt(numberPart);
        char expectedLetter = letters.charAt(number % 23);
        char actualLetter = Character.toUpperCase(nie.charAt(8));
        
        return expectedLetter == actualLetter;
    }
}
```

#### 🔍 Explicación Detallada de Comandos y Validaciones

**1. Inmutabilidad de Comandos:**
```java
@RequiredArgsConstructor  // Constructor con todos los campos final
private final String firstName;  // ✅ Inmutable una vez creado
```

**2. Validaciones en Capas:**
```java
// Capa 1: Bean Validation (anotaciones)
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 50, message = "El nombre no puede exceder 50 caracteres")

// Capa 2: Validadores personalizados (reglas de negocio)
if (ownerRepository.existsByEmail(emailValue)) {
    errors.add(ValidationError.of("email", "Email ya existe"));
}
```

**3. Reutilización de Value Objects:**
```java
@Valid
private final Email email;    // ✅ Reutiliza validación de Email
@Valid  
private final Phone phone;    // ✅ Reutiliza validación de Phone
```

**4. Validaciones Específicas de Actualización:**
```java
// En UpdateOwnerCommandValidator
public ValidationResult validate(Long ownerId, UpdateOwnerCommand command) {
    // ✅ Considera el ID para validaciones de unicidad
    if (ownerRepository.existsByEmailAndIdNot(emailValue, ownerId)) {
        // Email ya usado por OTRO dueño
    }
}
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
│   ├── model/
│   ├── event/
│   └── exception/
└── application/
    ├── port/
    │   ├── in/
    │   │   ├── OwnerUseCase.java
    │   │   └── command/                           ← ✅ Nueva carpeta
    │   │       ├── CreateOwnerCommand.java       ← ✅ Comando de creación
    │   │       ├── UpdateOwnerCommand.java       ← ✅ Comando de actualización
    │   │       └── package-info.java
    │   └── out/
    │       └── OwnerRepositoryPort.java
    └── validation/                                ← ✅ Nueva carpeta
        ├── CreateOwnerCommandValidator.java      ← ✅ Validador de creación
        ├── UpdateOwnerCommandValidator.java      ← ✅ Validador de actualización
        └── package-info.java
```

#### 🎯 Flujo de Validación Completo

```
1. Request DTO → Bean Validation (anotaciones)
2. Request DTO → Command (conversión)
3. Command → Bean Validation (anotaciones del comando)
4. Command → Custom Validator (reglas de negocio)
5. Command → Service (si todas las validaciones pasan)
```

**Ejemplo práctico:**
```java
// 1. En el Controller
@PostMapping
public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
    // 2. Convertir a comando
    CreateOwnerCommand command = CreateOwnerCommand.builder()
        .firstName(request.getFirstName())
        .email(Email.of(request.getEmail()))
        // ...
        .build();
    
    // 3. El servicio ejecuta validación personalizada
    OwnerResponse response = ownerService.createOwner(command);
    return ResponseEntity.status(201).body(response);
}
```

#### ✅ Checklist - Comandos y Validaciones

- [ ] ✅ Comandos son inmutables (campos `final`)
- [ ] ✅ Comandos usan Value Objects del shared
- [ ] ✅ Comandos tienen validaciones Bean Validation
- [ ] ✅ Comandos incluyen métodos de conveniencia
- [ ] ✅ Validadores implementan `Validator<T>`
- [ ] ✅ Validadores usan `ValidationResult` del shared
- [ ] ✅ Validadores verifican unicidad en base de datos
- [ ] ✅ Validadores incluyen reglas de negocio específicas
- [ ] ✅ UpdateValidator considera ID para unicidad
- [ ] ✅ Validadores están marcados como `@Component`

#### 🚀 Próximo Paso

Con los comandos y validadores implementados, el siguiente paso es crear los **servicios de aplicación** que orquestan todas estas operaciones y conectan el dominio con los puertos de salida.

### Servicios de Aplicación

Los **servicios de aplicación** son los orquestadores del sistema. Su responsabilidad es coordinar las operaciones del dominio, manejar transacciones, ejecutar validaciones, publicar eventos y conectar el dominio con la infraestructura externa. Son la implementación concreta de los puertos de entrada (UseCases).

#### 🎯 ¿Qué hace un Servicio de Aplicación?

**Responsabilidades principales:**
- **Orquestar** operaciones del dominio
- **Validar** comandos usando validadores personalizados
- **Coordinar** transacciones
- **Publicar** eventos de dominio
- **Convertir** entre entidades de dominio y DTOs
- **Manejar** errores y excepciones

**Lo que NO debe hacer:**
- ❌ Contener lógica de negocio (esa va en el dominio)
- ❌ Conocer detalles de infraestructura (base de datos, REST)
- ❌ Manejar validaciones de formato (esas van en Value Objects)

#### 🏗️ Creando OwnerService

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/service/OwnerService.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.service;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.application.mapper.OwnerMapper;
import com.datavet.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.datavet.owner.application.validation.UpdateOwnerCommandValidator;
import com.datavet.datavet.owner.domain.exception.OwnerNotFoundException;
import com.datavet.datavet.owner.domain.exception.OwnerValidationException;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa todos los casos de uso del dominio Owner.
 * 
 * Este servicio actúa como el orquestador principal para todas las operaciones
 * relacionadas con dueños de mascotas. Coordina entre el dominio, la persistencia
 * y otros servicios del sistema.
 * 
 * Características principales:
 * - Implementa OwnerUseCase (puerto de entrada)
 * - Extiende ApplicationService (funcionalidad base)
 * - Maneja transacciones con @Transactional
 * - Publica eventos de dominio automáticamente
 * - Ejecuta validaciones personalizadas
 * - Convierte entre entidades de dominio y DTOs
 * 
 * Patrón de operación típico:
 * 1. Validar comando de entrada
 * 2. Ejecutar lógica de dominio
 * 3. Persistir cambios
 * 4. Publicar eventos
 * 5. Retornar DTO de respuesta
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // Por defecto, operaciones de solo lectura
public class OwnerService extends ApplicationService implements OwnerUseCase {
    
    // Dependencias inyectadas
    private final OwnerRepositoryPort ownerRepository;
    private final CreateOwnerCommandValidator createValidator;
    private final UpdateOwnerCommandValidator updateValidator;
    private final OwnerMapper ownerMapper;
    private final DomainEventPublisher eventPublisher;
    
    // ========== OPERACIONES DE CREACIÓN ==========
    
    /**
     * Crea un nuevo dueño en el sistema.
     * 
     * Flujo de ejecución:
     * 1. Validar comando usando CreateOwnerCommandValidator
     * 2. Crear entidad de dominio Owner usando factory method
     * 3. Persistir en repositorio
     * 4. Publicar eventos de dominio
     * 5. Convertir a DTO de respuesta
     * 
     * @param command Comando con datos del dueño a crear
     * @return OwnerResponse con datos del dueño creado
     * @throws OwnerValidationException si la validación falla
     */
    @Override
    @Transactional  // Operación de escritura requiere transacción
    public OwnerResponse createOwner(CreateOwnerCommand command) {
        log.info("Creating new owner: {}", command);
        
        // 1. Validar comando
        ValidationResult validation = createValidator.validate(command);
        if (!validation.isValid()) {
            log.warn("Owner creation validation failed: {}", validation.getErrors());
            throw new OwnerValidationException(validation);
        }
        
        // 2. Crear entidad de dominio
        Owner owner = Owner.create(
            null, // El ID se asignará automáticamente por la base de datos
            command.getFirstName(),
            command.getLastName(),
            command.getEmail(),
            command.getPhone(),
            command.getAddress(),
            command.getIdentificationNumber(),
            command.getNotes()
        );
        
        // 3. Persistir
        Owner savedOwner = ownerRepository.save(owner);
        log.info("Owner created successfully with ID: {}", savedOwner.getId());
        
        // 4. Publicar eventos de dominio
        publishDomainEvents(savedOwner);
        
        // 5. Convertir a DTO y retornar
        return ownerMapper.toResponse(savedOwner);
    }
    
    // ========== OPERACIONES DE CONSULTA ==========
    
    /**
     * Busca un dueño por su ID único.
     */
    @Override
    public Optional<OwnerResponse> findOwnerById(Long ownerId) {
        log.debug("Finding owner by ID: {}", ownerId);
        
        return ownerRepository.findById(ownerId)
                .map(ownerMapper::toResponse);
    }
    
    /**
     * Busca un dueño por su ID único y lanza excepción si no existe.
     */
    @Override
    public OwnerResponse getOwnerById(Long ownerId) {
        log.debug("Getting owner by ID: {}", ownerId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        
        return ownerMapper.toResponse(owner);
    }
    
    /**
     * Busca un dueño por su email.
     */
    @Override
    public Optional<OwnerResponse> findOwnerByEmail(String email) {
        log.debug("Finding owner by email: {}", email);
        
        return ownerRepository.findByEmail(email)
                .map(ownerMapper::toResponse);
    }
    
    /**
     * Busca dueños por nombre (búsqueda parcial, case-insensitive).
     */
    @Override
    public List<OwnerResponse> findOwnersByName(String name) {
        log.debug("Finding owners by name containing: {}", name);
        
        List<Owner> owners = ownerRepository.findByNameContaining(name);
        return ownerMapper.toResponseList(owners);
    }
    
    /**
     * Busca un dueño por su número de identificación.
     */
    @Override
    public Optional<OwnerResponse> findOwnerByIdentificationNumber(String identificationNumber) {
        log.debug("Finding owner by identification number: {}", identificationNumber);
        
        return ownerRepository.findByIdentificationNumber(identificationNumber)
                .map(ownerMapper::toResponse);
    }
    
    /**
     * Obtiene todos los dueños del sistema.
     */
    @Override
    public List<OwnerResponse> getAllOwners() {
        log.debug("Getting all owners");
        
        List<Owner> owners = ownerRepository.findAll();
        return ownerMapper.toResponseList(owners);
    }
    
    /**
     * Obtiene dueños con paginación.
     */
    @Override
    public List<OwnerResponse> getOwners(int page, int size) {
        log.debug("Getting owners with pagination: page={}, size={}", page, size);
        
        List<Owner> owners = ownerRepository.findAll(page, size);
        return ownerMapper.toResponseList(owners);
    }
    
    // ========== OPERACIONES DE ACTUALIZACIÓN ==========
    
    /**
     * Actualiza la información de un dueño existente.
     * 
     * Flujo de ejecución:
     * 1. Validar comando usando UpdateOwnerCommandValidator
     * 2. Buscar dueño existente
     * 3. Actualizar entidad de dominio
     * 4. Persistir cambios
     * 5. Publicar eventos de dominio
     * 6. Convertir a DTO de respuesta
     * 
     * @param ownerId ID del dueño a actualizar
     * @param command Comando con nuevos datos
     * @return OwnerResponse con datos actualizados
     * @throws OwnerNotFoundException si el dueño no existe
     * @throws OwnerValidationException si la validación falla
     */
    @Override
    @Transactional  // Operación de escritura requiere transacción
    public OwnerResponse updateOwner(Long ownerId, UpdateOwnerCommand command) {
        log.info("Updating owner with ID: {} - {}", ownerId, command);
        
        // 1. Validar comando (incluyendo ID)
        ValidationResult validation = updateValidator.validate(ownerId, command);
        if (!validation.isValid()) {
            log.warn("Owner update validation failed: {}", validation.getErrors());
            throw new OwnerValidationException(validation);
        }
        
        // 2. Buscar dueño existente
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        
        // 3. Actualizar entidad de dominio
        owner.update(
            command.getFirstName(),
            command.getLastName(),
            command.getEmail(),
            command.getPhone(),
            command.getAddress(),
            command.getIdentificationNumber(),
            command.getNotes()
        );
        
        // 4. Persistir cambios
        Owner updatedOwner = ownerRepository.save(owner);
        log.info("Owner updated successfully: {}", updatedOwner.getId());
        
        // 5. Publicar eventos de dominio
        publishDomainEvents(updatedOwner);
        
        // 6. Convertir a DTO y retornar
        return ownerMapper.toResponse(updatedOwner);
    }
    
    // ========== OPERACIONES DE ELIMINACIÓN ==========
    
    /**
     * Elimina un dueño del sistema.
     * 
     * Flujo de ejecución:
     * 1. Verificar que el dueño existe
     * 2. Ejecutar validaciones de negocio (ej: no tiene mascotas)
     * 3. Marcar para eliminación en el dominio
     * 4. Eliminar del repositorio
     * 5. Publicar eventos de dominio
     * 
     * @param ownerId ID del dueño a eliminar
     * @throws OwnerNotFoundException si el dueño no existe
     * @throws OwnerValidationException si no se puede eliminar
     */
    @Override
    @Transactional  // Operación de escritura requiere transacción
    public void deleteOwner(Long ownerId) {
        log.info("Deleting owner with ID: {}", ownerId);
        
        // 1. Buscar dueño existente
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        
        // 2. Validaciones de negocio para eliminación
        validateOwnerCanBeDeleted(owner);
        
        // 3. Marcar para eliminación en el dominio (publica evento)
        owner.delete();
        
        // 4. Eliminar del repositorio
        ownerRepository.deleteById(ownerId);
        log.info("Owner deleted successfully: {}", ownerId);
        
        // 5. Publicar eventos de dominio
        publishDomainEvents(owner);
    }
    
    // ========== OPERACIONES DE VERIFICACIÓN ==========
    
    /**
     * Verifica si existe un dueño con el email especificado.
     */
    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if owner exists by email: {}", email);
        return ownerRepository.existsByEmail(email);
    }
    
    /**
     * Verifica si existe un dueño con el número de identificación especificado.
     */
    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        log.debug("Checking if owner exists by identification number: {}", identificationNumber);
        return ownerRepository.existsByIdentificationNumber(identificationNumber);
    }
    
    /**
     * Cuenta el total de dueños en el sistema.
     */
    @Override
    public long countOwners() {
        log.debug("Counting total owners");
        return ownerRepository.count();
    }
    
    // ========== MÉTODOS AUXILIARES PRIVADOS ==========
    
    /**
     * Valida que un dueño puede ser eliminado según las reglas de negocio.
     * 
     * @param owner Dueño a validar
     * @throws OwnerValidationException si no se puede eliminar
     */
    private void validateOwnerCanBeDeleted(Owner owner) {
        // Regla de negocio: No se puede eliminar un dueño que tiene mascotas
        // En un sistema real, aquí verificarías con el dominio Pet
        
        // Ejemplo de validación:
        // if (petService.countPetsByOwnerId(owner.getId()) > 0) {
        //     throw OwnerValidationException.of("No se puede eliminar un dueño que tiene mascotas registradas");
        // }
        
        // Por ahora, solo registramos que se ejecutó la validación
        log.debug("Validating owner can be deleted: {}", owner.getId());
        
        // Aquí podrías agregar más validaciones:
        // - Verificar que no tenga citas pendientes
        // - Verificar que no tenga facturas pendientes
        // - Verificar que no esté en procesos legales
        // etc.
    }
    
    /**
     * Publica todos los eventos de dominio pendientes de una entidad.
     * 
     * @param owner Entidad con eventos pendientes
     */
    private void publishDomainEvents(Owner owner) {
        if (owner.getDomainEvents() != null && !owner.getDomainEvents().isEmpty()) {
            log.debug("Publishing {} domain events for owner: {}", 
                     owner.getDomainEvents().size(), owner.getId());
            
            // Publicar cada evento
            owner.getDomainEvents().forEach(event -> {
                log.debug("Publishing domain event: {}", event);
                eventPublisher.publish(event);
            });
            
            // Limpiar eventos después de publicar
            owner.clearDomainEvents();
        }
    }
    
    /**
     * Método de conveniencia para logging de errores con contexto.
     * 
     * @param operation Operación que falló
     * @param ownerId ID del dueño (si aplica)
     * @param error Error ocurrido
     */
    private void logError(String operation, Long ownerId, Exception error) {
        if (ownerId != null) {
            log.error("Error in {} for owner ID {}: {}", operation, ownerId, error.getMessage(), error);
        } else {
            log.error("Error in {}: {}", operation, error.getMessage(), error);
        }
    }
    
    /**
     * Método de conveniencia para logging de operaciones exitosas.
     * 
     * @param operation Operación completada
     * @param ownerId ID del dueño
     * @param additionalInfo Información adicional
     */
    private void logSuccess(String operation, Long ownerId, String additionalInfo) {
        log.info("Successfully completed {} for owner ID {}: {}", operation, ownerId, additionalInfo);
    }
}
```

#### 🔍 Explicación Detallada del Servicio de Aplicación

**1. Anotaciones y Configuración:**
```java
@Slf4j                              // ✅ Logging automático
@Service                            // ✅ Componente Spring
@RequiredArgsConstructor            // ✅ Inyección de dependencias
@Transactional(readOnly = true)     // ✅ Transacciones por defecto de solo lectura
```

**2. Herencia y Implementación:**
```java
public class OwnerService extends ApplicationService implements OwnerUseCase
```
- `ApplicationService`: Funcionalidad base compartida
- `OwnerUseCase`: Implementa el puerto de entrada

**3. Patrón de Operación Típico:**
```java
@Transactional  // ✅ Transacción para operaciones de escritura
public OwnerResponse createOwner(CreateOwnerCommand command) {
    // 1. Validar
    ValidationResult validation = createValidator.validate(command);
    
    // 2. Ejecutar lógica de dominio
    Owner owner = Owner.create(...);
    
    // 3. Persistir
    Owner savedOwner = ownerRepository.save(owner);
    
    // 4. Publicar eventos
    publishDomainEvents(savedOwner);
    
    // 5. Retornar DTO
    return ownerMapper.toResponse(savedOwner);
}
```

**4. Manejo de Transacciones:**
```java
@Transactional(readOnly = true)   // ✅ Clase: solo lectura por defecto
@Transactional                    // ✅ Método: escritura cuando es necesario
```

**5. Publicación de Eventos:**
```java
private void publishDomainEvents(Owner owner) {
    owner.getDomainEvents().forEach(eventPublisher::publish);  // ✅ Publica eventos
    owner.clearDomainEvents();                                 // ✅ Limpia después
}
```

**6. Logging Estructurado:**
```java
log.info("Creating new owner: {}", command);           // ✅ Info de operaciones
log.debug("Finding owner by ID: {}", ownerId);        // ✅ Debug de consultas
log.warn("Owner creation validation failed: {}", ...); // ✅ Warnings de validación
log.error("Error in {} for owner ID {}: {}", ...);    // ✅ Errores con contexto
```

#### 🎯 Responsabilidades del Servicio

**✅ Lo que SÍ hace:**
- Orquesta operaciones del dominio
- Ejecuta validaciones personalizadas
- Maneja transacciones
- Publica eventos de dominio
- Convierte entre entidades y DTOs
- Registra logs de operaciones

**❌ Lo que NO hace:**
- Contener lógica de negocio (va en Owner)
- Conocer detalles de JPA (va en adaptadores)
- Manejar requests HTTP (va en controladores)
- Validar formatos (va en Value Objects)

#### 🔄 Flujo Completo de una Operación

```
1. Controller recibe HTTP Request
2. Controller convierte Request → Command
3. Controller llama OwnerService.createOwner(command)
4. Service valida Command con Validator
5. Service crea Owner usando factory method
6. Owner publica OwnerCreatedEvent automáticamente
7. Service persiste Owner usando Repository
8. Service publica eventos usando EventPublisher
9. Service convierte Owner → OwnerResponse usando Mapper
10. Controller retorna HTTP Response
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
│   ├── model/
│   ├── event/
│   └── exception/
└── application/
    ├── port/
    │   ├── in/
    │   │   ├── OwnerUseCase.java
    │   │   └── command/
    │   └── out/
    │       └── OwnerRepositoryPort.java
    ├── validation/
    │   ├── CreateOwnerCommandValidator.java
    │   └── UpdateOwnerCommandValidator.java
    └── service/                                    ← ✅ Nueva carpeta
        ├── OwnerService.java                      ← ✅ Servicio principal
        └── package-info.java
```

#### 🎯 Patrones Implementados

**1. Command Pattern:**
```java
public OwnerResponse createOwner(CreateOwnerCommand command)  // ✅ Comando encapsula datos
```

**2. Repository Pattern:**
```java
private final OwnerRepositoryPort ownerRepository;  // ✅ Abstracción de persistencia
```

**3. Mapper Pattern:**
```java
return ownerMapper.toResponse(savedOwner);  // ✅ Conversión entre capas
```

**4. Event Publishing Pattern:**
```java
publishDomainEvents(savedOwner);  // ✅ Publicación de eventos
```

**5. Validation Pattern:**
```java
ValidationResult validation = createValidator.validate(command);  // ✅ Validación separada
```

#### ✅ Checklist - Servicios de Aplicación

- [ ] ✅ Implementa puerto de entrada (UseCase)
- [ ] ✅ Extiende ApplicationService
- [ ] ✅ Está marcado como @Service
- [ ] ✅ Usa @Transactional apropiadamente
- [ ] ✅ Ejecuta validaciones personalizadas
- [ ] ✅ Publica eventos de dominio
- [ ] ✅ Convierte entidades a DTOs
- [ ] ✅ Maneja excepciones apropiadamente
- [ ] ✅ Incluye logging estructurado
- [ ] ✅ No contiene lógica de negocio

#### 🚀 Próximo Paso

Con el servicio de aplicación implementado, el siguiente paso es crear los **DTOs y mappers** que permiten convertir entre las entidades de dominio y los objetos de transferencia de datos que se exponen a las capas externas.

### DTOs y Mappers

Los **DTOs** (Data Transfer Objects) son objetos inmutables que se usan para transferir datos entre diferentes capas del sistema. Los **mappers** son responsables de convertir entre entidades de dominio y DTOs, manteniendo la separación entre capas y ocultando detalles internos del dominio.

#### 🎯 ¿Por qué usar DTOs y Mappers?

**Ventajas de los DTOs:**
- **Separación de capas**: El dominio no se expone directamente
- **Estabilidad de APIs**: Cambios internos no afectan clientes
- **Seguridad**: Solo se exponen datos necesarios
- **Versionado**: Diferentes versiones de API pueden usar diferentes DTOs

**Ventajas de los Mappers:**
- **Conversión centralizada**: Un solo lugar para lógica de conversión
- **Reutilización**: Mismo mapper para diferentes operaciones
- **Mantenibilidad**: Fácil cambiar formato de datos
- **Testing**: Se pueden probar conversiones independientemente

#### 🏗️ Creando OwnerResponse DTO

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/dto/OwnerResponse.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para operaciones con dueños.
 * 
 * Este DTO representa la información de un dueño que se retorna
 * a los clientes de la API. Está diseñado para ser:
 * 
 * - Inmutable (todos los campos son final)
 * - Serializable a JSON automáticamente
 * - Independiente del modelo de dominio
 * - Optimizado para transferencia de datos
 * 
 * Diferencias con el modelo de dominio Owner:
 * - No incluye métodos de negocio
 * - Campos complejos se simplifican (ej: Email → String)
 * - Incluye campos calculados (ej: fullName)
 * - Omite campos internos (ej: eventos de dominio)
 */
@Getter
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // No incluir campos null en JSON
public class OwnerResponse {
    
    /**
     * ID único del dueño.
     */
    private final Long ownerId;
    
    /**
     * Nombre del dueño.
     */
    private final String firstName;
    
    /**
     * Apellido del dueño.
     */
    private final String lastName;
    
    /**
     * Nombre completo del dueño (campo calculado).
     * Se genera automáticamente a partir de firstName y lastName.
     */
    private final String fullName;
    
    /**
     * Email del dueño.
     * Se expone como String simple, no como Value Object.
     */
    private final String email;
    
    /**
     * Teléfono del dueño.
     * Se expone como String simple, no como Value Object.
     */
    private final String phone;
    
    /**
     * Dirección completa del dueño.
     * Se expone como String formateado, no como Value Object.
     */
    private final String address;
    
    /**
     * Ciudad de la dirección (campo separado para facilitar filtros).
     */
    private final String city;
    
    /**
     * País de la dirección (campo separado para facilitar filtros).
     */
    private final String country;
    
    /**
     * Número de identificación del dueño.
     */
    private final String identificationNumber;
    
    /**
     * Notas adicionales sobre el dueño.
     */
    private final String notes;
    
    /**
     * Indica si el dueño tiene información de contacto completa.
     * Campo calculado útil para validaciones en el frontend.
     */
    private final Boolean hasCompleteContactInfo;
    
    /**
     * Fecha y hora de creación del dueño.
     * Formateada para JSON con patrón ISO.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;
    
    /**
     * Fecha y hora de última actualización del dueño.
     * Formateada para JSON con patrón ISO.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;
    
    /**
     * Versión del registro para control de concurrencia optimista.
     * Útil para detectar modificaciones concurrentes.
     */
    private final Long version;
    
    // ========== MÉTODOS DE CONVENIENCIA ==========
    
    /**
     * Verifica si el dueño tiene teléfono registrado.
     * 
     * @return true si tiene teléfono, false en caso contrario
     */
    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }
    
    /**
     * Verifica si el dueño tiene dirección registrada.
     * 
     * @return true si tiene dirección, false en caso contrario
     */
    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty();
    }
    
    /**
     * Verifica si el dueño tiene número de identificación registrado.
     * 
     * @return true si tiene número de identificación, false en caso contrario
     */
    public boolean hasIdentificationNumber() {
        return identificationNumber != null && !identificationNumber.trim().isEmpty();
    }
    
    /**
     * Verifica si el dueño tiene notas registradas.
     * 
     * @return true si tiene notas, false en caso contrario
     */
    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
    
    /**
     * Retorna una representación resumida del dueño para logging.
     * No incluye información sensible.
     * 
     * @return String con información básica del dueño
     */
    public String toSummary() {
        return String.format("Owner{id=%d, name='%s', email='%s', hasPhone=%s}", 
                ownerId, fullName, email, hasPhone());
    }
    
    @Override
    public String toString() {
        return String.format("OwnerResponse{ownerId=%d, fullName='%s', email='%s', createdAt=%s}", 
                ownerId, fullName, email, createdAt);
    }
}
```

#### 🏗️ Creando OwnerMapper

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/application/mapper/OwnerMapper.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.application.mapper;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.mapper.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Owner del dominio y DTOs de aplicación.
 * 
 * Este mapper centraliza toda la lógica de conversión entre la capa de dominio
 * y la capa de aplicación, manteniendo la separación de responsabilidades.
 * 
 * Responsabilidades:
 * - Convertir Owner → OwnerResponse
 * - Manejar campos null de manera segura
 * - Formatear Value Objects a strings simples
 * - Calcular campos derivados (ej: fullName, hasCompleteContactInfo)
 * - Convertir listas de entidades
 * 
 * Principios aplicados:
 * - Conversiones son stateless (sin estado)
 * - Manejo seguro de valores null
 * - Métodos estáticos para facilitar testing
 * - Reutilización para operaciones batch
 */
@Component
public class OwnerMapper implements Mapper {
    
    /**
     * Convierte una entidad Owner del dominio a OwnerResponse DTO.
     * 
     * Esta conversión:
     * - Simplifica Value Objects a strings
     * - Calcula campos derivados
     * - Maneja valores null de manera segura
     * - Formatea direcciones como string único
     * 
     * @param owner Entidad de dominio a convertir
     * @return OwnerResponse DTO, o null si owner es null
     */
    public OwnerResponse toResponse(Owner owner) {
        if (owner == null) {
            return null;
        }
        
        return OwnerResponse.builder()
                .ownerId(owner.getId())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .fullName(buildFullName(owner))
                .email(extractEmailValue(owner))
                .phone(extractPhoneValue(owner))
                .address(formatAddress(owner))
                .city(extractCity(owner))
                .country(extractCountry(owner))
                .identificationNumber(owner.getIdentificationNumber())
                .notes(owner.getNotes())
                .hasCompleteContactInfo(owner.hasCompleteContactInfo())
                .createdAt(owner.getCreatedAt())
                .updatedAt(owner.getUpdatedAt())
                .version(extractVersion(owner))
                .build();
    }
    
    /**
     * Convierte una lista de entidades Owner a lista de OwnerResponse DTOs.
     * 
     * @param owners Lista de entidades de dominio
     * @return Lista de DTOs, o lista vacía si owners es null
     */
    public List<OwnerResponse> toResponseList(List<Owner> owners) {
        if (owners == null) {
            return List.of();
        }
        
        return owners.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    // ========== MÉTODOS AUXILIARES PRIVADOS ==========
    
    /**
     * Construye el nombre completo del dueño.
     * Maneja casos donde firstName o lastName podrían ser null.
     * 
     * @param owner Entidad de dominio
     * @return Nombre completo formateado
     */
    private String buildFullName(Owner owner) {
        String firstName = owner.getFirstName();
        String lastName = owner.getLastName();
        
        if (firstName == null && lastName == null) {
            return null;
        }
        
        if (firstName == null) {
            return lastName;
        }
        
        if (lastName == null) {
            return firstName;
        }
        
        return firstName + " " + lastName;
    }
    
    /**
     * Extrae el valor string del Value Object Email.
     * 
     * @param owner Entidad de dominio
     * @return Valor del email como string, o null si no tiene email
     */
    private String extractEmailValue(Owner owner) {
        return owner.getEmail() != null ? owner.getEmail().getValue() : null;
    }
    
    /**
     * Extrae el valor string del Value Object Phone.
     * Formatea el teléfono en un formato legible.
     * 
     * @param owner Entidad de dominio
     * @return Teléfono formateado como string, o null si no tiene teléfono
     */
    private String extractPhoneValue(Owner owner) {
        if (owner.getPhone() == null) {
            return null;
        }
        
        // Formatear teléfono con código de país si está disponible
        String countryCode = owner.getPhone().getCountryCode();
        String number = owner.getPhone().getNumber();
        
        if (countryCode != null && !countryCode.isEmpty()) {
            return String.format("+%s %s", countryCode, number);
        }
        
        return number;
    }
    
    /**
     * Formatea la dirección completa como un string único.
     * Combina todos los campos de la dirección en un formato legible.
     * 
     * @param owner Entidad de dominio
     * @return Dirección formateada como string, o null si no tiene dirección
     */
    private String formatAddress(Owner owner) {
        if (owner.getAddress() == null) {
            return null;
        }
        
        var address = owner.getAddress();
        StringBuilder formatted = new StringBuilder();
        
        // Agregar calle y número
        if (address.getStreet() != null && !address.getStreet().isEmpty()) {
            formatted.append(address.getStreet());
            
            if (address.getNumber() != null && !address.getNumber().isEmpty()) {
                formatted.append(" ").append(address.getNumber());
            }
        }
        
        // Agregar ciudad
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (formatted.length() > 0) {
                formatted.append(", ");
            }
            formatted.append(address.getCity());
        }
        
        // Agregar código postal
        if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(address.getPostalCode());
        }
        
        // Agregar país
        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            if (formatted.length() > 0) {
                formatted.append(", ");
            }
            formatted.append(address.getCountry());
        }
        
        return formatted.length() > 0 ? formatted.toString() : null;
    }
    
    /**
     * Extrae la ciudad de la dirección.
     * 
     * @param owner Entidad de dominio
     * @return Ciudad como string, o null si no tiene dirección o ciudad
     */
    private String extractCity(Owner owner) {
        return owner.getAddress() != null ? owner.getAddress().getCity() : null;
    }
    
    /**
     * Extrae el país de la dirección.
     * 
     * @param owner Entidad de dominio
     * @return País como string, o null si no tiene dirección o país
     */
    private String extractCountry(Owner owner) {
        return owner.getAddress() != null ? owner.getAddress().getCountry() : null;
    }
    
    /**
     * Extrae la versión del registro para control de concurrencia.
     * En este ejemplo, usamos un valor fijo, pero en un sistema real
     * esto vendría de la entidad JPA o de un campo específico.
     * 
     * @param owner Entidad de dominio
     * @return Versión del registro
     */
    private Long extractVersion(Owner owner) {
        // En un sistema real, esto podría venir de:
        // - Un campo @Version en la entidad JPA
        // - Un hash de los campos importantes
        // - Un timestamp de última modificación
        // Por ahora, retornamos un valor fijo
        return 1L;
    }
    
    // ========== MÉTODOS ESTÁTICOS PARA TESTING ==========
    
    /**
     * Método estático para facilitar testing sin inyección de dependencias.
     * 
     * @param owner Entidad de dominio
     * @return OwnerResponse DTO
     */
    public static OwnerResponse mapToResponse(Owner owner) {
        return new OwnerMapper().toResponse(owner);
    }
    
    /**
     * Método estático para convertir listas sin inyección de dependencias.
     * 
     * @param owners Lista de entidades de dominio
     * @return Lista de DTOs
     */
    public static List<OwnerResponse> mapToResponseList(List<Owner> owners) {
        return new OwnerMapper().toResponseList(owners);
    }
}
```

#### 🔍 Explicación Detallada de DTOs y Mappers

**1. Inmutabilidad del DTO:**
```java
@RequiredArgsConstructor  // Constructor con todos los campos final
private final Long ownerId;  // ✅ Inmutable una vez creado
```

**2. Anotaciones JSON:**
```java
@JsonInclude(JsonInclude.Include.NON_NULL)  // ✅ No incluir campos null
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")  // ✅ Formato de fechas
```

**3. Simplificación de Value Objects:**
```java
// En el dominio: Email email
// En el DTO: String email

private String extractEmailValue(Owner owner) {
    return owner.getEmail() != null ? owner.getEmail().getValue() : null;
}
```

**4. Campos Calculados:**
```java
.fullName(buildFullName(owner))                    // ✅ Nombre completo
.hasCompleteContactInfo(owner.hasCompleteContactInfo())  // ✅ Info completa
.address(formatAddress(owner))                     // ✅ Dirección formateada
```

**5. Manejo Seguro de Nulls:**
```java
public OwnerResponse toResponse(Owner owner) {
    if (owner == null) {
        return null;  // ✅ Manejo seguro
    }
    // ...
}
```

**6. Conversión de Listas:**
```java
public List<OwnerResponse> toResponseList(List<Owner> owners) {
    return owners.stream()
            .map(this::toResponse)  // ✅ Reutiliza conversión individual
            .collect(Collectors.toList());
}
```

#### 🎯 Diferencias entre Entidad de Dominio y DTO

| Aspecto | Owner (Dominio) | OwnerResponse (DTO) |
|---------|-----------------|---------------------|
| **Propósito** | Lógica de negocio | Transferencia de datos |
| **Mutabilidad** | Mutable (con métodos) | Inmutable |
| **Value Objects** | `Email`, `Phone`, `Address` | `String` simples |
| **Métodos** | Lógica de negocio | Solo getters y utilidades |
| **Eventos** | Publica eventos de dominio | No tiene eventos |
| **Validaciones** | Validaciones de negocio | Solo estructura |
| **Dependencias** | Independiente | Optimizado para JSON |

#### 🔄 Flujo de Conversión

```
1. Dominio: Owner entity con Value Objects
2. Mapper: Convierte Owner → OwnerResponse
3. DTO: Objeto simple optimizado para transferencia
4. JSON: Serialización automática para API REST
```

**Ejemplo de conversión:**
```java
// Entidad de dominio
Owner owner = Owner.create(1L, "Juan", "Pérez", 
    Email.of("juan@example.com"), 
    Phone.of("+34", "123456789"),
    Address.of("Calle Mayor", "1", "Madrid", "28001", "España"),
    "12345678A", "Cliente VIP");

// Conversión a DTO
OwnerResponse response = ownerMapper.toResponse(owner);

// Resultado JSON
{
  "ownerId": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "fullName": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+34 123456789",
  "address": "Calle Mayor 1, Madrid 28001, España",
  "city": "Madrid",
  "country": "España",
  "identificationNumber": "12345678A",
  "notes": "Cliente VIP",
  "hasCompleteContactInfo": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "version": 1
}
```

#### 📁 Estructura de Carpetas Final

```
src/main/java/com/datavet/datavet/owner/
├── domain/
│   ├── model/
│   ├── event/
│   └── exception/
└── application/
    ├── port/
    │   ├── in/
    │   │   ├── OwnerUseCase.java
    │   │   └── command/
    │   └── out/
    │       └── OwnerRepositoryPort.java
    ├── validation/
    ├── service/
    │   └── OwnerService.java
    ├── dto/                                        ← ✅ Nueva carpeta
    │   ├── OwnerResponse.java                     ← ✅ DTO de respuesta
    │   └── package-info.java
    └── mapper/                                     ← ✅ Nueva carpeta
        ├── OwnerMapper.java                       ← ✅ Mapper principal
        └── package-info.java
```

#### 🎯 Mejores Prácticas para DTOs y Mappers

**DTOs:**
- ✅ Siempre inmutables
- ✅ Campos simples (String, Long, Boolean)
- ✅ Incluir campos calculados útiles
- ✅ Usar anotaciones JSON apropiadas
- ✅ Métodos de conveniencia para validaciones

**Mappers:**
- ✅ Manejo seguro de valores null
- ✅ Métodos estáticos para testing
- ✅ Conversión de listas
- ✅ Formateo consistente de datos
- ✅ Separación de lógica de conversión

#### ✅ Checklist - DTOs y Mappers

- [ ] ✅ DTO es inmutable (campos `final`)
- [ ] ✅ DTO usa tipos simples (no Value Objects)
- [ ] ✅ DTO incluye campos calculados útiles
- [ ] ✅ DTO tiene anotaciones JSON apropiadas
- [ ] ✅ Mapper implementa `Mapper` del shared
- [ ] ✅ Mapper maneja valores null de manera segura
- [ ] ✅ Mapper incluye conversión de listas
- [ ] ✅ Mapper formatea Value Objects correctamente
- [ ] ✅ Mapper está marcado como `@Component`
- [ ] ✅ Mapper incluye métodos estáticos para testing

#### 🎉 ¡Capa de Aplicación Completada!

¡Felicidades! Has completado toda la **capa de aplicación** del dominio Owner. Ahora tienes:

✅ **Puertos de entrada y salida** que definen contratos claros  
✅ **Comandos inmutables** que encapsulan datos de entrada  
✅ **Validadores personalizados** que implementan reglas de negocio  
✅ **Servicio de aplicación** que orquesta todas las operaciones  
✅ **DTOs y mappers** que manejan la transferencia de datos  

La capa de aplicación está completamente funcional y lista para conectarse con la capa de infraestructura, que implementará los adaptadores para REST APIs y persistencia en base de datos.

---

## 5. Capa de Infraestructura (Infrastructure Layer)

La **capa de infraestructura** es donde tu dominio se conecta con el mundo real. Aquí implementas los adaptadores que permiten que tu aplicación funcione con tecnologías específicas como REST APIs, bases de datos, sistemas de mensajería, etc. Esta capa contiene todos los detalles técnicos que el dominio no necesita conocer.

### Controladores REST

Los **controladores REST** son adaptadores de entrada que exponen tu dominio como APIs HTTP. Son el punto de contacto entre los clientes externos (aplicaciones web, móviles, otros servicios) y tu sistema. Su responsabilidad principal es traducir requests HTTP a comandos del dominio y responses del dominio a HTTP.

#### 🎯 Responsabilidades del Controlador

**✅ Lo que SÍ debe hacer un controlador:**
- Recibir y validar requests HTTP
- Convertir DTOs de request a comandos de dominio
- Llamar a los casos de uso apropiados
- Convertir responses del dominio a DTOs HTTP
- Manejar códigos de estado HTTP correctos
- Aplicar validaciones de entrada (Bean Validation)

**❌ Lo que NO debe hacer un controlador:**
- Contener lógica de negocio
- Acceder directamente a repositorios
- Manejar transacciones
- Realizar validaciones complejas de negocio
- Conocer detalles del modelo de dominio

#### 🏗️ Creando el OwnerController

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/adapter/input/OwnerController.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.adapter.input;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.infrastructure.adapter.input.dto.CreateOwnerRequest;
import com.datavet.datavet.owner.infrastructure.adapter.input.dto.UpdateOwnerRequest;
import com.datavet.datavet.shared.infrastructure.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar operaciones relacionadas con dueños de mascotas.
 * 
 * Este controlador expone endpoints HTTP para:
 * - Crear nuevos dueños
 * - Consultar dueños existentes
 * - Actualizar información de dueños
 * - Eliminar dueños
 * - Buscar dueños con diferentes criterios
 * 
 * Principios implementados:
 * - Separación de responsabilidades: solo maneja HTTP, delega lógica al UseCase
 * - Validación de entrada: usa @Valid para validar DTOs de request
 * - Códigos HTTP apropiados: 200, 201, 404, 400, 409, etc.
 * - Logging: registra operaciones importantes para auditoría
 * - Manejo de errores: el GlobalExceptionHandler maneja las excepciones
 */
@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {
    
    private final OwnerUseCase ownerUseCase;
    
    // ========== ENDPOINTS DE CREACIÓN ==========
    
    /**
     * Crea un nuevo dueño en el sistema.
     * 
     * POST /api/v1/owners
     * 
     * @param request DTO con los datos del dueño a crear
     * @return ResponseEntity con el dueño creado y código 201 Created
     */
    @PostMapping
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        log.info("Creating new owner with email: {}", request.getEmail());
        
        // Convertir DTO de request a comando de dominio
        CreateOwnerCommand command = CreateOwnerCommand.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .identificationNumber(request.getIdentificationNumber())
                .notes(request.getNotes())
                .build();
        
        // Ejecutar caso de uso
        OwnerResponse response = ownerUseCase.createOwner(command);
        
        log.info("Owner created successfully with ID: {}", response.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // ========== ENDPOINTS DE CONSULTA ==========
    
    /**
     * Obtiene un dueño por su ID.
     * 
     * GET /api/v1/owners/{id}
     * 
     * @param ownerId ID único del dueño
     * @return ResponseEntity con el dueño encontrado (200) o 404 si no existe
     */
    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable Long ownerId) {
        log.debug("Fetching owner with ID: {}", ownerId);
        
        Optional<OwnerResponse> owner = ownerUseCase.findOwnerById(ownerId);
        
        if (owner.isPresent()) {
            log.debug("Owner found with ID: {}", ownerId);
            return ResponseEntity.ok(owner.get());
        } else {
            log.debug("Owner not found with ID: {}", ownerId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Obtiene todos los dueños del sistema.
     * 
     * GET /api/v1/owners
     * 
     * @param page Número de página (opcional, por defecto 0)
     * @param size Tamaño de página (opcional, por defecto 20)
     * @return ResponseEntity con la lista de dueños
     */
    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching owners - page: {}, size: {}", page, size);
        
        List<OwnerResponse> owners;
        if (page == 0 && size == 20) {
            // Si son los valores por defecto, usar el método sin paginación para simplicidad
            owners = ownerUseCase.getAllOwners();
        } else {
            owners = ownerUseCase.getOwners(page, size);
        }
        
        log.debug("Found {} owners", owners.size());
        return ResponseEntity.ok(owners);
    }
    
    /**
     * Busca dueños por email.
     * 
     * GET /api/v1/owners/by-email?email=john@example.com
     * 
     * @param email Email del dueño a buscar
     * @return ResponseEntity con el dueño encontrado (200) o 404 si no existe
     */
    @GetMapping("/by-email")
    public ResponseEntity<OwnerResponse> getOwnerByEmail(@RequestParam String email) {
        log.debug("Fetching owner with email: {}", email);
        
        Optional<OwnerResponse> owner = ownerUseCase.findOwnerByEmail(email);
        
        if (owner.isPresent()) {
            log.debug("Owner found with email: {}", email);
            return ResponseEntity.ok(owner.get());
        } else {
            log.debug("Owner not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Busca dueños por nombre (búsqueda parcial).
     * 
     * GET /api/v1/owners/search?name=John
     * 
     * @param name Nombre o parte del nombre a buscar
     * @return ResponseEntity con la lista de dueños que coinciden
     */
    @GetMapping("/search")
    public ResponseEntity<List<OwnerResponse>> searchOwnersByName(@RequestParam String name) {
        log.debug("Searching owners with name containing: {}", name);
        
        List<OwnerResponse> owners = ownerUseCase.findOwnersByName(name);
        
        log.debug("Found {} owners matching name: {}", owners.size(), name);
        return ResponseEntity.ok(owners);
    }
    
    /**
     * Busca un dueño por número de identificación.
     * 
     * GET /api/v1/owners/by-identification?number=12345678A
     * 
     * @param identificationNumber Número de identificación del dueño
     * @return ResponseEntity con el dueño encontrado (200) o 404 si no existe
     */
    @GetMapping("/by-identification")
    public ResponseEntity<OwnerResponse> getOwnerByIdentificationNumber(
            @RequestParam("number") String identificationNumber) {
        
        log.debug("Fetching owner with identification number: {}", identificationNumber);
        
        Optional<OwnerResponse> owner = ownerUseCase.findOwnerByIdentificationNumber(identificationNumber);
        
        if (owner.isPresent()) {
            log.debug("Owner found with identification number: {}", identificationNumber);
            return ResponseEntity.ok(owner.get());
        } else {
            log.debug("Owner not found with identification number: {}", identificationNumber);
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== ENDPOINTS DE ACTUALIZACIÓN ==========
    
    /**
     * Actualiza un dueño existente.
     * 
     * PUT /api/v1/owners/{id}
     * 
     * @param ownerId ID del dueño a actualizar
     * @param request DTO con los nuevos datos del dueño
     * @return ResponseEntity con el dueño actualizado (200) o 404 si no existe
     */
    @PutMapping("/{ownerId}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable Long ownerId,
            @Valid @RequestBody UpdateOwnerRequest request) {
        
        log.info("Updating owner with ID: {}", ownerId);
        
        // Convertir DTO de request a comando de dominio
        UpdateOwnerCommand command = UpdateOwnerCommand.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .identificationNumber(request.getIdentificationNumber())
                .notes(request.getNotes())
                .build();
        
        // Ejecutar caso de uso
        OwnerResponse response = ownerUseCase.updateOwner(ownerId, command);
        
        log.info("Owner updated successfully with ID: {}", ownerId);
        return ResponseEntity.ok(response);
    }
    
    // ========== ENDPOINTS DE ELIMINACIÓN ==========
    
    /**
     * Elimina un dueño del sistema.
     * 
     * DELETE /api/v1/owners/{id}
     * 
     * @param ownerId ID del dueño a eliminar
     * @return ResponseEntity con código 204 No Content si se elimina correctamente
     */
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long ownerId) {
        log.info("Deleting owner with ID: {}", ownerId);
        
        ownerUseCase.deleteOwner(ownerId);
        
        log.info("Owner deleted successfully with ID: {}", ownerId);
        return ResponseEntity.noContent().build();
    }
    
    // ========== ENDPOINTS DE VERIFICACIÓN ==========
    
    /**
     * Verifica si existe un dueño con el email especificado.
     * 
     * GET /api/v1/owners/exists/by-email?email=john@example.com
     * 
     * @param email Email a verificar
     * @return ResponseEntity con boolean indicando si existe
     */
    @GetMapping("/exists/by-email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        log.debug("Checking if owner exists with email: {}", email);
        
        boolean exists = ownerUseCase.existsByEmail(email);
        
        log.debug("Owner exists with email {}: {}", email, exists);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Verifica si existe un dueño con el número de identificación especificado.
     * 
     * GET /api/v1/owners/exists/by-identification?number=12345678A
     * 
     * @param identificationNumber Número de identificación a verificar
     * @return ResponseEntity con boolean indicando si existe
     */
    @GetMapping("/exists/by-identification")
    public ResponseEntity<Boolean> existsByIdentificationNumber(
            @RequestParam("number") String identificationNumber) {
        
        log.debug("Checking if owner exists with identification number: {}", identificationNumber);
        
        boolean exists = ownerUseCase.existsByIdentificationNumber(identificationNumber);
        
        log.debug("Owner exists with identification number {}: {}", identificationNumber, exists);
        return ResponseEntity.ok(exists);
    }
    
    // ========== ENDPOINTS DE ESTADÍSTICAS ==========
    
    /**
     * Obtiene el número total de dueños en el sistema.
     * 
     * GET /api/v1/owners/count
     * 
     * @return ResponseEntity con el número total de dueños
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOwners() {
        log.debug("Counting total owners");
        
        long count = ownerUseCase.countOwners();
        
        log.debug("Total owners count: {}", count);
        return ResponseEntity.ok(count);
    }
}
```

#### 🔍 Explicación Detallada del Controlador

**1. Anotaciones Spring:**
```java
@RestController                    // Marca como controlador REST
@RequestMapping("/api/v1/owners")  // Base path para todos los endpoints
@RequiredArgsConstructor          // Inyección de dependencias por constructor
@Slf4j                           // Logging con SLF4J
```

**2. Inyección de Dependencias:**
```java
private final OwnerUseCase ownerUseCase;  // ✅ Solo depende del puerto, no de implementación
```

**3. Validación de Entrada:**
```java
@Valid @RequestBody CreateOwnerRequest request  // ✅ Valida automáticamente con Bean Validation
```

**4. Códigos HTTP Apropiados:**
```java
return ResponseEntity.status(HttpStatus.CREATED).body(response);  // 201 para creación
return ResponseEntity.ok(owner.get());                           // 200 para consulta exitosa
return ResponseEntity.notFound().build();                        // 404 para no encontrado
return ResponseEntity.noContent().build();                       // 204 para eliminación
```

**5. Logging Estratégico:**
```java
log.info("Creating new owner with email: {}", request.getEmail());  // Operaciones importantes
log.debug("Fetching owner with ID: {}", ownerId);                  // Operaciones de consulta
```

**6. Conversión de DTOs:**
```java
// ✅ Convierte DTO de request a comando de dominio
CreateOwnerCommand command = CreateOwnerCommand.builder()
    .firstName(request.getFirstName())
    .lastName(request.getLastName())
    // ...
    .build();
```

#### 🌐 Endpoints Implementados

| Método | Endpoint | Descripción | Código Éxito |
|--------|----------|-------------|--------------|
| `POST` | `/api/v1/owners` | Crear nuevo dueño | 201 Created |
| `GET` | `/api/v1/owners/{id}` | Obtener dueño por ID | 200 OK |
| `GET` | `/api/v1/owners` | Listar todos los dueños | 200 OK |
| `GET` | `/api/v1/owners/by-email?email=...` | Buscar por email | 200 OK |
| `GET` | `/api/v1/owners/search?name=...` | Buscar por nombre | 200 OK |
| `GET` | `/api/v1/owners/by-identification?number=...` | Buscar por ID número | 200 OK |
| `PUT` | `/api/v1/owners/{id}` | Actualizar dueño | 200 OK |
| `DELETE` | `/api/v1/owners/{id}` | Eliminar dueño | 204 No Content |
| `GET` | `/api/v1/owners/exists/by-email?email=...` | Verificar existencia por email | 200 OK |
| `GET` | `/api/v1/owners/exists/by-identification?number=...` | Verificar existencia por ID | 200 OK |
| `GET` | `/api/v1/owners/count` | Contar total de dueños | 200 OK |

#### 🧪 Ejemplos de Uso con curl

**Crear un nuevo dueño:**
```bash
curl -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@email.com",
    "phone": "+34123456789",
    "address": {
      "street": "Calle Mayor 123",
      "city": "Madrid",
      "postalCode": "28001",
      "country": "España"
    },
    "identificationNumber": "12345678A",
    "notes": "Cliente preferente"
  }'
```

**Obtener un dueño por ID:**
```bash
curl -X GET http://localhost:8080/api/v1/owners/1
```

**Buscar dueños por nombre:**
```bash
curl -X GET "http://localhost:8080/api/v1/owners/search?name=Juan"
```

**Actualizar un dueño:**
```bash
curl -X PUT http://localhost:8080/api/v1/owners/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan Carlos",
    "lastName": "Pérez García",
    "email": "juan.carlos@email.com",
    "phone": "+34987654321",
    "address": {
      "street": "Avenida de la Paz 456",
      "city": "Barcelona",
      "postalCode": "08001",
      "country": "España"
    },
    "identificationNumber": "12345678A",
    "notes": "Información actualizada"
  }'
```

**Eliminar un dueño:**
```bash
curl -X DELETE http://localhost:8080/api/v1/owners/1
```

#### 🎯 Manejo de Errores Automático

El controlador no maneja errores explícitamente porque el `GlobalExceptionHandler` del shared se encarga automáticamente:

| Excepción del Dominio | Código HTTP | Response Body |
|----------------------|-------------|---------------|
| `OwnerNotFoundException` | 404 Not Found | `{"message": "Owner not found with id: 1", "timestamp": "..."}` |
| `OwnerAlreadyExistsException` | 409 Conflict | `{"message": "Owner already exists with email: john@example.com", "timestamp": "..."}` |
| `OwnerValidationException` | 400 Bad Request | `{"message": "Owner validation failed: ...", "timestamp": "..."}` |
| Bean Validation errors | 400 Bad Request | `{"message": "Validation failed", "errors": [...], "timestamp": "..."}` |

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
├── application/
└── infrastructure/                                 ← ✅ Nueva capa
    └── adapter/                                    ← ✅ Adaptadores
        └── input/                                  ← ✅ Adaptadores de entrada
            ├── OwnerController.java               ← ✅ Controlador REST
            └── dto/                               ← DTOs de request (próxima sección)
```

#### ✅ Checklist - Controlador REST

- [ ] ✅ Usa `@RestController` y `@RequestMapping`
- [ ] ✅ Inyecta solo el puerto (`OwnerUseCase`), no implementaciones
- [ ] ✅ Valida entrada con `@Valid`
- [ ] ✅ Convierte DTOs de request a comandos de dominio
- [ ] ✅ Retorna códigos HTTP apropiados
- [ ] ✅ Incluye logging para operaciones importantes
- [ ] ✅ No contiene lógica de negocio
- [ ] ✅ Maneja paginación en endpoints de listado
- [ ] ✅ Incluye endpoints de verificación y estadísticas
- [ ] ✅ Sigue convenciones REST (GET, POST, PUT, DELETE)

#### 🚀 Próximo Paso

Con el controlador implementado, el siguiente paso es crear los **DTOs de Request** que el controlador usa para recibir datos del cliente y convertirlos a comandos de dominio.

### DTOs de Request

Los **DTOs de Request** son objetos que reciben datos del cliente (navegador web, aplicación móvil, otro servicio) y los validan antes de convertirlos a comandos de dominio. Son la primera línea de defensa contra datos inválidos y actúan como un contrato claro de qué información necesita cada endpoint.

#### 🎯 Responsabilidades de los DTOs de Request

**✅ Lo que SÍ deben hacer:**
- Recibir datos del cliente HTTP
- Validar formato y restricciones básicas (Bean Validation)
- Convertir strings a tipos apropiados
- Proporcionar mensajes de error claros
- Ser serializables desde/hacia JSON

**❌ Lo que NO deben hacer:**
- Contener lógica de negocio
- Conocer el modelo de dominio
- Realizar validaciones complejas de negocio
- Acceder a bases de datos o servicios externos

#### 🏗️ Creando CreateOwnerRequest

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/adapter/input/dto/CreateOwnerRequest.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.adapter.input.dto;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * DTO para recibir datos de creación de un nuevo dueño desde el cliente HTTP.
 * 
 * Este DTO:
 * - Recibe datos JSON del cliente
 * - Valida formato y restricciones básicas
 * - Se convierte a CreateOwnerCommand en el controlador
 * - Proporciona mensajes de error claros para el cliente
 * 
 * Validaciones incluidas:
 * - Campos obligatorios (@NotBlank, @NotNull)
 * - Longitud de strings (@Size)
 * - Formato de email (@Email)
 * - Validación de Value Objects (@Valid)
 * 
 * Ejemplo de JSON esperado:
 * {
 *   "firstName": "Juan",
 *   "lastName": "Pérez",
 *   "email": "juan.perez@email.com",
 *   "phone": "+34123456789",
 *   "address": {
 *     "street": "Calle Mayor 123",
 *     "city": "Madrid",
 *     "postalCode": "28001",
 *     "country": "España"
 *   },
 *   "identificationNumber": "12345678A",
 *   "notes": "Cliente preferente"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOwnerRequest {
    
    /**
     * Nombre del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @JsonProperty("firstName")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El nombre solo puede contener letras y espacios")
    private String firstName;
    
    /**
     * Apellido del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @JsonProperty("lastName")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El apellido solo puede contener letras y espacios")
    private String lastName;
    
    /**
     * Email del dueño.
     * Obligatorio, debe tener formato de email válido.
     * Se convierte a Value Object Email en el controlador.
     */
    @JsonProperty("email")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    /**
     * Teléfono del dueño.
     * Opcional, pero si se proporciona debe tener formato válido.
     * Se convierte a Value Object Phone en el controlador.
     */
    @JsonProperty("phone")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", 
             message = "El teléfono debe tener un formato válido (ej: +34123456789)")
    private String phone;
    
    /**
     * Dirección del dueño.
     * Opcional, pero si se proporciona debe ser válida.
     * Se valida como Value Object Address.
     */
    @JsonProperty("address")
    @Valid
    private AddressRequest address;
    
    /**
     * Número de identificación del dueño (DNI, NIE, pasaporte, etc.).
     * Opcional, máximo 20 caracteres alfanuméricos.
     */
    @JsonProperty("identificationNumber")
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", 
             message = "El número de identificación solo puede contener letras y números")
    private String identificationNumber;
    
    /**
     * Notas adicionales sobre el dueño.
     * Opcional, máximo 500 caracteres.
     */
    @JsonProperty("notes")
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
    
    /**
     * Convierte el string de email a Value Object Email.
     * Método de conveniencia para el controlador.
     * 
     * @return Email Value Object o null si el email es null/vacío
     */
    public Email getEmail() {
        return (email != null && !email.trim().isEmpty()) ? Email.of(email.trim()) : null;
    }
    
    /**
     * Convierte el string de teléfono a Value Object Phone.
     * Método de conveniencia para el controlador.
     * 
     * @return Phone Value Object o null si el teléfono es null/vacío
     */
    public Phone getPhone() {
        return (phone != null && !phone.trim().isEmpty()) ? Phone.of(phone.trim()) : null;
    }
    
    /**
     * Convierte el AddressRequest a Value Object Address.
     * Método de conveniencia para el controlador.
     * 
     * @return Address Value Object o null si la dirección es null
     */
    public Address getAddress() {
        return (address != null) ? address.toAddress() : null;
    }
    
    /**
     * DTO anidado para recibir datos de dirección.
     * Se valida independientemente y se convierte a Value Object Address.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        
        @JsonProperty("street")
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 100, message = "La calle no puede exceder 100 caracteres")
        private String street;
        
        @JsonProperty("city")
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
                 message = "La ciudad solo puede contener letras y espacios")
        private String city;
        
        @JsonProperty("postalCode")
        @NotBlank(message = "El código postal es obligatorio")
        @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
        @Pattern(regexp = "^[0-9]{5}$", 
                 message = "El código postal debe tener 5 dígitos")
        private String postalCode;
        
        @JsonProperty("country")
        @NotBlank(message = "El país es obligatorio")
        @Size(max = 50, message = "El país no puede exceder 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
                 message = "El país solo puede contener letras y espacios")
        private String country;
        
        /**
         * Convierte este DTO a Value Object Address.
         * 
         * @return Address Value Object
         */
        public Address toAddress() {
            return Address.of(street.trim(), city.trim(), postalCode.trim(), country.trim());
        }
    }
}
```

#### 🏗️ Creando UpdateOwnerRequest

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/adapter/input/dto/UpdateOwnerRequest.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.adapter.input.dto;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * DTO para recibir datos de actualización de un dueño existente desde el cliente HTTP.
 * 
 * Este DTO es muy similar a CreateOwnerRequest pero está específicamente diseñado
 * para operaciones de actualización. Podría incluir validaciones diferentes
 * o campos adicionales específicos de actualización en el futuro.
 * 
 * Diferencias con CreateOwnerRequest:
 * - No incluye el ID (se pasa en la URL)
 * - Podría tener validaciones específicas de actualización
 * - Todos los campos son obligatorios (actualización completa)
 * 
 * Ejemplo de JSON esperado:
 * {
 *   "firstName": "Juan Carlos",
 *   "lastName": "Pérez García",
 *   "email": "juan.carlos@email.com",
 *   "phone": "+34987654321",
 *   "address": {
 *     "street": "Avenida de la Paz 456",
 *     "city": "Barcelona",
 *     "postalCode": "08001",
 *     "country": "España"
 *   },
 *   "identificationNumber": "12345678A",
 *   "notes": "Información actualizada"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOwnerRequest {
    
    /**
     * Nombre actualizado del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @JsonProperty("firstName")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El nombre solo puede contener letras y espacios")
    private String firstName;
    
    /**
     * Apellido actualizado del dueño.
     * Obligatorio, no puede estar vacío, máximo 50 caracteres.
     */
    @JsonProperty("lastName")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El apellido solo puede contener letras y espacios")
    private String lastName;
    
    /**
     * Email actualizado del dueño.
     * Obligatorio, debe tener formato de email válido.
     * Se validará que no esté en uso por otro dueño.
     */
    @JsonProperty("email")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    /**
     * Teléfono actualizado del dueño.
     * Opcional, pero si se proporciona debe tener formato válido.
     */
    @JsonProperty("phone")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", 
             message = "El teléfono debe tener un formato válido (ej: +34123456789)")
    private String phone;
    
    /**
     * Dirección actualizada del dueño.
     * Opcional, pero si se proporciona debe ser válida.
     */
    @JsonProperty("address")
    @Valid
    private AddressRequest address;
    
    /**
     * Número de identificación actualizado del dueño.
     * Opcional, máximo 20 caracteres alfanuméricos.
     */
    @JsonProperty("identificationNumber")
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", 
             message = "El número de identificación solo puede contener letras y números")
    private String identificationNumber;
    
    /**
     * Notas actualizadas sobre el dueño.
     * Opcional, máximo 500 caracteres.
     */
    @JsonProperty("notes")
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
    
    /**
     * Convierte el string de email a Value Object Email.
     * Método de conveniencia para el controlador.
     * 
     * @return Email Value Object o null si el email es null/vacío
     */
    public Email getEmail() {
        return (email != null && !email.trim().isEmpty()) ? Email.of(email.trim()) : null;
    }
    
    /**
     * Convierte el string de teléfono a Value Object Phone.
     * Método de conveniencia para el controlador.
     * 
     * @return Phone Value Object o null si el teléfono es null/vacío
     */
    public Phone getPhone() {
        return (phone != null && !phone.trim().isEmpty()) ? Phone.of(phone.trim()) : null;
    }
    
    /**
     * Convierte el AddressRequest a Value Object Address.
     * Método de conveniencia para el controlador.
     * 
     * @return Address Value Object o null si la dirección es null
     */
    public Address getAddress() {
        return (address != null) ? address.toAddress() : null;
    }
    
    /**
     * DTO anidado para recibir datos de dirección en actualizaciones.
     * Idéntico al de CreateOwnerRequest para mantener consistencia.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        
        @JsonProperty("street")
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 100, message = "La calle no puede exceder 100 caracteres")
        private String street;
        
        @JsonProperty("city")
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
                 message = "La ciudad solo puede contener letras y espacios")
        private String city;
        
        @JsonProperty("postalCode")
        @NotBlank(message = "El código postal es obligatorio")
        @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
        @Pattern(regexp = "^[0-9]{5}$", 
                 message = "El código postal debe tener 5 dígitos")
        private String postalCode;
        
        @JsonProperty("country")
        @NotBlank(message = "El país es obligatorio")
        @Size(max = 50, message = "El país no puede exceder 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
                 message = "El país solo puede contener letras y espacios")
        private String country;
        
        /**
         * Convierte este DTO a Value Object Address.
         * 
         * @return Address Value Object
         */
        public Address toAddress() {
            return Address.of(street.trim(), city.trim(), postalCode.trim(), country.trim());
        }
    }
}
```

#### 🔍 Explicación Detallada de los DTOs

**1. Anotaciones de Validación:**
```java
@NotBlank(message = "El nombre es obligatorio")           // Campo obligatorio, no vacío
@Size(max = 50, message = "...")                         // Longitud máxima
@Email(message = "El email debe tener un formato válido") // Formato de email
@Pattern(regexp = "...", message = "...")                // Expresión regular personalizada
@Valid                                                    // Validar objeto anidado
```

**2. Anotaciones JSON:**
```java
@JsonProperty("firstName")  // ✅ Mapeo explícito de campos JSON
```

**3. Conversión a Value Objects:**
```java
public Email getEmail() {
    return (email != null && !email.trim().isEmpty()) ? Email.of(email.trim()) : null;
}
```
- Convierte strings a Value Objects
- Maneja casos null y vacíos
- Aplica trim automáticamente

**4. DTOs Anidados:**
```java
public static class AddressRequest {  // ✅ DTO anidado para direcciones
    // Validaciones específicas para direcciones
    public Address toAddress() {      // ✅ Conversión a Value Object
        return Address.of(street.trim(), city.trim(), postalCode.trim(), country.trim());
    }
}
```

#### 🎯 Validaciones Implementadas

| Campo | Validaciones | Ejemplo Válido | Ejemplo Inválido |
|-------|-------------|----------------|------------------|
| `firstName` | `@NotBlank`, `@Size(max=50)`, `@Pattern` | "Juan" | "", "Juan123" |
| `lastName` | `@NotBlank`, `@Size(max=50)`, `@Pattern` | "Pérez" | null, "Pérez@" |
| `email` | `@NotBlank`, `@Email`, `@Size(max=100)` | "juan@email.com" | "juan", "juan@" |
| `phone` | `@Pattern` (opcional) | "+34123456789" | "123abc" |
| `identificationNumber` | `@Size(max=20)`, `@Pattern` | "12345678A" | "123-456-789" |
| `notes` | `@Size(max=500)` | "Cliente preferente" | (texto > 500 chars) |

#### 🧪 Ejemplos de JSON Válidos

**CreateOwnerRequest completo:**
```json
{
  "firstName": "María",
  "lastName": "González",
  "email": "maria.gonzalez@email.com",
  "phone": "+34666777888",
  "address": {
    "street": "Plaza España 10",
    "city": "Sevilla",
    "postalCode": "41001",
    "country": "España"
  },
  "identificationNumber": "87654321B",
  "notes": "Veterinaria de confianza"
}
```

**CreateOwnerRequest mínimo:**
```json
{
  "firstName": "Pedro",
  "lastName": "Martín",
  "email": "pedro.martin@email.com"
}
```

#### 🚨 Ejemplos de Errores de Validación

**Request inválido:**
```json
{
  "firstName": "",
  "lastName": "González123",
  "email": "maria@",
  "phone": "123abc",
  "address": {
    "street": "",
    "city": "Sevilla",
    "postalCode": "410",
    "country": "España"
  }
}
```

**Response de error (400 Bad Request):**
```json
{
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00Z",
  "errors": [
    {
      "field": "firstName",
      "message": "El nombre es obligatorio"
    },
    {
      "field": "lastName", 
      "message": "El apellido solo puede contener letras y espacios"
    },
    {
      "field": "email",
      "message": "El email debe tener un formato válido"
    },
    {
      "field": "phone",
      "message": "El teléfono debe tener un formato válido (ej: +34123456789)"
    },
    {
      "field": "address.street",
      "message": "La calle es obligatoria"
    },
    {
      "field": "address.postalCode",
      "message": "El código postal debe tener 5 dígitos"
    }
  ]
}
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
├── application/
└── infrastructure/
    └── adapter/
        └── input/
            ├── OwnerController.java
            └── dto/                                    ← ✅ DTOs de request
                ├── CreateOwnerRequest.java            ← ✅ DTO para crear
                └── UpdateOwnerRequest.java            ← ✅ DTO para actualizar
```

#### ✅ Checklist - DTOs de Request

- [ ] ✅ Usan Bean Validation (`@NotBlank`, `@Email`, `@Pattern`, etc.)
- [ ] ✅ Incluyen `@JsonProperty` para mapeo explícito
- [ ] ✅ Tienen métodos de conversión a Value Objects
- [ ] ✅ Manejan DTOs anidados (AddressRequest)
- [ ] ✅ Proporcionan mensajes de error claros en español
- [ ] ✅ Validan formatos específicos (email, teléfono, código postal)
- [ ] ✅ Incluyen validaciones de longitud apropiadas
- [ ] ✅ Son inmutables o tienen setters controlados
- [ ] ✅ Incluyen documentación clara de uso

#### 🚀 Próximo Paso

Con los DTOs de Request implementados, el siguiente paso es crear los **ejemplos de persistencia JPA** que incluyen las entidades JPA, repositorios y adaptadores que conectan el dominio con la base de datos.

### Persistencia JPA

La **persistencia JPA** es donde tu dominio se conecta con la base de datos. Esta sección incluye las entidades JPA que mapean el modelo de dominio a tablas de base de datos, los repositorios que realizan las consultas, y los adaptadores que implementan los puertos de salida del dominio.

#### 🎯 Componentes de la Persistencia JPA

**Entidad JPA (`OwnerEntity`):**
- Mapea el modelo de dominio `Owner` a una tabla de base de datos
- Usa convertidores para Value Objects
- Incluye anotaciones JPA para relaciones y constraints

**Repositorio JPA (`JpaOwnerRepository`):**
- Interface que extiende `JpaRepository`
- Define consultas personalizadas con `@Query`
- Proporciona métodos de acceso a datos específicos

**Adaptador de Repositorio (`JpaOwnerRepositoryAdapter`):**
- Implementa el puerto de salida `OwnerRepositoryPort`
- Convierte entre entidades JPA y modelos de dominio
- Maneja la lógica de persistencia

#### 🏗️ Creando la Entidad JPA - OwnerEntity

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/persistence/entity/OwnerEntity.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.persistence.entity;

import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.AddressConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.EmailConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.PhoneConverter;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que mapea el modelo de dominio Owner a la tabla 'owner' en la base de datos.
 * 
 * Esta entidad:
 * - Extiende BaseEntity para campos comunes (id, createdAt, updatedAt)
 * - Usa convertidores para Value Objects (Email, Phone, Address)
 * - Define constraints de base de datos (unique, not null, length)
 * - Se convierte hacia/desde el modelo de dominio Owner
 * 
 * Tabla resultante:
 * CREATE TABLE owner (
 *   owner_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *   first_name VARCHAR(50) NOT NULL,
 *   last_name VARCHAR(50) NOT NULL,
 *   email VARCHAR(100) NOT NULL UNIQUE,
 *   phone VARCHAR(20),
 *   address TEXT,
 *   identification_number VARCHAR(20) UNIQUE,
 *   notes TEXT,
 *   created_at TIMESTAMP NOT NULL,
 *   updated_at TIMESTAMP NOT NULL
 * );
 */
@Entity
@Table(name = "owner", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_owner_email", columnNames = "email"),
           @UniqueConstraint(name = "uk_owner_identification", columnNames = "identification_number")
       },
       indexes = {
           @Index(name = "idx_owner_email", columnList = "email"),
           @Index(name = "idx_owner_name", columnList = "first_name, last_name"),
           @Index(name = "idx_owner_identification", columnList = "identification_number")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerEntity extends BaseEntity {
    
    /**
     * ID único del dueño.
     * Clave primaria auto-generada.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long ownerId;
    
    /**
     * Nombre del dueño.
     * Obligatorio, máximo 50 caracteres.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    /**
     * Apellido del dueño.
     * Obligatorio, máximo 50 caracteres.
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    /**
     * Email del dueño.
     * Obligatorio, único, máximo 100 caracteres.
     * Se almacena como string pero se convierte a/desde Value Object Email.
     */
    @Column(name = "email", nullable = false, length = 100, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email;
    
    /**
     * Teléfono del dueño.
     * Opcional, máximo 20 caracteres.
     * Se almacena como string pero se convierte a/desde Value Object Phone.
     */
    @Column(name = "phone", length = 20)
    @Convert(converter = PhoneConverter.class)
    private Phone phone;
    
    /**
     * Dirección del dueño.
     * Opcional, se almacena como JSON/TEXT.
     * Se convierte a/desde Value Object Address.
     */
    @Column(name = "address", columnDefinition = "TEXT")
    @Convert(converter = AddressConverter.class)
    private Address address;
    
    /**
     * Número de identificación del dueño (DNI, NIE, pasaporte, etc.).
     * Opcional, único si se proporciona, máximo 20 caracteres.
     */
    @Column(name = "identification_number", length = 20, unique = true)
    private String identificationNumber;
    
    /**
     * Notas adicionales sobre el dueño.
     * Opcional, texto largo.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Convierte esta entidad JPA al modelo de dominio Owner.
     * 
     * @return Instancia del modelo de dominio Owner
     */
    public Owner toDomainModel() {
        return Owner.builder()
                .ownerId(this.ownerId)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .email(this.email)
                .phone(this.phone)
                .address(this.address)
                .identificationNumber(this.identificationNumber)
                .notes(this.notes)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
    
    /**
     * Crea una entidad JPA desde el modelo de dominio Owner.
     * 
     * @param owner Modelo de dominio Owner
     * @return Nueva instancia de OwnerEntity
     */
    public static OwnerEntity fromDomainModel(Owner owner) {
        OwnerEntity entity = OwnerEntity.builder()
                .ownerId(owner.getOwnerId())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .email(owner.getEmail())
                .phone(owner.getPhone())
                .address(owner.getAddress())
                .identificationNumber(owner.getIdentificationNumber())
                .notes(owner.getNotes())
                .build();
        
        // Establecer fechas desde el modelo de dominio
        if (owner.getCreatedAt() != null) {
            entity.setCreatedAt(owner.getCreatedAt());
        }
        if (owner.getUpdatedAt() != null) {
            entity.setUpdatedAt(owner.getUpdatedAt());
        }
        
        return entity;
    }
    
    /**
     * Actualiza esta entidad con datos del modelo de dominio Owner.
     * Útil para operaciones de actualización.
     * 
     * @param owner Modelo de dominio Owner con datos actualizados
     */
    public void updateFromDomainModel(Owner owner) {
        this.firstName = owner.getFirstName();
        this.lastName = owner.getLastName();
        this.email = owner.getEmail();
        this.phone = owner.getPhone();
        this.address = owner.getAddress();
        this.identificationNumber = owner.getIdentificationNumber();
        this.notes = owner.getNotes();
        this.setUpdatedAt(owner.getUpdatedAt());
    }
    
    @Override
    public String toString() {
        return String.format("OwnerEntity{ownerId=%d, fullName='%s %s', email='%s'}", 
                ownerId, firstName, lastName, email != null ? email.getValue() : "null");
    }
}
```

#### 🏗️ Creando el Repositorio JPA - JpaOwnerRepository

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/persistence/repository/JpaOwnerRepository.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
import com.datavet.datavet.shared.domain.valueobject.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad OwnerEntity.
 * 
 * Este repositorio:
 * - Extiende JpaRepository para operaciones CRUD básicas
 * - Define consultas personalizadas con @Query
 * - Proporciona métodos de búsqueda específicos del dominio
 * - Maneja paginación para consultas que pueden retornar muchos resultados
 * 
 * Operaciones disponibles:
 * - CRUD básico (heredado de JpaRepository)
 * - Búsquedas por email, nombre, número de identificación
 * - Verificaciones de existencia
 * - Consultas con paginación
 * - Estadísticas y conteos
 */
@Repository
public interface JpaOwnerRepository extends JpaRepository<OwnerEntity, Long> {
    
    // ========== CONSULTAS POR CAMPOS ÚNICOS ==========
    
    /**
     * Busca un dueño por su email.
     * El email es único, por lo que retorna Optional.
     * 
     * @param email Email del dueño a buscar
     * @return Optional con la entidad encontrada, o empty si no existe
     */
    Optional<OwnerEntity> findByEmail(Email email);
    
    /**
     * Busca un dueño por su número de identificación.
     * El número de identificación es único cuando está presente.
     * 
     * @param identificationNumber Número de identificación del dueño
     * @return Optional con la entidad encontrada, o empty si no existe
     */
    Optional<OwnerEntity> findByIdentificationNumber(String identificationNumber);
    
    // ========== BÚSQUEDAS POR NOMBRE ==========
    
    /**
     * Busca dueños cuyo nombre contenga el texto especificado (case-insensitive).
     * Busca tanto en firstName como en lastName.
     * 
     * @param name Texto a buscar en el nombre
     * @return Lista de entidades que coinciden con el criterio
     */
    @Query("SELECT o FROM OwnerEntity o WHERE " +
           "LOWER(o.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(o.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OwnerEntity> findByNameContaining(@Param("name") String name);
    
    /**
     * Busca dueños por nombre con paginación.
     * Versión paginada de findByNameContaining.
     * 
     * @param name Texto a buscar en el nombre
     * @param pageable Información de paginación
     * @return Página de entidades que coinciden con el criterio
     */
    @Query("SELECT o FROM OwnerEntity o WHERE " +
           "LOWER(o.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(o.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<OwnerEntity> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    /**
     * Busca dueños por nombre completo exacto (case-insensitive).
     * Útil para búsquedas más precisas.
     * 
     * @param firstName Nombre exacto
     * @param lastName Apellido exacto
     * @return Lista de entidades que coinciden exactamente
     */
    @Query("SELECT o FROM OwnerEntity o WHERE " +
           "LOWER(o.firstName) = LOWER(:firstName) AND " +
           "LOWER(o.lastName) = LOWER(:lastName)")
    List<OwnerEntity> findByFirstNameAndLastNameIgnoreCase(
            @Param("firstName") String firstName, 
            @Param("lastName") String lastName);
    
    // ========== BÚSQUEDAS POR DIRECCIÓN ==========
    
    /**
     * Busca dueños por ciudad en su dirección.
     * Nota: Requiere que el convertidor de Address permita consultas por ciudad.
     * Esta consulta podría necesitar ajustes según cómo se almacene Address.
     * 
     * @param city Ciudad a buscar
     * @return Lista de entidades en la ciudad especificada
     */
    @Query("SELECT o FROM OwnerEntity o WHERE o.address IS NOT NULL AND " +
           "LOWER(o.address) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<OwnerEntity> findByAddressContainingCity(@Param("city") String city);
    
    // ========== VERIFICACIONES DE EXISTENCIA ==========
    
    /**
     * Verifica si existe un dueño con el email especificado.
     * Más eficiente que findByEmail cuando solo necesitas verificar existencia.
     * 
     * @param email Email a verificar
     * @return true si existe un dueño con ese email, false en caso contrario
     */
    boolean existsByEmail(Email email);
    
    /**
     * Verifica si existe un dueño con el número de identificación especificado.
     * 
     * @param identificationNumber Número de identificación a verificar
     * @return true si existe un dueño con ese número, false en caso contrario
     */
    boolean existsByIdentificationNumber(String identificationNumber);
    
    /**
     * Verifica si existe otro dueño (diferente al ID especificado) con el email dado.
     * Útil para validaciones de actualización.
     * 
     * @param email Email a verificar
     * @param ownerId ID del dueño a excluir de la búsqueda
     * @return true si existe otro dueño con ese email, false en caso contrario
     */
    boolean existsByEmailAndOwnerIdNot(Email email, Long ownerId);
    
    /**
     * Verifica si existe otro dueño con el número de identificación dado.
     * Similar a existsByEmailAndOwnerIdNot pero para número de identificación.
     * 
     * @param identificationNumber Número de identificación a verificar
     * @param ownerId ID del dueño a excluir de la búsqueda
     * @return true si existe otro dueño con ese número, false en caso contrario
     */
    boolean existsByIdentificationNumberAndOwnerIdNot(String identificationNumber, Long ownerId);
    
    // ========== CONSULTAS DE ESTADÍSTICAS ==========
    
    /**
     * Cuenta dueños por ciudad.
     * Útil para reportes de distribución geográfica.
     * 
     * @param city Ciudad a contar
     * @return Número de dueños en la ciudad especificada
     */
    @Query("SELECT COUNT(o) FROM OwnerEntity o WHERE o.address IS NOT NULL AND " +
           "LOWER(o.address) LIKE LOWER(CONCAT('%', :city, '%'))")
    long countByAddressContainingCity(@Param("city") String city);
    
    /**
     * Cuenta dueños que tienen información de contacto completa.
     * Útil para métricas de calidad de datos.
     * 
     * @return Número de dueños con email, teléfono y dirección
     */
    @Query("SELECT COUNT(o) FROM OwnerEntity o WHERE " +
           "o.email IS NOT NULL AND o.phone IS NOT NULL AND o.address IS NOT NULL")
    long countByCompleteContactInfo();
    
    /**
     * Obtiene estadísticas básicas de dueños.
     * Retorna un objeto con conteos útiles para dashboards.
     * 
     * @return Array con [total, conTeléfono, conDirección, conIdentificación]
     */
    @Query("SELECT " +
           "COUNT(o), " +
           "SUM(CASE WHEN o.phone IS NOT NULL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN o.address IS NOT NULL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN o.identificationNumber IS NOT NULL THEN 1 ELSE 0 END) " +
           "FROM OwnerEntity o")
    Object[] getOwnerStatistics();
    
    // ========== CONSULTAS DE ORDENAMIENTO ==========
    
    /**
     * Obtiene todos los dueños ordenados por nombre completo.
     * Útil para listados alfabéticos.
     * 
     * @return Lista de entidades ordenadas por firstName, lastName
     */
    @Query("SELECT o FROM OwnerEntity o ORDER BY o.firstName, o.lastName")
    List<OwnerEntity> findAllOrderByName();
    
    /**
     * Obtiene dueños recientes (últimos 30 días).
     * Útil para reportes de nuevos clientes.
     * 
     * @return Lista de entidades creadas recientemente
     */
    @Query("SELECT o FROM OwnerEntity o WHERE o.createdAt >= CURRENT_DATE - 30 ORDER BY o.createdAt DESC")
    List<OwnerEntity> findRecentOwners();
    
    // ========== CONSULTAS PERSONALIZADAS AVANZADAS ==========
    
    /**
     * Busca dueños con criterios múltiples.
     * Consulta flexible que permite buscar por varios campos a la vez.
     * 
     * @param name Texto a buscar en nombre (opcional)
     * @param email Email a buscar (opcional)
     * @param city Ciudad a buscar (opcional)
     * @return Lista de entidades que coinciden con los criterios
     */
    @Query("SELECT o FROM OwnerEntity o WHERE " +
           "(:name IS NULL OR LOWER(o.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR o.email = :email) AND " +
           "(:city IS NULL OR (o.address IS NOT NULL AND LOWER(o.address) LIKE LOWER(CONCAT('%', :city, '%'))))")
    List<OwnerEntity> findByMultipleCriteria(
            @Param("name") String name,
            @Param("email") Email email,
            @Param("city") String city);
}
```

#### 🏗️ Creando el Adaptador de Repositorio - JpaOwnerRepositoryAdapter

**Ubicación del archivo:**
```
src/main/java/com/datavet/datavet/owner/infrastructure/persistence/repository/JpaOwnerRepositoryAdapter.java
```

**Código completo:**

```java
package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de salida OwnerRepositoryPort usando JPA.
 * 
 * Este adaptador:
 * - Implementa OwnerRepositoryPort (puerto de salida del dominio)
 * - Usa JpaOwnerRepository para acceso a datos
 * - Convierte entre modelos de dominio (Owner) y entidades JPA (OwnerEntity)
 * - Maneja la lógica de persistencia y consultas
 * - Proporciona logging para operaciones importantes
 * 
 * Responsabilidades:
 * - Traducir llamadas del dominio a operaciones JPA
 * - Convertir entidades JPA a modelos de dominio y viceversa
 * - Manejar excepciones de persistencia
 * - Optimizar consultas para rendimiento
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaOwnerRepositoryAdapter implements OwnerRepositoryPort {
    
    private final JpaOwnerRepository jpaRepository;
    
    // ========== OPERACIONES BÁSICAS CRUD ==========
    
    @Override
    public Owner save(Owner owner) {
        log.debug("Saving owner: {}", owner.getFullName());
        
        OwnerEntity entity;
        if (owner.getId() == null) {
            // Nuevo dueño - crear entidad
            entity = OwnerEntity.fromDomainModel(owner);
            log.debug("Creating new owner entity");
        } else {
            // Dueño existente - actualizar entidad
            entity = jpaRepository.findById(owner.getId())
                    .orElse(OwnerEntity.fromDomainModel(owner));
            entity.updateFromDomainModel(owner);
            log.debug("Updating existing owner entity with ID: {}", owner.getId());
        }
        
        OwnerEntity savedEntity = jpaRepository.save(entity);
        Owner savedOwner = savedEntity.toDomainModel();
        
        log.debug("Owner saved successfully with ID: {}", savedOwner.getId());
        return savedOwner;
    }
    
    @Override
    public Optional<Owner> findById(Long id) {
        log.debug("Finding owner by ID: {}", id);
        
        Optional<OwnerEntity> entity = jpaRepository.findById(id);
        Optional<Owner> owner = entity.map(OwnerEntity::toDomainModel);
        
        if (owner.isPresent()) {
            log.debug("Owner found with ID: {}", id);
        } else {
            log.debug("Owner not found with ID: {}", id);
        }
        
        return owner;
    }
    
    @Override
    public List<Owner> findAll() {
        log.debug("Finding all owners");
        
        List<OwnerEntity> entities = jpaRepository.findAll();
        List<Owner> owners = entities.stream()
                .map(OwnerEntity::toDomainModel)
                .collect(Collectors.toList());
        
        log.debug("Found {} owners", owners.size());
        return owners;
    }
    
    @Override
    public void deleteById(Long id) {
        log.info("Deleting owner with ID: {}", id);
        
        if (!jpaRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent owner with ID: {}", id);
            return;
        }
        
        jpaRepository.deleteById(id);
        log.info("Owner deleted successfully with ID: {}", id);
    }
    
    @Override
    public boolean existsById(Long id) {
        log.debug("Checking if owner exists with ID: {}", id);
        
        boolean exists = jpaRepository.existsById(id);
        
        log.debug("Owner exists with ID {}: {}", id, exists);
        return exists;
    }
    
    @Override
    public long count() {
        log.debug("Counting total owners");
        
        long count = jpaRepository.count();
        
        log.debug("Total owners count: {}", count);
        return count;
    }
    
    // ========== CONSULTAS ESPECÍFICAS DEL DOMINIO ==========
    
    @Override
    public Optional<Owner> findByEmail(String email) {
        log.debug("Finding owner by email: {}", email);
        
        // Convertir string a Value Object Email para la consulta
        Email emailVO = Email.of(email);
        Optional<OwnerEntity> entity = jpaRepository.findByEmail(emailVO);
        Optional<Owner> owner = entity.map(OwnerEntity::toDomainModel);
        
        if (owner.isPresent()) {
            log.debug("Owner found with email: {}", email);
        } else {
            log.debug("Owner not found with email: {}", email);
        }
        
        return owner;
    }
    
    @Override
    public List<Owner> findByNameContaining(String name) {
        log.debug("Finding owners with name containing: {}", name);
        
        List<OwnerEntity> entities = jpaRepository.findByNameContaining(name);
        List<Owner> owners = entities.stream()
                .map(OwnerEntity::toDomainModel)
                .collect(Collectors.toList());
        
        log.debug("Found {} owners with name containing: {}", owners.size(), name);
        return owners;
    }
    
    @Override
    public Optional<Owner> findByIdentificationNumber(String identificationNumber) {
        log.debug("Finding owner by identification number: {}", identificationNumber);
        
        Optional<OwnerEntity> entity = jpaRepository.findByIdentificationNumber(identificationNumber);
        Optional<Owner> owner = entity.map(OwnerEntity::toDomainModel);
        
        if (owner.isPresent()) {
            log.debug("Owner found with identification number: {}", identificationNumber);
        } else {
            log.debug("Owner not found with identification number: {}", identificationNumber);
        }
        
        return owner;
    }
    
    @Override
    public List<Owner> findByAddressCity(String city) {
        log.debug("Finding owners by city: {}", city);
        
        List<OwnerEntity> entities = jpaRepository.findByAddressContainingCity(city);
        List<Owner> owners = entities.stream()
                .map(OwnerEntity::toDomainModel)
                .collect(Collectors.toList());
        
        log.debug("Found {} owners in city: {}", owners.size(), city);
        return owners;
    }
    
    // ========== OPERACIONES DE VERIFICACIÓN ==========
    
    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if owner exists with email: {}", email);
        
        Email emailVO = Email.of(email);
        boolean exists = jpaRepository.existsByEmail(emailVO);
        
        log.debug("Owner exists with email {}: {}", email, exists);
        return exists;
    }
    
    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        log.debug("Checking if owner exists with identification number: {}", identificationNumber);
        
        boolean exists = jpaRepository.existsByIdentificationNumber(identificationNumber);
        
        log.debug("Owner exists with identification number {}: {}", identificationNumber, exists);
        return exists;
    }
    
    @Override
    public boolean existsByEmailAndIdNot(String email, Long excludeOwnerId) {
        log.debug("Checking if another owner exists with email: {} (excluding ID: {})", email, excludeOwnerId);
        
        Email emailVO = Email.of(email);
        boolean exists = jpaRepository.existsByEmailAndOwnerIdNot(emailVO, excludeOwnerId);
        
        log.debug("Another owner exists with email {} (excluding ID {}): {}", email, excludeOwnerId, exists);
        return exists;
    }
    
    @Override
    public boolean existsByIdentificationNumberAndIdNot(String identificationNumber, Long excludeOwnerId) {
        log.debug("Checking if another owner exists with identification number: {} (excluding ID: {})", 
                 identificationNumber, excludeOwnerId);
        
        boolean exists = jpaRepository.existsByIdentificationNumberAndOwnerIdNot(identificationNumber, excludeOwnerId);
        
        log.debug("Another owner exists with identification number {} (excluding ID {}): {}", 
                 identificationNumber, excludeOwnerId, exists);
        return exists;
    }
    
    // ========== OPERACIONES DE PAGINACIÓN ==========
    
    @Override
    public List<Owner> findAll(int page, int size) {
        log.debug("Finding owners with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        List<OwnerEntity> entities = jpaRepository.findAll(pageable).getContent();
        List<Owner> owners = entities.stream()
                .map(OwnerEntity::toDomainModel)
                .collect(Collectors.toList());
        
        log.debug("Found {} owners on page {} (size: {})", owners.size(), page, size);
        return owners;
    }
    
    @Override
    public List<Owner> findByNameContaining(String name, int page, int size) {
        log.debug("Finding owners by name with pagination - name: {}, page: {}, size: {}", name, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        List<OwnerEntity> entities = jpaRepository.findByNameContaining(name, pageable).getContent();
        List<Owner> owners = entities.stream()
                .map(OwnerEntity::toDomainModel)
                .collect(Collectors.toList());
        
        log.debug("Found {} owners with name containing '{}' on page {} (size: {})", 
                 owners.size(), name, page, size);
        return owners;
    }
    
    // ========== OPERACIONES DE ESTADÍSTICAS ==========
    
    @Override
    public long countByAddressCity(String city) {
        log.debug("Counting owners by city: {}", city);
        
        long count = jpaRepository.countByAddressContainingCity(city);
        
        log.debug("Owners count in city {}: {}", city, count);
        return count;
    }
    
    @Override
    public long countByCompleteContactInfo() {
        log.debug("Counting owners with complete contact info");
        
        long count = jpaRepository.countByCompleteContactInfo();
        
        log.debug("Owners with complete contact info: {}", count);
        return count;
    }
}
```

#### 🔍 Explicación Detallada de la Persistencia JPA

**1. Entidad JPA (`OwnerEntity`):**
```java
@Entity
@Table(name = "owner", uniqueConstraints = {...}, indexes = {...})  // ✅ Configuración de tabla
@Convert(converter = EmailConverter.class)                          // ✅ Convertidor para Value Objects
```

**2. Repositorio JPA (`JpaOwnerRepository`):**
```java
@Query("SELECT o FROM OwnerEntity o WHERE ...")  // ✅ Consultas personalizadas
boolean existsByEmailAndOwnerIdNot(...)          // ✅ Métodos de verificación complejos
```

**3. Adaptador (`JpaOwnerRepositoryAdapter`):**
```java
@Component                                       // ✅ Componente Spring
implements OwnerRepositoryPort                   // ✅ Implementa puerto de salida
entity.map(OwnerEntity::toDomainModel)          // ✅ Conversión a modelo de dominio
```

#### 🗄️ Esquema de Base de Datos Resultante

```sql
CREATE TABLE owner (
    owner_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    identification_number VARCHAR(20) UNIQUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Índices para optimizar consultas
CREATE INDEX idx_owner_email ON owner(email);
CREATE INDEX idx_owner_name ON owner(first_name, last_name);
CREATE INDEX idx_owner_identification ON owner(identification_number);

-- Constraints únicos
ALTER TABLE owner ADD CONSTRAINT uk_owner_email UNIQUE (email);
ALTER TABLE owner ADD CONSTRAINT uk_owner_identification UNIQUE (identification_number);
```

#### 🔄 Flujo de Conversión de Datos

**Dominio → JPA (Guardar):**
```
Owner (dominio) → OwnerEntity.fromDomainModel() → JPA save() → Base de datos
```

**JPA → Dominio (Consultar):**
```
Base de datos → JPA find() → OwnerEntity.toDomainModel() → Owner (dominio)
```

**Value Objects:**
```
Email (dominio) → EmailConverter → String (DB) → EmailConverter → Email (dominio)
```

#### 📁 Estructura de Carpetas Actualizada

```
src/main/java/com/datavet/datavet/owner/
├── domain/
├── application/
└── infrastructure/
    ├── adapter/
    │   └── input/
    └── persistence/                                    ← ✅ Persistencia JPA
        ├── entity/                                     ← ✅ Entidades JPA
        │   └── OwnerEntity.java                       ← ✅ Mapeo a tabla
        └── repository/                                 ← ✅ Repositorios
            ├── JpaOwnerRepository.java                ← ✅ Interface JPA
            └── JpaOwnerRepositoryAdapter.java         ← ✅ Adaptador del puerto
```

#### ✅ Checklist - Persistencia JPA

- [ ] ✅ Entidad JPA extiende `BaseEntity`
- [ ] ✅ Usa convertidores para Value Objects
- [ ] ✅ Define constraints únicos apropiados
- [ ] ✅ Incluye índices para consultas frecuentes
- [ ] ✅ Repositorio JPA extiende `JpaRepository`
- [ ] ✅ Define consultas personalizadas con `@Query`
- [ ] ✅ Adaptador implementa puerto de salida
- [ ] ✅ Convierte entre entidades JPA y modelos de dominio
- [ ] ✅ Incluye logging para operaciones importantes
- [ ] ✅ Maneja paginación correctamente

#### 🚀 Próximo Paso

Con la persistencia JPA implementada, has completado toda la **capa de infraestructura** del dominio Owner. Ahora tienes:

✅ **Controlador REST** que expone APIs HTTP  
✅ **DTOs de Request** que validan datos de entrada  
✅ **Persistencia JPA** que conecta con la base de datos  

El siguiente paso sería implementar la **integración y configuración** para conectar todos estos componentes y hacer que funcionen juntos en el sistema completo.

---

## 6. Integración y Configuración

Una vez que has implementado todas las capas de tu dominio, necesitas **integrar y configurar** todos los componentes para que funcionen juntos como un sistema cohesivo. Esta sección te muestra cómo conectar las piezas usando Spring Boot y cómo configurar la base de datos.

### Configuración Spring

Spring Boot usa **inyección de dependencias** para conectar automáticamente todos los componentes de tu dominio. Aquí te explico cómo funciona y qué configuraciones necesitas.

#### 🔧 Cómo Funciona la Inyección de Dependencias

Spring Boot escanea automáticamente todas las clases anotadas y las registra como **beans** (componentes gestionados). Luego, cuando una clase necesita otra, Spring la inyecta automáticamente.

**Flujo de inyección en tu dominio Owner:**

```
┌─────────────────┐    inyecta    ┌─────────────────┐
│  OwnerController│ ──────────────▶│   OwnerService  │
│   @RestController│               │    @Service     │
└─────────────────┘               └─────────────────┘
                                           │
                                           │ inyecta
                                           ▼
                                  ┌─────────────────┐
                                  │OwnerRepositoryPort│
                                  │  (implementado   │
                                  │  por Adapter)    │
                                  └─────────────────┘
```

#### 🏗️ Configuración Básica del Dominio

**1. Configuración de Aplicación Principal**

Tu aplicación ya tiene la configuración básica en `DatavetApplication.java`:

```java
package com.datavet.datavet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación DataVet.
 * 
 * @SpringBootApplication incluye:
 * - @Configuration: Permite definir beans
 * - @EnableAutoConfiguration: Configura automáticamente Spring Boot
 * - @ComponentScan: Escanea componentes en este paquete y subpaquetes
 */
@SpringBootApplication
public class DatavetApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatavetApplication.class, args);
    }
}
```

**¿Qué hace `@SpringBootApplication`?**
- **Escanea automáticamente** todos los paquetes bajo `com.datavet.datavet`
- **Encuentra y registra** todas las clases con `@Service`, `@Repository`, `@Controller`, etc.
- **Configura automáticamente** JPA, base de datos, web, etc.

#### 🎯 Anotaciones Clave para tu Dominio Owner

**1. En la Capa de Aplicación:**

```java
// OwnerService.java
@Service  // ✅ Spring registra este servicio automáticamente
@RequiredArgsConstructor  // ✅ Lombok genera constructor con dependencias
public class OwnerService implements OwnerUseCase, ApplicationService {
    
    // ✅ Spring inyecta automáticamente estas dependencias
    private final OwnerRepositoryPort ownerRepositoryPort;
    private final CreateOwnerCommandValidator createValidator;
    private final UpdateOwnerCommandValidator updateValidator;
    private final DomainEventPublisher eventPublisher;
    
    // Métodos del servicio...
}
```

**2. En los Validadores:**

```java
// CreateOwnerCommandValidator.java
@Component  // ✅ Spring registra este validador
@RequiredArgsConstructor
public class CreateOwnerCommandValidator implements Validator<CreateOwnerCommand> {
    
    // Si necesitas dependencias, Spring las inyecta automáticamente
    // private final SomeExternalService externalService;
    
    @Override
    public ValidationResult validate(CreateOwnerCommand command) {
        // Lógica de validación...
    }
}
```

**3. En la Capa de Infraestructura:**

```java
// OwnerController.java
@RestController  // ✅ Spring registra este controlador
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    
    private final OwnerUseCase ownerUseCase;  // ✅ Spring inyecta OwnerService
    
    // Endpoints REST...
}

// JpaOwnerRepositoryAdapter.java
@Repository  // ✅ Spring registra este repositorio
public class JpaOwnerRepositoryAdapter implements OwnerRepositoryPort {
    
    private final JpaOwnerRepository jpaRepository;  // ✅ Spring inyecta automáticamente
    
    // Implementación del repositorio...
}
```

#### 🔄 Configuración de Eventos de Dominio

El sistema de eventos ya está configurado en el shared, pero aquí te muestro cómo funciona:

**1. Publicador de Eventos (Ya configurado):**

```java
// En shared/infrastructure/event/LoggingDomainEventPublisher.java
@Component  // ✅ Ya registrado como bean de Spring
public class LoggingDomainEventPublisher implements DomainEventPublisher {
    
    @Override
    public void publish(DomainEvent event) {
        // Lógica para publicar eventos (logging, mensajería, etc.)
        log.info("Publishing domain event: {}", event);
    }
}
```

**2. Uso en tu Servicio:**

```java
// En OwnerService.java
@Service
@RequiredArgsConstructor
public class OwnerService implements OwnerUseCase {
    
    private final DomainEventPublisher eventPublisher;  // ✅ Spring inyecta automáticamente
    
    @Override
    public Owner createOwner(CreateOwnerCommand command) {
        Owner owner = Owner.create(/* parámetros */);
        
        // Publicar eventos antes de guardar
        publishDomainEvents(owner);
        
        return ownerRepositoryPort.save(owner);
    }
    
    private void publishDomainEvents(Owner owner) {
        List<DomainEvent> events = owner.getDomainEvents();
        for (DomainEvent event : events) {
            eventPublisher.publish(event);  // ✅ Usa el publicador inyectado
        }
        owner.clearDomainEvents();
    }
}
```

#### 🗄️ Configuración de Base de Datos

La configuración de base de datos ya está establecida en el proyecto. Aquí te explico cómo funciona:

**1. Configuración Principal (Ya existe):**

```java
// En shared/infrastructure/config/DatabaseConfig.java
@Configuration
@EnableJpaAuditing  // ✅ Habilita auditoría automática (createdAt, updatedAt)
@EnableJpaRepositories(basePackages = "com.datavet.datavet")  // ✅ Escanea repositorios JPA
@EnableTransactionManagement  // ✅ Habilita transacciones automáticas
public class DatabaseConfig {
    // Configuración automática por Spring Boot
}
```

**2. Propiedades de Base de Datos:**

```properties
# En src/main/resources/application.properties

# Configuración de PostgreSQL (Producción)
spring.datasource.url=jdbc:postgresql://localhost:5432/clinic_db
spring.datasource.username=clinicdatavet
spring.datasource.password=dataVet
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# Configuración de Logging
logging.level.com.datavet.datavet=INFO
logging.level.com.datavet.datavet.shared.infrastructure.event=DEBUG
```

**3. Configuración para Desarrollo Local (H2):**

Si quieres usar H2 para desarrollo local, puedes crear un perfil separado:

```properties
# En src/main/resources/application-dev.properties

# Configuración de H2 (Desarrollo)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

Para usar el perfil de desarrollo:
```bash
# Ejecutar con perfil de desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 🔧 Configuración Específica del Dominio Owner (Opcional)

Si tu dominio necesita configuración específica, puedes crear una clase de configuración:

**Archivo:** `src/main/java/com/datavet/datavet/owner/infrastructure/config/OwnerConfig.java`

```java
package com.datavet.datavet.owner.infrastructure.config;

import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.infrastructure.persistence.repository.JpaOwnerRepository;
import com.datavet.datavet.owner.infrastructure.persistence.repository.JpaOwnerRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración específica del dominio Owner.
 * 
 * Normalmente no necesitas esta clase porque Spring Boot
 * maneja la inyección automáticamente, pero puede ser útil para:
 * - Configuraciones complejas
 * - Beans personalizados
 * - Configuración condicional
 */
@Configuration
public class OwnerConfig {
    
    /**
     * Ejemplo de bean personalizado (normalmente no necesario).
     * Spring Boot ya maneja esto automáticamente.
     */
    @Bean
    public OwnerRepositoryPort ownerRepositoryPort(JpaOwnerRepository jpaRepository) {
        return new JpaOwnerRepositoryAdapter(jpaRepository);
    }
    
    // Otros beans personalizados si son necesarios...
}
```

**⚠️ Nota Importante:** En la mayoría de casos **NO necesitas** crear esta clase de configuración porque Spring Boot maneja todo automáticamente con las anotaciones `@Service`, `@Repository`, etc.

#### 🎯 Verificación de la Configuración

**1. Verificar que Spring encuentra todos los componentes:**

```bash
# Compilar el proyecto
mvn clean compile

# Si no hay errores, Spring puede encontrar todas las dependencias
```

**2. Verificar inyección de dependencias en tiempo de ejecución:**

```java
// En OwnerService.java, agregar logging para verificar
@Service
@RequiredArgsConstructor
@Slf4j  // ✅ Lombok para logging
public class OwnerService implements OwnerUseCase {
    
    private final OwnerRepositoryPort ownerRepositoryPort;
    
    @PostConstruct  // ✅ Se ejecuta después de la inyección
    public void init() {
        log.info("OwnerService initialized with repository: {}", 
                 ownerRepositoryPort.getClass().getSimpleName());
    }
}
```

**3. Verificar configuración de base de datos:**

```bash
# Ejecutar la aplicación
mvn spring-boot:run

# Buscar en los logs:
# - "Started DatavetApplication" (aplicación iniciada)
# - "HikariPool-1 - Start completed" (conexión a BD establecida)
# - "Initialized JPA EntityManagerFactory" (JPA configurado)
```

#### 🚨 Problemas Comunes y Soluciones

**1. Error: "Could not autowire. No beans of type found"**

```java
// ❌ Problema: Falta anotación
public class OwnerService implements OwnerUseCase {
    // Spring no puede encontrar este servicio
}

// ✅ Solución: Agregar @Service
@Service
public class OwnerService implements OwnerUseCase {
    // Ahora Spring lo encuentra automáticamente
}
```

**2. Error: "Parameter 0 of constructor required a bean that could not be found"**

```java
// ❌ Problema: Dependencia no registrada
@Service
public class OwnerService {
    private final SomeUnknownService unknownService;  // No existe como @Component
}

// ✅ Solución: Verificar que la dependencia tenga anotación correcta
@Component  // o @Service, @Repository, etc.
public class SomeUnknownService {
    // Ahora Spring puede inyectarlo
}
```

**3. Error: "Circular dependency"**

```java
// ❌ Problema: Dependencia circular
@Service
public class OwnerService {
    private final PetService petService;  // PetService depende de OwnerService
}

// ✅ Solución: Usar eventos de dominio o refactorizar
@Service
public class OwnerService {
    private final DomainEventPublisher eventPublisher;  // Comunicación via eventos
    
    public void createOwner(CreateOwnerCommand command) {
        Owner owner = Owner.create(/*...*/);
        eventPublisher.publish(OwnerCreatedEvent.of(/*...*/));  // ✅ Sin dependencia directa
    }
}
```

#### ✅ Checklist - Configuración Spring

- [ ] ✅ `@SpringBootApplication` en la clase principal
- [ ] ✅ `@Service` en servicios de aplicación
- [ ] ✅ `@Component` en validadores y mappers
- [ ] ✅ `@RestController` en controladores
- [ ] ✅ `@Repository` en adaptadores de repositorio
- [ ] ✅ `@RequiredArgsConstructor` para inyección de dependencias
- [ ] ✅ Configuración de base de datos en `application.properties`
- [ ] ✅ `@EnableJpaRepositories` configurado (ya existe en shared)
- [ ] ✅ `@EnableTransactionManagement` configurado (ya existe en shared)

#### 🚀 Próximo Paso

Con la configuración Spring implementada, el siguiente paso es configurar las **migraciones de base de datos** para crear las tablas necesarias para tu dominio Owner.

### Migraciones de Base de Datos

Las **migraciones de base de datos** son scripts SQL que crean y modifican la estructura de tu base de datos de manera controlada y versionada. Son esenciales para mantener la consistencia entre diferentes entornos (desarrollo, testing, producción).

#### 🎯 ¿Por qué usar Migraciones?

**✅ Ventajas de las migraciones:**
- **Versionado**: Cada cambio está documentado y versionado
- **Reproducibilidad**: Misma estructura en todos los entornos
- **Colaboración**: Todo el equipo tiene la misma base de datos
- **Rollback**: Puedes deshacer cambios si es necesario
- **Automatización**: Se ejecutan automáticamente al desplegar

**❌ Sin migraciones:**
- Estructuras inconsistentes entre entornos
- Cambios manuales propensos a errores
- Difícil colaboración en equipo
- Pérdida de historial de cambios

#### 🏗️ Estructura de Migraciones para Owner

El proyecto DataVet usa **Hibernate DDL** por defecto (`spring.jpa.hibernate.ddl-auto=update`), pero para producción es mejor usar **migraciones explícitas**. Te muestro ambos enfoques:

#### Enfoque 1: Hibernate DDL (Desarrollo)

**Configuración actual en `application.properties`:**

```properties
# Configuración actual (buena para desarrollo)
spring.jpa.hibernate.ddl-auto=update  # ✅ Crea/actualiza tablas automáticamente
spring.jpa.show-sql=true              # ✅ Muestra SQL generado en logs
spring.jpa.properties.hibernate.format_sql=true  # ✅ Formatea SQL para legibilidad
```

**¿Qué hace `ddl-auto=update`?**
- **Analiza** tus entidades JPA (`OwnerEntity`)
- **Compara** con la estructura actual de la base de datos
- **Crea** tablas nuevas si no existen
- **Agrega** columnas nuevas si faltan
- **NO elimina** columnas o tablas (seguro para datos existentes)

**Tabla generada automáticamente para Owner:**

```sql
-- Tabla generada automáticamente por Hibernate
CREATE TABLE owner (
    owner_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    identification_number VARCHAR(20),
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Índices automáticos
CREATE UNIQUE INDEX idx_owner_email ON owner(email);
CREATE INDEX idx_owner_identification ON owner(identification_number);
```

#### Enfoque 2: Migraciones Explícitas con Flyway (Producción)

Para **producción** es recomendable usar migraciones explícitas. Aquí te muestro cómo configurar **Flyway**:

**1. Agregar dependencia de Flyway al `pom.xml`:**

```xml
<!-- En pom.xml, agregar en la sección <dependencies> -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**2. Configuración en `application.properties`:**

```properties
# Configuración para producción con Flyway
spring.jpa.hibernate.ddl-auto=validate  # ✅ Solo valida, no modifica
spring.flyway.enabled=true              # ✅ Habilita Flyway
spring.flyway.locations=classpath:db/migration  # ✅ Ubicación de scripts
spring.flyway.baseline-on-migrate=true  # ✅ Permite migrar BD existente
```

**3. Estructura de carpetas para migraciones:**

```
src/main/resources/
└── db/
    └── migration/
        ├── V1__Create_clinic_table.sql          ← Ya existe (dominio Clinic)
        ├── V2__Create_owner_table.sql           ← Tu nueva migración
        ├── V3__Add_owner_indexes.sql            ← Índices para Owner
        └── V4__Add_owner_constraints.sql        ← Constraints adicionales
```

#### 🗄️ Scripts de Migración para Owner

**Migración V2: Crear tabla Owner**

**Archivo:** `src/main/resources/db/migration/V2__Create_owner_table.sql`

```sql
-- =====================================================
-- Migración V2: Crear tabla Owner
-- Fecha: 2024-01-XX
-- Descripción: Crea la tabla owner con todos los campos necesarios
-- =====================================================

-- Crear tabla principal
CREATE TABLE owner (
    owner_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    identification_number VARCHAR(20),
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comentarios para documentación
COMMENT ON TABLE owner IS 'Tabla que almacena información de los dueños de mascotas';
COMMENT ON COLUMN owner.owner_id IS 'Identificador único del dueño';
COMMENT ON COLUMN owner.first_name IS 'Nombre del dueño (máximo 50 caracteres)';
COMMENT ON COLUMN owner.last_name IS 'Apellido del dueño (máximo 50 caracteres)';
COMMENT ON COLUMN owner.email IS 'Email del dueño (único en el sistema)';
COMMENT ON COLUMN owner.phone IS 'Teléfono del dueño (formato internacional)';
COMMENT ON COLUMN owner.address IS 'Dirección completa del dueño en formato JSON';
COMMENT ON COLUMN owner.identification_number IS 'Número de identificación (DNI, NIE, etc.)';
COMMENT ON COLUMN owner.notes IS 'Notas adicionales sobre el dueño';
COMMENT ON COLUMN owner.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN owner.updated_at IS 'Fecha y hora de última actualización';
```

**Migración V3: Agregar índices**

**Archivo:** `src/main/resources/db/migration/V3__Add_owner_indexes.sql`

```sql
-- =====================================================
-- Migración V3: Agregar índices para tabla Owner
-- Fecha: 2024-01-XX
-- Descripción: Crea índices para optimizar consultas frecuentes
-- =====================================================

-- Índice único para email (evita duplicados)
CREATE UNIQUE INDEX idx_owner_email 
ON owner(email);

-- Índice para búsquedas por nombre completo
CREATE INDEX idx_owner_full_name 
ON owner(first_name, last_name);

-- Índice para número de identificación (búsquedas frecuentes)
CREATE INDEX idx_owner_identification 
ON owner(identification_number) 
WHERE identification_number IS NOT NULL;

-- Índice para búsquedas por fecha de creación (reportes)
CREATE INDEX idx_owner_created_at 
ON owner(created_at);

-- Índice parcial para dueños con teléfono (contacto)
CREATE INDEX idx_owner_phone 
ON owner(phone) 
WHERE phone IS NOT NULL;
```

**Migración V4: Agregar constraints**

**Archivo:** `src/main/resources/db/migration/V4__Add_owner_constraints.sql`

```sql
-- =====================================================
-- Migración V4: Agregar constraints para tabla Owner
-- Fecha: 2024-01-XX
-- Descripción: Agrega constraints de integridad y validación
-- =====================================================

-- Constraint para validar formato de email
ALTER TABLE owner 
ADD CONSTRAINT chk_owner_email_format 
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Constraint para validar longitud mínima de nombres
ALTER TABLE owner 
ADD CONSTRAINT chk_owner_first_name_length 
CHECK (LENGTH(TRIM(first_name)) >= 2);

ALTER TABLE owner 
ADD CONSTRAINT chk_owner_last_name_length 
CHECK (LENGTH(TRIM(last_name)) >= 2);

-- Constraint para validar que created_at no sea futuro
ALTER TABLE owner 
ADD CONSTRAINT chk_owner_created_at_not_future 
CHECK (created_at <= CURRENT_TIMESTAMP);

-- Constraint para validar que updated_at >= created_at
ALTER TABLE owner 
ADD CONSTRAINT chk_owner_updated_at_after_created 
CHECK (updated_at >= created_at);

-- Constraint para validar longitud de identification_number
ALTER TABLE owner 
ADD CONSTRAINT chk_owner_identification_length 
CHECK (identification_number IS NULL OR LENGTH(TRIM(identification_number)) >= 5);

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_owner_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_owner_updated_at
    BEFORE UPDATE ON owner
    FOR EACH ROW
    EXECUTE FUNCTION update_owner_updated_at();
```

#### 🔧 Convenciones de Naming

**1. Nombres de Tablas:**
```sql
-- ✅ Correcto: Singular, lowercase
CREATE TABLE owner (...)
CREATE TABLE pet (...)
CREATE TABLE appointment (...)

-- ❌ Incorrecto: Plural o CamelCase
CREATE TABLE owners (...)
CREATE TABLE Owner (...)
```

**2. Nombres de Columnas:**
```sql
-- ✅ Correcto: snake_case
owner_id, first_name, created_at

-- ❌ Incorrecto: camelCase
ownerId, firstName, createdAt
```

**3. Nombres de Índices:**
```sql
-- ✅ Correcto: Descriptivo con prefijo
idx_owner_email
idx_owner_full_name
idx_owner_created_at

-- ❌ Incorrecto: Genérico
index1, owner_idx
```

**4. Nombres de Constraints:**
```sql
-- ✅ Correcto: Descriptivo con prefijo
chk_owner_email_format
fk_pet_owner_id
uk_owner_email

-- ❌ Incorrecto: Genérico
constraint1, owner_check
```

#### 🚀 Comandos para Ejecutar Migraciones

**1. Con Hibernate DDL (Desarrollo):**
```bash
# Simplemente ejecutar la aplicación
mvn spring-boot:run

# Hibernate creará/actualizará las tablas automáticamente
# Verifica en los logs: "Hibernate: create table owner..."
```

**2. Con Flyway (Producción):**
```bash
# Ejecutar migraciones manualmente
mvn flyway:migrate

# Ver estado de migraciones
mvn flyway:info

# Limpiar base de datos (¡CUIDADO! Solo en desarrollo)
mvn flyway:clean

# Reparar migraciones con problemas
mvn flyway:repair
```

**3. Verificar estructura creada:**
```sql
-- Conectar a PostgreSQL y verificar
\dt                          -- Listar tablas
\d owner                     -- Describir tabla owner
\di                          -- Listar índices
SELECT * FROM flyway_schema_history;  -- Ver historial de migraciones
```

#### 🧪 Scripts de Datos de Prueba

**Archivo:** `src/main/resources/db/migration/V5__Insert_owner_test_data.sql`

```sql
-- =====================================================
-- Migración V5: Datos de prueba para Owner
-- Fecha: 2024-01-XX
-- Descripción: Inserta datos de prueba para desarrollo y testing
-- =====================================================

-- Solo insertar en entornos de desarrollo/testing
-- En producción, esta migración no debería ejecutarse

INSERT INTO owner (
    first_name, 
    last_name, 
    email, 
    phone, 
    address, 
    identification_number, 
    notes,
    created_at,
    updated_at
) VALUES 
(
    'Juan', 
    'Pérez', 
    'juan.perez@email.com', 
    '+34612345678',
    '{"street": "Calle Mayor 123", "city": "Madrid", "postalCode": "28001", "country": "España"}',
    '12345678A',
    'Cliente frecuente, tiene 2 perros',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'María', 
    'García', 
    'maria.garcia@email.com', 
    '+34687654321',
    '{"street": "Avenida Libertad 45", "city": "Barcelona", "postalCode": "08001", "country": "España"}',
    '87654321B',
    'Propietaria de gatos, muy cuidadosa',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'Carlos', 
    'López', 
    'carlos.lopez@email.com', 
    NULL,
    NULL,
    NULL,
    'Contacto de emergencia pendiente',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### 🔍 Verificación de Migraciones

**1. Verificar que las tablas se crearon correctamente:**
```bash
# Ejecutar aplicación
mvn spring-boot:run

# En otra terminal, conectar a la base de datos
psql -h localhost -U clinicdatavet -d clinic_db

# Verificar estructura
\d owner
```

**2. Verificar que los datos de prueba se insertaron:**
```sql
-- En psql
SELECT owner_id, first_name, last_name, email 
FROM owner 
ORDER BY created_at;
```

**3. Verificar que los índices funcionan:**
```sql
-- Explicar plan de consulta (debería usar índice)
EXPLAIN SELECT * FROM owner WHERE email = 'juan.perez@email.com';
```

#### ⚠️ Mejores Prácticas para Migraciones

**✅ Hacer:**
- **Versionar** cada cambio con un número secuencial
- **Documentar** cada migración con comentarios
- **Probar** migraciones en desarrollo antes de producción
- **Hacer backup** antes de ejecutar en producción
- **Usar transacciones** para cambios complejos

**❌ No hacer:**
- **Modificar** migraciones ya ejecutadas en producción
- **Eliminar** datos sin backup
- **Hacer cambios** directamente en producción
- **Usar DDL** y DML en la misma migración
- **Olvidar** documentar cambios importantes

#### ✅ Checklist - Migraciones de Base de Datos

- [ ] ✅ Tabla `owner` creada con todos los campos
- [ ] ✅ Índices creados para consultas frecuentes
- [ ] ✅ Constraints de integridad implementados
- [ ] ✅ Trigger para `updated_at` automático
- [ ] ✅ Comentarios en tabla y columnas
- [ ] ✅ Datos de prueba insertados (desarrollo)
- [ ] ✅ Migraciones versionadas correctamente
- [ ] ✅ Convenciones de naming seguidas
- [ ] ✅ Scripts probados en desarrollo

#### 🚀 Próximo Paso

Con la configuración Spring y las migraciones de base de datos implementadas, tu dominio Owner está completamente integrado y configurado. El siguiente paso sería implementar la sección de **Testing y Validación** para asegurar que todo funciona correctamente.

---

## 7. Testing y Validación

Una vez que hayas implementado tu dominio Owner, es fundamental crear tests para asegurar que todo funciona correctamente. Esta sección te muestra cómo crear tests unitarios, de integración y usar herramientas para probar tu API.

### Tests Unitarios

Los **tests unitarios** prueban componentes individuales de manera aislada. Son rápidos de ejecutar y te ayudan a detectar errores temprano en el desarrollo.

#### 🎯 ¿Qué testear en cada capa?

**🏛️ Capa de Dominio:**
- Lógica de negocio del modelo
- Publicación de eventos de dominio
- Validaciones de negocio
- Factory methods

**🔧 Capa de Aplicación:**
- Orquestación de servicios
- Validación de comandos
- Manejo de excepciones
- Integración con puertos

**🌐 Capa de Infraestructura:**
- Conversión de DTOs
- Validaciones de entrada
- Mapeo de entidades JPA

#### 🏗️ Tests del Modelo de Dominio

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/domain/model/OwnerDomainEventsTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner.domain.model;

import com.datavet.datavet.owner.domain.event.OwnerCreatedEvent;
import com.datavet.datavet.owner.domain.event.OwnerDeletedEvent;
import com.datavet.datavet.owner.domain.event.OwnerUpdatedEvent;
import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Owner Domain Events Tests")
class OwnerDomainEventsTest {

    @Test
    @DisplayName("Should raise OwnerCreatedEvent when owner is created")
    void shouldRaiseOwnerCreatedEventWhenOwnerIsCreated() {
        // Given
        Long ownerId = 1L;
        String firstName = "Juan";
        String lastName = "Pérez";
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        String identificationNumber = "12345678";
        String notes = "Cliente VIP";

        // When
        Owner owner = Owner.create(ownerId, firstName, lastName, email, 
                                 phone, address, identificationNumber, notes);

        // Then
        List<DomainEvent> domainEvents = owner.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(OwnerCreatedEvent.class, event, "Should be OwnerCreatedEvent");
        
        OwnerCreatedEvent createdEvent = (OwnerCreatedEvent) event;
        assertEquals(ownerId, createdEvent.getOwnerId(), "Event should have correct owner ID");
        assertEquals(firstName, createdEvent.getFirstName(), "Event should have correct first name");
        assertEquals(lastName, createdEvent.getLastName(), "Event should have correct last name");
        assertEquals(email, createdEvent.getEmail(), "Event should have correct email");
        assertNotNull(createdEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should raise OwnerUpdatedEvent when owner is updated")
    void shouldRaiseOwnerUpdatedEventWhenOwnerIsUpdated() {
        // Given
        Email originalEmail = new Email("juan.perez@example.com");
        Phone originalPhone = new Phone("+51987654321");
        Address originalAddress = new Address("Av. Lima 123", "Lima", "15001");
        
        Owner owner = Owner.create(1L, "Juan", "Pérez", originalEmail, 
                                 originalPhone, originalAddress, "12345678", "Cliente VIP");
        
        // Clear the creation event
        owner.clearDomainEvents();
        
        // When
        Email updatedEmail = new Email("juan.perez.updated@example.com");
        Phone updatedPhone = new Phone("+51912345678");
        Address updatedAddress = new Address("Av. Arequipa 456", "Lima", "15002");
        
        owner.update("Juan Carlos", "Pérez García", updatedEmail, 
                    updatedPhone, updatedAddress, "87654321", "Cliente Premium");

        // Then
        List<DomainEvent> domainEvents = owner.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(OwnerUpdatedEvent.class, event, "Should be OwnerUpdatedEvent");
        
        OwnerUpdatedEvent updatedEvent = (OwnerUpdatedEvent) event;
        assertEquals(1L, updatedEvent.getOwnerId(), "Event should have correct owner ID");
        assertEquals("Juan Carlos", updatedEvent.getFirstName(), "Event should have correct updated first name");
        assertEquals("Pérez García", updatedEvent.getLastName(), "Event should have correct updated last name");
        assertNotNull(updatedEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should raise OwnerDeletedEvent when owner is deleted")
    void shouldRaiseOwnerDeletedEventWhenOwnerIsDeleted() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        
        Owner owner = Owner.create(1L, "Juan", "Pérez", email, 
                                 phone, address, "12345678", "Cliente VIP");
        
        // Clear the creation event
        owner.clearDomainEvents();
        
        // When
        owner.delete();

        // Then
        List<DomainEvent> domainEvents = owner.getDomainEvents();
        assertFalse(domainEvents.isEmpty(), "Should have domain events");
        assertEquals(1, domainEvents.size(), "Should have exactly one domain event");
        
        DomainEvent event = domainEvents.get(0);
        assertInstanceOf(OwnerDeletedEvent.class, event, "Should be OwnerDeletedEvent");
        
        OwnerDeletedEvent deletedEvent = (OwnerDeletedEvent) event;
        assertEquals(1L, deletedEvent.getOwnerId(), "Event should have correct owner ID");
        assertEquals("Juan", deletedEvent.getFirstName(), "Event should have correct first name");
        assertEquals("Pérez", deletedEvent.getLastName(), "Event should have correct last name");
        assertNotNull(deletedEvent.getOccurredOn(), "Event should have occurred timestamp");
    }

    @Test
    @DisplayName("Should implement Entity interface correctly")
    void shouldImplementEntityInterfaceCorrectly() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        
        Owner owner = Owner.create(1L, "Juan", "Pérez", email, 
                                 phone, address, "12345678", "Cliente VIP");

        // When & Then
        assertEquals(1L, owner.getId(), "getId() should return the owner ID");
        assertEquals(1L, owner.getOwnerId(), "getOwnerId() should return the same value as getId()");
        
        // Test that owner is an instance of Entity
        assertTrue(owner instanceof com.datavet.datavet.shared.domain.model.Entity, 
                "Owner should implement Entity interface");
        
        // Test entity identity consistency
        Owner sameOwner = Owner.create(1L, "Different Name", "Different Last Name", email, 
                                     phone, address, "87654321", "Different notes");
        assertEquals(owner.getId(), sameOwner.getId(), "Owners with same ID should have same identity");
        
        // Test different entity identity
        Owner differentOwner = Owner.create(2L, "Juan", "Pérez", email, 
                                          phone, address, "12345678", "Cliente VIP");
        assertNotEquals(owner.getId(), differentOwner.getId(), "Owners with different IDs should have different identity");
    }

    @Test
    @DisplayName("Should use value objects correctly")
    void shouldUseValueObjectsCorrectly() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        
        // When
        Owner owner = Owner.create(1L, "Juan", "Pérez", email, 
                                 phone, address, "12345678", "Cliente VIP");

        // Then
        assertNotNull(owner.getAddress(), "Address should not be null");
        assertEquals("Av. Lima 123", owner.getAddress().getStreet(), "Address street should be correct");
        assertEquals("Lima", owner.getAddress().getCity(), "Address city should be correct");
        assertEquals("15001", owner.getAddress().getPostalCode(), "Address postal code should be correct");
        
        assertNotNull(owner.getPhone(), "Phone should not be null");
        assertEquals("+51987654321", owner.getPhone().getValue(), "Phone value should be correct");
        
        assertNotNull(owner.getEmail(), "Email should not be null");
        assertEquals("juan.perez@example.com", owner.getEmail().getValue(), "Email value should be correct");
    }

    @Test
    @DisplayName("Should handle business logic methods correctly")
    void shouldHandleBusinessLogicMethodsCorrectly() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        
        Owner owner = Owner.create(1L, "Juan", "Pérez", email, 
                                 phone, address, "12345678", "Cliente VIP");

        // When & Then
        assertEquals("Juan Pérez", owner.getFullName(), "Should return correct full name");
        assertTrue(owner.hasCompleteContactInfo(), "Should have complete contact info");
        
        // Test with incomplete contact info
        Owner incompleteOwner = Owner.create(2L, "María", "García", email, 
                                           null, null, null, null);
        assertFalse(incompleteOwner.hasCompleteContactInfo(), "Should not have complete contact info");
    }

    @Test
    @DisplayName("Should validate business rules correctly")
    void shouldValidateBusinessRulesCorrectly() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");

        // Test required field validations
        assertThrows(IllegalArgumentException.class, () -> 
                Owner.create(1L, null, "Pérez", email, phone, address, "12345678", "Notes"), 
                "Should reject null first name");
        
        assertThrows(IllegalArgumentException.class, () -> 
                Owner.create(1L, "", "Pérez", email, phone, address, "12345678", "Notes"), 
                "Should reject empty first name");
        
        assertThrows(IllegalArgumentException.class, () -> 
                Owner.create(1L, "Juan", null, email, phone, address, "12345678", "Notes"), 
                "Should reject null last name");
        
        assertThrows(IllegalArgumentException.class, () -> 
                Owner.create(1L, "Juan", "", email, phone, address, "12345678", "Notes"), 
                "Should reject empty last name");
        
        assertThrows(IllegalArgumentException.class, () -> 
                Owner.create(1L, "Juan", "Pérez", null, phone, address, "12345678", "Notes"), 
                "Should reject null email");
    }

    @Test
    @DisplayName("Should extend AggregateRoot correctly")
    void shouldExtendAggregateRootCorrectly() {
        // Given
        Email email = new Email("juan.perez@example.com");
        Phone phone = new Phone("+51987654321");
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        
        // When
        Owner owner = Owner.create(1L, "Juan", "Pérez", email, 
                                 phone, address, "12345678", "Cliente VIP");

        // Then
        assertTrue(owner instanceof com.datavet.datavet.shared.domain.model.AggregateRoot, 
                "Owner should extend AggregateRoot");
        
        // Test domain events functionality
        assertNotNull(owner.getDomainEvents(), "Should have domain events collection");
        assertFalse(owner.getDomainEvents().isEmpty(), "Should have at least one domain event after creation");
        
        // Test that domain events are immutable
        List<DomainEvent> events = owner.getDomainEvents();
        assertThrows(UnsupportedOperationException.class, () -> events.add(null), 
                "Domain events collection should be immutable");
        
        // Test clear domain events
        owner.clearDomainEvents();
        assertTrue(owner.getDomainEvents().isEmpty(), "Domain events should be cleared");
        
        // Test that new events can be added after clearing
        owner.update("Juan Carlos", "Pérez García", email, phone, address, "87654321", "Updated notes");
        assertFalse(owner.getDomainEvents().isEmpty(), "Should have new domain events after update");
    }
}
```

#### 🏗️ Tests de Servicios de Aplicación

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/application/service/OwnerServiceTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner.application.service;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.application.port.out.OwnerRepositoryPort;
import com.datavet.datavet.owner.application.validation.CreateOwnerCommandValidator;
import com.datavet.datavet.owner.application.validation.UpdateOwnerCommandValidator;
import com.datavet.datavet.owner.domain.exception.OwnerAlreadyExistsException;
import com.datavet.datavet.owner.domain.exception.OwnerNotFoundException;
import com.datavet.datavet.owner.domain.exception.OwnerValidationException;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.service.ApplicationService;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Owner Service Tests")
class OwnerServiceTest {

    private OwnerService ownerService;

    @Mock
    private OwnerRepositoryPort ownerRepositoryPort;
    
    @Mock
    private CreateOwnerCommandValidator createValidator;
    
    @Mock
    private UpdateOwnerCommandValidator updateValidator;
    
    @Mock
    private DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        ownerService = new OwnerService(ownerRepositoryPort, createValidator, updateValidator, domainEventPublisher);
    }

    @Test
    @DisplayName("Should implement ApplicationService interface")
    void shouldImplementApplicationServiceInterface() {
        // Test ApplicationService integration
        assertThat(ownerService).isInstanceOf(ApplicationService.class);
    }

    @Test
    @DisplayName("Should throw OwnerValidationException when create command validation fails")
    void shouldThrowOwnerValidationExceptionWhenCreateCommandValidationFails() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "", // Invalid empty first name
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // Mock validation to fail using shared validation framework
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("firstName", "First name is required");
        when(createValidator.validate(command)).thenReturn(validationResult);

        // When & Then
        assertThatThrownBy(() -> ownerService.createOwner(command))
                .isInstanceOf(OwnerValidationException.class);
        
        // Verify validation was called
        verify(createValidator).validate(command);
    }

    @Test
    @DisplayName("Should throw OwnerAlreadyExistsException when email already exists")
    void shouldThrowOwnerAlreadyExistsExceptionWhenEmailAlreadyExists() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // Mock validation to pass
        when(createValidator.validate(command)).thenReturn(new ValidationResult());
        when(ownerRepositoryPort.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ownerService.createOwner(command))
                .isInstanceOf(OwnerAlreadyExistsException.class)
                .hasMessage("Owner already exists with email: juan.perez@example.com");
    }

    @Test
    @DisplayName("Should create owner successfully when validation passes and no conflicts")
    void shouldCreateOwnerSuccessfullyWhenValidationPassesAndNoConflicts() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        Owner savedOwner = Owner.create(1L, "Juan", "Pérez", email, 
                                      phone, address, "12345678", "Cliente VIP");

        // Mock validation to pass using shared validation framework
        ValidationResult validationResult = new ValidationResult(); // Empty result = valid
        when(createValidator.validate(command)).thenReturn(validationResult);
        when(ownerRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(ownerRepositoryPort.existsByIdentificationNumber("12345678")).thenReturn(false);
        when(ownerRepositoryPort.save(any(Owner.class))).thenReturn(savedOwner);

        // When
        Owner result = ownerService.createOwner(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOwnerId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Pérez");
        assertThat(result.getEmail().getValue()).isEqualTo("juan.perez@example.com");
        
        // Verify shared validation framework was used
        verify(createValidator).validate(command);
        // Verify shared repository interface methods were used
        verify(ownerRepositoryPort).save(any(Owner.class));
        // Verify domain events were published
        verify(domainEventPublisher).publishEvents(any());
    }

    @Test
    @DisplayName("Should throw OwnerNotFoundException when updating non-existent owner")
    void shouldThrowOwnerNotFoundExceptionWhenUpdatingNonExistentOwner() {
        // Given
        Address address = new Address("Av. Arequipa 456", "Lima", "15002");
        Phone phone = new Phone("+51912345678");
        Email email = new Email("juan.updated@example.com");
        
        UpdateOwnerCommand command = UpdateOwnerCommand.builder()
                .ownerId(999L)
                .firstName("Juan Carlos")
                .lastName("Pérez García")
                .email(email)
                .phone(phone)
                .address(address)
                .identificationNumber("87654321")
                .notes("Cliente Premium")
                .build();

        // Mock validation to pass
        when(updateValidator.validate(command)).thenReturn(new ValidationResult());
        when(ownerRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ownerService.updateOwner(command))
                .isInstanceOf(OwnerNotFoundException.class)
                .hasMessage("Owner not found with id: 999");
    }

    @Test
    @DisplayName("Should use shared repository interface methods")
    void shouldUseSharedRepositoryInterfaceMethods() {
        // Given
        List<Owner> expectedOwners = Arrays.asList(
                Owner.create(1L, "Juan", "Pérez", new Email("juan@example.com"), 
                           new Phone("+51987654321"), new Address("Av. Lima 123", "Lima", "15001"), 
                           "12345678", "Cliente VIP"),
                Owner.create(2L, "María", "García", new Email("maria@example.com"), 
                           new Phone("+51912345678"), new Address("Av. Arequipa 456", "Lima", "15002"), 
                           "87654321", "Cliente Premium")
        );
        
        when(ownerRepositoryPort.findAll()).thenReturn(expectedOwners);

        // When
        List<Owner> result = ownerService.getAllOwners();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedOwners);
        verify(ownerRepositoryPort).findAll(); // Method from shared Repository interface
    }

    @Test
    @DisplayName("Should delete owner using shared repository interface")
    void shouldDeleteOwnerUsingSharedRepositoryInterface() {
        // Given
        Long ownerId = 1L;

        // When
        ownerService.deleteOwner(ownerId);

        // Then
        verify(ownerRepositoryPort).deleteById(ownerId); // Method from shared Repository interface
    }
}
```

#### 🏗️ Tests de Validadores

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/application/validation/CreateOwnerCommandValidatorTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateOwnerCommandValidator Tests")
class CreateOwnerCommandValidatorTest {

    private CreateOwnerCommandValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateOwnerCommandValidator();
    }

    @Test
    @DisplayName("Should pass validation with valid command")
    void shouldPassValidationWithValidCommand() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Should be valid");
        assertTrue(result.getErrors().isEmpty(), "Should have no errors");
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "", // Empty first name
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Should be invalid");
        assertTrue(result.hasError("firstName"), "Should have firstName error");
        assertEquals("First name is required", result.getError("firstName").getMessage());
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "", // Empty last name
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Should be invalid");
        assertTrue(result.hasError("lastName"), "Should have lastName error");
        assertEquals("Last name is required", result.getError("lastName").getMessage());
    }

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                null, // Null email
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertFalse(result.isValid(), "Should be invalid");
        assertTrue(result.hasError("email"), "Should have email error");
        assertEquals("Email is required", result.getError("email").getMessage());
    }

    @Test
    @DisplayName("Should pass validation with optional fields null")
    void shouldPassValidationWithOptionalFieldsNull() {
        // Given
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand command = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                email,
                null, // Optional phone
                null, // Optional address
                null, // Optional identification number
                null  // Optional notes
        );

        // When
        ValidationResult result = validator.validate(command);

        // Then
        assertTrue(result.isValid(), "Should be valid with optional fields null");
        assertTrue(result.getErrors().isEmpty(), "Should have no errors");
    }

    @Test
    @DisplayName("Should validate using shared validation framework")
    void shouldValidateUsingSharedValidationFramework() {
        // Given
        Address address = new Address("Av. Lima 123", "Lima", "15001");
        Phone phone = new Phone("+51987654321");
        Email email = new Email("juan.perez@example.com");
        
        CreateOwnerCommand validCommand = new CreateOwnerCommand(
                "Juan",
                "Pérez",
                email,
                phone,
                address,
                "12345678",
                "Cliente VIP"
        );

        // When
        ValidationResult result = validator.validate(validCommand);

        // Then
        assertNotNull(result, "Should return ValidationResult from shared framework");
        assertTrue(result instanceof ValidationResult, "Should be instance of shared ValidationResult");
        assertTrue(result.isValid(), "Valid command should pass validation");
    }
}
```

#### ✅ Checklist - Tests Unitarios

- [ ] ✅ Tests del modelo de dominio (eventos, validaciones, lógica de negocio)
- [ ] ✅ Tests de servicios de aplicación (orquestación, manejo de errores)
- [ ] ✅ Tests de validadores (usando framework compartido)
- [ ] ✅ Tests de comandos (validaciones de entrada)
- [ ] ✅ Uso de mocks para dependencias externas
- [ ] ✅ Verificación de integración con componentes compartidos
- [ ] ✅ Tests de casos de error y excepciones
- [ ] ✅ Cobertura de métodos de negocio importantes

#### 🚀 Comandos para Ejecutar Tests Unitarios

```bash
# Ejecutar todos los tests unitarios del dominio Owner
mvn test -Dtest="*Owner*Test"

# Ejecutar solo tests de dominio
mvn test -Dtest="*Owner*Domain*Test"

# Ejecutar solo tests de aplicación
mvn test -Dtest="*Owner*Service*Test"

# Ejecutar tests con reporte de cobertura
mvn test jacoco:report

# Ver resultados en el navegador
open target/site/jacoco/index.html
```

### Tests de Integración

Los **tests de integración** prueban cómo interactúan múltiples componentes juntos. Son más lentos que los tests unitarios pero te dan más confianza de que el sistema funciona correctamente en conjunto.

#### 🎯 ¿Qué testear en integración?

**🌐 Controladores REST:**
- Endpoints HTTP completos
- Serialización/deserialización JSON
- Validación de requests
- Códigos de respuesta HTTP
- Manejo de errores

**🗄️ Repositorios JPA:**
- Persistencia en base de datos
- Consultas personalizadas
- Conversión de Value Objects
- Transacciones

**🔄 Flujos End-to-End:**
- Casos de uso completos
- Integración entre capas
- Manejo de eventos de dominio

#### 🏗️ Tests de Controlador REST

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/infrastructure/adapter/input/OwnerControllerIntegrationTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner.infrastructure.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OwnerController endpoints.
 * Tests all CRUD operations, validation errors, and error scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@DisplayName("Owner Controller Integration Tests")
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test data constants
    private static final String VALID_FIRST_NAME = "Juan";
    private static final String VALID_LAST_NAME = "Pérez";
    private static final String VALID_EMAIL = "juan.perez@example.com";
    private static final String VALID_PHONE = "+51987654321";
    private static final String VALID_ADDRESS = "Av. Lima 123";
    private static final String VALID_CITY = "Lima";
    private static final String VALID_POSTAL_CODE = "15001";
    private static final String VALID_IDENTIFICATION = "12345678";
    private static final String VALID_NOTES = "Cliente VIP";

    @Test
    @DisplayName("Should create owner with valid data and return 201")
    void createOwner_WithValidData_ShouldReturn201AndOwnerResponse() throws Exception {
        // Create request DTO for the REST endpoint
        String requestJson = """
            {
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s",
                "phone": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s",
                "identificationNumber": "%s",
                "notes": "%s"
            }
            """.formatted(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PHONE,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_POSTAL_CODE,
                VALID_IDENTIFICATION,
                VALID_NOTES
            );

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.firstName").value(VALID_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.fullName").value(VALID_FIRST_NAME + " " + VALID_LAST_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_POSTAL_CODE))
                .andExpect(jsonPath("$.identificationNumber").value(VALID_IDENTIFICATION))
                .andExpect(jsonPath("$.notes").value(VALID_NOTES))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating owner with invalid data")
    void createOwner_WithInvalidData_ShouldReturn400WithValidationErrors() throws Exception {
        // Create invalid request JSON to test validation
        String invalidRequestJson = """
            {
                "firstName": "",
                "lastName": "",
                "email": "invalid-email",
                "phone": "invalid-phone",
                "address": "",
                "city": "",
                "postalCode": "12345",
                "identificationNumber": null,
                "notes": null
            }
            """;

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.path").value("/owners"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should get owner by ID and return 200")
    void getOwnerById_WithValidId_ShouldReturn200AndOwnerResponse() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID from the response
        Long ownerId = objectMapper.readTree(response).get("ownerId").asLong();

        // Test GET by ID
        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value(VALID_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.phone").value(VALID_PHONE));
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent owner")
    void getOwnerById_WithNonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(get("/owners/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.path").value("/owners/" + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should get all owners and return 200")
    void getAllOwners_ShouldReturn200AndListOfOwnerResponses() throws Exception {
        // Create two owners
        String request1 = createValidRequestJson();
        String request2 = """
            {
                "firstName": "María",
                "lastName": "García",
                "email": "maria.garcia@example.com",
                "phone": "+51912345678",
                "address": "Av. Arequipa 456",
                "city": "Lima",
                "postalCode": "15002",
                "identificationNumber": "87654321",
                "notes": "Cliente Premium"
            }
            """;

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2))
                .andExpect(status().isCreated());

        // Test GET all
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].firstName").exists())
                .andExpect(jsonPath("$[1].lastName").exists())
                .andExpect(jsonPath("$[1].email").exists());
    }

    @Test
    @DisplayName("Should update owner and return 200")
    void updateOwner_WithValidData_ShouldReturn200AndUpdatedOwnerResponse() throws Exception {
        // First create an owner
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ownerId = objectMapper.readTree(createResponse).get("ownerId").asLong();

        // Update the owner
        String updateRequest = """
            {
                "firstName": "Juan Carlos",
                "lastName": "Pérez García",
                "email": "juan.carlos.perez@example.com",
                "phone": "+51998877665",
                "address": "Av. Javier Prado 789",
                "city": "Lima",
                "postalCode": "15003",
                "identificationNumber": "11223344",
                "notes": "Cliente Premium Actualizado"
            }
            """;

        mockMvc.perform(put("/owners/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Pérez García"))
                .andExpect(jsonPath("$.fullName").value("Juan Carlos Pérez García"))
                .andExpect(jsonPath("$.email").value("juan.carlos.perez@example.com"))
                .andExpect(jsonPath("$.phone").value("+51998877665"))
                .andExpect(jsonPath("$.address.street").value("Av. Javier Prado 789"))
                .andExpect(jsonPath("$.identificationNumber").value("11223344"))
                .andExpect(jsonPath("$.notes").value("Cliente Premium Actualizado"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return 409 when creating owner with duplicate email")
    void createOwner_WithDuplicateEmail_ShouldReturn409() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        // Try to create another owner with the same email
        String duplicateRequest = """
            {
                "firstName": "María",
                "lastName": "García",
                "email": "%s",
                "phone": "+51912345678",
                "address": "Av. Arequipa 456",
                "city": "Lima",
                "postalCode": "15002",
                "identificationNumber": "87654321",
                "notes": "Cliente Premium"
            }
            """.formatted(VALID_EMAIL);

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Owner already exists with email: " + VALID_EMAIL))
                .andExpect(jsonPath("$.path").value("/owners"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should delete owner and return 204")
    void deleteOwner_WithValidId_ShouldReturn204() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ownerId = objectMapper.readTree(response).get("ownerId").asLong();

        // Delete the owner
        mockMvc.perform(delete("/owners/{id}", ownerId))
                .andExpect(status().isNoContent());

        // Verify the owner is deleted
        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isNotFound());
    }

    private String createValidRequestJson() {
        return """
            {
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s",
                "phone": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s",
                "identificationNumber": "%s",
                "notes": "%s"
            }
            """.formatted(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_EMAIL,
                VALID_PHONE,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_POSTAL_CODE,
                VALID_IDENTIFICATION,
                VALID_NOTES
            );
    }
}
```

#### 🏗️ Tests de Repositorio JPA

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/infrastructure/persistence/repository/OwnerRepositoryIntegrationTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.infrastructure.persistence.entity.OwnerEntity;
import com.datavet.datavet.shared.application.port.Repository;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JpaOwnerRepositoryAdapter shared repository implementation.
 * Verifies that the repository properly implements the shared Repository interface.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Owner Repository Integration Tests")
class OwnerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaOwnerRepositoryAdapter repository;

    private OwnerEntity testOwner;
    private Address testAddress;
    private Email testEmail;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testAddress = new Address("Av. Lima 123", "Lima", "15001");
        testEmail = new Email("juan.perez@example.com");
        testPhone = new Phone("+51987654321");
        
        testOwner = OwnerEntity.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email(testEmail)
                .phone(testPhone)
                .address(testAddress)
                .identificationNumber("12345678")
                .notes("Cliente VIP")
                .build();
    }

    @Test
    @DisplayName("Repository should implement shared Repository interface")
    void repository_ShouldImplementSharedRepositoryInterface() {
        assertTrue(Repository.class.isAssignableFrom(JpaOwnerRepositoryAdapter.class),
                "JpaOwnerRepositoryAdapter should implement Repository interface");
    }

    @Test
    @DisplayName("Should save and retrieve owner entity with value objects")
    void repository_ShouldSaveAndRetrieveOwnerWithValueObjects() {
        // Save the owner
        OwnerEntity savedOwner = repository.save(testOwner);
        
        assertNotNull(savedOwner, "Saved owner should not be null");
        assertNotNull(savedOwner.getOwnerId(), "Saved owner should have an ID");
        
        // Flush to ensure database persistence
        entityManager.flush();
        entityManager.clear();
        
        // Retrieve the owner
        Optional<OwnerEntity> retrievedOwner = repository.findById(savedOwner.getOwnerId());
        
        assertTrue(retrievedOwner.isPresent(), "Retrieved owner should be present");
        
        OwnerEntity owner = retrievedOwner.get();
        assertEquals("Juan", owner.getFirstName());
        assertEquals("Pérez", owner.getLastName());
        assertEquals("12345678", owner.getIdentificationNumber());
        assertEquals("Cliente VIP", owner.getNotes());
        
        // Verify value objects are properly persisted and retrieved
        assertNotNull(owner.getAddress(), "Address should not be null");
        assertEquals("Av. Lima 123", owner.getAddress().getStreet());
        assertEquals("Lima", owner.getAddress().getCity());
        assertEquals("15001", owner.getAddress().getPostalCode());
        
        assertNotNull(owner.getEmail(), "Email should not be null");
        assertEquals("juan.perez@example.com", owner.getEmail().getValue());
        
        assertNotNull(owner.getPhone(), "Phone should not be null");
        assertEquals("+51987654321", owner.getPhone().getValue());
    }

    @Test
    @DisplayName("Should find owner by email")
    void repository_ShouldFindOwnerByEmail() {
        // Save the owner
        OwnerEntity savedOwner = repository.save(testOwner);
        entityManager.flush();
        entityManager.clear();
        
        // Find by email
        Optional<OwnerEntity> foundOwner = repository.findByEmail(testEmail);
        
        assertTrue(foundOwner.isPresent(), "Owner should be found by email");
        assertEquals(savedOwner.getOwnerId(), foundOwner.get().getOwnerId());
        assertEquals("juan.perez@example.com", foundOwner.get().getEmail().getValue());
    }

    @Test
    @DisplayName("Should check if owner exists by email")
    void repository_ShouldCheckIfOwnerExistsByEmail() {
        // Initially should not exist
        assertFalse(repository.existsByEmail(testEmail), "Owner should not exist initially");
        
        // Save the owner
        repository.save(testOwner);
        entityManager.flush();
        
        // Now should exist
        assertTrue(repository.existsByEmail(testEmail), "Owner should exist after saving");
    }

    @Test
    @DisplayName("Should find owner by identification number")
    void repository_ShouldFindOwnerByIdentificationNumber() {
        // Save the owner
        OwnerEntity savedOwner = repository.save(testOwner);
        entityManager.flush();
        entityManager.clear();
        
        // Find by identification number
        Optional<OwnerEntity> foundOwner = repository.findByIdentificationNumber("12345678");
        
        assertTrue(foundOwner.isPresent(), "Owner should be found by identification number");
        assertEquals(savedOwner.getOwnerId(), foundOwner.get().getOwnerId());
        assertEquals("12345678", foundOwner.get().getIdentificationNumber());
    }

    @Test
    @DisplayName("Should check if owner exists by identification number")
    void repository_ShouldCheckIfOwnerExistsByIdentificationNumber() {
        // Initially should not exist
        assertFalse(repository.existsByIdentificationNumber("12345678"), 
                   "Owner should not exist initially");
        
        // Save the owner
        repository.save(testOwner);
        entityManager.flush();
        
        // Now should exist
        assertTrue(repository.existsByIdentificationNumber("12345678"), 
                  "Owner should exist after saving");
    }

    @Test
    @DisplayName("Should find all owners")
    void repository_ShouldFindAllOwners() {
        // Create and save multiple owners
        OwnerEntity owner2 = OwnerEntity.builder()
                .firstName("María")
                .lastName("García")
                .email(new Email("maria.garcia@example.com"))
                .phone(new Phone("+51912345678"))
                .address(new Address("Av. Arequipa 456", "Lima", "15002"))
                .identificationNumber("87654321")
                .notes("Cliente Premium")
                .build();
        
        repository.save(testOwner);
        repository.save(owner2);
        entityManager.flush();
        
        // Find all
        List<OwnerEntity> allOwners = repository.findAll();
        
        assertEquals(2, allOwners.size(), "Should find exactly 2 owners");
        assertTrue(allOwners.stream().anyMatch(o -> "Juan".equals(o.getFirstName())));
        assertTrue(allOwners.stream().anyMatch(o -> "María".equals(o.getFirstName())));
    }

    @Test
    @DisplayName("Should delete owner by ID")
    void repository_ShouldDeleteOwnerById() {
        // Save the owner
        OwnerEntity savedOwner = repository.save(testOwner);
        Long ownerId = savedOwner.getOwnerId();
        entityManager.flush();
        
        // Verify it exists
        assertTrue(repository.findById(ownerId).isPresent(), "Owner should exist before deletion");
        
        // Delete the owner
        repository.deleteById(ownerId);
        entityManager.flush();
        
        // Verify it's deleted
        assertFalse(repository.findById(ownerId).isPresent(), "Owner should not exist after deletion");
    }

    @Test
    @DisplayName("Should handle value object conversions correctly")
    void repository_ShouldHandleValueObjectConversionsCorrectly() {
        // Test with null optional fields
        OwnerEntity ownerWithNulls = OwnerEntity.builder()
                .firstName("Test")
                .lastName("User")
                .email(new Email("test@example.com"))
                .phone(null) // Optional field
                .address(null) // Optional field
                .identificationNumber(null) // Optional field
                .notes(null) // Optional field
                .build();
        
        // Save and retrieve
        OwnerEntity savedOwner = repository.save(ownerWithNulls);
        entityManager.flush();
        entityManager.clear();
        
        Optional<OwnerEntity> retrievedOwner = repository.findById(savedOwner.getOwnerId());
        
        assertTrue(retrievedOwner.isPresent());
        OwnerEntity owner = retrievedOwner.get();
        
        assertEquals("Test", owner.getFirstName());
        assertEquals("User", owner.getLastName());
        assertNotNull(owner.getEmail());
        assertEquals("test@example.com", owner.getEmail().getValue());
        assertNull(owner.getPhone());
        assertNull(owner.getAddress());
        assertNull(owner.getIdentificationNumber());
        assertNull(owner.getNotes());
    }

    @Test
    @DisplayName("Should use shared repository interface methods")
    void repository_ShouldUseSharedRepositoryInterfaceMethods() {
        // Save using shared interface method
        OwnerEntity savedOwner = repository.save(testOwner);
        assertNotNull(savedOwner.getOwnerId());
        
        // Find by ID using shared interface method
        Optional<OwnerEntity> foundOwner = repository.findById(savedOwner.getOwnerId());
        assertTrue(foundOwner.isPresent());
        
        // Find all using shared interface method
        List<OwnerEntity> allOwners = repository.findAll();
        assertEquals(1, allOwners.size());
        
        // Delete by ID using shared interface method
        repository.deleteById(savedOwner.getOwnerId());
        entityManager.flush();
        
        // Verify deletion using shared interface method
        assertFalse(repository.findById(savedOwner.getOwnerId()).isPresent());
    }
}
```

#### 🏗️ Tests End-to-End

**Ubicación del archivo:**
```
src/test/java/com/datavet/datavet/owner/OwnerEndToEndTest.java
```

**Código completo del test:**

```java
package com.datavet.datavet.owner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for Owner domain.
 * Tests complete user workflows from API to database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@DisplayName("Owner End-to-End Tests")
class OwnerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Complete owner lifecycle: create, read, update, delete")
    void completeOwnerLifecycle_ShouldWorkEndToEnd() throws Exception {
        // 1. CREATE - Create a new owner
        String createRequest = """
            {
                "firstName": "Juan",
                "lastName": "Pérez",
                "email": "juan.perez@example.com",
                "phone": "+51987654321",
                "address": "Av. Lima 123",
                "city": "Lima",
                "postalCode": "15001",
                "identificationNumber": "12345678",
                "notes": "Cliente VIP"
            }
            """;

        String createResponse = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ownerId = objectMapper.readTree(createResponse).get("ownerId").asLong();

        // 2. READ - Get the created owner
        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.phone").value("+51987654321"))
                .andExpect(jsonPath("$.address.street").value("Av. Lima 123"))
                .andExpect(jsonPath("$.identificationNumber").value("12345678"))
                .andExpect(jsonPath("$.notes").value("Cliente VIP"));

        // 3. UPDATE - Update the owner
        String updateRequest = """
            {
                "firstName": "Juan Carlos",
                "lastName": "Pérez García",
                "email": "juan.carlos.perez@example.com",
                "phone": "+51998877665",
                "address": "Av. Javier Prado 789",
                "city": "Lima",
                "postalCode": "15003",
                "identificationNumber": "11223344",
                "notes": "Cliente Premium Actualizado"
            }
            """;

        mockMvc.perform(put("/owners/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Pérez García"))
                .andExpect(jsonPath("$.fullName").value("Juan Carlos Pérez García"))
                .andExpect(jsonPath("$.email").value("juan.carlos.perez@example.com"))
                .andExpect(jsonPath("$.phone").value("+51998877665"))
                .andExpect(jsonPath("$.address.street").value("Av. Javier Prado 789"))
                .andExpect(jsonPath("$.identificationNumber").value("11223344"))
                .andExpect(jsonPath("$.notes").value("Cliente Premium Actualizado"));

        // 4. READ AGAIN - Verify the update persisted
        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"))
                .andExpect(jsonPath("$.lastName").value("Pérez García"))
                .andExpect(jsonPath("$.email").value("juan.carlos.perez@example.com"));

        // 5. DELETE - Delete the owner
        mockMvc.perform(delete("/owners/{id}", ownerId))
                .andExpect(status().isNoContent());

        // 6. VERIFY DELETION - Confirm the owner is deleted
        mockMvc.perform(get("/owners/{id}", ownerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + ownerId));
    }

    @Test
    @DisplayName("Multiple owners workflow with search and filtering")
    void multipleOwnersWorkflow_ShouldWorkEndToEnd() throws Exception {
        // Create multiple owners
        String owner1Request = """
            {
                "firstName": "Juan",
                "lastName": "Pérez",
                "email": "juan.perez@example.com",
                "phone": "+51987654321",
                "address": "Av. Lima 123",
                "city": "Lima",
                "postalCode": "15001",
                "identificationNumber": "12345678",
                "notes": "Cliente VIP"
            }
            """;

        String owner2Request = """
            {
                "firstName": "María",
                "lastName": "García",
                "email": "maria.garcia@example.com",
                "phone": "+51912345678",
                "address": "Av. Arequipa 456",
                "city": "Lima",
                "postalCode": "15002",
                "identificationNumber": "87654321",
                "notes": "Cliente Premium"
            }
            """;

        String owner3Request = """
            {
                "firstName": "Carlos",
                "lastName": "López",
                "email": "carlos.lopez@example.com",
                "phone": "+51955443322",
                "address": "Jr. Cusco 789",
                "city": "Cusco",
                "postalCode": "08001",
                "identificationNumber": "11223344",
                "notes": "Cliente Regular"
            }
            """;

        // Create all owners
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(owner1Request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(owner2Request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(owner3Request))
                .andExpect(status().isCreated());

        // Get all owners
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].firstName", containsInAnyOrder("Juan", "María", "Carlos")))
                .andExpect(jsonPath("$[*].lastName", containsInAnyOrder("Pérez", "García", "López")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                    "juan.perez@example.com", 
                    "maria.garcia@example.com", 
                    "carlos.lopez@example.com")));

        // Test search by email (if implemented)
        mockMvc.perform(get("/owners/search")
                .param("email", "maria.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.lastName").value("García"))
                .andExpect(jsonPath("$.email").value("maria.garcia@example.com"));
    }

    @Test
    @DisplayName("Error handling workflow")
    void errorHandlingWorkflow_ShouldWorkEndToEnd() throws Exception {
        // Test validation errors
        String invalidRequest = """
            {
                "firstName": "",
                "lastName": "",
                "email": "invalid-email",
                "phone": "invalid-phone",
                "address": "",
                "city": "",
                "postalCode": "12345",
                "identificationNumber": null,
                "notes": null
            }
            """;

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));

        // Test not found error
        mockMvc.perform(get("/owners/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: 99999"));

        // Test duplicate email error
        String validRequest = """
            {
                "firstName": "Juan",
                "lastName": "Pérez",
                "email": "duplicate@example.com",
                "phone": "+51987654321",
                "address": "Av. Lima 123",
                "city": "Lima",
                "postalCode": "15001",
                "identificationNumber": "12345678",
                "notes": "Cliente VIP"
            }
            """;

        // Create first owner
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest))
                .andExpect(status().isCreated());

        // Try to create second owner with same email
        String duplicateRequest = """
            {
                "firstName": "María",
                "lastName": "García",
                "email": "duplicate@example.com",
                "phone": "+51912345678",
                "address": "Av. Arequipa 456",
                "city": "Lima",
                "postalCode": "15002",
                "identificationNumber": "87654321",
                "notes": "Cliente Premium"
            }
            """;

        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Owner already exists with email: duplicate@example.com"));
    }
}
```

#### ✅ Checklist - Tests de Integración

- [ ] ✅ Tests de controladores REST (endpoints completos)
- [ ] ✅ Tests de repositorios JPA (persistencia y consultas)
- [ ] ✅ Tests end-to-end (flujos completos de usuario)
- [ ] ✅ Tests de validación HTTP (códigos de respuesta)
- [ ] ✅ Tests de manejo de errores (404, 400, 409)
- [ ] ✅ Tests de serialización/deserialización JSON
- [ ] ✅ Tests de integración con base de datos H2
- [ ] ✅ Tests de conversión de Value Objects

#### 🚀 Comandos para Ejecutar Tests de Integración

```bash
# Ejecutar todos los tests de integración del dominio Owner
mvn test -Dtest="*Owner*IntegrationTest"

# Ejecutar solo tests de controlador
mvn test -Dtest="*OwnerController*IntegrationTest"

# Ejecutar solo tests de repositorio
mvn test -Dtest="*OwnerRepository*IntegrationTest"

# Ejecutar tests end-to-end
mvn test -Dtest="*Owner*EndToEndTest"

# Ejecutar todos los tests con perfil de integración
mvn test -Dspring.profiles.active=test
```

### Herramientas de Testing

Además de los tests automatizados, es importante saber cómo probar tu API manualmente usando diferentes herramientas. Esta sección te muestra cómo usar **Postman**, **curl**, y crear scripts automatizados.

#### 🚀 Testing con Postman

**Postman** es una herramienta gráfica muy popular para probar APIs REST. Te permite crear colecciones de requests, automatizar tests, y compartir con tu equipo.

##### Configuración Inicial

1. **Descargar e instalar Postman:**
   - Ve a [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
   - Descarga e instala la versión para tu sistema operativo

2. **Crear una nueva colección:**
   - Abre Postman
   - Haz clic en "New" → "Collection"
   - Nombra la colección: "DataVet Owner API"
   - Agrega descripción: "Tests para el dominio Owner"

##### Requests de Ejemplo

**1. Crear un nuevo dueño (POST)**

```http
POST http://localhost:8080/owners
Content-Type: application/json

{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "+51987654321",
    "address": "Av. Lima 123",
    "city": "Lima",
    "postalCode": "15001",
    "identificationNumber": "12345678",
    "notes": "Cliente VIP"
}
```

**Tests automáticos en Postman:**
```javascript
// Test que el status sea 201
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

// Test que la respuesta tenga los campos esperados
pm.test("Response has required fields", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('ownerId');
    pm.expect(responseJson).to.have.property('firstName');
    pm.expect(responseJson).to.have.property('lastName');
    pm.expect(responseJson).to.have.property('email');
    pm.expect(responseJson).to.have.property('fullName');
});

// Guardar el ID para usar en otros requests
pm.test("Save owner ID", function () {
    const responseJson = pm.response.json();
    pm.globals.set("ownerId", responseJson.ownerId);
});

// Test que el nombre completo sea correcto
pm.test("Full name is correct", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.fullName).to.eql("Juan Pérez");
});
```

**2. Obtener dueño por ID (GET)**

```http
GET http://localhost:8080/owners/{{ownerId}}
```

**Tests automáticos:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Owner data is correct", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.firstName).to.eql("Juan");
    pm.expect(responseJson.lastName).to.eql("Pérez");
    pm.expect(responseJson.email).to.eql("juan.perez@example.com");
});
```

**3. Actualizar dueño (PUT)**

```http
PUT http://localhost:8080/owners/{{ownerId}}
Content-Type: application/json

{
    "firstName": "Juan Carlos",
    "lastName": "Pérez García",
    "email": "juan.carlos.perez@example.com",
    "phone": "+51998877665",
    "address": "Av. Javier Prado 789",
    "city": "Lima",
    "postalCode": "15003",
    "identificationNumber": "11223344",
    "notes": "Cliente Premium Actualizado"
}
```

**4. Obtener todos los dueños (GET)**

```http
GET http://localhost:8080/owners
```

**5. Eliminar dueño (DELETE)**

```http
DELETE http://localhost:8080/owners/{{ownerId}}
```

##### Variables de Entorno en Postman

Crea variables para diferentes entornos:

**Entorno: Local Development**
```
baseUrl: http://localhost:8080
```

**Entorno: Test**
```
baseUrl: http://localhost:8081
```

**Entorno: Staging**
```
baseUrl: https://staging-api.datavet.com
```

#### 🖥️ Testing con curl

**curl** es una herramienta de línea de comandos perfecta para testing rápido y scripts automatizados.

##### Comandos Básicos

**1. Crear un nuevo dueño:**
```bash
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "+51987654321",
    "address": "Av. Lima 123",
    "city": "Lima",
    "postalCode": "15001",
    "identificationNumber": "12345678",
    "notes": "Cliente VIP"
  }' \
  -w "\nStatus: %{http_code}\nTime: %{time_total}s\n"
```

**2. Obtener dueño por ID:**
```bash
curl -X GET http://localhost:8080/owners/1 \
  -H "Accept: application/json" \
  -w "\nStatus: %{http_code}\n"
```

**3. Actualizar dueño:**
```bash
curl -X PUT http://localhost:8080/owners/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan Carlos",
    "lastName": "Pérez García",
    "email": "juan.carlos.perez@example.com",
    "phone": "+51998877665",
    "address": "Av. Javier Prado 789",
    "city": "Lima",
    "postalCode": "15003",
    "identificationNumber": "11223344",
    "notes": "Cliente Premium Actualizado"
  }' \
  -w "\nStatus: %{http_code}\n"
```

**4. Obtener todos los dueños:**
```bash
curl -X GET http://localhost:8080/owners \
  -H "Accept: application/json" \
  | jq '.'  # jq para formatear JSON (opcional)
```

**5. Eliminar dueño:**
```bash
curl -X DELETE http://localhost:8080/owners/1 \
  -w "\nStatus: %{http_code}\n"
```

##### Comandos Avanzados con curl

**Test de validación (debería devolver 400):**
```bash
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "",
    "lastName": "",
    "email": "invalid-email"
  }' \
  -w "\nStatus: %{http_code}\n" \
  -v  # -v para ver headers completos
```

**Test de duplicado (debería devolver 409):**
```bash
# Primero crear un dueño
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "duplicate@example.com",
    "phone": "+51987654321",
    "address": "Av. Lima 123",
    "city": "Lima",
    "postalCode": "15001"
  }'

# Luego intentar crear otro con el mismo email
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "María",
    "lastName": "García",
    "email": "duplicate@example.com",
    "phone": "+51912345678",
    "address": "Av. Arequipa 456",
    "city": "Lima",
    "postalCode": "15002"
  }' \
  -w "\nStatus: %{http_code}\n"
```

#### 📜 Scripts de Prueba Automatizados

Crea scripts para automatizar las pruebas de tu API.

##### Script Bash Completo

**Archivo:** `test-owner-api.sh`

```bash
#!/bin/bash

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuración
BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

# Función para imprimir resultados
print_result() {
    if [ $1 -eq $2 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $3"
    else
        echo -e "${RED}✗ FAIL${NC}: $3 (Expected: $2, Got: $1)"
    fi
}

# Función para hacer requests
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local expected_status=$4
    local description=$5
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "%{http_code}" -X $method "$BASE_URL$url" \
                       -H "$CONTENT_TYPE" -d "$data")
    else
        response=$(curl -s -w "%{http_code}" -X $method "$BASE_URL$url")
    fi
    
    # Extraer status code (últimos 3 caracteres)
    status_code="${response: -3}"
    # Extraer body (todo excepto los últimos 3 caracteres)
    body="${response%???}"
    
    print_result $status_code $expected_status "$description"
    
    # Retornar el body para uso posterior
    echo "$body"
}

echo -e "${YELLOW}🧪 Iniciando tests de Owner API...${NC}\n"

# Test 1: Crear dueño válido
echo "1. Testing owner creation..."
create_data='{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "+51987654321",
    "address": "Av. Lima 123",
    "city": "Lima",
    "postalCode": "15001",
    "identificationNumber": "12345678",
    "notes": "Cliente VIP"
}'

create_response=$(make_request "POST" "/owners" "$create_data" 201 "Create owner with valid data")

# Extraer owner ID del response
owner_id=$(echo "$create_response" | jq -r '.ownerId' 2>/dev/null)

if [ "$owner_id" != "null" ] && [ -n "$owner_id" ]; then
    echo -e "${GREEN}✓${NC} Owner created with ID: $owner_id\n"
else
    echo -e "${RED}✗${NC} Failed to extract owner ID\n"
    exit 1
fi

# Test 2: Obtener dueño por ID
echo "2. Testing get owner by ID..."
make_request "GET" "/owners/$owner_id" "" 200 "Get owner by ID" > /dev/null

# Test 3: Obtener todos los dueños
echo "3. Testing get all owners..."
make_request "GET" "/owners" "" 200 "Get all owners" > /dev/null

# Test 4: Actualizar dueño
echo "4. Testing owner update..."
update_data='{
    "firstName": "Juan Carlos",
    "lastName": "Pérez García",
    "email": "juan.carlos.perez@example.com",
    "phone": "+51998877665",
    "address": "Av. Javier Prado 789",
    "city": "Lima",
    "postalCode": "15003",
    "identificationNumber": "11223344",
    "notes": "Cliente Premium Actualizado"
}'

make_request "PUT" "/owners/$owner_id" "$update_data" 200 "Update owner with valid data" > /dev/null

# Test 5: Validación - datos inválidos
echo "5. Testing validation errors..."
invalid_data='{
    "firstName": "",
    "lastName": "",
    "email": "invalid-email"
}'

make_request "POST" "/owners" "$invalid_data" 400 "Create owner with invalid data" > /dev/null

# Test 6: Email duplicado
echo "6. Testing duplicate email..."
duplicate_data='{
    "firstName": "María",
    "lastName": "García",
    "email": "juan.carlos.perez@example.com",
    "phone": "+51912345678",
    "address": "Av. Arequipa 456",
    "city": "Lima",
    "postalCode": "15002"
}'

make_request "POST" "/owners" "$duplicate_data" 409 "Create owner with duplicate email" > /dev/null

# Test 7: Dueño no encontrado
echo "7. Testing owner not found..."
make_request "GET" "/owners/99999" "" 404 "Get non-existent owner" > /dev/null

# Test 8: Eliminar dueño
echo "8. Testing owner deletion..."
make_request "DELETE" "/owners/$owner_id" "" 204 "Delete owner" > /dev/null

# Test 9: Verificar eliminación
echo "9. Testing deletion verification..."
make_request "GET" "/owners/$owner_id" "" 404 "Get deleted owner" > /dev/null

echo -e "\n${GREEN}🎉 Tests completados!${NC}"
```

**Hacer el script ejecutable y correrlo:**
```bash
chmod +x test-owner-api.sh
./test-owner-api.sh
```

##### Script Python Avanzado

**Archivo:** `test_owner_api.py`

```python
#!/usr/bin/env python3
import requests
import json
import sys
from typing import Dict, Any, Optional

class OwnerAPITester:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({'Content-Type': 'application/json'})
        self.tests_passed = 0
        self.tests_failed = 0
        
    def print_result(self, success: bool, description: str, details: str = ""):
        if success:
            print(f"✓ PASS: {description}")
            self.tests_passed += 1
        else:
            print(f"✗ FAIL: {description}")
            if details:
                print(f"  Details: {details}")
            self.tests_failed += 1
    
    def make_request(self, method: str, endpoint: str, data: Optional[Dict] = None, 
                    expected_status: int = 200) -> tuple[bool, Optional[Dict]]:
        url = f"{self.base_url}{endpoint}"
        
        try:
            if method.upper() == 'GET':
                response = self.session.get(url)
            elif method.upper() == 'POST':
                response = self.session.post(url, json=data)
            elif method.upper() == 'PUT':
                response = self.session.put(url, json=data)
            elif method.upper() == 'DELETE':
                response = self.session.delete(url)
            else:
                raise ValueError(f"Unsupported method: {method}")
            
            success = response.status_code == expected_status
            response_data = response.json() if response.content else None
            
            return success, response_data
            
        except Exception as e:
            return False, {"error": str(e)}
    
    def test_create_owner(self) -> Optional[int]:
        """Test creating a new owner"""
        print("\n1. Testing owner creation...")
        
        owner_data = {
            "firstName": "Juan",
            "lastName": "Pérez",
            "email": "juan.perez@example.com",
            "phone": "+51987654321",
            "address": "Av. Lima 123",
            "city": "Lima",
            "postalCode": "15001",
            "identificationNumber": "12345678",
            "notes": "Cliente VIP"
        }
        
        success, response = self.make_request("POST", "/owners", owner_data, 201)
        self.print_result(success, "Create owner with valid data")
        
        if success and response:
            owner_id = response.get('ownerId')
            print(f"  Owner created with ID: {owner_id}")
            return owner_id
        
        return None
    
    def test_get_owner(self, owner_id: int):
        """Test getting owner by ID"""
        print("\n2. Testing get owner by ID...")
        
        success, response = self.make_request("GET", f"/owners/{owner_id}")
        self.print_result(success, f"Get owner by ID {owner_id}")
        
        if success and response:
            expected_fields = ['ownerId', 'firstName', 'lastName', 'email', 'fullName']
            for field in expected_fields:
                has_field = field in response
                self.print_result(has_field, f"Response has field '{field}'")
    
    def test_get_all_owners(self):
        """Test getting all owners"""
        print("\n3. Testing get all owners...")
        
        success, response = self.make_request("GET", "/owners")
        self.print_result(success, "Get all owners")
        
        if success and response:
            is_list = isinstance(response, list)
            self.print_result(is_list, "Response is a list")
            
            if is_list and len(response) > 0:
                self.print_result(True, f"Found {len(response)} owner(s)")
    
    def test_update_owner(self, owner_id: int):
        """Test updating an owner"""
        print("\n4. Testing owner update...")
        
        update_data = {
            "firstName": "Juan Carlos",
            "lastName": "Pérez García",
            "email": "juan.carlos.perez@example.com",
            "phone": "+51998877665",
            "address": "Av. Javier Prado 789",
            "city": "Lima",
            "postalCode": "15003",
            "identificationNumber": "11223344",
            "notes": "Cliente Premium Actualizado"
        }
        
        success, response = self.make_request("PUT", f"/owners/{owner_id}", update_data)
        self.print_result(success, f"Update owner {owner_id}")
        
        if success and response:
            name_updated = response.get('firstName') == 'Juan Carlos'
            self.print_result(name_updated, "First name was updated correctly")
    
    def test_validation_errors(self):
        """Test validation with invalid data"""
        print("\n5. Testing validation errors...")
        
        invalid_data = {
            "firstName": "",
            "lastName": "",
            "email": "invalid-email"
        }
        
        success, response = self.make_request("POST", "/owners", invalid_data, 400)
        self.print_result(success, "Validation error for invalid data")
    
    def test_duplicate_email(self):
        """Test duplicate email error"""
        print("\n6. Testing duplicate email...")
        
        # First create a valid owner
        owner_data = {
            "firstName": "Test",
            "lastName": "User",
            "email": "duplicate.test@example.com",
            "phone": "+51987654321",
            "address": "Test Address",
            "city": "Lima",
            "postalCode": "15001"
        }
        
        self.make_request("POST", "/owners", owner_data, 201)
        
        # Try to create another with same email
        duplicate_data = {
            "firstName": "Another",
            "lastName": "User",
            "email": "duplicate.test@example.com",
            "phone": "+51912345678",
            "address": "Another Address",
            "city": "Lima",
            "postalCode": "15002"
        }
        
        success, response = self.make_request("POST", "/owners", duplicate_data, 409)
        self.print_result(success, "Duplicate email error")
    
    def test_not_found(self):
        """Test not found error"""
        print("\n7. Testing owner not found...")
        
        success, response = self.make_request("GET", "/owners/99999", expected_status=404)
        self.print_result(success, "Owner not found error")
    
    def test_delete_owner(self, owner_id: int):
        """Test deleting an owner"""
        print("\n8. Testing owner deletion...")
        
        success, response = self.make_request("DELETE", f"/owners/{owner_id}", expected_status=204)
        self.print_result(success, f"Delete owner {owner_id}")
        
        # Verify deletion
        print("\n9. Testing deletion verification...")
        success, response = self.make_request("GET", f"/owners/{owner_id}", expected_status=404)
        self.print_result(success, "Verify owner was deleted")
    
    def run_all_tests(self):
        """Run all tests"""
        print("🧪 Starting Owner API Tests...\n")
        
        # Test creation and get the owner ID
        owner_id = self.test_create_owner()
        
        if owner_id:
            self.test_get_owner(owner_id)
            self.test_get_all_owners()
            self.test_update_owner(owner_id)
            self.test_delete_owner(owner_id)
        
        # Test error cases
        self.test_validation_errors()
        self.test_duplicate_email()
        self.test_not_found()
        
        # Print summary
        total_tests = self.tests_passed + self.tests_failed
        print(f"\n🎉 Tests completed!")
        print(f"   Passed: {self.tests_passed}/{total_tests}")
        print(f"   Failed: {self.tests_failed}/{total_tests}")
        
        if self.tests_failed > 0:
            sys.exit(1)

if __name__ == "__main__":
    tester = OwnerAPITester()
    tester.run_all_tests()
```

**Ejecutar el script Python:**
```bash
python3 test_owner_api.py
```

#### 🔧 Herramientas Adicionales

##### HTTPie - Alternativa moderna a curl

**Instalación:**
```bash
pip install httpie
```

**Ejemplos de uso:**
```bash
# Crear dueño
http POST localhost:8080/owners \
  firstName="Juan" \
  lastName="Pérez" \
  email="juan.perez@example.com" \
  phone="+51987654321" \
  address="Av. Lima 123" \
  city="Lima" \
  postalCode="15001"

# Obtener dueño
http GET localhost:8080/owners/1

# Actualizar dueño
http PUT localhost:8080/owners/1 \
  firstName="Juan Carlos" \
  lastName="Pérez García" \
  email="juan.carlos@example.com"

# Eliminar dueño
http DELETE localhost:8080/owners/1
```

##### Newman - Ejecutar colecciones de Postman desde CLI

**Instalación:**
```bash
npm install -g newman
```

**Exportar colección de Postman y ejecutar:**
```bash
newman run DataVet-Owner-API.postman_collection.json \
  --environment Local-Development.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export test-results.html
```

#### ✅ Checklist - Herramientas de Testing

- [ ] ✅ Colección de Postman configurada con todos los endpoints
- [ ] ✅ Variables de entorno para diferentes ambientes
- [ ] ✅ Tests automáticos en Postman para validar responses
- [ ] ✅ Comandos curl para todos los casos de uso
- [ ] ✅ Scripts bash para testing automatizado
- [ ] ✅ Scripts Python para testing avanzado
- [ ] ✅ Configuración de HTTPie como alternativa
- [ ] ✅ Integración con Newman para CI/CD

#### 🚀 Comandos de Verificación Final

```bash
# Iniciar la aplicación
mvn spring-boot:run

# En otra terminal, ejecutar tests manuales
curl -X GET http://localhost:8080/owners

# Ejecutar script de testing
./test-owner-api.sh

# Ejecutar tests Python
python3 test_owner_api.py

# Ejecutar colección de Postman
newman run DataVet-Owner-API.postman_collection.json

# Ejecutar todos los tests automatizados
mvn test

# Generar reporte de cobertura
mvn test jacoco:report
```

Con estas herramientas de testing, tienes una suite completa para validar tu dominio Owner desde diferentes perspectivas: tests unitarios para lógica de negocio, tests de integración para verificar el funcionamiento conjunto, y herramientas manuales para testing exploratorio y debugging.

---

## 8. Mejores Prácticas y Troubleshooting

Esta sección te ayudará a escribir código de alta calidad, siguiendo los patrones establecidos en el proyecto y evitando errores comunes. Aquí encontrarás las mejores prácticas que han surgido de la experiencia del equipo y las soluciones a los problemas más frecuentes.

### Patrones de Diseño

#### 🏗️ Patrones Arquitectónicos Utilizados

**1. Arquitectura Hexagonal (Puertos y Adaptadores)**

```java
// ✅ CORRECTO: Definir puertos como interfaces
public interface OwnerUseCase {
    OwnerResponse createOwner(CreateOwnerCommand command);
    OwnerResponse getOwnerById(Long ownerId);
}

// ✅ CORRECTO: Implementar adaptadores
@Service
public class OwnerService implements OwnerUseCase {
    // Implementación que orquesta el dominio
}
```

**❌ Evitar:**
```java
// ❌ INCORRECTO: Acoplar directamente capas
@RestController
public class OwnerController {
    @Autowired
    private OwnerRepository repository; // ❌ Saltarse la capa de aplicación
}
```

**2. Domain-Driven Design (DDD)**

```java
// ✅ CORRECTO: Agregados con comportamiento rico
public class Owner extends AggregateRoot<Long> {
    
    // Factory method que encapsula lógica de creación
    public static Owner create(String firstName, String lastName, Email email) {
        // Validaciones de negocio
        validateBusinessRules(firstName, lastName, email);
        
        Owner owner = new Owner(firstName, lastName, email);
        owner.addDomainEvent(OwnerCreatedEvent.of(owner.getId()));
        return owner;
    }
    
    // Métodos de negocio con nombres expresivos
    public void updateContactInfo(Email newEmail, Phone newPhone) {
        this.email = newEmail;
        this.phone = newPhone;
        addDomainEvent(OwnerUpdatedEvent.of(this.getId()));
    }
}
```

**❌ Evitar:**
```java
// ❌ INCORRECTO: Modelos anémicos sin comportamiento
public class Owner {
    private String firstName;
    private String lastName;
    // Solo getters y setters, sin lógica de negocio
}
```

**3. Repository Pattern**

```java
// ✅ CORRECTO: Puerto de salida en la capa de aplicación
public interface OwnerRepositoryPort {
    Owner save(Owner owner);
    Optional<Owner> findById(Long id);
    Optional<Owner> findByEmail(Email email);
    List<Owner> findByLastName(String lastName);
}

// ✅ CORRECTO: Adaptador en la capa de infraestructura
@Repository
public class JpaOwnerRepositoryAdapter implements OwnerRepositoryPort {
    // Implementación específica de JPA
}
```

**4. Command Query Responsibility Segregation (CQRS) Ligero**

```java
// ✅ CORRECTO: Separar comandos de consultas
public interface OwnerUseCase {
    // Comandos (modifican estado)
    OwnerResponse createOwner(CreateOwnerCommand command);
    OwnerResponse updateOwner(UpdateOwnerCommand command);
    void deleteOwner(Long ownerId);
    
    // Consultas (solo lectura)
    OwnerResponse getOwnerById(Long ownerId);
    List<OwnerResponse> findOwnersByLastName(String lastName);
}
```

**5. Event-Driven Architecture**

```java
// ✅ CORRECTO: Eventos de dominio para comunicación asíncrona
public class Owner extends AggregateRoot<Long> {
    
    public void delete() {
        // Lógica de eliminación
        addDomainEvent(OwnerDeletedEvent.of(this.getId(), this.getFullName()));
    }
}

// ✅ CORRECTO: Manejadores de eventos desacoplados
@EventHandler
public class OwnerEventHandler {
    
    @EventListener
    public void handleOwnerDeleted(OwnerDeletedEvent event) {
        // Limpiar datos relacionados
        // Notificar otros sistemas
    }
}
```

#### 🎯 Patrones de Implementación Específicos

**1. Factory Methods para Creación Controlada**

```java
// ✅ CORRECTO: Factory method con validaciones
public static Owner create(String firstName, String lastName, Email email) {
    // Validaciones de negocio
    if (firstName == null || firstName.trim().isEmpty()) {
        throw new OwnerValidationException("First name is required");
    }
    
    // Creación controlada
    Owner owner = Owner.builder()
        .firstName(firstName.trim())
        .lastName(lastName.trim())
        .email(email)
        .createdAt(LocalDateTime.now())
        .build();
    
    // Evento automático
    owner.addDomainEvent(OwnerCreatedEvent.of(owner.getId()));
    return owner;
}
```

**2. Value Objects para Encapsular Validaciones**

```java
// ✅ CORRECTO: Reutilizar Value Objects del shared
public class Owner {
    private Email email;        // ✅ Validación automática
    private Phone phone;        // ✅ Formato consistente
    private Address address;    // ✅ Estructura estándar
}

// ❌ INCORRECTO: Validaciones dispersas
public class Owner {
    private String email;       // ❌ Sin validación
    private String phone;       // ❌ Formato inconsistente
}
```

**3. Builder Pattern para Objetos Complejos**

```java
// ✅ CORRECTO: Builder con Lombok para DTOs
@Builder
@Getter
public class CreateOwnerCommand {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String address;
}

// Uso limpio
CreateOwnerCommand command = CreateOwnerCommand.builder()
    .firstName("John")
    .lastName("Doe")
    .email("john@example.com")
    .build();
```

**4. Mapper Pattern para Conversiones**

```java
// ✅ CORRECTO: Mappers estáticos para conversiones
public class OwnerMapper {
    
    public static OwnerResponse toResponse(Owner owner) {
        return OwnerResponse.builder()
            .ownerId(owner.getId())
            .firstName(owner.getFirstName())
            .lastName(owner.getLastName())
            .email(owner.getEmail().getValue())
            .fullName(owner.getFullName())
            .build();
    }
    
    public static CreateOwnerCommand toCommand(CreateOwnerRequest request) {
        return CreateOwnerCommand.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .build();
    }
}
```

### Convenciones de Naming

#### 📁 Estructura de Paquetes

**Patrón General:**
```
com.datavet.datavet.[dominio].[capa].[subcapa]
```

**Ejemplos Específicos:**
```java
// ✅ CORRECTO: Seguir la estructura establecida
com.datavet.datavet.owner.domain.model.Owner
com.datavet.datavet.owner.domain.event.OwnerCreatedEvent
com.datavet.datavet.owner.application.service.OwnerService
com.datavet.datavet.owner.infrastructure.adapter.input.OwnerController
```

#### 🏷️ Nombres de Clases

| Tipo | Patrón | Ejemplo | ❌ Evitar |
|------|--------|---------|-----------|
| **Modelo de Dominio** | `[Dominio]` | `Owner` | `OwnerModel`, `OwnerEntity` |
| **Evento** | `[Dominio][Acción]Event` | `OwnerCreatedEvent` | `CreateOwnerEvent` |
| **Excepción** | `[Dominio][Tipo]Exception` | `OwnerNotFoundException` | `OwnerNotFound` |
| **Comando** | `[Acción][Dominio]Command` | `CreateOwnerCommand` | `OwnerCreateCommand` |
| **UseCase** | `[Dominio]UseCase` | `OwnerUseCase` | `OwnerService` |
| **Service** | `[Dominio]Service` | `OwnerService` | `OwnerApplicationService` |
| **Controller** | `[Dominio]Controller` | `OwnerController` | `OwnerRestController` |
| **Entity JPA** | `[Dominio]Entity` | `OwnerEntity` | `Owner` |
| **Repository** | `Jpa[Dominio]Repository` | `JpaOwnerRepository` | `OwnerRepository` |
| **Adapter** | `[Dominio]RepositoryAdapter` | `OwnerRepositoryAdapter` | `OwnerAdapter` |

#### 🗄️ Nombres de Base de Datos

**Tablas:**
```sql
-- ✅ CORRECTO: Singular, lowercase
CREATE TABLE owner (
    owner_id BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
);

-- ❌ INCORRECTO: Plural o camelCase
CREATE TABLE owners (...)  -- ❌ Plural
CREATE TABLE Owner (...)   -- ❌ PascalCase
```

**Columnas:**
```sql
-- ✅ CORRECTO: snake_case
owner_id, first_name, last_name, email_address, phone_number

-- ❌ INCORRECTO: camelCase o PascalCase
ownerId, firstName, LastName  -- ❌ Inconsistente
```

**Índices:**
```sql
-- ✅ CORRECTO: Descriptivo con prefijo
CREATE INDEX idx_owner_email ON owner(email_address);
CREATE INDEX idx_owner_last_name ON owner(last_name);

-- ❌ INCORRECTO: Nombres genéricos
CREATE INDEX index1 ON owner(email_address);  -- ❌ No descriptivo
```

#### 🔗 Nombres de Endpoints REST

**Patrón RESTful:**
```java
// ✅ CORRECTO: Recursos en plural, verbos HTTP
@GetMapping("/owners")              // Listar todos
@GetMapping("/owners/{id}")         // Obtener por ID
@PostMapping("/owners")             // Crear nuevo
@PutMapping("/owners/{id}")         // Actualizar completo
@PatchMapping("/owners/{id}")       // Actualización parcial
@DeleteMapping("/owners/{id}")      // Eliminar

// ❌ INCORRECTO: Verbos en la URL
@GetMapping("/owners/getAll")       // ❌ Verbo innecesario
@PostMapping("/owners/create")      // ❌ Verbo redundante
@GetMapping("/getOwner/{id}")       // ❌ Verbo en la URL
```

#### 📝 Nombres de Métodos

**Servicios de Aplicación:**
```java
// ✅ CORRECTO: Verbos claros que expresan la acción de negocio
public OwnerResponse createOwner(CreateOwnerCommand command)
public OwnerResponse updateOwner(UpdateOwnerCommand command)
public void deleteOwner(Long ownerId)
public OwnerResponse getOwnerById(Long ownerId)
public List<OwnerResponse> findOwnersByLastName(String lastName)

// ❌ INCORRECTO: Nombres técnicos o ambiguos
public OwnerResponse save(CreateOwnerCommand command)     // ❌ Muy técnico
public OwnerResponse process(CreateOwnerCommand command)  // ❌ Ambiguo
public OwnerResponse handle(CreateOwnerCommand command)   // ❌ Genérico
```

**Repositorios:**
```java
// ✅ CORRECTO: Patrones estándar de Spring Data
Optional<Owner> findById(Long id)
Optional<Owner> findByEmail(Email email)
List<Owner> findByLastName(String lastName)
List<Owner> findByLastNameContaining(String partialName)
boolean existsByEmail(Email email)

// ❌ INCORRECTO: Nombres inconsistentes
Owner getById(Long id)              // ❌ No maneja Optional
Owner searchByEmail(String email)   // ❌ Inconsistente con find*
```

#### 🏷️ Nombres de Variables y Campos

**Campos de Clase:**
```java
// ✅ CORRECTO: Descriptivos y consistentes
private String firstName;
private String lastName;
private Email email;
private Phone phone;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;

// ❌ INCORRECTO: Abreviaciones o nombres confusos
private String fName;               // ❌ Abreviación
private String name1, name2;        // ❌ No descriptivo
private Date created;               // ❌ Tipo impreciso
```

**Variables Locales:**
```java
// ✅ CORRECTO: Nombres claros y concisos
Owner existingOwner = ownerRepository.findById(ownerId);
CreateOwnerCommand command = mapper.toCommand(request);
OwnerResponse response = mapper.toResponse(owner);

// ❌ INCORRECTO: Nombres genéricos o confusos
Owner o = ownerRepository.findById(ownerId);     // ❌ Muy corto
Owner temp = ownerRepository.findById(ownerId);  // ❌ No descriptivo
Owner ownerFromDatabase = ownerRepository.findById(ownerId); // ❌ Muy largo
```

#### 🎯 Consejos de Performance

**1. Lazy Loading en JPA**

```java
// ✅ CORRECTO: Usar fetch joins cuando necesites los datos
@Query("SELECT o FROM OwnerEntity o LEFT JOIN FETCH o.pets WHERE o.id = :id")
Optional<OwnerEntity> findByIdWithPets(@Param("id") Long id);

// ✅ CORRECTO: Lazy loading por defecto para relaciones opcionales
@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
private List<PetEntity> pets;
```

**2. Paginación para Listas Grandes**

```java
// ✅ CORRECTO: Usar Pageable para listas grandes
@GetMapping("/owners")
public Page<OwnerResponse> getAllOwners(
    @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
    return ownerService.getAllOwners(pageable);
}
```

**3. Cacheo Estratégico**

```java
// ✅ CORRECTO: Cachear consultas frecuentes y estables
@Cacheable(value = "owners", key = "#ownerId")
public OwnerResponse getOwnerById(Long ownerId) {
    // Implementación
}

// ✅ CORRECTO: Invalidar caché en actualizaciones
@CacheEvict(value = "owners", key = "#command.ownerId")
public OwnerResponse updateOwner(UpdateOwnerCommand command) {
    // Implementación
}
```

**4. Validaciones Eficientes**

```java
// ✅ CORRECTO: Validar en el orden correcto (más rápido primero)
public void validateOwnerData(CreateOwnerCommand command) {
    // 1. Validaciones rápidas primero
    if (command.getFirstName() == null || command.getFirstName().trim().isEmpty()) {
        throw new OwnerValidationException("First name is required");
    }
    
    // 2. Validaciones que requieren base de datos al final
    if (ownerRepository.existsByEmail(Email.of(command.getEmail()))) {
        throw new OwnerAlreadyExistsException.withEmail(command.getEmail());
    }
}
```

**5. Consultas Optimizadas**

```java
// ✅ CORRECTO: Proyecciones para consultas que solo necesitan algunos campos
public interface OwnerSummary {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
}

@Query("SELECT o.id as id, o.firstName as firstName, o.lastName as lastName, o.email as email FROM OwnerEntity o")
List<OwnerSummary> findAllSummaries();
```

**6. Transacciones Apropiadas**

```java
// ✅ CORRECTO: Transacciones solo donde son necesarias
@Transactional
public OwnerResponse createOwner(CreateOwnerCommand command) {
    // Operación que modifica datos
}

@Transactional(readOnly = true)
public OwnerResponse getOwnerById(Long ownerId) {
    // Operación de solo lectura
}
```

#### 🔒 Mejores Prácticas de Seguridad

**1. Validación de Entrada**

```java
// ✅ CORRECTO: Validar y sanitizar todas las entradas
@PostMapping("/owners")
public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
    // @Valid activa Bean Validation automáticamente
    CreateOwnerCommand command = ownerMapper.toCommand(request);
    OwnerResponse response = ownerService.createOwner(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**2. Manejo Seguro de Excepciones**

```java
// ✅ CORRECTO: No exponer información sensible
@ExceptionHandler(OwnerNotFoundException.class)
public ResponseEntity<ErrorResponse> handleOwnerNotFound(OwnerNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .message("Owner not found")  // ✅ Mensaje genérico
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}

// ❌ INCORRECTO: Exponer detalles internos
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleGenericError(Exception ex) {
    return ResponseEntity.status(500).body(ex.getMessage()); // ❌ Puede exponer información sensible
}
```

**3. Logging Seguro**

```java
// ✅ CORRECTO: Log información útil sin datos sensibles
log.info("Creating new owner with ID: {}", ownerId);
log.debug("Owner created successfully: {}", owner.getId());

// ❌ INCORRECTO: Log información sensible
log.info("Creating owner: {}", owner.toString()); // ❌ Puede incluir email, teléfono, etc.
```

#### 🧪 Mejores Prácticas de Testing

**1. Estructura de Tests**

```java
// ✅ CORRECTO: Estructura AAA (Arrange, Act, Assert)
@Test
void shouldCreateOwnerSuccessfully() {
    // Arrange
    CreateOwnerCommand command = CreateOwnerCommand.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john@example.com")
        .build();
    
    // Act
    OwnerResponse response = ownerService.createOwner(command);
    
    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getFirstName()).isEqualTo("John");
    assertThat(response.getLastName()).isEqualTo("Doe");
}
```

**2. Nombres de Tests Descriptivos**

```java
// ✅ CORRECTO: Nombres que explican el escenario
@Test
void shouldThrowExceptionWhenCreatingOwnerWithDuplicateEmail() { }

@Test
void shouldReturnOwnerWhenValidIdProvided() { }

@Test
void shouldUpdateOwnerContactInfoSuccessfully() { }

// ❌ INCORRECTO: Nombres genéricos
@Test
void testCreateOwner() { }  // ❌ No específico

@Test
void test1() { }  // ❌ No descriptivo
```

**3. Tests de Integración Efectivos**

```java
// ✅ CORRECTO: Test de integración completo
@SpringBootTest
@Transactional
class OwnerControllerIntegrationTest {
    
    @Test
    void shouldCreateOwnerThroughRestApi() {
        // Given
        CreateOwnerRequest request = CreateOwnerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();
        
        // When
        ResponseEntity<OwnerResponse> response = restTemplate.postForEntity(
            "/owners", request, OwnerResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getFirstName()).isEqualTo("John");
        
        // Verify in database
        Optional<OwnerEntity> savedOwner = ownerRepository.findById(response.getBody().getOwnerId());
        assertThat(savedOwner).isPresent();
    }
}
```

Estas mejores prácticas te ayudarán a mantener un código consistente, eficiente y mantenible en tu dominio Owner y en futuros dominios que implementes.

### Solución de Problemas Comunes

Esta sección te ayudará a identificar y resolver los errores más frecuentes que pueden aparecer durante el desarrollo de un nuevo dominio. Cada problema incluye síntomas, causas posibles y soluciones paso a paso.

#### 🚨 Errores de Compilación

**1. Error: "Cannot resolve symbol 'AggregateRoot'"**

```bash
Error: java: cannot find symbol
  symbol:   class AggregateRoot
  location: package com.datavet.datavet.shared.domain.model
```

**🔍 Causa:** No se está importando correctamente la clase base del shared.

**✅ Solución:**
```java
// Verificar que el import sea correcto
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Entity;

// Verificar que la clase extienda correctamente
public class Owner extends AggregateRoot<Long> implements Entity<Long> {
    // ...
}
```

**🔧 Comandos de diagnóstico:**
```bash
# Verificar que las clases shared existan
find src -name "AggregateRoot.java"
find src -name "Entity.java"

# Compilar solo el shared primero
mvn compile -pl :shared
```

**2. Error: "Package does not exist"**

```bash
Error: java: package com.datavet.datavet.owner.domain.event does not exist
```

**🔍 Causa:** La estructura de carpetas no coincide con los packages declarados.

**✅ Solución:**
```bash
# Crear la estructura de carpetas correcta
mkdir -p src/main/java/com/datavet/datavet/owner/domain/event
mkdir -p src/main/java/com/datavet/datavet/owner/domain/model
mkdir -p src/main/java/com/datavet/datavet/owner/domain/exception

# Verificar que los packages en los archivos Java coincidan
# En OwnerCreatedEvent.java:
package com.datavet.datavet.owner.domain.event;  // ✅ Debe coincidir con la carpeta
```

**3. Error: "Lombok annotations not working"**

```bash
Error: java: cannot find symbol
  symbol:   method builder()
  location: class Owner
```

**🔍 Causa:** Lombok no está configurado correctamente en el IDE.

**✅ Solución para IntelliJ IDEA:**
```bash
# 1. Instalar plugin de Lombok
File → Settings → Plugins → Search "Lombok" → Install

# 2. Habilitar annotation processing
File → Settings → Build → Compiler → Annotation Processors
☑️ Enable annotation processing

# 3. Reimportar el proyecto Maven
View → Tool Windows → Maven → Reload All Maven Projects
```

**✅ Solución para Eclipse:**
```bash
# 1. Descargar lombok.jar desde https://projectlombok.org/download
# 2. Ejecutar: java -jar lombok.jar
# 3. Seleccionar la instalación de Eclipse
# 4. Reiniciar Eclipse
```

#### 🗄️ Errores de Base de Datos

**1. Error: "Table 'owner' doesn't exist"**

```bash
Caused by: java.sql.SQLSyntaxErrorException: Table 'datavet.owner' doesn't exist
```

**🔍 Causa:** No se ha creado la tabla en la base de datos.

**✅ Solución:**
```sql
-- Crear la tabla manualmente (para desarrollo)
CREATE TABLE owner (
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email_address VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    identification_number VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Crear índices para mejorar performance
CREATE INDEX idx_owner_email ON owner(email_address);
CREATE INDEX idx_owner_last_name ON owner(last_name);
```

**🔧 Comandos de diagnóstico:**
```bash
# Verificar conexión a la base de datos
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.org.springframework.jdbc=DEBUG"

# Ver las consultas SQL que se ejecutan
# En application.properties:
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**2. Error: "Column 'email_address' cannot be null"**

```bash
Caused by: java.sql.SQLIntegrityConstraintViolationException: Column 'email_address' cannot be null
```

**🔍 Causa:** El convertidor de Email no está funcionando correctamente.

**✅ Solución:**
```java
// Verificar que el convertidor esté registrado
@Entity
@Table(name = "owner")
public class OwnerEntity extends BaseEntity {
    
    @Convert(converter = EmailConverter.class)  // ✅ Asegurar que esté presente
    @Column(name = "email_address", nullable = false)
    private Email email;
}

// Verificar que el EmailConverter esté implementado correctamente
@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {
    
    @Override
    public String convertToDatabaseColumn(Email email) {
        return email != null ? email.getValue() : null;  // ✅ Manejar nulls
    }
    
    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData != null ? Email.of(dbData) : null;  // ✅ Manejar nulls
    }
}
```

#### 🌐 Errores de API REST

**1. Error: "404 Not Found" en endpoints que deberían existir**

```bash
GET http://localhost:8080/owners/1
Response: 404 Not Found
```

**🔍 Causa:** El controlador no está siendo detectado por Spring Boot.

**✅ Solución:**
```java
// Verificar que el controlador tenga las anotaciones correctas
@RestController  // ✅ Debe estar presente
@RequestMapping("/owners")  // ✅ Ruta base
@Validated  // ✅ Para validaciones
public class OwnerController {
    
    @GetMapping("/{id}")  // ✅ Mapeo correcto
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable Long id) {
        // Implementación
    }
}
```

**🔧 Comandos de diagnóstico:**
```bash
# Ver todos los endpoints registrados
curl http://localhost:8080/actuator/mappings | jq

# O en los logs al iniciar la aplicación, buscar:
# "Mapped \"{[/owners/{id}],methods=[GET]}\""

# Verificar que el controlador esté en el package correcto
find src -name "*Controller.java" -exec grep -l "OwnerController" {} \;
```

**2. Error: "400 Bad Request" con validaciones**

```bash
POST http://localhost:8080/owners
{
  "firstName": "",
  "lastName": "Doe",
  "email": "invalid-email"
}

Response: 400 Bad Request
{
  "message": "Validation failed",
  "errors": [...]
}
```

**🔍 Causa:** Las validaciones de Bean Validation no están configuradas correctamente.

**✅ Solución:**
```java
// En el DTO de request
public class CreateOwnerRequest {
    
    @NotBlank(message = "First name is required")  // ✅ Validación presente
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")  // ✅ Validación de formato
    private String email;
}

// En el controlador
@PostMapping
public ResponseEntity<OwnerResponse> createOwner(
    @Valid @RequestBody CreateOwnerRequest request) {  // ✅ @Valid es crucial
    // Implementación
}
```

#### 🔄 Errores de Eventos de Dominio

**1. Error: "Events not being published"**

**🔍 Síntomas:** Los eventos se crean pero no se ejecutan los handlers.

**✅ Solución:**
```java
// Verificar que el servicio publique los eventos
@Service
@Transactional
public class OwnerService implements OwnerUseCase {
    
    private final DomainEventPublisher eventPublisher;
    
    @Override
    public OwnerResponse createOwner(CreateOwnerCommand command) {
        Owner owner = Owner.create(...);
        Owner savedOwner = ownerRepository.save(owner);
        
        // ✅ CRÍTICO: Publicar eventos después de guardar
        eventPublisher.publishEvents(savedOwner.getDomainEvents());
        savedOwner.clearDomainEvents();
        
        return ownerMapper.toResponse(savedOwner);
    }
}
```

**🔧 Comandos de diagnóstico:**
```bash
# Verificar que los eventos se estén creando
# Agregar logs temporales en el modelo:
public static Owner create(...) {
    Owner owner = // ... creación
    owner.addDomainEvent(OwnerCreatedEvent.of(...));
    log.debug("Domain event added: {}", owner.getDomainEvents().size());  // Debug temporal
    return owner;
}
```

**2. Error: "Event handler not found"**

```bash
WARN: No event handler found for event: OwnerCreatedEvent
```

**🔍 Causa:** El handler no está registrado correctamente.

**✅ Solución:**
```java
// Crear el handler con las anotaciones correctas
@Component  // ✅ Debe ser un componente Spring
public class OwnerEventHandler {
    
    @EventListener  // ✅ Anotación correcta
    public void handleOwnerCreated(OwnerCreatedEvent event) {
        log.info("Owner created: {}", event.getFullName());
        // Lógica del handler
    }
}
```

#### 🧪 Errores de Testing

**1. Error: "No qualifying bean of type found" en tests**

```bash
@Autowired
private OwnerService ownerService;  // ❌ Falla en tests

org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type 'OwnerService' available
```

**🔍 Causa:** El contexto de Spring no está cargando correctamente en los tests.

**✅ Solución:**
```java
// Para tests de integración
@SpringBootTest  // ✅ Carga el contexto completo
@Transactional   // ✅ Rollback automático
class OwnerServiceIntegrationTest {
    
    @Autowired
    private OwnerService ownerService;  // ✅ Ahora funciona
}

// Para tests unitarios
@ExtendWith(MockitoExtension.class)
class OwnerServiceUnitTest {
    
    @Mock
    private OwnerRepositoryPort ownerRepository;  // ✅ Mock las dependencias
    
    @InjectMocks
    private OwnerService ownerService;  // ✅ Inyecta los mocks
}
```

**2. Error: "Tests failing with database constraints"**

```bash
Caused by: org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: 
Unique index or primary key violation: "UK_EMAIL_ADDRESS"
```

**🔍 Causa:** Los tests no están limpiando datos entre ejecuciones.

**✅ Solución:**
```java
@SpringBootTest
@Transactional  // ✅ Rollback automático
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)  // ✅ Limpieza explícita
class OwnerControllerIntegrationTest {
    
    @Test
    void shouldCreateOwnerSuccessfully() {
        // Test implementation
    }
    
    // O limpiar manualmente
    @AfterEach
    void cleanup() {
        ownerRepository.deleteAll();
    }
}
```

#### 🔧 Comandos de Diagnóstico Útiles

**Compilación y Build:**
```bash
# Compilar solo el dominio específico
mvn compile -Dinclude="**/owner/**"

# Compilar con información detallada de errores
mvn compile -X

# Limpiar y recompilar completamente
mvn clean compile

# Verificar dependencias
mvn dependency:tree | grep -i lombok
```

**Base de Datos:**
```bash
# Conectar a H2 console (desarrollo)
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# User: sa, Password: (vacío)

# Ver estructura de tablas
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC';

# Ver datos de la tabla owner
SELECT * FROM owner LIMIT 10;
```

**Logs y Debugging:**
```bash
# Habilitar logs detallados en application.properties
logging.level.com.datavet.datavet.owner=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Ver logs en tiempo real
tail -f logs/datavet.log | grep -i owner

# Filtrar logs por nivel
tail -f logs/datavet.log | grep -E "(ERROR|WARN)"
```

**Testing:**
```bash
# Ejecutar solo tests del dominio owner
mvn test -Dtest="*Owner*"

# Ejecutar tests con información detallada
mvn test -Dtest="OwnerServiceTest" -X

# Ejecutar tests de integración
mvn test -Dtest="*IntegrationTest"

# Ver cobertura de tests
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

**API Testing:**
```bash
# Probar endpoint con curl
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john@example.com"
  }'

# Probar con datos inválidos
curl -X POST http://localhost:8080/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "",
    "lastName": "Doe",
    "email": "invalid-email"
  }' \
  -v  # Verbose para ver headers y status codes
```

#### 🚀 Herramientas de Debugging Avanzado

**1. Debugging con IDE:**
```java
// Puntos de breakpoint estratégicos
@Service
public class OwnerService {
    
    public OwnerResponse createOwner(CreateOwnerCommand command) {
        // 🔴 Breakpoint aquí para verificar el comando
        Owner owner = Owner.create(...);
        
        // 🔴 Breakpoint aquí para verificar el modelo creado
        Owner savedOwner = ownerRepository.save(owner);
        
        // 🔴 Breakpoint aquí para verificar eventos
        eventPublisher.publishEvents(savedOwner.getDomainEvents());
        
        return ownerMapper.toResponse(savedOwner);
    }
}
```

**2. Profiling de Performance:**
```bash
# Usar JProfiler o similar para analizar performance
java -javaagent:jprofiler.jar -jar target/datavet-application.jar

# O usar herramientas built-in de Spring Boot
# Habilitar actuator endpoints
management.endpoints.web.exposure.include=health,metrics,httptrace

# Ver métricas
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

**3. Monitoring en Producción:**
```bash
# Logs estructurados para mejor análisis
# En logback-spring.xml:
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp/>
            <logLevel/>
            <loggerName/>
            <message/>
            <mdc/>
        </providers>
    </encoder>
</appender>
```

Con estas soluciones y herramientas de diagnóstico, deberías poder resolver la mayoría de problemas que encuentres durante el desarrollo de tu dominio Owner. Recuerda siempre verificar los logs, usar las herramientas de debugging de tu IDE, y no dudar en consultar la documentación oficial de Spring Boot y las librerías utilizadas.

---

## 9. Checklists y Verificación

*[Esta sección se completará en las siguientes tareas]*

### Checklist por Capa

*Contenido pendiente de implementación*

### Comandos de Verificación

*Contenido pendiente de implementación*

---

## Próximos Pasos

Esta guía se irá completando progresivamente. Las siguientes secciones incluirán:

1. **Ejemplos de código completos** para cada componente
2. **Instrucciones paso a paso** para la implementación
3. **Comandos específicos** para compilar y probar
4. **Checklists detallados** para verificar cada paso
5. **Soluciones a problemas comunes** que puedas encontrar

¡Empecemos a construir tu nuevo dominio!