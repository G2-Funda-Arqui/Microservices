# Guia de ejecucion y prueba - Medication Service

## 1. Proposito del servicio

`medication-service` gestiona medicamentos, horarios de medicacion, administracion de dosis, omision de dosis y control de stock.

Este servicio corresponde al bounded context `medicationmanagement` del monolito y queda separado con:

- Base de datos propia: `medibridge_medication`
- Puerto local: `8086`
- Swagger UI: `http://localhost:8086/swagger-ui.html`
- Exchange RabbitMQ: `medibridge.events`

## 2. Dependencias necesarias

Antes de ejecutar `medication-service`, deben estar disponibles:

- PostgreSQL local en Docker, puerto externo `5433`
- RabbitMQ local en Docker, management UI en `http://localhost:15672`
- `iam-service` ejecutandose en `http://localhost:8081`
- `profiles-service` ejecutandose en `http://localhost:8082`

Credenciales locales usadas por defecto:

```text
PostgreSQL:
Host: localhost
Port: 5433
Username: postgres
Password: 12345678
Database: medibridge_medication

RabbitMQ:
Host: localhost
Port: 5672
Management UI: http://localhost:15672
Username: medibridge
Password: medibridge
```

## 3. Levantar infraestructura local

Desde la raiz del proyecto:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Verificar contenedores:

```powershell
docker ps
```

La base `medibridge_medication` ya esta incluida en `docker/postgres-init.sql`. Si tu contenedor de PostgreSQL fue creado antes de que esa base existiera en el init script, debes crearla manualmente desde pgAdmin o con:

```powershell
docker exec -it medibridge-postgres psql -U postgres -d postgres
```

Dentro de `psql`:

```sql
CREATE DATABASE medibridge_medication;
```

## 4. Ejecutar servicios requeridos

Ejecuta primero `iam-service`:

```powershell
.\mvnw.cmd -f services\iam-service\pom.xml spring-boot:run
```

Luego `profiles-service`:

```powershell
.\mvnw.cmd -f services\profiles-service\pom.xml spring-boot:run
```

Finalmente `medication-service`:

```powershell
.\mvnw.cmd -f services\medication-service\pom.xml spring-boot:run
```

Swagger de Medication:

```text
http://localhost:8086/swagger-ui.html
```

Health check:

```text
http://localhost:8086/actuator/health
```

## 4.1 Ejecutar Medication Service con Docker

Si quieres ejecutar `medication-service` como contenedor Docker, primero asegúrate de tener levantados PostgreSQL y RabbitMQ:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Construye la imagen desde la raíz del proyecto:

```powershell
docker build -t medibridge-medication-service:local services/medication-service
```

Ejecuta el contenedor:

```powershell
docker run --name medibridge-medication-service --rm -p 8086:8086 `
  -e MEDICATION_DB_URL=jdbc:postgresql://host.docker.internal:5433/medibridge_medication `
  -e MEDICATION_DB_USERNAME=postgres `
  -e MEDICATION_DB_PASSWORD=12345678 `
  -e RABBITMQ_HOST=host.docker.internal `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e RABBITMQ_VHOST=/ `
  -e IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json `
  medibridge-medication-service:local
```

Uso de `host.docker.internal`:

- Dentro del contenedor, `localhost` apunta al propio contenedor.
- Para conectarse al PostgreSQL, RabbitMQ e IAM que están expuestos en tu PC, se usa `host.docker.internal`.
- Antes de probar endpoints protegidos, `iam-service` debe estar corriendo en tu máquina en el puerto `8081`.

Verifica que inició correctamente:

```text
http://localhost:8086/actuator/health
```

Swagger:

```text
http://localhost:8086/swagger-ui.html
```

Para detenerlo, presiona `Ctrl + C` en la terminal donde ejecutaste `docker run`. Como se usa `--rm`, el contenedor se elimina automáticamente al detenerse.

## 5. Flujo importante de RabbitMQ antes de probar

`medication-service` no consulta directamente a `profiles-service` para validar pacientes. En su lugar mantiene una tabla local `patient_references`.

Esa tabla se llena cuando `medication-service` consume el evento:

```text
patient.registered
```

Por eso, para la prueba basica:

1. Levanta RabbitMQ.
2. Levanta `profiles-service`.
3. Levanta `medication-service`.
4. Recien despues crea un paciente desde `profiles-service`.

Si creaste el paciente antes de levantar `medication-service`, es normal que `POST /api/v1/medications` responda error porque el servicio aun no tiene la referencia local del paciente.

Queues que declara `medication-service`:

```text
medication.patient-registered
medication.patient-deactivated
```

Eventos que publica:

```text
medication.registered
dose.administered
dose.skipped
stock.low
```

## 6. Obtener token JWT

Desde Swagger de IAM:

```text
http://localhost:8081/swagger-ui.html
```

Haz sign in en:

```text
POST /api/v1/authentication/sign-in
```

Copia el token JWT y en Swagger de Medication usa:

```text
Bearer TU_TOKEN
```

## 7. Crear paciente en Profiles para sincronizar Medication

Con `profiles-service` levantado, crea un paciente desde:

```text
http://localhost:8082/swagger-ui.html
```

Cuando Profiles publique `patient.registered`, `medication-service` consumira el evento en la queue:

```text
medication.patient-registered
```

Para verificarlo en RabbitMQ:

1. Entra a `http://localhost:15672`
2. Login: `medibridge` / `medibridge`
3. Ve a `Queues and Streams`
4. Abre `medication.patient-registered`
5. Revisa:
   - `Ready`: normalmente debe quedar en `0` si el servicio consumio el mensaje.
   - `Total`: puede subir cuando se publica el evento.
   - En logs de `medication-service` debe aparecer que sincronizo el `patientId`.

