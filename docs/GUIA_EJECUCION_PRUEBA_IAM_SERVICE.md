# Guia de ejecucion y prueba: iam-service

Esta guia asume Windows, PowerShell y que aun no tienes Docker instalado.

## 1. Que vas a levantar

Para probar `iam-service` necesitas tres piezas:

- `iam-service`: aplicacion Spring Boot en el puerto `8081`.
- PostgreSQL: base de datos `medibridge_iam` en el puerto externo `5433`.
- RabbitMQ: broker de eventos en el puerto `5672`, con consola web en `15672`.

Las claves JWT se dejan como estan ahora: si no configuras `IAM_JWT_PRIVATE_KEY` e `IAM_JWT_PUBLIC_KEY`, el servicio genera claves RSA efimeras al iniciar. Para desarrollo esta bien. Si reinicias el servicio, los tokens anteriores dejan de validar.

## 2. Instalar Docker Desktop

Docker Desktop permite ejecutar PostgreSQL y RabbitMQ sin instalarlos manualmente en Windows.

1. Instala o verifica WSL:

```powershell
wsl --version
```

Si no existe o falla, instala WSL:

```powershell
wsl --install
```

Reinicia Windows si el instalador lo pide.

2. Descarga Docker Desktop para Windows desde la documentacion oficial:

```text
https://docs.docker.com/desktop/setup/install/windows-install/
```

3. Durante la instalacion, usa WSL 2 como backend.

4. Abre Docker Desktop desde el menu Inicio y espera a que diga que esta corriendo.

5. Verifica desde PowerShell:

```powershell
docker --version
docker compose version
```

Referencias oficiales:

- Docker Desktop para Windows: https://docs.docker.com/desktop/setup/install/windows-install/
- Docker Desktop con WSL 2: https://docs.docker.com/desktop/features/wsl/

## 3. Levantar PostgreSQL y RabbitMQ

Desde la raiz del proyecto:

```powershell
cd C:\Users\USER\Downloads\medibridge\medibridge
docker compose -f docker/docker-compose.yml up -d
```

Verifica que los contenedores esten arriba:

```powershell
docker ps
```

Debes ver:

- `medibridge-postgres`
- `medibridge-rabbitmq`

RabbitMQ tiene consola web:

```text
http://localhost:15672
```

Credenciales:

- Usuario: `medibridge`
- Password: `medibridge`

PostgreSQL queda asi:

- Host: `localhost`
- Puerto: `5433`
- Usuario: `postgres`
- Password: `12345678`
- Base IAM: `medibridge_iam`

## 3.1 Conectar pgAdmin4 a PostgreSQL Docker

Si usas pgAdmin4 instalado en Windows, crea un server con estos datos:

Pestana `General`:

- Name: `MediBridge Local Docker`

Pestana `Connection`:

- Host name/address: `127.0.0.1`
- Port: `5433`
- Maintenance database: `postgres`
- Username: `postgres`
- Password: `12345678`
- Save password: activado

Si pgAdmin muestra `password fallo para el usuario postgres`, ejecuta este reset desde la raiz del proyecto:

```powershell
docker exec medibridge-postgres psql -U postgres -d medibridge_iam -c "ALTER USER postgres WITH PASSWORD '12345678';"
```

Luego en pgAdmin elimina el server creado y vuelvelo a crear. pgAdmin a veces conserva una contrasena antigua aunque cambies campos en la ventana de conexion.

Si pgAdmin4 tambien corre en Docker, no uses `127.0.0.1` como host. En ese caso el host debe ser el nombre del servicio o contenedor de PostgreSQL, por ejemplo:

```text
medibridge-postgres
```

## 4. Compilar iam-service

Desde la raiz del proyecto:

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml -DskipTests package
```

Resultado esperado:

```text
BUILD SUCCESS
```

## 5. Ejecutar iam-service

Con PostgreSQL y RabbitMQ ya corriendo:

```powershell
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```

El servicio debe iniciar en:

```text
http://localhost:8081
```

Si ves un warning sobre `IAM_JWT_PRIVATE_KEY/IAM_JWT_PUBLIC_KEY`, es esperado en desarrollo.

## 6. Pruebas HTTP basicas

Puedes hacer las pruebas desde Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

El JSON OpenAPI esta disponible en:

```text
http://localhost:8081/v3/api-docs
```

### 6.1 Health check

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8081/actuator/health
```

