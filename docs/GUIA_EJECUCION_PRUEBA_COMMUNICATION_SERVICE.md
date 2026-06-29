# Guia de ejecucion y prueba - Communication Service

## 1. Proposito

`communication-service` gestiona comunicacion en tiempo real, chat y notificaciones in-app para MediBridge.

Este servicio se construye como microservicio nuevo porque en el monolito no existia como bounded context completo. Segun la guia de migracion, usa:

- MongoDB para persistir mensajes, salas, usuarios conectados y notificaciones.
- WebSocket/STOMP para chat y entrega en tiempo real.
- RabbitMQ para consumir eventos clinicos y de medicacion.
- Consulta interna a `profiles-service` para resolver el equipo de cuidado del paciente.
- JWT/JWK de `iam-service` para proteger endpoints REST.

Puerto local:

```text
8088
```

Swagger:

```text
http://localhost:8088/swagger-ui.html
```

Health check:

```text
http://localhost:8088/actuator/health
```

## 2. Infraestructura necesaria

Levanta la infraestructura local:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Esto levanta:

```text
PostgreSQL: localhost:5433
RabbitMQ: localhost:5672
RabbitMQ UI: http://localhost:15672
MongoDB: localhost:27017
```

Credenciales MongoDB:

```text
Username: medibridge
Password: medibridge
Database: medibridge_communication
Auth DB: admin
```

URI local:

```text
mongodb://medibridge:medibridge@localhost:27017/medibridge_communication?authSource=admin
```

## 3. Ejecutar Communication Service localmente

Desde la raiz del proyecto:

```powershell
.\mvnw.cmd -f services\communication-service\pom.xml spring-boot:run
```

Si necesitas pasar variables explicitamente:

```powershell
$env:COMMUNICATION_MONGODB_URI="mongodb://medibridge:medibridge@localhost:27017/medibridge_communication?authSource=admin"
$env:RABBITMQ_HOST="localhost"
$env:RABBITMQ_PORT="5672"
$env:RABBITMQ_USER="medibridge"
$env:RABBITMQ_PASSWORD="medibridge"
$env:IAM_JWK_SET_URI="http://localhost:8081/api/v1/jwks/.well-known/jwks.json"
$env:PROFILES_SERVICE_BASE_URL="http://localhost:8082"
.\mvnw.cmd -f services\communication-service\pom.xml spring-boot:run
```

## 4. Ejecutar Communication Service con Docker

Construir imagen:

```powershell
docker build -t medibridge-communication-service:local services/communication-service
```

Ejecutar contenedor:

```powershell
docker run --name medibridge-communication-service --rm --network docker_default -p 8088:8088 `
  -e COMMUNICATION_MONGODB_URI="mongodb://medibridge:medibridge@mongodb:27017/medibridge_communication?authSource=admin" `
  -e RABBITMQ_HOST=rabbitmq `
  -e RABBITMQ_PORT=5672 `
  -e RABBITMQ_USER=medibridge `
  -e RABBITMQ_PASSWORD=medibridge `
  -e RABBITMQ_VHOST=/ `
  -e IAM_JWK_SET_URI=http://host.docker.internal:8081/api/v1/jwks/.well-known/jwks.json `
  -e PROFILES_SERVICE_BASE_URL=http://host.docker.internal:8082 `
  medibridge-communication-service:local