Si quieres ver el payload antes de que se consuma, detén `medication-service`, crea el paciente y luego revisa la queue. Con el servicio activo, el mensaje puede consumirse inmediatamente y por eso no queda visible.

## 8. Registrar medicamento

Endpoint:

```text
POST http://localhost:8086/api/v1/medications
```

Body de ejemplo:

```json
{
  "patientId": 1,
  "name": "Paracetamol",
  "dosageAmount": 500,
  "dosageUnit": "MG",
  "administrationRoute": "ORAL",
  "stockQuantity": 3,
  "lowStockThreshold": 2,
  "expirationDate": "2027-12-31"
}
```

Resultado esperado:

- HTTP `201`
- Se crea el medicamento.
- Se publica `medication.registered`.
- Si `stockQuantity <= lowStockThreshold`, tambien se publica `stock.low`.

## 9. Crear horario de medicacion

Endpoint:

```text
POST http://localhost:8086/api/v1/medication-schedules
```

Body:

```json
{
  "medicationId": 1,
  "patientId": 1,
  "frequencyType": "DAILY",
  "timesPerDay": 1,
  "administrationTime": "08:00:00",
  "startDate": "2026-06-06",
  "endDate": "2026-12-31"
}
```

Resultado esperado:

- HTTP `201`
- Se crea un horario activo para el medicamento.

## 10. Registrar dosis administrada

Endpoint:

```text
POST http://localhost:8086/api/v1/dose-administrations
```

Body:

```json
{
  "medicationId": 1,
  "scheduleId": 1,
  "patientId": 1,
  "administeredAt": "2026-06-06T08:00:00",
  "notes": "Dosis tomada correctamente"
}
```

Resultado esperado:

- HTTP `201`
- Se registra la dosis.
- Se decrementa `stockQuantity` en 1.
- Se publica `dose.administered`.
- Si el stock queda por debajo o igual al umbral, se publica `stock.low`.

Regla importante:

- No puedes registrar dos dosis administradas para el mismo `scheduleId` en el mismo dia.
- Si el stock llega a `0`, el siguiente intento de administrar dosis falla por stock insuficiente.

## 11. Omitir dosis

Endpoint:

```text
POST http://localhost:8086/api/v1/dose-administrations/skip
```

Body:

```json
{
  "medicationId": 1,
  "scheduleId": 1,
  "patientId": 1,
  "skippedAt": "2026-06-07T08:00:00",
  "reason": "Paciente no disponible"
}
```

Resultado esperado:

- HTTP `201`
- Se registra la dosis como omitida.
- Se publica `dose.skipped`.
- No se decrementa stock.

## 12. Consultas basicas

Obtener medicamento:

```text
GET http://localhost:8086/api/v1/medications/1
```

Listar medicamentos de un paciente:

```text
GET http://localhost:8086/api/v1/medications/patients/1
```

Listar medicamentos con stock bajo:

```text
GET http://localhost:8086/api/v1/medications/patients/1/low-stock
```

Listar horarios activos:

```text
GET http://localhost:8086/api/v1/medication-schedules/patients/1/active
```

Historial de dosis por medicamento:

```text
GET http://localhost:8086/api/v1/dose-administrations/medications/1
```

Summary interno para Reports:

```text
GET http://localhost:8086/api/v1/internal/medications/patients/1/summary
```

## 13. Verificar eventos publicados en RabbitMQ

RabbitMQ no muestra eventos sueltos en el exchange. Solo ves mensajes si existe una queue enlazada al routing key.

Para verificar `medication.registered`, `dose.administered`, `dose.skipped` o `stock.low`, crea una queue temporal desde RabbitMQ Management:

1. Entra a `http://localhost:15672`
2. Ve a `Queues and Streams`
3. Crea una queue, por ejemplo:

```text
debug.medication-events
```

4. Entra a la queue creada.
5. En `Bindings`, agrega bindings hacia el exchange:

```text
From exchange: medibridge.events
Routing key: medication.registered
Routing key: dose.administered
Routing key: dose.skipped
Routing key: stock.low
```

6. Ejecuta los endpoints del servicio.
7. Vuelve a `debug.medication-events`.
8. Usa `Get messages` para ver el payload.

Si la queue se creo despues de ejecutar el endpoint, no vera eventos pasados. RabbitMQ no guarda historico en el exchange; solo enruta mensajes hacia queues existentes en ese momento.

## 14. Variables de entorno relevantes

```text
PORT=8086
MEDICATION_DB_URL=jdbc:postgresql://localhost:5433/medibridge_medication
MEDICATION_DB_USERNAME=postgres
MEDICATION_DB_PASSWORD=12345678
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=medibridge
RABBITMQ_PASSWORD=medibridge
RABBITMQ_VHOST=/
IAM_JWK_SET_URI=http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

## 15. Checklist de validacion

- `medication-service` inicia en puerto `8086`.
- Swagger abre en `http://localhost:8086/swagger-ui.html`.
- Flyway crea tablas en `medibridge_medication`.
- `medication.patient-registered` existe en RabbitMQ.
- Crear paciente en Profiles sincroniza `patient_references`.
- Registrar medicamento publica `medication.registered`.
- Registrar dosis publica `dose.administered`.
- Registrar dosis decrementa stock.
- Stock bajo publica `stock.low`.
- Omitir dosis publica `dose.skipped`.
- El endpoint interno de summary responde para Reports.
