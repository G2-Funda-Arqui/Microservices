# Guia de ejecucion y prueba: payments-service

Esta guia asume que ya tienes `iam-service`, `profiles-service`, PostgreSQL y RabbitMQ configurados como en la guia anterior.

## 1. Que se migro

`payments-service` es el siguiente servicio segun la Fase 3 de la guia de migracion. Queda separado del monolito con estas responsabilidades:

- Gestionar planes, suscripciones, metodos de pago, facturas y transacciones.
- Validar usuarios contra `iam-service` por Feign.
- Consumir el evento `user.registered` desde RabbitMQ.
- Publicar el evento `subscription.activated` hacia RabbitMQ.
- Integrarse con Stripe para planes pagos.

Puerto local:

```text
http://localhost:8083
```

Swagger:

```text
http://localhost:8083/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8083/v3/api-docs
```

## 2. Dependencias necesarias

Antes de ejecutar `payments-service`, deben estar activos:

- PostgreSQL Docker en `localhost:5433`.
- RabbitMQ Docker en `localhost:5672`.
- `iam-service` en `localhost:8081`.

La base usada por este servicio es:

```text
medibridge_payments
```

Ya esta contemplada en `docker/postgres-init.sql`.

## 3. Variables importantes

Locales por defecto:

```text
PAYMENTS_DB_URL=jdbc:postgresql://localhost:5433/medibridge_payments
PAYMENTS_DB_USERNAME=postgres
PAYMENTS_DB_PASSWORD=12345678
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=medibridge
RABBITMQ_PASSWORD=medibridge
IAM_SERVICE_URL=http://localhost:8081
IAM_JWK_SET_URI=http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

Stripe:

```text
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET=
```

Para pruebas locales sin Stripe, usa el plan `FREE`. Ese flujo no llama a Stripe. Para planes pagos si necesitas configurar `STRIPE_SECRET_KEY`.

## 4. Ejecutar localmente con Maven

Desde la raiz del proyecto:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Ejecuta IAM:

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```

En otra terminal, ejecuta Payments:

```powershell
.\mvnw.cmd -f services/payments-service/pom.xml spring-boot:run
```

Resultado esperado:

```text
Tomcat initialized with port 8083
Started PaymentsServiceApplication
```

## 5. Ejecutar localmente con Docker

Construye la imagen:

```powershell
docker build -t medibridge/payments-service:local ./services/payments-service
```

Ejecuta el contenedor conectado a PostgreSQL, RabbitMQ e IAM:

```powershell
docker run --rm --name payments-service-local `
  --network docker_default `
  -p 8083:8083 `
  -e PAYMENTS_DB_URL=jdbc:postgresql://medibridge-postgres:5432/medibridge_payments `
  -e PAYMENTS_DB_USERNAME=postgres `
  -e PAYMENTS_DB_PASSWORD=12345678 `
  -e RABBITMQ_HOST=medibridge-rabbitmq `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e IAM_SERVICE_URL=http://iam-service-local:8081 `
  -e IAM_JWK_SET_URI=http://iam-service-local:8081/api/v1/jwks/.well-known/jwks.json `
  medibridge/payments-service:local
```

Si ejecutas IAM con Maven y Payments con Docker, `http://iam-service-local:8081` no existe dentro del contenedor. En ese caso usa `host.docker.internal`:

```text
IAM_SERVICE_URL=http://host.docker.internal:8081
IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json
```

## 6. Prueba basica completa

### 6.1 Registrar usuario en IAM

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
  "username": "payments-test-1",
  "password": "Password123",
  "roles": [
    "ROLE_USER"
  ]
}
```

Guarda el `id` retornado. Ese sera el `userId` para Payments.

### 6.2 Hacer login en IAM

Ejecuta:

```text
POST /api/v1/authentication/sign-in
```

Body:

```json
{
  "username": "payments-test-1",
  "password": "Password123"
}
```

Copia el `token`.

En Swagger de Payments:

```text
http://localhost:8083/swagger-ui.html
```

Pulsa `Authorize` y pega:

```text
Bearer <token>
```

### 6.3 Crear suscripcion gratuita

Ejecuta en Payments:

```text
POST /api/v1/subscriptions
```

Body, reemplazando `1` por el id real del usuario:

```json
{
  "userId": 1,
  "commercialLine": "FAMILY",
  "planType": "FREE",
  "billingCycle": "MONTHLY"
}
```

Respuesta esperada:

```json
{
  "id": 1,
  "userId": 1,
  "plan": {
    "commercialLine": "FAMILY",
    "planType": "FREE",
    "billingCycle": "MONTHLY"
  },
  "status": "ACTIVE"
}
```

Este flujo:

- Valida el usuario contra `iam-service`.
- Crea la suscripcion en `medibridge_payments`.
- Crea una factura pagada.
- Publica `subscription.activated` en RabbitMQ.

## 7. Verificar eventos en RabbitMQ

RabbitMQ Management:

```text
http://localhost:15672
```

Credenciales:

```text
Usuario: medibridge
Password: medibridge
```

### 7.1 Verificar consumo de `user.registered`

Cuando `payments-service` inicia, declara esta queue:

```text
payments.user-registered
```

Esta queue queda vinculada al exchange:

```text
medibridge.events
```

Routing key:

```text
user.registered
```

Para verificar:

1. Entra a `Queues and Streams`.
2. Busca `payments.user-registered`.
3. Registra un usuario nuevo en IAM.
4. Si `payments-service` esta encendido, normalmente no veras mensajes acumulados porque los consume de inmediato.
5. Revisa logs de `payments-service`; debe aparecer algo parecido a:

```text
User registration event received by payments: userId=..., username=...
```

Ademas se crea un registro en la tabla:

```sql
SELECT * FROM user_references;
```

### 7.2 Verificar publicacion de `subscription.activated`

Como IAM ya tiene su propia queue `iam.subscription-activated`, puedes verificarlo de dos formas:

- Logs de `iam-service`.
- RabbitMQ Management.

En logs de IAM debes ver:

```text
Subscription activation event received by IAM: userId=..., subscriptionId=..., status=ACTIVE
```

Si quieres ver el mensaje manualmente, crea una queue de debug antes de crear la suscripcion:

```text
Queue: debug.subscription-activated
Exchange: medibridge.events
Routing key: subscription.activated
```

Luego crea una suscripcion en Payments y abre `Get messages` en esa queue.

## 8. Endpoints principales

Subscriptions:

```text
POST /api/v1/subscriptions
POST /api/v1/subscriptions/{subscriptionId}/cancel
POST /api/v1/subscriptions/{subscriptionId}/renew
GET  /api/v1/subscriptions/users/{userId}
GET  /api/v1/subscriptions/users/{userId}/active
POST /api/v1/subscriptions/payment-methods
```

Invoices:

```text
GET /api/v1/invoices/users/{userId}
```

Stripe webhook:

```text
POST /api/v1/stripe-webhooks
```

El webhook no requiere JWT, pero valida la firma `Stripe-Signature` con `STRIPE_WEBHOOK_SECRET`.

## 9. Notas sobre Stripe

Para probar planes pagos necesitas crear/configurar fuera del proyecto:

- Cuenta de Stripe.
- `STRIPE_SECRET_KEY`.
- `STRIPE_WEBHOOK_SECRET`.

Sin esas claves, usa solamente el plan:

```text
commercialLine=FAMILY
planType=FREE
billingCycle=MONTHLY
```
