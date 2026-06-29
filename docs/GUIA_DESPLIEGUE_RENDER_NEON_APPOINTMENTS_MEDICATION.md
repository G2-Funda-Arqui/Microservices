# Guia de despliegue: Appointments + Medication en Render y NeonDB

Esta guia despliega:

- `appointments-service` en Render.
- `medication-service` en Render.
- PostgreSQL administrado en NeonDB.
- RabbitMQ administrado externo, recomendado CloudAMQP, porque ambos servicios consumen/publican eventos.

## 1. Estado del proyecto

Los dos servicios ya estan listos para Render:

- Usan `server.port: ${PORT:${SERVER_PORT:...}}`, asi que aceptan el puerto dinamico que Render inyecta.
- Tienen Dockerfile multi-stage dentro de cada servicio.
- Usan Flyway para crear tablas en PostgreSQL.
- Compilan correctamente con Java 21.

Build validado localmente:

```powershell
.\mvnw.cmd -f services\appointments-service\pom.xml -DskipTests package
.\mvnw.cmd -f services\medication-service\pom.xml -DskipTests package
```

## 2. Dependencias obligatorias

Antes de desplegar estos dos servicios, deberias tener desplegados o disponibles:

- `iam-service`, porque ambos validan JWT con `IAM_JWK_SET_URI`.
- `profiles-service`, porque ambos usan referencias de pacientes y relaciones publicadas desde Profiles.
- RabbitMQ externo, porque los servicios declaran listeners y dependen de eventos como `patient.registered`.
- Dos databases NeonDB: `medibridge_appointments` y `medibridge_medication`.

Si ya seguiste la guia de IAM + Profiles, reutiliza:

```text
IAM_JWK_SET_URI=https://<iam-service>.onrender.com/api/v1/jwks/.well-known/jwks.json
PROFILES_SERVICE_URL=https://<profiles-service>.onrender.com
RABBITMQ_HOST=<host-cloudamqp>
RABBITMQ_PORT=<puerto-amqp>
RABBITMQ_USER=<usuario>
RABBITMQ_PASSWORD=<password>
RABBITMQ_VHOST=<vhost>
```

## 3. Crear databases en NeonDB

Crea dos databases dentro de tu proyecto Neon, o dos proyectos separados:

```text
medibridge_appointments
medibridge_medication
```

Neon muestra connection strings tipo:

```text
postgresql://USER:PASSWORD@HOST/medibridge_appointments?sslmode=require&channel_binding=require
postgresql://USER:PASSWORD@HOST/medibridge_medication?sslmode=require&channel_binding=require
```

Para Spring Boot no pegues la URL tal cual. Convierte a JDBC y separa usuario/password.

Appointments:

```text
APPOINTMENTS_DB_URL=jdbc:postgresql://HOST:5432/medibridge_appointments?sslmode=require
APPOINTMENTS_DB_USERNAME=USER
APPOINTMENTS_DB_PASSWORD=PASSWORD
```

Medication:

```text
MEDICATION_DB_URL=jdbc:postgresql://HOST:5432/medibridge_medication?sslmode=require
MEDICATION_DB_USERNAME=USER
MEDICATION_DB_PASSWORD=PASSWORD
```

Notas:

- Si Neon te da host `...-pooler...`, puedes usarlo para apps web con muchas conexiones.
- Conserva `sslmode=require`, porque Neon requiere conexiones TLS/SSL.
- Si tu password tiene caracteres especiales y lo pones separado en `*_DB_PASSWORD`, no necesitas URL-encodearlo.

## 4. Opcion A: Deploy manual en Render

### 4.1 Appointments Service

Crea un `Web Service` en Render con:

```text
Name: medibridge-appointments-service
Environment / Runtime: Docker
Root Directory: services/appointments-service
Dockerfile Path: Dockerfile
Health Check Path: /actuator/health
```

Variables:

```text
APPOINTMENTS_DB_URL=jdbc:postgresql://HOST:5432/medibridge_appointments?sslmode=require
APPOINTMENTS_DB_USERNAME=USER
APPOINTMENTS_DB_PASSWORD=PASSWORD
JPA_DDL_AUTO=validate
RABBITMQ_HOST=<RABBITMQ_HOST>
RABBITMQ_PORT=<RABBITMQ_PORT>
RABBITMQ_USER=<RABBITMQ_USER>
RABBITMQ_PASSWORD=<RABBITMQ_PASSWORD>
RABBITMQ_VHOST=<RABBITMQ_VHOST>
IAM_JWK_SET_URI=https://<iam-service>.onrender.com/api/v1/jwks/.well-known/jwks.json
PROFILES_SERVICE_URL=https://<profiles-service>.onrender.com
```

Despues del deploy, prueba:

```text
https://medibridge-appointments-service.onrender.com/actuator/health
https://medibridge-appointments-service.onrender.com/swagger-ui.html
```

### 4.2 Medication Service

Crea otro `Web Service` en Render con:

```text
Name: medibridge-medication-service
Environment / Runtime: Docker
Root Directory: services/medication-service
Dockerfile Path: Dockerfile
Health Check Path: /actuator/health
```

