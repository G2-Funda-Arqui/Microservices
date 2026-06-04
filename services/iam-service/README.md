# IAM Service

Primer microservicio extraido del monolito MediBridge. Es responsable de usuarios, roles, login, firma JWT con RSA, exposicion JWK y publicacion del evento `user.registered` en RabbitMQ.

## Endpoints principales

- `POST /api/v1/authentication/sign-up`
- `POST /api/v1/authentication/sign-in`
- `GET /api/v1/jwks/.well-known/jwks.json`
- `GET /api/v1/internal/users/{userId}/exists`
- `GET /api/v1/internal/users/{userId}`

## Variables externas

Necesitas PostgreSQL y RabbitMQ disponibles. No hay API keys obligatorias para compilar.

- `IAM_DB_URL`: por defecto `jdbc:postgresql://localhost:5433/medibridge_iam`
- `IAM_DB_USERNAME`: por defecto `postgres`
- `IAM_DB_PASSWORD`: por defecto `12345678`
- `RABBITMQ_HOST`: por defecto `localhost`
- `RABBITMQ_PORT`: por defecto `5672`
- `RABBITMQ_USER`: por defecto `medibridge`
- `RABBITMQ_PASSWORD`: por defecto `medibridge`
- `IAM_JWT_PRIVATE_KEY`: opcional, clave RSA privada PKCS8 en PEM o Base64 DER
- `IAM_JWT_PUBLIC_KEY`: opcional, clave RSA publica X509 en PEM o Base64 DER

Si no configuras las claves RSA, el servicio genera un par efimero al iniciar. Eso sirve para desarrollo, pero en Docker/produccion debes configurar claves estables para que los JWT no se invaliden al reiniciar.

## Build

Desde la raiz del workspace:

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml -DskipTests package
```

## Run local

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```