```

Uso de `host.docker.internal`:

- Dentro del contenedor, `localhost` apunta al contenedor.
- Para conectarse a MongoDB, RabbitMQ e IAM levantados en tu PC se usa `host.docker.internal`.

## 5. Endpoints REST

Todos estos endpoints requieren JWT salvo Swagger, health y WebSocket.

### Enviar mensaje por REST

```text
POST /api/v1/chat/messages
```

Body:

```json
{
  "recipientUserId": 15,
  "content": "Hola, este es un mensaje de prueba"
}
```

Resultado esperado:

- Persiste un documento en `chat_messages`.
- Crea sala en `chat_rooms` si no existe.
- Envia evento WebSocket al topic:

```text
/topic/users/15/messages
```

### Consultar historial de mensajes

```text
GET /api/v1/chat/messages/{senderUserId}/{recipientUserId}
```

Ejemplo:

```text
GET /api/v1/chat/messages/10/15
```

### Registrar usuario conectado

```text
POST /api/v1/chat/users/connect
```

Body:

```json
{
  "userId": 2,
  "username": "doctor@test.com",
  "fullName": "Doctor Demo"
}
```

### Desconectar usuario

```text
POST /api/v1/chat/users/disconnect
```

Body:

```json
{
  "userId": 2,
  "username": "doctor@test.com",
  "fullName": "Doctor Demo"
}
```

### Usuarios conectados

```text
GET /api/v1/chat/users/connected
```

### Notificaciones por usuario

```text
GET /api/v1/notifications/recipients/{recipientUserId}
```

### Notificaciones no leidas

```text
GET /api/v1/notifications/recipients/{recipientUserId}/unread
```

### Marcar notificacion como leida

```text
PATCH /api/v1/notifications/{notificationId}/read
```

## 6. WebSocket/STOMP

Endpoint de conexion:

```text
ws://localhost:8088/ws
```

Si tu cliente usa SockJS:

```text
http://localhost:8088/ws
```

Prefijo para enviar mensajes:

```text
/app
```

### Enviar mensaje por WebSocket

Destino:

```text
/app/chat
```

Payload:

```json
{
  "recipientUserId": 15,
  "content": "Hola por WebSocket"
}
```

### Suscribirse a mensajes de un usuario

El usuario receptor debe suscribirse a:

```text
/topic/users/{iamUserId}/messages
```

Ejemplo para el usuario `2`:

```text
/topic/users/15/messages
```

### Suscribirse a notificaciones de un usuario

```text
/topic/users/{iamUserId}/notifications
```

Ejemplo:

```text
/topic/users/15/notifications
```

## 7. Eventos RabbitMQ que consume

Exchange:

```text
medibridge.events
```

Queues declaradas por `communication-service`:

```text
communication.alert-critical
communication.dose-administered
communication.dose-skipped
communication.stock-low
```

Routing keys:

```text
alert.critical.triggered
dose.administered
dose.skipped
stock.low
```

Al recibir estos eventos, `communication-service` consulta a `profiles-service`:

```text
GET /api/v1/internal/profiles/patients/{patientId}/care-team-members
```

Ese endpoint devuelve:

```json
{
  "patientId": 1,
  "doctorProfileIds": [2],
  "familyMemberProfileIds": [3],
  "careTeamUserIds": [10, 15]
}
```

Luego `communication-service` crea una notificacion independiente para cada usuario IAM del equipo de cuidado.

Importante: el paciente no recibe notificaciones porque no es usuario directo de la app. El `patientId` identifica al paciente como objeto clinico; los usuarios operativos son el doctor/cuidador y el familiar.

## 8. Como verificar eventos en RabbitMQ

1. Levanta RabbitMQ y `communication-service`.
2. Entra a:

```text
http://localhost:15672
```

3. Login:

```text
medibridge / medibridge
```

4. Ve a `Queues and Streams`.
5. Deben existir:

```text
communication.alert-critical
communication.dose-administered
communication.dose-skipped
communication.stock-low
```

6. Ejecuta un flujo que publique eventos:

- En `healthmonitoring-service`, genera una alerta critica para publicar `alert.critical.triggered`.
- En `medication-service`, registra dosis para publicar `dose.administered`.
- En `medication-service`, omite dosis para publicar `dose.skipped`.
- En `medication-service`, baja stock para publicar `stock.low`.

7. Si `communication-service` esta encendido, los mensajes se consumen rapido y la queue puede quedar en `Ready = 0`.
8. Verifica en MongoDB la coleccion:

```text
notifications
```

Tambien puedes consultar:

```text
GET http://localhost:8088/api/v1/notifications/recipients/{doctorIamUserId}
GET http://localhost:8088/api/v1/notifications/recipients/{familyMemberIamUserId}
```

No deberias consultar notificaciones del `patientId`, porque el paciente no es un usuario receptor.

Antes de probar eventos, verifica que el paciente tenga al menos un doctor o familiar vinculado en `profiles-service`:

```text
POST http://localhost:8082/api/v1/profiles/patients/{patientId}/doctors/{doctorProfileId}
POST http://localhost:8082/api/v1/profiles/patients/{patientId}/family-members/{familyMemberProfileId}
```

El resultado esperado es que el mismo evento haya generado notificaciones para el doctor y/o familiar asociados al paciente.

## 9. Colecciones MongoDB

`communication-service` crea estas colecciones automaticamente:

```text
chat_messages
chat_rooms
connected_users
notifications
```

## 10. Flujo completo

### Chat directo

```text
Frontend conecta a /ws
Frontend se suscribe a /topic/users/{recipientUserId}/messages
Frontend envia mensaje a /app/chat
communication-service guarda mensaje en MongoDB
communication-service envia mensaje al topic del destinatario
```

### Notificacion por evento clinico

```text
healthmonitoring-service publica alert.critical.triggered
RabbitMQ enruta hacia communication.alert-critical
communication-service consume el evento
communication-service consulta profiles-service para obtener usuarios IAM de doctores y familiares asociados al paciente
communication-service crea una Notification en MongoDB por cada destinatario
communication-service emite WebSocket a /topic/users/{recipientUserId}/notifications
```

### Notificacion por medicacion

```text
medication-service publica dose.administered / dose.skipped / stock.low
RabbitMQ enruta hacia queues de communication
communication-service consulta el equipo de cuidado del paciente en profiles-service
communication-service crea notificaciones para doctores y familiares asociados
communication-service emite WebSocket a cada destinatario
```

## 11. Comportamiento si Profiles Service no esta disponible

Si `profiles-service` esta apagado o no responde, `communication-service` consume el evento pero no crea notificaciones, porque no puede resolver usuarios notificables.

En ese caso aplica este comportamiento:

```text
careTeamUserIds = []
```

Esto evita crear notificaciones incorrectas para el paciente. Para pruebas reales, levanta `profiles-service` y vincula al menos un doctor o familiar al paciente antes de publicar eventos.