Respuesta esperada:

```json
{"status":"UP"}
```

### 6.2 Ver JWK publico

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

Debe devolver un objeto JSON con `keys`.

### 6.3 Registrar usuario

```powershell
$body = @{
  username = "admin@medibridge.com"
  password = "Password123"
  roles = @("ROLE_ADMIN")
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8081/api/v1/authentication/sign-up `
  -ContentType "application/json" `
  -Body $body
```

Respuesta esperada: un usuario con `id`, `username` y `roles`.

Nota: al registrar usuario, IAM publica el evento RabbitMQ `user.registered`.

### 6.4 Login

```powershell
$body = @{
  username = "admin@medibridge.com"
  password = "Password123"
} | ConvertTo-Json

$auth = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8081/api/v1/authentication/sign-in `
  -ContentType "application/json" `
  -Body $body

$auth
```

La respuesta debe incluir `token`.

Guarda el token:

```powershell
$token = $auth.token
```

### 6.5 Probar endpoint protegido

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri http://localhost:8081/api/v1/users `
  -Headers @{ Authorization = "Bearer $token" }
```

Debe devolver la lista de usuarios.

### 6.6 Probar endpoint interno para futuros microservicios

Reemplaza `1` por el id que te devolvio el registro:

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8081/api/v1/internal/users/1/exists
```

Respuesta esperada:

```text
True
```

## 7. Verificar evento en RabbitMQ

Abre:

```text
http://localhost:15672
```

Entra con:

- Usuario: `medibridge`
- Password: `medibridge`

Ve a `Exchanges` y busca:

```text
medibridge.events
```

Importante: `medibridge.events` es un exchange, no una bandeja de mensajes. RabbitMQ no guarda historial en el exchange. Si publicas `user.registered` y no existe una queue vinculada a ese routing key, el mensaje se descarta.

Como `payments-service` aun no fue migrado, todavia no hay consumidor real para `user.registered`. Para verificar el evento manualmente, crea una queue de debug antes de hacer el `sign-up`.

### 7.1 Crear queue de debug

En RabbitMQ Management:

1. Entra a `Queues and Streams`.
2. Abre `Add a new queue`.
3. Completa:

```text
Type: Classic
Name: debug.user-registered
Durability: Durable
```

4. Clic en `Add queue`.

### 7.2 Vincular la queue al exchange

1. Entra a `Exchanges`.
2. Abre `medibridge.events`.
3. Busca la seccion `Bindings`.
4. En `Add binding from this exchange`, completa:

```text
To queue: debug.user-registered
Routing key: user.registered
```

5. Clic en `Bind`.

### 7.3 Publicar el evento

Ahora registra un usuario nuevo desde Swagger:

```text
POST /api/v1/authentication/sign-up
```

Ejemplo:

```json
{
  "username": "rabbit-test-1",
  "password": "Password123",
  "roles": [
    "ROLE_USER"
  ]
}
```

Si ya registraste ese username antes, usa otro. El username debe ser unico.

### 7.4 Ver el mensaje

1. Entra a `Queues and Streams`.
2. Abre `debug.user-registered`.
3. Debes ver `Ready: 1` o un numero mayor.
4. Abre `Get messages`.
5. Usa:

```text
Ack mode: Ack message requeue false
Messages: 1
Encoding: Auto string/base64
```

6. Clic en `Get Message(s)`.

Debes ver un payload parecido a:

```json
{
  "userId": 3,
  "username": "rabbit-test-1",
  "occurredAt": "2026-06-04T...",
  "version": 1
}
```

Si no aparece nada:

- Verifica que la queue fue vinculada antes de hacer el `sign-up`.
- Verifica que el binding use exactamente `user.registered`.
- Repite el `sign-up` con un username nuevo.
- Revisa que `iam-service` siga ejecutandose.

## 8. Detener servicios

Para detener PostgreSQL y RabbitMQ:

```powershell
docker compose -f docker/docker-compose.yml down
```

Para detener y borrar tambien los datos guardados:

```powershell
docker compose -f docker/docker-compose.yml down -v
```

