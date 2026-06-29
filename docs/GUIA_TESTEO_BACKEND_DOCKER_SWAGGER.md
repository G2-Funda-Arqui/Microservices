# Guia de testeo backend con Docker y Swagger

Esta guia sirve para levantar toda la infraestructura y los 8 microservicios en Docker, abrir Swagger por servicio y validar el flujo principal del backend.

Notas importantes del estado actual:

- El proyecto usa Spring Boot 3.x y Resilience4j `2.2.0` con `resilience4j-spring-boot3`.
- Si recreas `iam-service`, los tokens anteriores pueden quedar invalidos porque IAM genera un par RSA efimero cuando no estan configuradas `IAM_JWT_PRIVATE_KEY` y `IAM_JWT_PUBLIC_KEY`.
- En Payments, `userId` siempre se refiere al id del usuario de IAM, no al id del perfil de Profiles.
- En Reports Analytics, el rango `startDate`/`endDate` se usa actualmente en `POST /api/v1/clinical-reports`; no hay un endpoint publico separado solo para previsualizar resumen por rango. Internamente Reports llama a Health Monitoring, Medication y Appointments usando ese rango.

## 1. Levantar contenedores

Desde la raiz del repo:

```powershell
docker compose -f docker\docker-compose.yml up --build
```

Si quieres reiniciar desde cero borrando datos:

```powershell
docker compose -f docker\docker-compose.yml down -v
docker compose -f docker\docker-compose.yml up --build
```

Validar estado:

```powershell
docker compose -f docker\docker-compose.yml ps
```

Validar tambien contenedores detenidos:

```powershell
docker compose -f docker\docker-compose.yml ps -a
```

Si los microservicios aparecen como `Created` pero no como `Up`, recrea los contenedores sin borrar volumenes:

```powershell
docker compose -f docker\docker-compose.yml up -d --force-recreate --remove-orphans
```

Ese caso puede pasar si quedo un contenedor creado sin red de Docker. El sintoma tipico en logs de IAM es:

```text
java.net.UnknownHostException: postgres
```

Despues de recrear, valida otra vez con:

```powershell
docker compose -f docker\docker-compose.yml ps
```

Logs de un servicio:

```powershell
docker compose -f docker\docker-compose.yml logs -f iam-service
docker compose -f docker\docker-compose.yml logs -f profiles-service
```

Logs recientes sin seguir el stream:

```powershell
docker compose -f docker\docker-compose.yml logs --tail=120 reports-analytics-service
```

Si cambiaste codigo Java y necesitas reconstruir un solo servicio:

```powershell
docker compose -f docker\docker-compose.yml up -d --build --force-recreate reports-analytics-service
```

Si cambiaste dependencias comunes o quieres reconstruir todo:

```powershell
docker compose -f docker\docker-compose.yml up -d --build --force-recreate --remove-orphans
```

## 2. URLs de Swagger

- IAM: http://localhost:8081/swagger-ui.html
- Profiles: http://localhost:8082/swagger-ui.html
- Payments: http://localhost:8083/swagger-ui.html
- Appointments: http://localhost:8084/swagger-ui.html
- Health Monitoring: http://localhost:8085/swagger-ui.html
- Medication: http://localhost:8086/swagger-ui.html
- Reports Analytics: http://localhost:8087/swagger-ui.html
- Communication: http://localhost:8088/swagger-ui.html

Infraestructura:

- PostgreSQL: `localhost:5433`
- RabbitMQ Management: http://localhost:15672, usuario `medibridge`, password `medibridge`
- MongoDB: `localhost:27017`

## 3. Autenticacion en Swagger

1. Abre IAM Swagger.
2. Ejecuta `POST /api/v1/authentication/sign-up`.
3. Ejecuta `POST /api/v1/authentication/sign-in`.
4. Copia el `token` de la respuesta.
5. En los otros Swagger, usa `Authorize` con:

