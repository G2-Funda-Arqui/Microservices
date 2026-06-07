# Guia de ejecucion y prueba: reports-analytics-service

Esta guia asume que ya tienes los microservicios base separados como en las guias anteriores.

`reports-analytics-service` depende de otros bounded contexts para armar el reporte clinico, por eso no se prueba aislado si quieres validar el flujo completo.

## 1. Que se migro

`reports-analytics-service` queda separado del monolito con estas responsabilidades:

- Generar reportes clinicos por paciente.
- Generar PDF de reportes clinicos.
- Consultar dashboards analiticos por paciente.
- Validar pacientes contra `profiles-service` por Feign.
- Consultar resumen clinico desde `healthmonitoring-service`.
- Consultar resumen de medicamentos desde `medication-service`.
- Consultar resumen de citas desde `appointments-service`.
- Validar JWT usando el JWK publico de `iam-service`.
- Consumir eventos RabbitMQ publicados por otros servicios.
- Publicar el evento `clinical-report.generated`.

Puerto local:

```text
http://localhost:8087
```

Swagger:

```text
http://localhost:8087/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8087/v3/api-docs
```

## 2. Dependencias necesarias

Antes de ejecutar `reports-analytics-service`, deben estar activos:

- PostgreSQL Docker en `localhost:5433`.
- RabbitMQ Docker en `localhost:5672`.
- `iam-service` en `localhost:8081`.
- `profiles-service` en `localhost:8082`.
- `appointments-service` en `localhost:8084`.
- `healthmonitoring-service` en `localhost:8085`.
- `medication-service` en `localhost:8086`.

`payments-service` no es necesario para probar Reports.

La base usada por este servicio es:

```text
medibridge_reports
```

Ya esta contemplada en:

```text
docker/postgres-init.sql
```

Si tu volumen de PostgreSQL ya existia antes de agregar esa database, creala manualmente:

```powershell
docker exec -it medibridge-postgres psql -U postgres -d medibridge_iam -c "CREATE DATABASE medibridge_reports;"
```

Si ya existe, PostgreSQL devolvera error de database existente. Ese error se puede ignorar.

## 3. Variables importantes

Locales por defecto:

```text
REPORTS_DB_URL=jdbc:postgresql://localhost:5433/medibridge_reports
REPORTS_DB_USERNAME=postgres
REPORTS_DB_PASSWORD=12345678
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=medibridge
RABBITMQ_PASSWORD=medibridge
RABBITMQ_VHOST=/
IAM_JWK_SET_URI=http://localhost:8081/api/v1/jwks/.well-known/jwks.json
PROFILES_SERVICE_URL=http://localhost:8082
APPOINTMENTS_SERVICE_URL=http://localhost:8084
HEALTHMONITORING_SERVICE_URL=http://localhost:8085
MEDICATION_SERVICE_URL=http://localhost:8086
```

No necesitas configurar estas variables si ejecutas todo local con los puertos indicados.

## 4. Ejecutar localmente con Maven

Desde la raiz del proyecto:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Ejecuta cada servicio en una terminal distinta.

### 4.1 IAM

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8081"
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```

### 4.2 Profiles

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8082"
.\mvnw.cmd -f services/profiles-service/pom.xml spring-boot:run
```

### 4.3 Appointments

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8084"
.\mvnw.cmd -f services/appointments-service/pom.xml spring-boot:run
```

### 4.4 Health Monitoring

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8085"
.\mvnw.cmd -f services/healthmonitoring-service/pom.xml spring-boot:run
```

