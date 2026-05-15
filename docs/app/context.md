# DATAVET TECHNICAL CONTEXT — Complete Architecture Reference

## OVERVIEW

**Project:** DataVet - Backend Spring Boot para gestión de clínicas veterinarias
**Architecture:** Domain-Driven Design (DDD) with Hexagonal Architecture per domain
**Database:** MongoDB (no SQL)
**Java Version:** 24
**Spring Boot:** 3.5.6
**Framework:** Spring Security (JWT + Cookie-based authentication)

### Dominio Definitions
El proyecto está organizado en 6 dominios independientes:
- **auth** — Autenticación, gestión de usuarios, JWT, onboarding
- **clinic** — Clínicas veterinarias, datos corporativos, horarios
- **employee** — Empleados, roles, salarios, políticas de vacaciones
- **owner** — Propietarios de mascotas
- **pet** — Mascotas, registros médicos, historiales clínicos
- **appointment** — Citas, agendas, estados de reserva
- **product** — Catálogo de productos, inventario, gestión de stock por categoría
- **shared** — Componentes comunes: excepciones, value objects, configuración

---

## DEPLOYMENT & CONFIGURATION

### Base de Datos
```
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=datavet_db
spring.data.mongodb.auto-index-creation=true
```

### Variables de Entorno Requeridas
```
JWT_SECRET=<LONG_SECURE_KEY>        # Usada para firmar tokens JWT
EMAIL_SECRET=<GMAIL_ACCOUNT>        # Email para enviar verificaciones
EMAIL_PASSWORD_SECRET=<GMAIL_APP_PASSWORD>  # Contraseña de aplicación Gmail
```

### Dependencias Clave
- **spring-boot-starter-security** — Autenticación y autorización
- **jjwt (0.12.6)** — Generación y validación de JWT
- **spring-boot-starter-data-mongodb** — Persistencia
- **spring-boot-starter-mail + thymeleaf** — Envío de emails con templates
- **springdoc-openapi (2.8.5)** — Swagger UI y documentación OpenAPI
- **lombok** — Generación de boilerplate
- **dotenv-java (3.0.0)** — Carga de variables de entorno desde .env

### Puertos
- **8080** — API REST (default)
- **27017** — MongoDB

---

## ARCHITECTURE LAYERS

### 1. DOMAIN LAYER (com.datavet.{domain}.domain)
**Responsabilidad:** Lógica de negocio pura, sin dependencias de frameworks.