Variables:

```text
MEDICATION_DB_URL=jdbc:postgresql://HOST:5432/medibridge_medication?sslmode=require
MEDICATION_DB_USERNAME=USER
MEDICATION_DB_PASSWORD=PASSWORD
JPA_DDL_AUTO=validate
RABBITMQ_HOST=<RABBITMQ_HOST>
RABBITMQ_PORT=<RABBITMQ_PORT>
RABBITMQ_USER=<RABBITMQ_USER>
RABBITMQ_PASSWORD=<RABBITMQ_PASSWORD>
RABBITMQ_VHOST=<RABBITMQ_VHOST>
IAM_JWK_SET_URI=https://<iam-service>.onrender.com/api/v1/jwks/.well-known/jwks.json
PROFILES_SERVICE_URL=https://<profiles-service>.onrender.com
```

Despues del deploy, prueba:

```text
https://medibridge-medication-service.onrender.com/actuator/health
https://medibridge-medication-service.onrender.com/swagger-ui.html
```

## 5. Opcion B: Deploy con Blueprint `render.yaml`

Agregue `render.yaml` en la raiz del repo con los dos servicios.

En Render:

1. Ve a `Blueprints`.
2. Conecta el repo.
3. Selecciona el `render.yaml` de la raiz.
4. Render te pedira los valores marcados como `sync: false`.
5. Pega las variables de NeonDB, RabbitMQ, IAM y Profiles.
6. Aplica el blueprint.

No se guardaron secretos en Git. Las variables sensibles quedan como prompts en Render.

## 6. Orden recomendado de despliegue

```text
1. NeonDB: crear medibridge_appointments y medibridge_medication
2. RabbitMQ externo: CloudAMQP u otro broker accesible desde Render
3. iam-service
4. profiles-service
5. appointments-service
6. medication-service
```

Motivo:

- Appointments y Medication validan tokens usando JWKS de IAM.
- Ambos necesitan eventos de Profiles para poblar referencias locales.
- Si creas pacientes/relaciones antes de levantar estos servicios, los eventos pueden perderse para estos consumidores y sus tablas locales quedaran vacias.

## 7. Validacion despues del deploy

### 7.1 Health checks

```text
GET https://<appointments-url>/actuator/health
GET https://<medication-url>/actuator/health
```

Respuesta esperada:

```json
{"status":"UP"}
```

### 7.2 Verificar Flyway en Neon

En Neon SQL Editor ejecuta:

Appointments:

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
SELECT COUNT(*) FROM appointments;
SELECT COUNT(*) FROM patient_references;
```

Medication:

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
SELECT COUNT(*) FROM medications;
SELECT COUNT(*) FROM patient_references;
```

### 7.3 Validar JWT

Abre:

```text
https://<iam-service>.onrender.com/api/v1/jwks/.well-known/jwks.json
```

Debe responder JSON con `keys`.

Luego entra a Swagger de cada servicio y autoriza con:

```text
Bearer <token-de-iam>
```

## 8. Flujo minimo de prueba

1. En IAM, registra usuario y haz login.
2. En Profiles, crea paciente y relacion doctor/familiar usando el token.
3. Con Appointments y Medication encendidos, espera unos segundos para que consuman `patient.registered` y relaciones.
4. En Appointments, agenda cita medica o visita familiar.
5. En Medication, registra medicamento para el `patientId` creado.

Endpoints utiles:

```text
Appointments Swagger: https://<appointments-url>/swagger-ui.html
Medication Swagger: https://<medication-url>/swagger-ui.html
```

## 9. Problemas comunes

### El servicio no levanta por PostgreSQL

Revisa que la URL sea JDBC:

```text
jdbc:postgresql://HOST:5432/DATABASE?sslmode=require
```

Incorrecto:

```text
postgresql://USER:PASSWORD@HOST/DATABASE?sslmode=require
jdbc:postgresql://USER:PASSWORD@HOST/DATABASE?sslmode=require
```

Correcto:

```text
*_DB_URL=jdbc:postgresql://HOST:5432/DATABASE?sslmode=require
*_DB_USERNAME=USER
*_DB_PASSWORD=PASSWORD
```

### Health check falla por RabbitMQ

Configura RabbitMQ externo. Render no provee RabbitMQ administrado nativo para este caso. Usa CloudAMQP o un broker accesible publicamente.

### Los endpoints dicen que el paciente no existe

El servicio depende de `patient_references`, que se llena por eventos RabbitMQ. Crea un paciente nuevo en Profiles mientras Appointments/Medication estan encendidos, o inserta referencias manualmente solo para pruebas.

### JWT invalido

Verifica:

```text
IAM_JWK_SET_URI=https://<iam-service>.onrender.com/api/v1/jwks/.well-known/jwks.json
```

Si IAM usa claves efimeras y se reinicio, los tokens anteriores dejan de validar. Vuelve a hacer login.

## 10. Fuentes oficiales consultadas

- Render Docker: https://render.com/docs/docker
- Render Blueprint YAML: https://render.com/docs/blueprint-spec
- Neon conectar apps: https://neon.com/docs/connect/connect-from-any-app