### 4.5 Medication

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8086"
.\mvnw.cmd -f services/medication-service/pom.xml spring-boot:run
```

### 4.6 Reports Analytics

```powershell
Remove-Item Env:PORT -ErrorAction SilentlyContinue
$env:SERVER_PORT="8087"
.\mvnw.cmd -f services/reports-analytics-service/pom.xml spring-boot:run
```

Resultado esperado:

```text
Tomcat initialized with port 8087
Started ReportsAnalyticsServiceApplication
```

## 5. Verificar health checks

Abre estas URLs:

```text
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8084/actuator/health
http://localhost:8085/actuator/health
http://localhost:8086/actuator/health
http://localhost:8087/actuator/health
```

Todas deben responder:

```json
{
  "status": "UP"
}
```

## 6. Ejecutar localmente con Docker

Construye la imagen:

```powershell
docker build -t medibridge/reports-analytics-service:local ./services/reports-analytics-service
```

Si PostgreSQL y RabbitMQ estan en Docker, pero los demas microservicios corren con Maven en tu host, ejecuta:

```powershell
docker run --rm --name reports-analytics-service-local `
  --network docker_default `
  -p 8087:8087 `
  -e REPORTS_DB_URL=jdbc:postgresql://medibridge-postgres:5432/medibridge_reports `
  -e REPORTS_DB_USERNAME=postgres `
  -e REPORTS_DB_PASSWORD=12345678 `
  -e RABBITMQ_HOST=medibridge-rabbitmq `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e RABBITMQ_VHOST=/ `
  -e IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json `
  -e PROFILES_SERVICE_URL=http://host.docker.internal:8082 `
  -e APPOINTMENTS_SERVICE_URL=http://host.docker.internal:8084 `
  -e HEALTHMONITORING_SERVICE_URL=http://host.docker.internal:8085 `
  -e MEDICATION_SERVICE_URL=http://host.docker.internal:8086 `
  medibridge/reports-analytics-service:local