Usa `down -v` solo si quieres reiniciar la base desde cero.

## 9. Problemas comunes

### Docker no responde

Abre Docker Desktop y espera a que termine de iniciar. Luego prueba:

```powershell
docker ps
```

### Puerto 5432 ocupado

Ya tienes PostgreSQL local usando el puerto `5432`. Opciones:

- Detener tu PostgreSQL local.
- El proyecto ya usa el puerto externo `5433` para evitar conflicto con PostgreSQL local. En pgAdmin usa puerto `5433`, no `5432`.

### Puerto 8081 ocupado

Ejecuta IAM en otro puerto:

```powershell
$env:SERVER_PORT="8082"
.\mvnw.cmd -f services/iam-service/pom.xml spring-boot:run
```

### Error de conexion a PostgreSQL

Verifica contenedor:

```powershell
docker ps
```

Verifica logs:

```powershell
docker logs medibridge-postgres
```

### Error de conexion a RabbitMQ

Verifica logs:

```powershell
docker logs medibridge-rabbitmq
```

Y abre la consola:

```text
http://localhost:15672
```

## 10. Flujo completo de IAM

`iam-service` es la fuente de verdad de usuarios, roles y autenticacion. En esta primera fase de migracion cumple cuatro responsabilidades:

- Registrar usuarios.
- Autenticar usuarios.
- Emitir JWT firmados con RSA.
- Publicar el evento `user.registered` para futuros microservicios.

### 10.1 Registro

Endpoint:

```text
POST /api/v1/authentication/sign-up
```

Que hace:

- Valida que el username no exista.
- Hashea el password con BCrypt.
- Asocia roles existentes, por ejemplo `ROLE_USER` o `ROLE_ADMIN`.
- Guarda el usuario en PostgreSQL.
- Publica `user.registered` en RabbitMQ con routing key `user.registered`.

Respuesta:

```json
{
  "id": 1,
  "username": "fab",
  "roles": [
    "ROLE_USER"
  ]
}
```

### 10.2 Login

Endpoint:

```text
POST /api/v1/authentication/sign-in
```

Que hace:

- Busca el usuario por username.
- Compara el password enviado contra el hash BCrypt.
- Genera un JWT firmado con clave privada RSA.

Respuesta:

```json
{
  "id": 1,
  "username": "fab",
  "token": "eyJ..."
}
```

### 10.3 JWK publico

Endpoint:

```text
GET /api/v1/jwks/.well-known/jwks.json
```

Que hace:

- Expone la clave publica RSA en formato JWK.
- Permite que otros microservicios validen JWT sin conocer la clave privada.

Este endpoint no es para el usuario final. Es infraestructura de seguridad distribuida.

### 10.4 Listar usuarios

Endpoint protegido:

```text
GET /api/v1/users
```

Que hace:

- Devuelve los usuarios registrados.
- Requiere header:

```text
Authorization: Bearer <token>
```

En Swagger:

1. Ejecuta `sign-in`.
2. Copia el `token`.
3. Clic en `Authorize`.
4. Pega:

```text
Bearer <token>
```

5. Ejecuta `GET /api/v1/users`.

### 10.5 Obtener usuario por id

Endpoint protegido:

```text
GET /api/v1/users/{userId}
```

Que hace:

- Devuelve el usuario solicitado.
- Requiere JWT.

### 10.6 Listar roles

Endpoint protegido:

```text
GET /api/v1/roles
```

Que hace:

- Devuelve los roles inicializados al arrancar el servicio.
- Actualmente existen:

```text
ROLE_USER
ROLE_ADMIN
```

### 10.7 Endpoints internos para microservicios

Endpoint:

```text
GET /api/v1/internal/users/{userId}/exists
```

Que hace:

- Devuelve `true` o `false`.
- Sera usado por futuros servicios como `profiles-service` para validar que un usuario existe antes de crear un perfil.

Endpoint:

```text
GET /api/v1/internal/users/{userId}
```

Que hace:

- Devuelve una representacion basica del usuario.
- Sirve para consultas sincronas internas via Feign.

En esta fase academica los endpoints internos estan abiertos dentro de la red local. En una version mas realista deberian protegerse con JWT de servicio a servicio, mTLS o reglas de red.
