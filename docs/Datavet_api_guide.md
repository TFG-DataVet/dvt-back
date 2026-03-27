# Datavet — Guía de prueba de flujo completo

## Orden de ejecución

```
1. Registrar clínica (onboarding paso 1)
2. Verificar email (onboarding paso 2)
3. Completar setup de clínica (onboarding paso 3)  ← devuelve JWT definitivo
4. Login (alternativa a paso 3 si ya tienes cuenta)
5. Registrar empleado
6. Registrar owner
7. Registrar mascota
8. Login con el empleado
```

---

## 1. Registrar clínica — Paso 1 del onboarding

**Método:** `POST`
**URL:** `http://localhost:8080/auth/register`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "clinicName": "",
  "firstName": "",
  "lastName": "",
  "email": "",
  "phone": "",
  "password": ""
}
```
**Respuesta esperada:** `201 Created`
```json
{
  "userId": "...",
  "clinicId": "...",
  "email": "...",
  "message": "Registro completado. Revisa tu email para verificar tu cuenta."
}
```
> El token de verificación aparecerá en los **logs de la consola** (MVP sin SMTP real).
> Copia el token del log — lo necesitas en el paso 2.

---

## 2. Verificar email — Paso 2 del onboarding

**Método:** `GET`
**URL:** `http://localhost:8080/auth/verify-email?token=TOKEN_DEL_LOG`
**Headers:** *(ninguno)*
**Body:** *(ninguno)*

**Respuesta esperada:** `200 OK`
```json
{
  "accessToken": "JWT_TEMPORAL_ONBOARDING",
  "refreshToken": null,
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": "...",
    "employeeId": null,
    "clinicId": "...",
    "email": "...",
    "role": "CLINIC_OWNER"
  }
}
```
> Guarda el `accessToken` — es el JWT temporal con scope `ONBOARDING_ONLY`.
> Lo necesitas en el paso 3.

---

## 3. Completar setup de clínica — Paso 3 del onboarding

**Método:** `PATCH`
**URL:** `http://localhost:8080/clinic/{clinicId}/complete-setup`
> Sustituye `{clinicId}` por el `clinicId` recibido en el paso 1.

**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_TEMPORAL_DEL_PASO_2
```
**Body:**
```json
{
  "legalName": "",
  "legalNumber": "",
  "legalType": "AUTONOMO",
  "address": "",
  "city": "",
  "codePostal": "",
  "phone": "",
  "email": "",
  "logoUrl": "",
  "scheduleOpenDays": "",
  "scheduleOpenTime": "09:00",
  "scheduleCloseTime": "18:00",
  "scheduleNotes": "",
  "ownerDocumentNumber": "",
  "ownerAddress": "",
  "ownerCity": "",
  "ownerPostalCode": "",
  "ownerHireDate": "2024-01-01",
  "ownerAvatarUrl": "",
  "ownerSpeciality": ""
}
```
> Valores válidos para `legalType`:
> `AUTONOMO` `SOCIEDAD_LIMITADA` `SOCIEDAD_ANONIMA` `SOCIEDAD_CIVIL` `COMUNIDAD_DE_BIENES` `OTHER`

**Respuesta esperada:** `200 OK`
```json
{
  "accessToken": "JWT_DEFINITIVO",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": "...",
    "employeeId": "...",
    "clinicId": "...",
    "email": "...",
    "role": "CLINIC_OWNER"
  }
}
```
> Guarda el `accessToken` y el `refreshToken`.
> A partir de aquí usas el `accessToken` en todas las llamadas siguientes.

---

## 4. Login (para sesiones posteriores)

**Método:** `POST`
**URL:** `http://localhost:8080/auth/login`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "email": "",
  "password": ""
}
```
**Respuesta esperada:** `200 OK`
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": "...",
    "employeeId": "...",
    "clinicId": "...",
    "email": "...",
    "role": "CLINIC_OWNER"
  }
}
```

---

## 5. Registrar empleado