```text
Bearer TU_TOKEN
```

Si un endpoint devuelve `401` con token que antes funcionaba, inicia sesion otra vez en IAM. Esto ocurre despues de recrear `iam-service` cuando las llaves JWT son efimeras.

Usuarios recomendados para el flujo:

```json
{
  "username": "doctor@test.com",
  "password": "Password123!",
  "roles": ["ROLE_USER"]
}
```

```json
{
  "username": "family@test.com",
  "password": "Password123!",
  "roles": ["ROLE_USER"]
}
```

Guarda los `id` devueltos por IAM. Los vas a necesitar como `doctorUserId` y `familyUserId`.

## 4. Crear perfiles

En Profiles Swagger, con el token del usuario correspondiente.

Crear paciente:

`POST /api/v1/profiles/patients`

```json
{
  "fullName": "Paciente Geriatrico Demo"
}
```

Guarda `patientId`.

Crear perfil de doctor usando token del doctor:

`POST /api/v1/profiles/doctors`

```json
{
  "fullName": "Dra. Demo"
}
```

Guarda `doctorProfileId`. El backend asocia el perfil al usuario autenticado por JWT.

Crear perfil familiar usando token del familiar:

`POST /api/v1/profiles/family-members`

```json
{
  "fullName": "Familiar Demo"
}
```

Guarda `familyMemberProfileId`.

## 5. Suscripciones

En Payments Swagger, con token valido.

Importante: todos los `userId` de Payments son ids de IAM. No uses `doctorProfileId`, `familyMemberProfileId` ni `patientId` en estos campos.

Para familiar puedes probar plan gratuito:

`POST /api/v1/subscriptions`

```json
{
  "userId": FAMILY_USER_ID,
  "commercialLine": "FAMILY",
  "planType": "FREE",
  "billingCycle": "MONTHLY"
}
```

Para que el doctor pueda tomar/asignarse pacientes, necesita suscripcion institucional activa:

`POST /api/v1/subscriptions`

```json
{
  "userId": DOCTOR_USER_ID,
  "commercialLine": "INSTITUTION",
  "planType": "INSTITUTION_BASIC",
  "billingCycle": "MONTHLY"
}
```

Importante: los planes institucionales tienen precio mayor a cero y usan Stripe. Para probarlos por Swagger necesitas configurar `STRIPE_SECRET_KEY` en el compose antes de levantar contenedores.

Alternativa local sin Stripe: insertar una suscripcion activa de prueba en PostgreSQL:

```powershell
docker exec -it medibridge-postgres psql -U postgres -d medibridge_payments
```

```sql
INSERT INTO subscriptions (
  user_id, plan_id, status, stripe_customer_id, started_at, current_period_end, created_at, updated_at
)
SELECT
  DOCTOR_USER_ID,
  id,
  'ACTIVE',
  'local-doctor-' || DOCTOR_USER_ID,
  CURRENT_DATE,
  CURRENT_DATE + INTERVAL '30 days',
  NOW(),
  NOW()
FROM plans
WHERE commercial_line = 'INSTITUTION'
  AND plan_type = 'INSTITUTION_BASIC'
  AND billing_cycle = 'MONTHLY'
LIMIT 1;
```

Reemplaza `DOCTOR_USER_ID` por el id real.

Consultar suscripcion activa del usuario IAM:

`GET /api/v1/subscriptions/users/{userId}`

Ejemplo:

```text
GET /api/v1/subscriptions/users/5
```

Resultado esperado si el usuario tiene suscripcion:

```json
{
  "userId": 5,
  "status": "ACTIVE",
  "plan": {
    "commercialLine": "INSTITUTION",
    "planType": "INSTITUTION_BASIC"
  }
}
```

## 6. Vincular care team

En Profiles Swagger.

Asignar doctor a paciente usando token del doctor:

`POST /api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}`

Resultado esperado:

- Sin suscripcion institucional: `403`.
- Con suscripcion institucional activa: `201`.

