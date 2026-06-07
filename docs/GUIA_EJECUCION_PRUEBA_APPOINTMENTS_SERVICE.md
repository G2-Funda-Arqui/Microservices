# Guia de ejecucion y prueba: appointments-service

Esta guia asume que ya tienes `iam-service`, `profiles-service`, PostgreSQL y RabbitMQ funcionando localmente.

## 1. Que se migro

`appointments-service` corresponde a la Fase 4 de la guia de migracion. Queda separado del monolito con estas responsabilidades:

- Agendar citas medicas.
- Agendar visitas familiares.
- Validar disponibilidad de horarios.
- Validar pacientes y relaciones usando tablas locales.
- Consumir eventos de `profiles-service`.
- Publicar `appointment.scheduled` en RabbitMQ.
- Exponer un endpoint interno de resumen para futuros reportes.

Puerto local:

```text
http://localhost:8084
```

Swagger:

```text
http://localhost:8084/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8084/v3/api-docs
```

## 2. Dependencias necesarias

Antes de ejecutar `appointments-service`, deben estar activos:

- PostgreSQL Docker en `localhost:5433`.
- RabbitMQ Docker en `localhost:5672`.
- `iam-service` en `localhost:8081`.
- `profiles-service` en `localhost:8082`.

Base de datos:

```text
medibridge_appointments
```

## 3. Variables locales

```text
APPOINTMENTS_DB_URL=jdbc:postgresql://localhost:5433/medibridge_appointments
APPOINTMENTS_DB_USERNAME=postgres
APPOINTMENTS_DB_PASSWORD=12345678
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=medibridge
RABBITMQ_PASSWORD=medibridge
IAM_JWK_SET_URI=http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

## 4. Ejecutar con Maven

Desde la raiz del proyecto:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Ejecuta IAM:

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```

Ejecuta Profiles:

```powershell
.\mvnw.cmd -f services/profiles-service/pom.xml spring-boot:run
```

Ejecuta Appointments:

```powershell
.\mvnw.cmd -f services/appointments-service/pom.xml spring-boot:run
```

Resultado esperado:

```text
Tomcat initialized with port 8084
Started AppointmentsServiceApplication
```

## 5. Ejecutar con Docker

Construye la imagen:

```powershell
docker build -t medibridge/appointments-service:local ./services/appointments-service
```

Ejecuta el contenedor:

```powershell
docker run --rm --name appointments-service-local `
  --network docker_default `
  -p 8084:8084 `
  -e APPOINTMENTS_DB_URL=jdbc:postgresql://medibridge-postgres:5432/medibridge_appointments `
  -e APPOINTMENTS_DB_USERNAME=postgres `
  -e APPOINTMENTS_DB_PASSWORD=12345678 `
  -e RABBITMQ_HOST=medibridge-rabbitmq `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e IAM_JWK_SET_URI=http://iam-service-local:8081/api/v1/jwks/.well-known/jwks.json `
  medibridge/appointments-service:local
```

Si IAM corre con Maven y Appointments con Docker, usa:

```text
IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json
```

## 6. Flujo de prueba

### 6.1 Crear datos en IAM y Profiles

1. Registra o usa usuarios existentes en IAM.
2. Haz login y copia el token.
3. En Swagger de Profiles autoriza con:

```text
Bearer <token>
```

4. Crea un paciente en Profiles:

```text
POST /api/v1/profiles/patients
```

5. Crea un doctor o familiar.
6. Crea una relacion:

```text
POST /api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}
```

o:

```text
POST /api/v1/profiles/patients/{patientId}/family-members/{familyMemberProfileId}
```

## 7. Verificar referencias locales en RabbitMQ

`appointments-service` declara estas queues:

```text
appointments.patient-registered
appointments.patient-deactivated
appointments.doctor-assigned-patient
appointments.family-assigned-patient
```

Exchange:

```text
medibridge.events
```

Routing keys:

```text
patient.registered
patient.deactivated
doctor.assigned.patient
family.assigned.patient
```

Si Appointments esta encendido, normalmente no veras mensajes acumulados porque los consume de inmediato. Verifica en logs:

```text
Patient reference received by appointments
Doctor-patient relation received by appointments
Family-patient relation received by appointments
```

Tambien puedes verificar en PostgreSQL:

```sql
SELECT * FROM patient_references;
SELECT * FROM doctor_patient_relations;
SELECT * FROM family_patient_relations;
```

Importante: Appointments valida usando esas tablas locales. Si creaste el paciente o la relacion antes de levantar Appointments, el evento ya se perdio para este consumidor y tendras que crear nuevos datos o reinsertar la referencia manualmente.

## 8. Agendar cita medica

En Swagger de Appointments:

```text
http://localhost:8084/swagger-ui.html
```

Autoriza con:

```text
Bearer <token>
```

Ejecuta:

```text
POST /api/v1/appointments/medical
```

Body:

```json
{
  "patientId": 1,
  "doctorProfileId": 1,
  "startsAt": "2026-06-10T10:00:00",
  "durationInMinutes": 60,
  "reason": "Consulta de control"
}
```

Respuesta esperada:

```json
{
  "id": 1,
  "patientId": 1,
  "doctorProfileId": 1,
  "appointmentType": "MEDICAL",
  "status": "SCHEDULED",
  "startsAt": "2026-06-10T10:00:00",
  "endsAt": "2026-06-10T11:00:00",
  "reason": "Consulta de control"
}
```

Reglas actuales:

- La cita debe estar en el futuro.
- Duracion fija: `60` minutos.
- Horario permitido: `09:00` a `18:00`.
- No se permiten domingos.
- El paciente debe existir en `patient_references`.
- El doctor debe estar en `doctor_patient_relations`.
- No puede existir una cita solapada para el mismo paciente.

## 9. Agendar visita familiar

Ejecuta:

```text
POST /api/v1/appointments/family-visits
```

Body:

```json
{
  "patientId": 1,
  "familyMemberProfileId": 1,
  "startsAt": "2026-06-10T11:00:00",
  "durationInMinutes": 60,
  "reason": "Visita familiar"
}
```

Respuesta esperada:

```json
{
  "id": 2,
  "patientId": 1,
  "familyMemberProfileId": 1,
  "appointmentType": "FAMILY_VISIT",
  "status": "SCHEDULED",
  "startsAt": "2026-06-10T11:00:00",
  "endsAt": "2026-06-10T12:00:00",
  "reason": "Visita familiar"
}
```

## 10. Consultas

Obtener cita por id:

```text
GET /api/v1/appointments/{appointmentId}
```

Listar citas por paciente:

```text
GET /api/v1/appointments/patient/{patientId}
```

Endpoint interno para reportes:

```text
GET /api/v1/internal/appointments/patients/{patientId}/summary
```

## 11. Verificar evento `appointment.scheduled`

Para verlo manualmente, crea una queue de debug antes de agendar:

```text
Queue: debug.appointment-scheduled
Exchange: medibridge.events
Routing key: appointment.scheduled
```

Luego agenda una cita y usa `Get messages` en RabbitMQ Management.
