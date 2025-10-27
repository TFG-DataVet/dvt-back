# Guía para Crear un Nuevo Dominio - Ejemplo: Dueño

## Introducción

Esta guía proporciona un paso a paso en lenguaje sencillo para crear un nuevo dominio en el proyecto DataVet, aprovechando toda la infraestructura compartida (shared) que ya existe. Usaremos como ejemplo la creación del dominio "Dueño" (Owner), que representa a los propietarios de las mascotas.

## Glosario

- **Dominio**: Una área específica del negocio (como Clínica, Dueño, Mascota)
- **Agregado**: La entidad principal de un dominio que agrupa información relacionada
- **Value Object**: Objetos que representan valores (como Email, Teléfono, Dirección)
- **Puerto (Port)**: Interfaces que definen qué puede hacer el dominio
- **Adaptador (Adapter)**: Implementaciones concretas de los puertos
- **Comando**: Objeto que contiene la información necesaria para realizar una acción
- **Evento de Dominio**: Notificación de que algo importante pasó en el dominio

## Requisitos

### Requisito 1

**Historia de Usuario:** Como desarrollador del equipo DataVet, quiero una guía clara y paso a paso para crear nuevos dominios, para que pueda implementar funcionalidades de manera consistente con la arquitectura existente.

#### Criterios de Aceptación

1. CUANDO un desarrollador siga la guía, EL Sistema_Guía DEBERÁ proporcionar instrucciones claras para cada capa arquitectónica
2. MIENTRAS el desarrollador implemente un nuevo dominio, EL Sistema_Guía DEBERÁ mostrar cómo reutilizar los componentes compartidos existentes
3. CUANDO se complete la implementación siguiendo la guía, EL Nuevo_Dominio DEBERÁ seguir los mismos patrones que el dominio Clínica existente
4. EL Sistema_Guía DEBERÁ incluir ejemplos prácticos usando el dominio "Dueño"
5. EL Sistema_Guía DEBERÁ explicar el propósito de cada archivo y carpeta creada

### Requisito 2

**Historia de Usuario:** Como desarrollador nuevo en el proyecto, quiero entender la estructura de carpetas y archivos, para que pueda navegar y organizar el código correctamente.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ explicar la estructura de carpetas por capas (domain, application, infrastructure)
2. CUANDO se cree un nuevo dominio, EL Sistema_Guía DEBERÁ mostrar dónde ubicar cada tipo de archivo
3. EL Sistema_Guía DEBERÁ explicar la diferencia entre los puertos de entrada (in) y salida (out)
4. EL Sistema_Guía DEBERÁ mostrar cómo organizar los DTOs, comandos y respuestas
5. EL Sistema_Guía DEBERÁ explicar cuándo usar los componentes compartidos vs crear nuevos

### Requisito 3

**Historia de Usuario:** Como desarrollador, quiero ejemplos concretos de código para cada componente del dominio, para que pueda implementar sin errores y siguiendo las mejores prácticas del proyecto.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ incluir ejemplos de código para el modelo de dominio con eventos
2. EL Sistema_Guía DEBERÁ mostrar cómo crear excepciones específicas del dominio
3. EL Sistema_Guía DEBERÁ incluir ejemplos de comandos de entrada con validaciones
4. EL Sistema_Guía DEBERÁ mostrar cómo implementar validadores usando el framework compartido
5. EL Sistema_Guía DEBERÁ incluir ejemplos de servicios de aplicación con manejo de eventos

### Requisito 4

**Historia de Usuario:** Como desarrollador, quiero ejemplos de la capa de infraestructura completa, para que pueda conectar mi dominio con la base de datos y APIs REST.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ mostrar cómo crear entidades JPA con convertidores
2. EL Sistema_Guía DEBERÁ incluir ejemplos de repositorios JPA con consultas personalizadas
3. EL Sistema_Guía DEBERÁ mostrar cómo implementar adaptadores de repositorio
4. EL Sistema_Guía DEBERÁ incluir ejemplos de controladores REST con DTOs
5. EL Sistema_Guía DEBERÁ mostrar cómo crear mappers entre capas

### Requisito 5

**Historia de Usuario:** Como desarrollador, quiero ejemplos de componentes auxiliares y configuración, para que mi dominio esté completamente integrado con el sistema.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ mostrar cómo crear DTOs de respuesta y request
2. EL Sistema_Guía DEBERÁ incluir ejemplos de mappers de aplicación
3. EL Sistema_Guía DEBERÁ mostrar cómo reutilizar value objects compartidos
4. EL Sistema_Guía DEBERÁ incluir ejemplos de configuración de Spring
5. EL Sistema_Guía DEBERÁ mostrar cómo integrar con el publicador de eventos

### Requisito 6

**Historia de Usuario:** Como desarrollador, quiero una lista de verificación completa y comandos de prueba, para que pueda confirmar que he implementado todo correctamente antes de continuar.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ incluir una checklist detallada para cada capa arquitectónica
2. EL Sistema_Guía DEBERÁ mostrar cómo probar cada componente individualmente
3. EL Sistema_Guía DEBERÁ incluir comandos Maven para compilar y validar
4. EL Sistema_Guía DEBERÁ mostrar cómo probar las APIs con ejemplos de requests
5. EL Sistema_Guía DEBERÁ incluir una sección de troubleshooting con errores comunes

### Requisito 7

**Historia de Usuario:** Como desarrollador, quiero entender las mejores prácticas y patrones específicos del proyecto, para que mi código sea mantenible y consistente.

#### Criterios de Aceptación

1. EL Sistema_Guía DEBERÁ explicar cuándo usar agregados vs entidades simples
2. EL Sistema_Guía DEBERÁ mostrar cómo manejar relaciones entre dominios
3. EL Sistema_Guía DEBERÁ incluir patrones para consultas complejas
4. EL Sistema_Guía DEBERÁ mostrar cómo implementar soft deletes si es necesario
5. EL Sistema_Guía DEBERÁ explicar las convenciones de naming del proyecto