Vincular familiar a paciente usando token del familiar:

`POST /api/v1/profiles/patients/{patientId}/family-members/{familyMemberProfileId}`

Resultado esperado:

- Familiar sin plan: puede vincular hasta 1 paciente.
- Familiar con `FAMILY_PREMIUM`: puede vincular hasta el `maxPatients` del plan.

Validar endpoint interno:

`GET /api/v1/internal/profiles/users/{userId}/can-access/{patientId}`

Debe devolver `true` para doctor/familiar vinculados.

## 7. Producto base: citas

En Appointments Swagger.

Crear visita familiar con token del familiar:

`POST /api/v1/appointments/family-visits`

```json
{
  "patientId": PATIENT_ID,
  "familyMemberProfileId": FAMILY_MEMBER_PROFILE_ID,
  "startsAt": "2026-07-01T10:00:00",
  "durationInMinutes": 60,
  "reason": "Visita familiar semanal"
}
```

Crear cita medica con token del doctor:

`POST /api/v1/appointments/medical`

```json
{
  "patientId": PATIENT_ID,
  "doctorProfileId": DOCTOR_PROFILE_ID,
  "startsAt": "2026-07-01T12:00:00",
  "durationInMinutes": 45,
  "reason": "Control geriatrico"
}
```

Consultar citas:

- `GET /api/v1/appointments/{appointmentId}`
- `GET /api/v1/appointments/patient/{patientId}`

Validacion esperada: usuario fuera del care team debe recibir `403`.

## 8. Producto base: medicacion

En Medication Swagger.

Registrar medicacion:

`POST /api/v1/medications`

```json
{
  "patientId": PATIENT_ID,
  "name": "Losartan",
  "dosageAmount": 50,
  "dosageUnit": "MG",
  "administrationRoute": "ORAL",
  "stockQuantity": 20,
  "lowStockThreshold": 5,
  "expirationDate": "2027-01-01"
}
```

Crear horario:

`POST /api/v1/medication-schedules`

```json
{
  "medicationId": MEDICATION_ID,
  "patientId": PATIENT_ID,
  "frequencyType": "DAILY",
  "timesPerDay": 1,
  "administrationTime": "08:00:00",
  "startDate": "2026-07-01",
  "endDate": "2026-12-31"
}
```

Registrar dosis:

`POST /api/v1/dose-administrations`

```json
{
  "medicationId": MEDICATION_ID,
  "scheduleId": SCHEDULE_ID,
  "patientId": PATIENT_ID,
  "administeredAt": "2026-07-01T08:05:00",
  "notes": "Dosis administrada sin incidentes"
}
```

Consultar:

- `GET /api/v1/medications/patients/{patientId}`
- `GET /api/v1/medication-schedules/patients/{patientId}/active`
- `GET /api/v1/dose-administrations/medications/{medicationId}`

Validacion esperada: producto base, pero solo care team autorizado.

## 9. Premium: monitoreo avanzado

En Health Monitoring Swagger.

Registrar observacion con token del doctor:

`POST /api/v1/health-monitoring/patients/{patientId}/observations`

```json
{
  "recordedByDoctorProfileId": DOCTOR_PROFILE_ID,
  "systolicBloodPressure": 160,
  "diastolicBloodPressure": 95,
  "bodyTemperature": 37.8,
  "painLevel": 4,
  "emotionalState": "CONFUSED",
  "emotionalNotes": "Desorientacion leve",
  "clinicalNotes": "Controlar presion arterial",
  "recordedAt": "2026-07-01T09:00:00"
}
```

Consultar:

- `GET /api/v1/health-monitoring/patients/{patientId}/observations`
- `GET /api/v1/health-monitoring/patients/{patientId}/alerts/active`
- `GET /api/v1/health-monitoring/patients/{patientId}/summary`

Resultado esperado:

- Sin plan pagado del usuario autenticado: `403` en endpoints premium.
- Con plan activo pagado: respuesta `200`.
- Si el usuario no pertenece al care team: `403`.

## 10. Premium: reportes, PDF y dashboard

En Reports Analytics Swagger.

Generar reporte:

`POST /api/v1/clinical-reports`

```json
{
  "patientId": PATIENT_ID,
  "reportType": "FULL_CLINICAL",
  "startDate": "2026-07-01",
  "endDate": "2026-07-31"
}
```

Este es el endpoint que concentra la consulta por rango de fechas. Al ejecutarlo, Reports llama internamente a:

- Health Monitoring: `GET /api/v1/internal/health-monitoring/patients/{patientId}/summary?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- Medication: `GET /api/v1/internal/medications/patients/{patientId}/summary?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- Appointments: `GET /api/v1/internal/appointments/patients/{patientId}/summary?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

Esos endpoints internos no aparecen como flujo principal en Swagger de Reports porque no son publicos del servicio Reports; son usados por Feign desde Reports hacia los otros servicios.

Validacion de rango de fechas:

- Antes de generar el reporte, registra al menos una observacion, una dosis y una cita dentro del rango `2026-07-01` a `2026-07-31`.
- Registra tambien otra observacion, otra dosis o una cita fuera del rango, por ejemplo en `2026-08-05`.
- El reporte debe resumir solo observaciones, dosis administradas y citas del periodo solicitado.
- En la seccion de medicacion, `activeMedications` y `lowStockMedications` representan el inventario activo actual; `doseAdministrations` es el contador filtrado por `startDate` y `endDate`.

Generar PDF:

`POST /api/v1/clinical-reports/{reportId}/pdf`

Descargar PDF:

`GET /api/v1/clinical-reports/{reportId}/pdf`

Dashboard:

`GET /api/v1/analytics-dashboards/patients/{patientId}`

Si despues de recrear contenedores este endpoint devuelve `401`, vuelve a hacer `sign-in` en IAM y reemplaza el token en Swagger.

Resultado esperado:

- Sin plan pagado: `403`.
- Con plan pagado y care team valido: `200`.
- Usuario fuera del care team: `403`.

## 11. Communication sin premium

En Communication Swagger.

Enviar mensaje usando token del usuario autenticado:

`POST /api/v1/chat/messages`
 
```json
{
  "recipientUserId": RECIPIENT_USER_ID,
  "content": "Mensaje de prueba",
  "sentAt": "2026-07-01T11:00:00"
}
```

El backend usa como remitente al usuario autenticado por JWT.

Consultar conversacion:

`GET /api/v1/chat/messages/{senderUserId}/{recipientUserId}`

Resultado esperado:

- Si el usuario autenticado es sender o recipient: `200`.
- Si no participa en el chat: `403`.
- No hay bloqueo premium en communication.

## 12. Prueba de circuit breaker

Los circuit breakers estan aplicados en llamadas salientes reales:

- Feign HTTP entre microservicios.
- Stripe en Payments.
- Publicacion RabbitMQ con `RabbitTemplate`.

La forma mas simple de probarlos en local es detener temporalmente una dependencia y ejecutar un endpoint que la consuma. Despues vuelve a levantar el servicio detenido.

### 12.1 Reports contra Health Monitoring

1. Asegurate de tener token valido, paciente existente y suscripcion pagada activa.
2. Deten Health Monitoring:

```powershell
docker compose -f docker\docker-compose.yml stop healthmonitoring-service
```

3. En Reports Swagger ejecuta:

`POST /api/v1/clinical-reports`

```json
{
  "patientId": PATIENT_ID,
  "reportType": "FULL_CLINICAL",
  "startDate": "2026-07-01",
  "endDate": "2026-07-31"
}
```

Resultado esperado:

- El endpoint no debe caer por stacktrace no controlado.
- La seccion "Health monitoring" debe devolver un texto de fallback similar a `Health monitoring summary is temporarily unavailable.`
- Las otras secciones deben seguir respondiendo si sus dependencias estan activas.

4. Levanta Health Monitoring otra vez:

```powershell
docker compose -f docker\docker-compose.yml up -d healthmonitoring-service
```

### 12.2 Reports contra Medication

1. Deten Medication:

```powershell
docker compose -f docker\docker-compose.yml stop medication-service
```

2. Genera un reporte en Reports.

Resultado esperado:

- La seccion "Medication management" debe mostrar fallback similar a `Medication summary is temporarily unavailable.`

3. Levanta Medication:

```powershell
docker compose -f docker\docker-compose.yml up -d medication-service
```

### 12.3 Reports contra Appointments

1. Deten Appointments:

```powershell
docker compose -f docker\docker-compose.yml stop appointments-service
```

2. Genera un reporte en Reports.

Resultado esperado:

- La seccion "Appointments" debe mostrar fallback similar a `Appointment summary is temporarily unavailable.`

3. Levanta Appointments:

```powershell
docker compose -f docker\docker-compose.yml up -d appointments-service
```

### 12.4 Appointments o Medication contra Profiles

1. Deten Profiles:

```powershell
docker compose -f docker\docker-compose.yml stop profiles-service
```

2. Ejecuta un endpoint que requiera validar acceso del paciente, por ejemplo en Medication:

`GET /api/v1/medications/patients/{patientId}`

Resultado esperado:

- Debe responder `403` o una respuesta controlada de acceso no autorizado, no un `500` por conexion rechazada.

3. Levanta Profiles:

```powershell
docker compose -f docker\docker-compose.yml up -d profiles-service
```

### 12.5 RabbitMQ publisher

1. Deten RabbitMQ:

```powershell
docker compose -f docker\docker-compose.yml stop rabbitmq
```

2. Ejecuta una accion que publique evento, por ejemplo crear usuario en IAM o generar reporte.

Resultado esperado:

- En IAM, crear usuario debe responder `503 Service Unavailable` con mensaje similar a `RabbitMQ user registered publishing failed`.
- La creacion del usuario debe revertirse porque el registro y la publicacion quedan dentro de una transaccion.
- En otros servicios, la operacion puede fallar si publicar el evento es parte obligatoria del flujo.
- El error debe ser controlado por el fallback del publisher y aparecer en logs como fallo de `RabbitMQ ... publishing failed`, no como excepcion inesperada sin contexto.

3. Levanta RabbitMQ:

```powershell
docker compose -f docker\docker-compose.yml up -d rabbitmq
```

### 12.6 Ver logs del circuit breaker

Para revisar los errores controlados:

```powershell
docker compose -f docker\docker-compose.yml logs --tail=160 reports-analytics-service
docker compose -f docker\docker-compose.yml logs --tail=160 medication-service
docker compose -f docker\docker-compose.yml logs --tail=160 appointments-service
```

Para confirmar que los contenedores volvieron a estar arriba:

```powershell
docker compose -f docker\docker-compose.yml ps
```

## 13. Checklist final

Marca el flujo como validado cuando confirmes:

- Todos los Swagger abren.
- IAM permite sign-up y sign-in.
- Los JWT funcionan en los otros servicios.
- Profiles crea paciente, doctor y familiar.
- Doctor no puede tomar paciente sin suscripcion institucional.
- Doctor puede tomar paciente con suscripcion institucional.
- Familiar puede vincular paciente y respeta `maxPatients`.
- Appointments funciona como producto base.
- Medication funciona como producto base.
- Health Monitoring premium bloquea sin plan y permite con plan.
- Reports/PDF/Dashboard bloquean sin plan y permiten con plan.
- Communication funciona sin premium y valida sender/participantes.
- Endpoints internos no requieren JWT para comunicacion entre servicios.
- Circuit breakers devuelven fallback o errores controlados cuando una dependencia esta detenida.