**Contenido:**
- **model/** — Entidades agregadas (AggregateRoot), Value Objects
- **exception/** — Excepciones específicas del dominio
- **event/** — Eventos de dominio emitidos por cambios importantes
- **valueobject/** — Objetos de valor immutables (Email, Phone, Address, etc.)
- **service/** — Servicios de dominio complejos (opcionales)

---

### 2. APPLICATION LAYER (com.datavet.{domain}.application)

**Responsabilidad:** Orquestar la lógica de dominio, coordinar entre capas.

**Contenido:**
- **port/in/** — Interfaces de casos de uso (inbound ports)
- **port/out/** — Interfaces de repositorios (outbound ports)
- **port/in/command/** — Objetos request que encapsulan parámetros
- **service/** — Implementa los casos de uso
- **dto/** — Respuestas HTTP standarizadas
- **mapper/** — Conversión entre domain model y DTO

---

### 3. INFRASTRUCTURE LAYER (com.datavet.{domain}.infrastructure)

**Responsabilidad:** Implementaciones técnicas, frameworks, bases de datos.

- **adapter/input/** — Controllers REST + DTOs request del cliente
- **adapter/output/** — Implementaciones de repositorios
- **persistence/** — Documentos MongoDB + Interfaces Spring Data
- **config/** — Configuración específica del dominio

---

## SHARED COMPONENTS

### Value Objects (shared/domain/valueobject/)

| Clase | Descripción | Validación |
|-------|-------------|-----------|
| `Email` | Email con validación | Formato RFC completo |
| `Phone` | Teléfono | 9-15 dígitos |
| `Address` | Dirección (street, city, postalCode) | Valor object |
| `DocumentId` | ID documento (tipo + número) | Custom por tipo |

### Formato de Errores API
```json
{
  "timestamp": "2026-05-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "details": [
    {
      "field": "email",
      "message": "debe ser un email válido",
      "rejectedValue": "invalid"
    }
  ],
  "path": "/auth/register"
}
```

---

## AUTHENTICATION & JWT

### Sistema de Tokens

#### Tipos de Tokens
```
// Token de acceso completo (1 hora)
generateAccessToken(userId, employeeId, clinicId, email, role)
  → Claims: subject=userId, employeeId, clinicId, email, role, scope=FULL_ACCESS

// Token temporal onboarding (1 hora, scope ONBOARDING_ONLY)
generateOnboardingToken(userId, clinicId, email, role)
  → Solo valido para endpoint PATCH /clinic/{id}/complete-setup
```

#### Ciclo de Vida Completo

```
Paso 1: POST /auth/register
  → Crea User + Clinic en estado PENDING_SETUP
  → Envía email con verification token
  → Respuesta: { userId, clinicId, email, message }

Paso 2: GET /auth/verify-email?token=XXX
  → Valida token, transiciona User a PENDING_CLINIC_SETUP
  → Genera JWT temporal (ONBOARDING_ONLY, 1h)
  → Respuesta: { accessToken (temporal), user, nextStep: "COMPLETE_SETUP" }

Paso 3: PATCH /clinic/{clinicId}/complete-setup
  → Header: Authorization: Bearer <token temporal>
  → Completa Clinic, crea Employee de CLINIC_OWNER, activa User
  → Genera JWT definitivo (FULL_ACCESS)
  → Respuesta: { accessToken (definitivo), refreshToken, user, nextStep: null }

Paso 4: Uso normal
  → Cookie HttpOnly: accessToken (1 hora)
  → Cookie HttpOnly: refreshToken (30 días, solo path /auth/refresh)
```

### Almacenamiento de Tokens
- **Access Token:** HttpOnly Cookie automática en login/refresh
  - Path: `/`, MaxAge: 3600s, Secure: false en localhost
- **Refresh Token:** HttpOnly Cookie
  - Path: `/auth/refresh`, MaxAge: 30 días
- **Tokens de Onboarding:** En response body (cliente los guarda en memoria)

### Extracción del Token (JwtAuthenticationFilter)
```
1. Cookie "accessToken" (prioridad 1)
2. Header Authorization: Bearer XXX (fallback)
```

### Roles
```java
enum UserRole {
  SUPER_ADMIN,          // Acceso global, sin clinicId
  CLINIC_OWNER,         // Dueño fundador, acceso total a su clínica
  CLINIC_ADMIN,         // Admin, RRHH y finanzas
  CLINIC_VETERINARIAN,  // Veterinario, operaciones clínicas
  CLINIC_STAFF          // Recepción, baño, limpieza, etc.
}

enum UserStatus {
  PENDING_EMAIL_VERIFICATION,  // Registrado, verifica email
  PENDING_CLINIC_SETUP,        // Email verificado, completa datos clínica
  ACTIVE,                      // Operativo
  INACTIVE                     // Desactivado (soft-delete)
}
```

### CORS Configuration
```
AllowedOrigins: http://localhost:5258
AllowedMethods: GET, POST, PUT, PATCH, DELETE, OPTIONS
AllowedHeaders: Authorization, Content-Type, Accept, X-Requested-With
AllowCredentials: true (CRÍTICO para cookies)
```

---

## ENDPOINTS POR DOMINIO

### AUTH DOMAIN

| Método | Ruta | Auth | Descripción | Response |
|--------|------|------|-------------|----------|
| POST | /auth/register | No | Paso 1: Registra clinic owner | RegisterResponse |
| GET | /auth/verify-email?token=X | No | Paso 2: Verifica email | TokenResponse temporal |
| POST | /auth/resend-verification | No | Reenvía email verificación | 204 |
| POST | /auth/login | No | Login | TokenResponse + Cookies |
| POST | /auth/refresh | No | Rota tokens | TokenResponse + Cookies |
| POST | /auth/logout | Sí | Logout | 204 |
| PATCH | /auth/password | Sí | Cambiar contraseña | 204 |
| POST | /auth/activate-account | No | Empleado activa cuenta | 204 |
| POST | /auth/forgot-password | No | Solicita reset | 204 |
| POST | /auth/reset-password | No | Reset con token | 204 |
| GET | /auth/me | Sí | Usuario actual | TokenResponse |

**Request DTOs:**
```json
// POST /auth/register
{
  "clinicName": "Veterinaria XYZ",
  "firstName": "Ana",
  "lastName": "García",
  "email": "ana@example.com",
  "phone": "612345678",
  "password": "Segura1234"
}

// POST /auth/login
{
  "email": "ana@example.com",
  "password": "Segura1234"
}

// PATCH /auth/password
{
  "currentPassword": "OldPass123",
  "newPassword": "NewPass456"
}

// POST /auth/activate-account (empleado nuevo)
{
  "token": "abc123...",
  "password": "MiPassword1"
}
```

---

### CLINIC DOMAIN

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| PATCH | /clinic/{id}/complete-setup | JWT temporal | CLINIC_OWNER | Completa onboarding |
| POST | /clinic | Sí | SUPER_ADMIN | Crea clínica completa |
| GET | /clinic/{id} | Sí | Any | Obtiene clínica |
| GET | /clinic | Sí | Any | Lista clínicas |
| PUT | /clinic/{id} | Sí | CLINIC_OWNER, SUPER_ADMIN | Actualiza clínica |
| DELETE | /clinic/{id} | Sí | CLINIC_OWNER, SUPER_ADMIN | Desactiva clínica |

**PATCH /clinic/{id}/complete-setup Request:**
```json
{
  "legalName": "Veterinaria XYZ SL",
  "legalNumber": "B12345678",
  "legalType": "SOCIEDAD_LIMITADA",
  "address": "Calle Principal 123",
  "city": "Madrid",
  "codePostal": "28001",
  "phone": "912345678",
  "email": "vet@example.com",
  "logoUrl": "https://cdn.example.com/logo.png",
  "scheduleOpenDays": "lunes,martes,miércoles,jueves,viernes",
  "scheduleOpenTime": "09:00",
  "scheduleCloseTime": "18:00",
  "scheduleNotes": "Abierto sábados por emergencias",
  "ownerDocumentType": "DNI",
  "ownerDocumentNumber": "12345678A",
  "ownerAddress": "Calle Residencial 2",
  "ownerCity": "Madrid",
  "ownerPostalCode": "28002",
  "ownerAvatarUrl": "https://cdn.example.com/avatar.png"
}
```

**ClinicResponse:**
```json
{
  "clinicId": "uuid",
  "clinicName": "Veterinaria XYZ",
  "legalName": "Veterinaria XYZ SL",
  "legalNumber": "B12345678",
  "legalType": "SOCIEDAD_LIMITADA",
  "address": {
    "street": "Calle Principal 123",
    "city": "Madrid",
    "postalCode": "28001",
    "fullAddress": "Calle Principal 123, Madrid 28001"
  },
  "phone": "912345678",
  "email": "vet@example.com",
  "logoUrl": "https://...",
  "schedule": {
    "openDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "openTime": "09:00",
    "closeTime": "18:00",
    "notes": "..."
  },
  "status": "ACTIVE"
}
```

**Enums:**
```java
enum ClinicStatus { ACTIVE, PENDING_SETUP, INACTIVE }
enum LegalType { AUTONOMO, SOCIEDAD_LIMITADA, SOCIEDAD_ANONIMA, SOCIEDAD_CIVIL, COMUNIDAD_DE_BIENES, OTHER }
```

---

### EMPLOYEE DOMAIN

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| POST | /employees | Sí | CLINIC_OWNER, CLINIC_ADMIN | Crea empleado |
| GET | /employees/{id} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Obtiene empleado |
| GET | /employees | Sí | CLINIC_OWNER, CLINIC_ADMIN | Lista empleados de la clínica |
| PUT | /employees/{id} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Actualiza empleado |
| DELETE | /employees/{id} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Desactiva empleado |
| PATCH | /employees/{id}/salary | Sí | CLINIC_OWNER, CLINIC_ADMIN | Actualiza salario |
| PATCH | /employees/{id}/vacation-policy | Sí | CLINIC_OWNER, CLINIC_ADMIN | Actualiza vacaciones |
| PATCH | /employees/{id}/work-schedule | Sí | CLINIC_OWNER, CLINIC_ADMIN | Actualiza horario |

**CreateEmployeeRequest:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "documentType": "DNI",
  "documentNumber": "12345678A",
  "phone": "634567890",
  "email": "juan@clinica.com",
  "address": "Calle Trabajo 1",
  "city": "Madrid",
  "postalCode": "28001",
  "avatarUrl": "https://...",
  "speciality": "Cirugía",
  "licenseNumber": "COL12345",
  "hireDate": "2024-01-01",
  "role": "CLINIC_VETERINARIAN"
}
```

**EmployeeResponse:**
```json
{
  "employeeId": "uuid",
  "userId": "uuid",
  "clinicId": "uuid",
  "firstName": "Juan",
  "lastName": "Pérez",
  "fullName": "Juan Pérez",
  "documentNumber": { "type": "DNI", "number": "12345678A" },
  "phone": "634567890",
  "address": {
    "street": "Calle Trabajo 1",
    "city": "Madrid",
    "postalCode": "28001",
    "fullAddress": "Calle Trabajo 1, Madrid 28001"
  },
  "avatarUrl": "https://...",
  "speciality": "Cirugía",
  "licenseNumber": "COL12345",
  "hireDate": "2024-01-01",
  "active": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-02T11:00:00",
  "salary": {
    "amount": 2500,
    "currency": "EUR",
    "paymentsPerYear": 14,
    "effectiveFrom": "2024-01-01"
  },
  "vacationPolicy": { "annualDays": 30, "effectiveFrom": "2024-01-01" },
  "workSchedule": {
    "weeklyHours": 40,
    "workDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "entryTime": "09:00",
    "exitTime": "18:00",
    "notes": ""
  }
}
```

---

### OWNER DOMAIN

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | /owner | Sí | Crea propietario |
| GET | /owner/{id} | Sí | Obtiene propietario |
| GET | /owner | Sí | Lista propietarios de la clínica |
| PUT | /owner/{id} | Sí | Actualiza propietario |
| DELETE | /owner/{id} | Sí | Elimina propietario |

**CreateOwnerRequest:**
```json
{
  "name": "Pedro",
  "lastName": "García",
  "documentId": "DNI",
  "documentNumber": "87654321B",
  "phone": "634567890",
  "email": "pedro@example.com",
  "address": "Calle Residencial 2",
  "city": "Barcelona",
  "postalCode": "08001",
  "url": "https://...",
  "acceptTermsAndCond": true
}
```

**OwnerResponse:**
```json
{
  "ownerId": "uuid",
  "firstName": "Pedro",
  "lastName": "García",
  "dni": "87654321B",
  "phone": "634567890",
  "email": "pedro@example.com",
  "address": {
    "street": "Calle Residencial 2",
    "city": "Barcelona",
    "postalCode": "08001",
    "fullAddress": "Calle Residencial 2, Barcelona 08001"
  }
}
```

---

### PET DOMAIN

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | /pet | Sí | Crea mascota |
| GET | /pet/{id} | Sí | Obtiene mascota |
| GET | /pet/clinic | Sí | Lista mascotas de la clínica |
| GET | /pet/owner/{ownerId} | Sí | Lista mascotas del propietario |
| PUT | /pet/{id} | Sí | Actualiza mascota |
| PATCH | /pet/{id}/deactivate | Sí | Desactiva mascota |
| PATCH | /pet/{id}/activate | Sí | Reactiva mascota |
| PATCH | /pet/{id}/correct-breed | Sí | Corrige raza |
| PATCH | /pet/{id}/correct-birthdate | Sí | Corrige fecha nacimiento |
| PATCH | /pet/{id}/correct-sex | Sí | Corrige sexo |
| PATCH | /pet/{id}/owner | Sí | Actualiza datos propietario embebidos |

**CreatePetRequest:**
```json
{
  "name": "Fluffy",
  "species": "Gato",
  "breed": "Persa",
  "sex": "FEMALE",
  "dateOfBirth": "2020-03-15",
  "chipNumber": "985112345678901",
  "avatarUrl": "https://...",
  "owner": {
    "ownerId": "uuid",
    "ownerName": "Pedro",
    "ownerLastName": "García",
    "ownerPhone": "634567890"
  }
}
```

**PetResponse:**
```json
{
  "id": "uuid",
  "clinicId": "uuid",
  "name": "Fluffy",
  "species": "Gato",
  "breed": "Persa",
  "sex": "FEMALE",
  "dateOfBirth": "2020-03-15",
  "ageInYears": 6,
  "chipNumber": "985112345678901",
  "avatarUrl": "https://...",
  "owner": {
    "ownerId": "uuid",
    "name": "Pedro",
    "lastName": "García",
    "phone": "634567890",
    "fullName": "Pedro García"
  },
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-02T11:00:00"
}
```

**Enums:**
```java
enum Sex { MALE, FEMALE, UNKNOWN }
```

---

### APPOINTMENT DOMAIN

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| POST | /appointments | Sí | CLINIC_VETERINARIAN, CLINIC_ADMIN, CLINIC_OWNER | Crea cita |
| GET | /appointments/{id} | Sí | Any | Obtiene cita |
| GET | /appointments | Sí | Any | Lista citas de la clínica |
| PATCH | /appointments/{id}/status | Sí | CLINIC_VETERINARIAN, CLINIC_ADMIN | Avanza estado |
| DELETE | /appointments/{id} | Sí | Any | Cancela cita |

**Restricciones:** CLINIC_STAFF con speciality CLEANING o MAINTENANCE están bloqueados de la agenda.

**CreateAppointmentRequest:**
```json
{
  "emergency": false,
  "type": "RUTINA",
  "scheduledAt": "2024-05-15T10:30:00",
  "ownerId": "uuid",
  "ownerName": "Pedro",
  "ownerEmail": "pedro@example.com",
  "ownerPhone": "634567890",
  "petId": "uuid",
  "petName": "Fluffy",
  "petSpecies": "Gato",
  "creationEmployeeId": "uuid",
  "medicalEmployeeId": "uuid",
  "notes": "Revisión general anual",
  "productIds": [],
  "source": "PANEL"
}
```

**AppointmentResponse:**
```json
{
  "id": "uuid",
  "clinicId": "uuid",
  "emergency": false,
  "type": "RUTINA",
  "status": "RESERVADA",
  "scheduledAt": "2024-05-15T10:30:00",
  "owner": { "ownerId": "uuid", "name": "Pedro", "email": "pedro@example.com", "phone": "634567890" },
  "pet": { "petId": "uuid", "name": "Fluffy", "species": "Gato" },
  "creationEmployeeId": "uuid",
  "medicalEmployeeId": "uuid",
  "notes": "Revisión general anual",
  "productIds": [],
  "source": "PANEL",
  "createdAt": "2024-05-14T09:00:00",
  "updatedAt": "2024-05-15T10:30:00"
}
```

**Enums:**
```java
enum AppointmentStatus { RESERVADA, CLIENTE_LLEGADO, PROXIMO_A_ATENDER, EN_CONSULTA, FINALIZADA, REQUIERE_SEGUIMIENTO, CANCELADA }
enum AppointmentType { RUTINA, EXAMEN, VACUNAS, EMERGENCIA, BAÑO, CIRUGIA, OTRO }
enum AppointmentSource { PANEL, PHONE, EMAIL, WALK_IN }
```

**Transiciones de estado válidas:**
```
RESERVADA → CLIENTE_LLEGADO → PROXIMO_A_ATENDER → EN_CONSULTA → FINALIZADA
                                                              → REQUIERE_SEGUIMIENTO
Cualquier estado → CANCELADA
```

---

### PRODUCT DOMAIN

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| POST | /product/clinic/{clinicId} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Crea producto |
| GET | /product/{id} | Sí | Any | Obtiene producto por id |
| GET | /product/clinic/{clinicId}?category=X | Sí | Any | Lista productos de la clínica (filtro opcional por categoría) |
| PUT | /product/{id} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Actualiza producto |
| DELETE | /product/{id} | Sí | CLINIC_OWNER, CLINIC_ADMIN | Desactiva producto (soft-delete) |

**ProductResponse:**
```json
{
  "productId": "uuid",
  "clinicId": "uuid",
  "name": "Amoxicilina 500mg",
  "description": "Antibiótico de amplio espectro",
  "category": "MEDICATION",
  "sku": "SKU-001",
  "barcode": "BAR-001",
  "price": 29.99,
  "taxRate": 0.21,
  "stock": 100,
  "minStock": 10,
  "isActive": true,
  "createdAt": "2026-05-12T10:00:00",
  "updatedAt": "2026-05-12T10:00:00",
  "details": { "type": "MEDICATION", "activeIngredient": "Amoxicilina", "..." }
}
```

**Enums:**
```java
enum ProductCategory {
  MEDICATION, VACCINE, FOOD, SUPPLEMENT, HYGIENE, GROOMING_SERVICE,
  ACCESSORY, TOY, MEDICAL_SUPPLY, ANTIPARASITIC, DIAGNOSTIC, PROSTHESIS_IMPLANT
}
```

**Detalles por categoría (`details`):**
- `MEDICATION` / `VACCINE` / `ANTIPARASITIC`: campos farmacológicos (activeIngredient, dosageForm, concentration, manufacturer, species…)
- `FOOD` / `SUPPLEMENT`: campos nutricionales (brand, weightGrams, speciesSuitability, ingredients…)
- `HYGIENE` / `GROOMING_SERVICE`: campos de higiene (brand, scentType, volumeMl…)
- `ACCESSORY` / `TOY`: campos físicos (brand, sizes, colors, material…)
- `MEDICAL_SUPPLY` / `DIAGNOSTIC`: campos clínicos (manufacturer, referenceCode, isSterile, batchNumber…)
- `PROSTHESIS_IMPLANT`: campos quirúrgicos (implantType, requiresSurgery, isoStandard…)

El campo `details` incluye siempre `"type": "<CATEGORY>"` para deserialización polimórfica.

---

## EXCEPCIONES & MAPEO HTTP

| Excepción | HTTP |
|-----------|------|
| EntityNotFoundException | 404 |
| EntityAlreadyExistsException | 409 |
| BusinessRuleException | 400 |
| Validation exceptions | 400 |
| InvalidCredentialsException | 401 |
| EmailTokenExpiredException | 401 |
| AccessDeniedException | 403 |
| Exception genérica | 500 |

---

## STACK COMPLETO

- **Language:** Java 24
- **Framework:** Spring Boot 3.5.6
- **Security:** JWT (JJWT 0.12.6) + Spring Security
- **DB:** MongoDB (Spring Data)
- **Mail:** Spring Mail + Thymeleaf templates
- **API Docs:** SpringDoc OpenAPI → http://localhost:8080/swagger-ui.html
- **Build:** Maven

### Comandos Maven
```bash
mvn clean install           # Build completo
mvn spring-boot:run         # Ejecutar localmente
mvn test                    # Tests
mvn clean package -DskipTests  # JAR sin tests
```

### CORS permitido
- **Frontend:** http://localhost:5258
- **Credentials:** true (necesario para cookies)