```

Si todos los microservicios corren como contenedores en la misma red Docker, cambia las URLs `host.docker.internal` por los nombres reales de esos contenedores.

## 7. Prueba basica completa

### 7.1 Registrar usuario en IAM

En Swagger de IAM:

```text
http://localhost:8081/swagger-ui.html
```

Ejecuta:

```text
POST /api/v1/authentication/sign-up
```

Body:

```json
{
  "username": "reports-test-1",
  "password": "Password123",
  "roles": [
    "ROLE_USER"
  ]
}
```

Guarda el `id` retornado. Ese sera el `userId` para crear el doctor en `profiles-service`.

### 7.2 Hacer login en IAM

Ejecuta:

```text
POST /api/v1/authentication/sign-in
```

Body:

```json
{
  "username": "reports-test-1",
  "password": "Password123"
}
```

Copia el `token`.

En Swagger de cada servicio protegido, pulsa `Authorize` y pega:

```text
Bearer <token>
```

### 7.3 Crear paciente en Profiles

Swagger:

```text
http://localhost:8082/swagger-ui.html
```

Ejecuta:

```text
POST /api/v1/profiles/patients
```

Body:

```json
{
  "fullName": "Paciente Reports Demo"
}
```

Guarda el `id` retornado como `patientId`.

### 7.4 Crear doctor en Profiles

Ejecuta:

```text
POST /api/v1/profiles/doctors
```

Body, reemplazando `1` por el `userId` real retornado por IAM:

```json
{
  "userId": 1,
  "fullName": "Doctor Reports Demo"
}
```

Guarda el `id` retornado como `doctorProfileId`.

### 7.5 Asignar doctor al paciente

Ejecuta, reemplazando los IDs:

```text
POST /api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}
```

Ejemplo:

```text
POST /api/v1/profiles/patients/1/doctors/1
```

Este paso publica el evento:

```text
doctor.assigned.patient
```

Y permite que `healthmonitoring-service` valide que el doctor puede registrar observaciones del paciente.

### 7.6 Registrar observacion clinica

Swagger:

```text
http://localhost:8085/swagger-ui.html
```

Ejecuta:

```text
POST /api/v1/health-monitoring/patients/{patientId}/observations
```

Ejemplo:

```text
POST /api/v1/health-monitoring/patients/1/observations
```

Body, reemplazando `recordedByDoctorProfileId` por el `doctorProfileId` real:

```json
{
  "recordedByDoctorProfileId": 1,
  "systolicBloodPressure": 120,
  "diastolicBloodPressure": 80,
  "bodyTemperature": 36.7,
  "painLevel": 2,
  "emotionalState": "CALM",
  "emotionalNotes": "Paciente tranquilo",
  "clinicalNotes": "Sin novedades relevantes",
  "recordedAt": "2026-06-06T22:00:00"
}
```

Este paso publica el evento:

```text
observation.recorded
```

### 7.7 Crear cita medica

Swagger:

```text
http://localhost:8084/swagger-ui.html
```

Ejecuta:

```text
POST /api/v1/appointments/medical
```

Body:

La cita debe cumplir estas reglas del `appointments-service`:

- `startsAt` debe estar en el futuro.
- No puede caer domingo.
- Debe estar entre 09:00 y 18:00.
- `durationInMinutes` debe ser `60`.

Ejemplo valido si ejecutas la prueba el 2026-06-07. Si la ejecutas despues, cambia `startsAt` por otra fecha futura que cumpla las reglas:

```json
{
  "patientId": 1,
  "doctorProfileId": 1,
  "startsAt": "2026-06-08T10:00:00",
  "durationInMinutes": 60,
  "reason": "Control general"
}
```

Este paso publica el evento:

```text
appointment.scheduled
```

### 7.8 Registrar medicamento

Swagger:

```text
http://localhost:8086/swagger-ui.html
```

Ejecuta:

```text
POST /api/v1/medications
```

Body:

```json
{
  "patientId": 1,
  "name": "Paracetamol",
  "dosageAmount": 500,
  "dosageUnit": "MG",
  "administrationRoute": "ORAL",
  "stockQuantity": 20,
  "lowStockThreshold": 5,
  "expirationDate": "2027-01-01"
}
```

Este paso publica el evento:

```text
medication.registered
```

### 7.9 Generar reporte clinico

Swagger:

```text
http://localhost:8087/swagger-ui.html
```

Autoriza con:

```text
Bearer <token>
```

Ejecuta:

```text
POST /api/v1/clinical-reports
```

Body:

```json
{
  "patientId": 1,
  "reportType": "FULL_CLINICAL",
  "startDate": "2026-06-01",
  "endDate": "2026-06-08"
}
```

Respuesta esperada:

```json
{
  "id": 1,
  "patientId": 1,
  "reportType": "FULL_CLINICAL",
  "summary": "Clinical report generated for Paciente Reports Demo from 2026-06-01 to 2026-06-08."
}
```

La respuesta completa incluye tambien:

- Periodo del reporte.
- Fecha de generacion.
- Ruta PDF si ya fue generado. Justo despues de crear el reporte, `pdfPath` normalmente sera `null`.
- Secciones del reporte.

Este flujo:

- Valida que el paciente existe en `profiles-service`.
- Consulta resumen clinico en `healthmonitoring-service`.
- Consulta resumen de medicamentos en `medication-service`.
- Consulta resumen de citas en `appointments-service`.
- Guarda el reporte en `medibridge_reports`.
- Publica `clinical-report.generated` en RabbitMQ.

### 7.10 Consultar reporte por id

Ejecuta:

```text
GET /api/v1/clinical-reports/{reportId}
```

Ejemplo:

```text
GET /api/v1/clinical-reports/1
```

### 7.11 Generar registro PDF

Ejecuta:

```text
POST /api/v1/clinical-reports/{reportId}/pdf
```

Ejemplo:

```text
POST /api/v1/clinical-reports/1/pdf
```

Esto actualiza `pdfPath` del reporte.

Respuesta esperada parcial:

```json
{
  "id": 1,
  "pdfPath": "reports/clinical-report-1.pdf"
}
```

Por defecto, el archivo se guarda en:

```text
reports/clinical-report-{reportId}.pdf
```

Puedes cambiar esa carpeta con:

```text
REPORTS_PDF_STORAGE_PATH=<ruta>
```

### 7.12 Descargar PDF

Ejecuta en navegador o Postman:

```text
GET http://localhost:8087/api/v1/clinical-reports/1/pdf
```

Debe devolver un archivo:

```text
clinical-report-1.pdf
```

### 7.13 Consultar reportes por paciente

Ejecuta:

```text
GET /api/v1/clinical-reports/patients/{patientId}
```

Ejemplo:

```text
GET /api/v1/clinical-reports/patients/1
```

### 7.14 Consultar dashboard analitico

Ejecuta:

```text
GET /api/v1/analytics-dashboards/patients/{patientId}
```

Ejemplo:

```text
GET /api/v1/analytics-dashboards/patients/1
```

Si no hay informacion historica agregada, el dashboard se crea con metricas iniciales en cero. Esto es esperado con la implementacion actual.

## 8. Verificar eventos en RabbitMQ

RabbitMQ Management:

```text
http://localhost:15672
```

Credenciales:

```text
Usuario: medibridge
Password: medibridge
```

Cuando `reports-analytics-service` inicia, declara estas queues:

```text
reportsanalytics.appointment-scheduled
reportsanalytics.observation-recorded
reportsanalytics.alert-critical-triggered
reportsanalytics.medication-registered
reportsanalytics.dose-administered
reportsanalytics.dose-skipped
reportsanalytics.stock-low
```

Todas quedan vinculadas al exchange:

```text
medibridge.events
```

Routing keys consumidas:

```text
appointment.scheduled
observation.recorded
alert.critical.triggered
medication.registered
dose.administered
dose.skipped
stock.low
```

Para verificar consumo:

1. Entra a `Queues and Streams`.
2. Busca las queues `reportsanalytics.*`.
3. Ejecuta los pasos de Health, Appointments y Medication.
4. Si `reports-analytics-service` esta encendido, normalmente no veras mensajes acumulados porque los consume de inmediato.
5. Revisa logs de `reports-analytics-service`; deben aparecer mensajes como:

```text
Appointment scheduled event received by reports analytics
Health observation recorded event received by reports analytics
Medication registered event received by reports analytics
```

Para verificar publicacion de `clinical-report.generated`, crea una queue de debug antes de generar el reporte:

```text
Queue: debug.clinical-report-generated
Exchange: medibridge.events
Routing key: clinical-report.generated
```

Luego ejecuta:

```text
POST /api/v1/clinical-reports
```

Y abre `Get messages` en esa queue.

## 9. Endpoints principales

Clinical Reports:

```text
POST /api/v1/clinical-reports
POST /api/v1/clinical-reports/{reportId}/pdf
GET  /api/v1/clinical-reports/{reportId}
GET  /api/v1/clinical-reports/{reportId}/pdf
GET  /api/v1/clinical-reports/patients/{patientId}
```

Analytics Dashboards:

```text
GET /api/v1/analytics-dashboards/patients/{patientId}
```

Health:

```text
GET /actuator/health
```

Swagger:

```text
GET /swagger-ui.html
```

## 10. Problemas comunes

### Reports responde 401

Falta autorizar Swagger con:

```text
Bearer <token>
```

El token se obtiene desde:

```text
POST http://localhost:8081/api/v1/authentication/sign-in
```

### Reports no valida JWT

Verifica que IAM este activo:

```text
http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

Debe devolver JSON con `keys`.

### Reports no encuentra el paciente

Verifica que el paciente exista:

```text
GET http://localhost:8082/api/v1/internal/profiles/patients/{patientId}/exists
```

Debe devolver:

```text
true
```

### Reports no puede armar secciones del reporte

Verifica que esten vivos:

```text
http://localhost:8084/actuator/health
http://localhost:8085/actuator/health
http://localhost:8086/actuator/health
```

Reports llama internamente a:

```text
GET http://localhost:8084/api/v1/internal/appointments/patients/{patientId}/summary?startDate={startDate}&endDate={endDate}
GET http://localhost:8085/api/v1/internal/health-monitoring/patients/{patientId}/summary
GET http://localhost:8086/api/v1/internal/medications/patients/{patientId}/summary
```

### Error de base de datos

Verifica que exista:

```text
medibridge_reports
```

Y que la URL sea:

```text
jdbc:postgresql://localhost:5433/medibridge_reports
```

### Error de RabbitMQ

Verifica:

```text
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=medibridge
RABBITMQ_PASSWORD=medibridge
RABBITMQ_VHOST=/
```

Y que RabbitMQ Management abra:

```text
http://localhost:15672
```
