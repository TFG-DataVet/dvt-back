# Datavet — Guía de prueba de flujo completo

## Orden de ejecución

```
1.  Registrar clínica (onboarding paso 1)
2.  Verificar email (onboarding paso 2)
3.  Completar setup de clínica (onboarding paso 3) ← devuelve JWT definitivo
4.  Login (para sesiones posteriores o si el JWT temporal expiró)
5.  Registrar empleado
6.  Empleado activa su cuenta (establece contraseña)
7.  Login con el empleado
8.  Registrar owner (dueño de mascota)
9.  Registrar mascota
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

> El token de verificación aparecerá en los **logs de la consola**.
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
  "nextStep": "COMPLETE_SETUP",
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
> Expira en 1 hora. Si expira antes de completar el paso 3, usa el **paso 4 (login)**
> para obtener un nuevo JWT temporal automáticamente.

---

## 2b. Reenviar email de verificación (si el token expiró antes del paso 2)

Solo necesario si el token del paso 1 expiró antes de verificarlo.

**Método:** `POST`
**URL:** `http://localhost:8080/auth/resend-verification`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "email": ""
}
```

**Respuesta esperada:** `204 No Content`

> El nuevo token aparecerá en los logs de consola.

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
  "nextStep": null,
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

## 4. Login

Úsalo en dos situaciones:
- Sesiones posteriores (el JWT definitivo expiró)
- El JWT temporal del paso 2 expiró antes de completar el paso 3

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

Si el usuario ya completó el setup (`nextStep: null`):
```json
{
  "accessToken": "JWT_DEFINITIVO",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "nextStep": null,
  "user": {
    "userId": "...",
    "employeeId": "...",
    "clinicId": "...",
    "email": "...",
    "role": "CLINIC_OWNER"
  }
}
```

Si el usuario aún no completó el setup (`nextStep: "COMPLETE_SETUP"`):
```json
{
  "accessToken": "JWT_TEMPORAL_ONBOARDING",
  "refreshToken": null,
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "nextStep": "COMPLETE_SETUP",
  "user": { ... }
}
```

> Si recibes `nextStep: "COMPLETE_SETUP"` ve directamente al paso 3
> usando el `accessToken` recibido.

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
  "email": "",
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

> Valores válidos para `role`: `CLINIC_ADMIN` `CLINIC_VETERINARIAN` `CLINIC_STAFF`
>
> `licenseNumber` es obligatorio si `role` es `CLINIC_VETERINARIAN`.
>
> Al crear el empleado se le enviará automáticamente un email de activación.
> El token de activación aparecerá en los **logs de la consola**.

**Respuesta esperada:** `201 Created`
```json
{
  "employeeId": "...",
  "userId": "...",
  "clinicId": "...",
  "firstName": "...",
  "lastName": "...",
  "fullName": "...",
  "documentNumber": "...",
  "phone": "...",
  "speciality": "...",
  "licenseNumber": "...",
  "hireDate": "...",
  "active": true,
  ...
}
```

> Copia el token de activación del log de consola — lo necesitas en el paso 6.

---

## 6. Empleado activa su cuenta

El empleado usa el token recibido por email para establecer su contraseña
y activar su cuenta.

**Método:** `POST`
**URL:** `http://localhost:8080/auth/activate-account`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "token": "TOKEN_DEL_LOG_DE_CONSOLA",
  "password": ""
}
```

> La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula,
> una minúscula y un número.

**Respuesta esperada:** `204 No Content`

> La cuenta queda activa. El empleado ya puede hacer login.

---

## 7. Login con el empleado

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
  "nextStep": null,
  "user": {
    "userId": "...",
    "employeeId": "...",
    "clinicId": "...",
    "email": "...",
    "role": "CLINIC_VETERINARIAN"
  }
}
```

---

## 8. Registrar owner (dueño de mascota)

**Método:** `POST`
**URL:** `http://localhost:8080/owner`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEL_PASO_3_4_O_7
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
  "address": {
    "street": "...",
    "city": "...",
    "postalCode": "...",
    "fullAddress": "..."
  }
}
```

> Guarda el `ownerId` — lo necesitas para registrar la mascota.

---

## 9. Registrar mascota

**Método:** `POST`
**URL:** `http://localhost:8080/pets`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEL_PASO_3_4_O_7
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
> al owner registrado en el paso 8.

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
  "owner": {
    "ownerId": "...",
    "name": "...",
    "lastName": "...",
    "phone": "...",
    "fullName": "..."
  },
  "createdAt": "...",
  "updatedAt": null
}
```

---

## Referencia rápida de tokens

| Paso | Token obtenido | Scope | Úsalo en |
|---|---|---|---|
| Paso 2 / Login con setup pendiente | JWT temporal | `ONBOARDING_ONLY` | Solo en el paso 3 |
| Paso 3 / Login normal | JWT definitivo | `FULL_ACCESS` | Todos los demás endpoints |
| Paso 3 / Login normal | Refresh token | — | `POST /auth/refresh` |

---

## Refresh del access token

Cuando el `accessToken` expire:

**Método:** `POST`
**URL:** `http://localhost:8080/auth/refresh`
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
  "refreshToken": ""
}
```

**Respuesta esperada:** `200 OK` — mismo formato que el login.

---

## Cambiar contraseña

**Método:** `PATCH`
**URL:** `http://localhost:8080/auth/password`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEFINITIVO
```
**Body:**
```json
{
  "currentPassword": "",
  "newPassword": ""
}
```

**Respuesta esperada:** `204 No Content`

> Tras cambiar la contraseña todos los refresh tokens activos quedan invalidados.
> Tendrás que hacer login de nuevo en todos los dispositivos.

---

## Logout

**Método:** `POST`
**URL:** `http://localhost:8080/auth/logout`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer JWT_DEFINITIVO
```
**Body:**
```json
{
  "refreshToken": ""
}
```

**Respuesta esperada:** `204 No Content`