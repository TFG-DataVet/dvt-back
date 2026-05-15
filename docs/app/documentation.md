# DataVet API — Guía de integración para desarrolladores frontend

**Versión:** 1.0 | **Fecha:** Mayo 2026 | **Proyecto:** TFG — DataVet Backend

---

## Índice

1. [Introducción](#1-introducción)
2. [Requisitos y puesta en marcha](#2-requisitos-y-puesta-en-marcha)
3. [Autenticación y seguridad](#3-autenticación-y-seguridad)
4. [Flujo de onboarding completo](#4-flujo-de-onboarding-completo)
5. [Roles y permisos](#5-roles-y-permisos)
6. [Referencia de endpoints](#6-referencia-de-endpoints)
7. [Formato de errores](#7-formato-de-errores)
8. [Exploración interactiva con Swagger](#8-exploración-interactiva-con-swagger)

---

## 1. Introducción

DataVet es un backend REST desarrollado con Spring Boot para la gestión integral de clínicas veterinarias. Permite administrar clínicas, empleados, propietarios de mascotas, mascotas, citas y catálogo de productos.

Esta guía cubre exclusivamente lo que un desarrollador frontend necesita para consumir la API: cómo arrancarla, cómo autenticarse y una referencia completa de todos los endpoints disponibles con sus contratos de request y response.

**URL base:** `http://localhost:8080`

**Documentación interactiva (Swagger):** `http://localhost:8080/swagger-ui.html`

---

## 2. Requisitos y puesta en marcha

### 2.1 Requisitos previos

| Requisito | Versión mínima |
|-----------|----------------|
| Java (JDK) | 24 |
| Maven | 3.9+ |
| MongoDB | 7.0+ (en ejecución local) |
| Cuenta Gmail | Para envío de emails de verificación |

MongoDB debe estar corriendo en `localhost:27017` antes de arrancar el backend. La base de datos `datavet_db` se crea automáticamente en el primer arranque.

### 2.2 Variables de entorno

El proyecto usa un archivo `.env` en la raíz del proyecto. Crea ese archivo con el siguiente contenido:

```env
JWT_SECRET=una_clave_secreta_muy_larga_y_segura_de_al_menos_32_caracteres
EMAIL_SECRET=tucuenta@gmail.com
EMAIL_PASSWORD_SECRET=tu_contrasena_de_aplicacion_gmail
```

> **Nota:** `EMAIL_PASSWORD_SECRET` debe ser una contraseña de aplicación de Google (no tu contraseña de Gmail). Se genera en la configuración de seguridad de tu cuenta de Google en "Contraseñas de aplicación".

### 2.3 Arrancar el backend

```bash
# Clonar el repositorio e instalar dependencias
mvn clean install

# Arrancar en modo desarrollo
mvn spring-boot:run
```

El servidor queda disponible en `http://localhost:8080`. La primera vez que se arranca crea los índices de MongoDB automáticamente.

### 2.4 Configuración CORS

El backend está configurado para aceptar peticiones del frontend en `http://localhost:5258`. Si tu frontend corre en otro puerto, deberás actualizar la configuración CORS en el backend.

La clave importante es que `credentials: true` está activado, lo que significa que **el frontend debe enviar las cookies en cada petición** (`credentials: 'include'` en fetch, o `withCredentials: true` en axios).

---

## 3. Autenticación y seguridad

### 3.1 Mecanismo de autenticación

DataVet usa **JSON Web Tokens (JWT)**. Existen dos formas de enviar el token en cada petición, por orden de prioridad:

1. **Cookie HttpOnly** (recomendado): el backend la establece automáticamente tras el login. No requiere ninguna acción adicional del frontend más que incluir `credentials: 'include'`.
2. **Cabecera Authorization** (alternativa): útil para clientes que no gestionan cookies, como apps móviles o herramientas como Postman.

```javascript
// Opción A — Cookie automática (recomendado para web)
const response = await fetch('http://localhost:8080/employees', {
  method: 'GET',
  credentials: 'include', // <-- esto envía la cookie automáticamente
});

// Opción B — Cabecera Authorization manual
const token = 'eyJhbGciOi...'; // guardado en memoria o sessionStorage
const response = await fetch('http://localhost:8080/employees', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
  },
});
```

> **Seguridad:** Los tokens en cookie son `HttpOnly`, por lo que JavaScript no puede leerlos directamente. Esto los protege de ataques XSS. Nunca guardes el token en `localStorage`.

### 3.2 Ciclo de vida de los tokens

| Token | Duración | Almacenamiento | Alcance |
|-------|----------|----------------|---------|
| Access Token | 1 hora | Cookie HttpOnly (`accessToken`) | Todas las rutas autenticadas |
| Refresh Token | 30 días | Cookie HttpOnly (`refreshToken`) | Solo `POST /auth/refresh` |
| Token de Onboarding | 1 hora | Body de la respuesta (guardar en memoria) | Solo `PATCH /clinic/{id}/complete-setup` |

Cuando el access token caduque, llama a `POST /auth/refresh` para obtener nuevos tokens sin que el usuario tenga que volver a iniciar sesión.

```javascript
// Renovar tokens con el refresh token (la cookie se envía automáticamente)
const response = await fetch('http://localhost:8080/auth/refresh', {
  method: 'POST',
  credentials: 'include',
});
// El backend renueva las cookies automáticamente si el refresh token es válido
```

---

## 4. Flujo de onboarding completo

El registro de una nueva clínica veterinaria requiere tres pasos. Este flujo solo lo realiza el **fundador de la clínica** (rol `CLINIC_OWNER`).

```
Paso 1: Registro       →  Paso 2: Verificación email  →  Paso 3: Completar datos clínica
POST /auth/register       GET /auth/verify-email           PATCH /clinic/{id}/complete-setup
       ↓                          ↓                                  ↓
  Email de verificación    Token temporal (1h)             Token definitivo (cookies)
  enviado al correo        en el body de la respuesta      Acceso completo a la API
```

### Paso 1 — Registrar la clínica

```javascript
const response = await fetch('http://localhost:8080/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    clinicName: 'Veterinaria XYZ',
    firstName: 'Ana',
    lastName: 'García',
    email: 'ana@example.com',
    phone: '612345678',
    password: 'Segura1234',
  }),
});

// Respuesta exitosa: 201 Created
// {
//   "userId": "uuid",
//   "clinicId": "uuid",
//   "email": "ana@example.com",
//   "message": "Revisa tu correo para verificar la cuenta"
// }
```

Tras este paso, el usuario recibe un correo electrónico con un enlace de verificación. Guarda el `clinicId` de la respuesta, lo necesitarás en el Paso 3.

### Paso 2 — Verificar el email

El enlace del correo redirige al frontend con el token como parámetro de query (`?token=XXX`). El frontend debe capturar ese token y llamar al backend:

```javascript
const urlParams = new URLSearchParams(window.location.search);
const emailToken = urlParams.get('token');

const response = await fetch(
  `http://localhost:8080/auth/verify-email?token=${emailToken}`,
  { method: 'GET' }
);

const data = await response.json();

// Respuesta exitosa: 200 OK
// {
//   "accessToken": "eyJhbGci...",  <-- TOKEN TEMPORAL, guárdalo en memoria
//   "user": { "userId": "uuid", "email": "ana@example.com", "role": "CLINIC_OWNER" },
//   "nextStep": "COMPLETE_SETUP"
// }

// Guarda el token temporal en memoria (NO en localStorage)
const onboardingToken = data.accessToken;
const clinicId = data.user.clinicId; // o el que guardaste en el Paso 1
```

### Paso 3 — Completar los datos de la clínica

Con el token temporal del Paso 2, el frontend puede ahora enviar los datos corporativos de la clínica. Este es el único endpoint que **requiere el token en la cabecera Authorization** (porque aún no hay cookies definitivas):

```javascript
const response = await fetch(
  `http://localhost:8080/clinic/${clinicId}/complete-setup`,
  {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${onboardingToken}`, // token del Paso 2
    },
    body: JSON.stringify({
      legalName: 'Veterinaria XYZ SL',
      legalNumber: 'B12345678',
      legalType: 'SOCIEDAD_LIMITADA',
      address: 'Calle Principal 123',
      city: 'Madrid',
      codePostal: '28001',
      phone: '912345678',
      email: 'vet@example.com',
      logoUrl: 'https://cdn.example.com/logo.png',
      scheduleOpenDays: 'lunes,martes,miércoles,jueves,viernes',
      scheduleOpenTime: '09:00',
      scheduleCloseTime: '18:00',
      scheduleNotes: 'Abierto sábados por emergencias',
      ownerDocumentType: 'DNI',
      ownerDocumentNumber: '12345678A',
      ownerAddress: 'Calle Residencial 2',
      ownerCity: 'Madrid',
      ownerPostalCode: '28002',
      ownerAvatarUrl: 'https://cdn.example.com/avatar.png',
    }),
  }
);

// Respuesta exitosa: 200 OK
// {
//   "accessToken": "eyJhbGci...",  <-- TOKEN DEFINITIVO (también en cookie)
//   "refreshToken": "eyJhbGci...", <-- (también en cookie)
//   "user": { ... },
//   "nextStep": null               <-- onboarding completado
// }
```

Tras este paso, el backend establece las cookies de sesión definitivas. A partir de aquí, el frontend puede operar con normalidad usando `credentials: 'include'`.

---

## 5. Roles y permisos

### 5.1 Roles disponibles

| Rol | Descripción |
|-----|-------------|
| `SUPER_ADMIN` | Administrador global del sistema. Acceso a todas las clínicas. |
| `CLINIC_OWNER` | Fundador de la clínica. Acceso total a su clínica. |
| `CLINIC_ADMIN` | Administrador de RRHH y finanzas. |
| `CLINIC_VETERINARIAN` | Veterinario. Gestiona citas y operaciones clínicas. |
| `CLINIC_STAFF` | Personal de recepción, baño, limpieza, etc. |

### 5.2 Permisos por área funcional

| Recurso | SUPER_ADMIN | CLINIC_OWNER | CLINIC_ADMIN | CLINIC_VET | CLINIC_STAFF |
|---------|:-----------:|:------------:|:------------:|:----------:|:------------:|
| Clínicas (lectura) | Todas | La suya | La suya | La suya | La suya |
| Clínicas (edición) | Todas | La suya | — | — | — |
| Empleados (CRUD) | — | Si | Si | — | — |
| Salario / Vacaciones | — | Si | Si | — | — |
| Propietarios (CRUD) | — | Si | Si | Si | Si |
| Mascotas (CRUD) | — | Si | Si | Si | Si |
| Citas (crear) | — | Si | Si | Si | — |
| Citas (avanzar estado) | — | Si | Si | Si | — |
| Citas (cancelar) | — | Si | Si | Si | Si |

> **Restricción especial:** Los empleados con rol `CLINIC_STAFF` y especialidad `CLEANING` o `MAINTENANCE` no pueden acceder a la agenda de citas.

### 5.3 Activación de cuenta para empleados nuevos

Cuando un administrador crea un empleado, este recibe un correo con un enlace de activación. El empleado debe establecer su contraseña mediante:

```javascript
await fetch('http://localhost:8080/auth/activate-account', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    token: 'token_del_enlace_del_correo',
    password: 'MiPassword1',
  }),
});
// Respuesta: 204 No Content
```

---

## 6. Referencia de endpoints

Todos los endpoints autenticados requieren la cookie `accessToken` (enviada automáticamente con `credentials: 'include'`) o la cabecera `Authorization: Bearer <token>`.

### 6.1 Autenticación (`/auth`)

| Método | Ruta | Auth | Descripción | Response |
|--------|------|:----:|-------------|---------|
| POST | `/auth/register` | No | Registra clínica y owner | 201 + `{ userId, clinicId, email, message }` |
| GET | `/auth/verify-email?token=X` | No | Verifica email | 200 + token temporal |
| POST | `/auth/resend-verification` | No | Reenvía email de verificación | 204 |
| POST | `/auth/login` | No | Inicia sesión | 200 + cookies + token |
| POST | `/auth/refresh` | No (cookie) | Rota tokens | 200 + nuevas cookies |
| POST | `/auth/logout` | Si | Cierra sesión, limpia cookies | 204 |
| PATCH | `/auth/password` | Si | Cambia contraseña | 204 |
| POST | `/auth/activate-account` | No | Empleado activa su cuenta | 204 |
| POST | `/auth/forgot-password` | No | Solicita enlace de reset | 204 |
| POST | `/auth/reset-password` | No | Establece nueva contraseña con token | 204 |
| GET | `/auth/me` | Si | Devuelve el usuario autenticado | 200 + datos de usuario |

**Ejemplo — Login:**

```javascript
// Request
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include', // necesario para recibir las cookies
  body: JSON.stringify({
    email: 'ana@example.com',
    password: 'Segura1234',
  }),
});

// Response: 200 OK + cookies establecidas automáticamente
// {
//   "accessToken": "eyJhbGci...",
//   "user": {
//     "userId": "uuid",
//     "email": "ana@example.com",
//     "role": "CLINIC_OWNER",
//     "clinicId": "uuid"
//   }
// }
```

**Ejemplo — Cambio de contraseña:**

```javascript
await fetch('http://localhost:8080/auth/password', {
  method: 'PATCH',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    currentPassword: 'OldPass123',
    newPassword: 'NewPass456',
  }),
});
// Response: 204 No Content
```

---

### 6.2 Clínicas (`/clinic`)

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|:----:|-----|-------------|
| PATCH | `/clinic/{id}/complete-setup` | JWT temporal | `CLINIC_OWNER` | Paso 3 del onboarding |
| POST | `/clinic` | Si | `SUPER_ADMIN` | Crea clínica directamente |
| GET | `/clinic/{id}` | Si | Cualquiera | Obtiene datos de una clínica |
| GET | `/clinic` | Si | Cualquiera | Lista todas las clínicas |
| PUT | `/clinic/{id}` | Si | `CLINIC_OWNER`, `SUPER_ADMIN` | Actualiza datos de la clínica |
| DELETE | `/clinic/{id}` | Si | `CLINIC_OWNER`, `SUPER_ADMIN` | Desactiva la clínica |

**Respuesta típica de `GET /clinic/{id}`:**

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
  "logoUrl": "https://cdn.example.com/logo.png",
  "schedule": {
    "openDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "openTime": "09:00",
    "closeTime": "18:00",
    "notes": "Abierto sábados por emergencias"
  },
  "status": "ACTIVE"
}
```

**Valores válidos para `legalType`:** `AUTONOMO`, `SOCIEDAD_LIMITADA`, `SOCIEDAD_ANONIMA`, `SOCIEDAD_CIVIL`, `COMUNIDAD_DE_BIENES`, `OTHER`

---

### 6.3 Empleados (`/employees`)

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|:----:|-----|-------------|
| POST | `/employees` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Crea un empleado |
| GET | `/employees/{id}` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Obtiene un empleado |
| GET | `/employees` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Lista empleados de la clínica |
| PUT | `/employees/{id}` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Actualiza datos del empleado |
| DELETE | `/employees/{id}` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Desactiva el empleado |
| PATCH | `/employees/{id}/salary` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Actualiza salario |
| PATCH | `/employees/{id}/vacation-policy` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Actualiza política de vacaciones |
| PATCH | `/employees/{id}/work-schedule` | Si | `CLINIC_OWNER`, `CLINIC_ADMIN` | Actualiza horario laboral |

**Ejemplo — Crear empleado:**

```javascript
const response = await fetch('http://localhost:8080/employees', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    firstName: 'Juan',
    lastName: 'Pérez',
    documentType: 'DNI',
    documentNumber: '12345678A',
    phone: '634567890',
    email: 'juan@clinica.com',
    address: 'Calle Trabajo 1',
    city: 'Madrid',
    postalCode: '28001',
    avatarUrl: 'https://cdn.example.com/juan.png',
    speciality: 'Cirugía',
    licenseNumber: 'COL12345',
    hireDate: '2024-01-01',
    role: 'CLINIC_VETERINARIAN',
  }),
});
// Response: 201 Created + EmployeeResponse
```

**Respuesta típica de un empleado:**

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
  },
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-02T11:00:00"
}
```

---

### 6.4 Propietarios de mascotas (`/owner`)

| Método | Ruta | Auth | Descripción |
|--------|------|:----:|-------------|
| POST | `/owner` | Si | Registra un propietario |
| GET | `/owner/{id}` | Si | Obtiene un propietario |
| GET | `/owner` | Si | Lista propietarios de la clínica |
| PUT | `/owner/{id}` | Si | Actualiza datos del propietario |
| DELETE | `/owner/{id}` | Si | Elimina el propietario |

**Ejemplo — Crear propietario:**

```javascript
const response = await fetch('http://localhost:8080/owner', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    name: 'Pedro',
    lastName: 'García',
    documentId: 'DNI',
    documentNumber: '87654321B',
    phone: '634567890',
    email: 'pedro@example.com',
    address: 'Calle Residencial 2',
    city: 'Barcelona',
    postalCode: '08001',
    url: 'https://cdn.example.com/pedro.png',
    acceptTermsAndCond: true,
  }),
});
// Response: 201 Created
// {
//   "ownerId": "uuid",
//   "firstName": "Pedro",
//   "lastName": "García",
//   "dni": "87654321B",
//   "phone": "634567890",
//   "email": "pedro@example.com",
//   "address": { "street": "...", "city": "Barcelona", "postalCode": "08001", "fullAddress": "..." }
// }
```

---

### 6.5 Mascotas (`/pet`)

| Método | Ruta | Auth | Descripción |
|--------|------|:----:|-------------|
| POST | `/pet` | Si | Registra una mascota |
| GET | `/pet/{id}` | Si | Obtiene una mascota |
| GET | `/pet/clinic` | Si | Lista mascotas de la clínica |
| GET | `/pet/owner/{ownerId}` | Si | Lista mascotas de un propietario |
| PUT | `/pet/{id}` | Si | Actualiza datos de la mascota |
| PATCH | `/pet/{id}/deactivate` | Si | Desactiva la mascota |
| PATCH | `/pet/{id}/activate` | Si | Reactiva la mascota |
| PATCH | `/pet/{id}/correct-breed` | Si | Corrige la raza |
| PATCH | `/pet/{id}/correct-birthdate` | Si | Corrige la fecha de nacimiento |
| PATCH | `/pet/{id}/correct-sex` | Si | Corrige el sexo |
| PATCH | `/pet/{id}/owner` | Si | Actualiza los datos del propietario embebidos |

**Ejemplo — Crear mascota:**

```javascript
const response = await fetch('http://localhost:8080/pet', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    name: 'Fluffy',
    species: 'Gato',
    breed: 'Persa',
    sex: 'FEMALE',
    dateOfBirth: '2020-03-15',
    chipNumber: '985112345678901',
    avatarUrl: 'https://cdn.example.com/fluffy.png',
    owner: {
      ownerId: 'uuid-del-propietario',
      ownerName: 'Pedro',
      ownerLastName: 'García',
      ownerPhone: '634567890',
    },
  }),
});

// Response: 201 Created
// {
//   "id": "uuid",
//   "clinicId": "uuid",
//   "name": "Fluffy",
//   "species": "Gato",
//   "breed": "Persa",
//   "sex": "FEMALE",
//   "dateOfBirth": "2020-03-15",
//   "ageInYears": 6,
//   "chipNumber": "985112345678901",
//   "avatarUrl": "https://...",
//   "owner": { "ownerId": "uuid", "name": "Pedro", "lastName": "García", "phone": "634567890", "fullName": "Pedro García" },
//   "createdAt": "2024-01-01T10:00:00",
//   "updatedAt": "2024-01-01T10:00:00"
// }
```

**Valores válidos para `sex`:** `MALE`, `FEMALE`, `UNKNOWN`

---

### 6.6 Citas (`/appointments`)

| Método | Ruta | Auth | Rol mínimo | Descripción |
|--------|------|:----:|-----------|-------------|
| POST | `/appointments` | Si | `CLINIC_STAFF` | Crea una cita |
| GET | `/appointments/{id}` | Si | Cualquiera | Obtiene una cita |
| GET | `/appointments` | Si | Cualquiera | Lista citas de la clínica |
| PATCH | `/appointments/{id}/status` | Si | `CLINIC_ADMIN` | Avanza el estado de la cita |
| DELETE | `/appointments/{id}` | Si | Cualquiera | Cancela una cita |

**Ejemplo — Crear cita:**

```javascript
const response = await fetch('http://localhost:8080/appointments', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    emergency: false,
    type: 'RUTINA',
    scheduledAt: '2024-05-15T10:30:00',
    ownerId: 'uuid-propietario',
    ownerName: 'Pedro',
    ownerEmail: 'pedro@example.com',
    ownerPhone: '634567890',
    petId: 'uuid-mascota',
    petName: 'Fluffy',
    petSpecies: 'Gato',
    creationEmployeeId: 'uuid-empleado-que-crea',
    medicalEmployeeId: 'uuid-veterinario-asignado',
    notes: 'Revisión general anual',
    productIds: [],
    source: 'PANEL',
  }),
});
// Response: 201 Created + AppointmentResponse
```

**Respuesta típica de una cita:**

```json
{
  "id": "uuid",
  "clinicId": "uuid",
  "emergency": false,
  "type": "RUTINA",
  "status": "RESERVADA",
  "scheduledAt": "2024-05-15T10:30:00",
  "owner": {
    "ownerId": "uuid",
    "name": "Pedro",
    "email": "pedro@example.com",
    "phone": "634567890"
  },
  "pet": {
    "petId": "uuid",
    "name": "Fluffy",
    "species": "Gato"
  },
  "creationEmployeeId": "uuid",
  "medicalEmployeeId": "uuid",
  "notes": "Revisión general anual",
  "productIds": [],
  "source": "PANEL",
  "createdAt": "2024-05-14T09:00:00",
  "updatedAt": "2024-05-15T10:30:00"
}
```

**Estados y transiciones válidas:**

```
RESERVADA → CLIENTE_LLEGADO → PROXIMO_A_ATENDER → EN_CONSULTA → FINALIZADA
                                                             → REQUIERE_SEGUIMIENTO
Cualquier estado → CANCELADA
```

**Tipos de cita (`type`):** `RUTINA`, `EXAMEN`, `VACUNAS`, `EMERGENCIA`, `BAÑO`, `CIRUGIA`, `OTRO`

**Origen de la cita (`source`):** `PANEL`, `PHONE`, `EMAIL`, `WALK_IN`

**Avanzar el estado de una cita:**

```javascript
await fetch(`http://localhost:8080/appointments/${appointmentId}/status`, {
  method: 'PATCH',
  credentials: 'include',
  // El backend determina automáticamente el siguiente estado válido
});
// Response: 200 OK + AppointmentResponse con el nuevo estado
```

---

### 6.7 Productos (`/product`)

| Método | Ruta | Auth | Rol mínimo | Descripción |
|--------|------|:----:|-----------|-------------|
| POST | `/product/clinic/{clinicId}` | Sí | `CLINIC_ADMIN` | Crea un producto |
| GET | `/product/{id}` | Sí | Cualquiera | Obtiene un producto |
| GET | `/product/clinic/{clinicId}` | Sí | Cualquiera | Lista productos de la clínica |
| GET | `/product/clinic/{clinicId}?category=MEDICATION` | Sí | Cualquiera | Filtra por categoría |
| PUT | `/product/{id}` | Sí | `CLINIC_ADMIN` | Actualiza un producto |
| DELETE | `/product/{id}` | Sí | `CLINIC_ADMIN` | Desactiva un producto |

**Ejemplo — Crear un medicamento:**

```javascript
const response = await fetch('http://localhost:8080/product/clinic/uuid-clinica', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({
    name: 'Amoxicilina 500mg',
    description: 'Antibiótico de amplio espectro',
    sku: 'SKU-001',
    barcode: 'BAR-001',
    price: 29.99,
    taxRate: 0.21,
    stock: 100,
    minStock: 10,
    details: {
      type: 'MEDICATION',
      activeIngredient: 'Amoxicilina',
      dosageForm: 'Comprimidos',
      concentration: '500mg',
      manufacturer: 'Pfizer',
      registrationNumber: 'REG-001',
      prescriptionRequired: false,
      storageConditions: 'Temperatura ambiente',
      batchNumber: 'BATCH-001',
      expirationDate: '2027-12-31',
      species: ['Perro', 'Gato'],
      administrationRoute: 'Oral',
    },
  }),
});
// Response: 201 Created + ProductResponse
```

**Respuesta típica de un producto:**

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
  "details": {
    "type": "MEDICATION",
    "activeIngredient": "Amoxicilina",
    "dosageForm": "Comprimidos",
    "concentration": "500mg",
    "manufacturer": "Pfizer",
    "registrationNumber": "REG-001",
    "prescriptionRequired": false,
    "storageConditions": "Temperatura ambiente",
    "batchNumber": "BATCH-001",
    "expirationDate": "2027-12-31",
    "species": ["Perro", "Gato"],
    "administrationRoute": "Oral"
  }
}
```

**Categorías disponibles (`category`):**

| Valor | Descripción |
|-------|-------------|
| `MEDICATION` | Medicamentos con receta o sin receta |
| `VACCINE` | Vacunas |
| `FOOD` | Alimentos y piensos |
| `SUPPLEMENT` | Suplementos nutricionales |
| `HYGIENE` | Productos de higiene (champús, colonias…) |
| `GROOMING_SERVICE` | Servicios de peluquería |
| `ACCESSORY` | Accesorios (correas, collares…) |
| `TOY` | Juguetes |
| `MEDICAL_SUPPLY` | Material clínico (jeringas, gasas…) |
| `ANTIPARASITIC` | Antiparasitarios (pipetas, collares…) |
| `DIAGNOSTIC` | Pruebas de diagnóstico (tests rápidos…) |
| `PROSTHESIS_IMPLANT` | Prótesis e implantes quirúrgicos |

El campo `details` varía según la categoría e incluye siempre `"type": "<CATEGORY>"`. Consulta Swagger para el esquema exacto de cada tipo.

**Desactivar un producto (con motivo opcional):**

```javascript
await fetch(`http://localhost:8080/product/${productId}`, {
  method: 'DELETE',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify({ reason: 'Producto descontinuado' }),
});
// Response: 204 No Content
```

---

## 7. Formato de errores

Todos los errores de la API siguen el mismo formato JSON, lo que facilita su gestión centralizada en el frontend.

```json
{
  "timestamp": "2026-05-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción legible del error",
  "details": [
    {
      "field": "email",
      "message": "debe ser un email válido",
      "rejectedValue": "esto-no-es-un-email"
    }
  ],
  "path": "/auth/register"
}
```

El campo `details` solo aparece en errores de validación (status 400) y puede contener múltiples entradas si hay varios campos inválidos en la misma petición. En el resto de casos es un array vacío o está ausente.

### Códigos de estado HTTP utilizados

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| 200 | OK | Petición exitosa con cuerpo de respuesta |
| 201 | Created | Recurso creado correctamente |
| 204 | No Content | Operación exitosa sin cuerpo (logout, cambio de contraseña…) |
| 400 | Bad Request | Datos de entrada inválidos o regla de negocio violada |
| 401 | Unauthorized | Token ausente, inválido, caducado o credenciales incorrectas |
| 403 | Forbidden | El usuario no tiene el rol necesario para esa operación |
| 404 | Not Found | El recurso solicitado no existe |
| 409 | Conflict | El recurso ya existe (email duplicado, etc.) |
| 500 | Internal Server Error | Error inesperado en el servidor |

### Gestión de errores recomendada (axios)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, // equivale a credentials: 'include' en fetch
});

// Interceptor global de errores
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { status, data } = error.response;

    if (status === 401) {
      // Intentar refrescar el token
      try {
        await api.post('/auth/refresh');
        // Reintentar la petición original
        return api.request(error.config);
      } catch {
        // Si el refresh falla, redirigir al login
        window.location.href = '/login';
      }
    }

    if (status === 403) {
      console.error('Acceso denegado:', data.message);
    }

    if (status === 400 && data.details?.length > 0) {
      // Mostrar errores de validación campo a campo
      data.details.forEach(({ field, message }) => {
        console.error(`Campo "${field}": ${message}`);
      });
    }

    return Promise.reject(error);
  }
);
```

---

## 8. Exploración interactiva con Swagger

El backend expone una interfaz Swagger UI que permite explorar y probar todos los endpoints directamente desde el navegador, sin necesidad de herramientas externas como Postman.

**URL de Swagger:** `http://localhost:8080/swagger-ui.html`

Swagger muestra el contrato completo de cada endpoint: parámetros, cuerpos de request, esquemas de response y códigos de estado. Es especialmente útil durante el desarrollo para verificar el formato exacto de cada campo antes de implementar las llamadas en el frontend.

Para probar endpoints autenticados desde Swagger, usa el botón "Authorize" e introduce el token JWT en el campo `Authorization` con el formato `Bearer <token>`.

---

*Documentación generada para el proyecto DataVet — TFG 2026*