**Método:** `POST`
**URL:** `http://localhost:8080/employees`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEL_PASO_3_O_4
```
**Body:**
```json
{
  "userId": "",
  "firstName": "",
  "lastName": "",
  "documentNumber": "",
  "phone": "",
  "address": "",
  "city": "",
  "postalCode": "",
  "avatarUrl": "",
  "speciality": "",
  "licenseNumber": "",
  "hireDate": "2024-01-01",
  "role": "CLINIC_VETERINARIAN"
}
```
> Valores válidos para `role`:
> `CLINIC_ADMIN` `CLINIC_VETERINARIAN` `CLINIC_STAFF`
>
> `licenseNumber` es obligatorio si `role` es `CLINIC_VETERINARIAN`.
> `userId` es el ID del User que se habrá creado previamente con el endpoint de creación de usuario empleado (pendiente de implementar en el MVP — por ahora puedes pasar cualquier string).

**Respuesta esperada:** `201 Created`
```json
{
  "employeeId": "...",
  "userId": "...",
  "clinicId": "...",
  "firstName": "...",
  "lastName": "...",
  "fullName": "...",
  ...
}
```

---

## 6. Registrar owner (dueño de mascota)

**Método:** `POST`
**URL:** `http://localhost:8080/owner`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEL_PASO_3_O_4
```
**Body:**
```json
{
  "name": "",
  "lastName": "",
  "dni": "",
  "phone": "",
  "email": "",
  "address": "",
  "city": "",
  "postalCode": "",
  "url": ""
}
```
**Respuesta esperada:** `201 Created`
```json
{
  "ownerId": "...",
  "firstName": "...",
  "lastName": "...",
  "dni": "...",
  "phone": "...",
  "email": "...",
  "address": { ... }
}
```
> Guarda el `ownerId` — lo necesitas para registrar la mascota.

---

## 7. Registrar mascota

**Método:** `POST`
**URL:** `http://localhost:8080/pets`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEL_PASO_3_O_4
```
**Body:**
```json
{
  "clinicId": "",
  "name": "",
  "species": "",
  "breed": "",
  "sex": "MALE",
  "dateOfBirth": "2020-01-01",
  "chipNumber": "",
  "avatarUrl": "",
  "ownerId": "",
  "ownerName": "",
  "ownerLastName": "",
  "ownerPhone": ""
}
```
> Valores válidos para `sex`: `MALE` `FEMALE` `UNKNOWN`
>
> `ownerId`, `ownerName`, `ownerLastName` y `ownerPhone` deben corresponder
> al owner registrado en el paso 6.

**Respuesta esperada:** `201 Created`
```json
{
  "id": "...",
  "clinicId": "...",
  "name": "...",
  "species": "...",
  "breed": "...",
  "sex": "...",
  "dateOfBirth": "...",
  "ageInYears": 0,
  "chipNumber": "...",
  "owner": { ... },
  ...
}
```

---

## 8. Login con el empleado

> Primero necesitas crear el User del empleado. En el MVP esto se hace
> llamando al endpoint interno de creación de usuario empleado.
> Si no lo has implementado aún, salta este paso.

**Método:** `POST`
**URL:** `http://localhost:8080/auth/login`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "email": "",
  "password": ""
}
```
**Respuesta esperada:** `200 OK` — misma estructura que el paso 4.

---

## Referencia rápida de tokens

| Paso | Token obtenido | Úsalo en |
|---|---|---|
| Paso 2 | JWT temporal `ONBOARDING_ONLY` | Solo en el paso 3 |
| Paso 3 / Login | JWT definitivo `FULL_ACCESS` | Todos los demás endpoints |
| Paso 3 / Login | Refresh token | `POST /auth/refresh` cuando expire el access token |

## Refresh del access token

Cuando el `accessToken` expire (3600 segundos), llama a:

**Método:** `POST`
**URL:** `http://localhost:8080/auth/refresh`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "refreshToken": "TU_REFRESH_TOKEN"
}
```