# Profiles Service

Microservicio de perfiles de MediBridge. Es responsable de pacientes, doctores, familiares y relaciones de cuidado.

## Endpoints principales

- `POST /api/v1/profiles/patients`
- `GET /api/v1/profiles/patients/{patientId}`
- `POST /api/v1/profiles/doctors`
- `GET /api/v1/profiles/doctors/{doctorProfileId}`
- `POST /api/v1/profiles/family-members`
- `GET /api/v1/profiles/family-members/{familyMemberProfileId}`
- `POST /api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}`
- `POST /api/v1/profiles/patients/{patientId}/family-members/{familyMemberProfileId}`
- `GET /api/v1/internal/profiles/patients/{patientId}/exists`
- `GET /api/v1/internal/profiles/doctors/{doctorId}/can-attend/{patientId}`
- `GET /api/v1/internal/profiles/family-members/{familyMemberId}/can-visit/{patientId}`

## Integraciones

- Valida usuarios contra `iam-service` por Feign.
- Valida JWT usando el JWK público de `iam-service`.
- Publica eventos RabbitMQ en `medibridge.events`:
  - `patient.registered`
  - `doctor.assigned.patient`
  - `family.assigned.patient`

## Variables externas

- `PROFILES_DB_URL`: por defecto `jdbc:postgresql://localhost:5433/medibridge_profiles`
- `PROFILES_DB_USERNAME`: por defecto `postgres`
- `PROFILES_DB_PASSWORD`: por defecto `12345678`
- `IAM_SERVICE_URL`: por defecto `http://localhost:8081`
- `IAM_JWK_SET_URI`: por defecto `http://localhost:8081/api/v1/jwks/.well-known/jwks.json`
- `RABBITMQ_HOST`: por defecto `localhost`
- `RABBITMQ_PORT`: por defecto `5672`
- `RABBITMQ_USER`: por defecto `medibridge`
- `RABBITMQ_PASSWORD`: por defecto `medibridge`

## Build

Desde la raiz del workspace:

```powershell
.\mvnw.cmd -f services/profiles-service/pom.xml -DskipTests package
```

## Run local

```powershell
.\mvnw.cmd -f services/profiles-service/pom.xml spring-boot:run
```