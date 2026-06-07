# Guia de ejecucion y prueba: healthmonitoring-service

Esta guia asume que ya tienes `iam-service`, `profiles-service`, PostgreSQL y RabbitMQ funcionando localmente.

## 1. Que se migro

`healthmonitoring-service` corresponde a la siguiente fase de la guia de migracion. Queda separado del monolito con estas responsabilidades:

- Registrar observaciones clinicas de pacientes.
- Validar pacientes y relacion doctor-paciente usando tablas locales.
- Detectar alertas clinicas.
- Publicar `observation.recorded`.
- Publicar `alert.critical.triggered` cuando la alerta es critica.
- Exponer resumen interno para futuros reportes.

Puerto local:

```text
http://localhost:8085
```

Swagger:

```text
http://localhost:8085/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8085/v3/api-docs
```

## 2. Dependencias necesarias

Antes de ejecutar `healthmonitoring-service`, deben estar activos:

- PostgreSQL Docker en `localhost:5433`.
- RabbitMQ Docker en `localhost:5672`.
- `iam-service` en `localhost:8081`.
- `profiles-service` en `localhost:8082`.

Base de datos:

```text
medibridge_healthmonitoring
```

## 3. Variables locales

```text
HEALTHMONITORING_DB_URL=jdbc:postgresql://localhost:5433/medibridge_healthmonitoring
HEALTHMONITORING_DB_USERNAME=postgres
HEALTHMONITORING_DB_PASSWORD=12345678
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

Ejecuta Health Monitoring:

```powershell
.\mvnw.cmd -f services/healthmonitoring-service/pom.xml spring-boot:run
```

Resultado esperado:

```text
Tomcat initialized with port 8085
Started HealthMonitoringServiceApplication
```

## 5. Ejecutar con Docker

Construye la imagen:

```powershell
docker build -t medibridge/healthmonitoring-service:local ./services/healthmonitoring-service
```

Ejecuta el contenedor:

```powershell
docker run --rm --name healthmonitoring-service-local `
  --network docker_default `
  -p 8085:8085 `
  -e HEALTHMONITORING_DB_URL=jdbc:postgresql://medibridge-postgres:5432/medibridge_healthmonitoring `
  -e HEALTHMONITORING_DB_USERNAME=postgres `
  -e HEALTHMONITORING_DB_PASSWORD=12345678 `
  -e RABBITMQ_HOST=medibridge-rabbitmq `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e IAM_JWK_SET_URI=http://iam-service-local:8081/api/v1/jwks/.well-known/jwks.json `
  medibridge/healthmonitoring-service:local
```

Si IAM corre con Maven y Health Monitoring con Docker, usa:

```text
IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json
```

## 6. Flujo de prueba

1. Haz login en IAM y copia el token.
2. En Swagger de Profiles, autoriza con:

```text
Bearer <token>
```

3. Crea un paciente.
4. Crea un doctor.
5. Asigna el doctor al paciente:

```text
POST /api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}
```

Health Monitoring debe consumir:

```text
patient.registered
doctor.assigned.patient
```

Si `healthmonitoring-service` estaba apagado cuando creaste esos datos, sus referencias locales no se llenan. Crea nuevos datos o inserta referencias manualmente.

## 7. Verificar referencias locales

Queues declaradas por Health Monitoring:

```text
healthmonitoring.patient-registered
healthmonitoring.patient-deactivated
healthmonitoring.doctor-assigned-patient
```

Logs esperados:

```text
Patient reference received by health monitoring
Doctor-patient relation received by health monitoring
```

Tablas:

```sql
SELECT * FROM patient_references;
SELECT * FROM doctor_patient_relations;
```

## 8. Registrar observacion clinica

En Swagger:

```text
http://localhost:8085/swagger-ui.html
```

Autoriza con:

```text
Bearer <token>
```

Ejecuta:

```text
POST /api/v1/health-monitoring/patients/{patientId}/observations
```

Body normal:

```json
{
  "recordedByDoctorProfileId": 1,
  "systolicBloodPressure": 120,
  "diastolicBloodPressure": 80,
  "bodyTemperature": 36.7,
  "painLevel": 2,
  "emotionalState": "CALM",
  "emotionalNotes": "Paciente tranquilo",
  "clinicalNotes": "Control sin hallazgos criticos",
  "recordedAt": "2026-06-10T10:00:00"
}
```

Este caso publica:

```text
observation.recorded
```

## 9. Probar alerta critica

Body con presion alta:

```json
{
  "recordedByDoctorProfileId": 1,
  "systolicBloodPressure": 185,
  "diastolicBloodPressure": 125,
  "bodyTemperature": 38.2,
  "painLevel": 8,
  "emotionalState": "CONFUSED",
  "emotionalNotes": "Confusion leve",
  "clinicalNotes": "Requiere seguimiento inmediato",
  "recordedAt": "2026-06-10T10:00:00"
}
```

Este caso guarda la observacion, crea una alerta `HIGH` y publica:

```text
observation.recorded
alert.critical.triggered
```

## 10. Verificar eventos en RabbitMQ

RabbitMQ Management:

```text
http://localhost:15672
```

Para ver `observation.recorded`, crea antes una queue debug:

```text
Queue: debug.observation-recorded
Exchange: medibridge.events
Routing key: observation.recorded
```

Para ver `alert.critical.triggered`, crea antes:

```text
Queue: debug.alert-critical-triggered
Exchange: medibridge.events
Routing key: alert.critical.triggered
```

Luego registra la observacion y usa `Get messages` en la queue correspondiente.

## 11. Consultas

Observaciones del paciente:

```text
GET /api/v1/health-monitoring/patients/{patientId}/observations
```

Alertas activas:

```text
GET /api/v1/health-monitoring/patients/{patientId}/alerts/active
```

Resumen:

```text
GET /api/v1/health-monitoring/patients/{patientId}/summary
```

Endpoint interno para Reports:

```text
GET /api/v1/internal/health-monitoring/patients/{patientId}/summary
```
