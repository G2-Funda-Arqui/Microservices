# Guia de despliegue: IAM + Profiles en Render, Neon y CloudAMQP

Esta guia despliega:

- `iam-service` en Render.
- `profiles-service` en Render.
- PostgreSQL administrado en Neon.
- RabbitMQ administrado en CloudAMQP.

## 1. Arquitectura objetivo

```text
Cliente / Swagger
   |
   v
iam-service en Render
   |
   +--> Neon PostgreSQL: medibridge_iam
   +--> CloudAMQP: publica user.registered
   |
   +--> expone JWK publico

profiles-service en Render
   |
   +--> Neon PostgreSQL: medibridge_profiles
   +--> CloudAMQP: eventos de profiles
   +--> llama a iam-service por REST/Feign
   +--> valida JWT usando JWK de iam-service
```

## 2. Cambios ya preparados en el proyecto

Los Dockerfiles de ambos servicios son multi-stage. Render puede construir la imagen directamente desde GitHub sin que subas la carpeta `target`.

Tambien se ajusto el puerto para Render:

```yaml
server:
  port: ${PORT:${SERVER_PORT:8081}}
```

Render inyecta `PORT` automaticamente. Localmente puedes seguir usando `SERVER_PORT` o el valor por defecto.

## 3. Requisitos previos

Necesitas:

- Cuenta en Render.
- Cuenta en Neon.
- Cuenta en CloudAMQP.
- Repositorio en GitHub con este proyecto.
- Dockerfiles en:

```text
services/iam-service/Dockerfile
services/profiles-service/Dockerfile
```

## 4. Crear RabbitMQ en CloudAMQP

1. Entra a CloudAMQP.
2. Crea una instancia RabbitMQ.
3. Elige el plan que prefieras para pruebas.
4. Cuando la instancia este creada, entra al dashboard.
5. Copia estos datos:

```text
Host
Port
User
Password
Vhost
```

CloudAMQP tambien suele mostrar una URL completa tipo:

```text
amqps://user:password@host/vhost
```

Para este proyecto usaremos las variables separadas:

```text
RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USER
RABBITMQ_PASSWORD
RABBITMQ_VHOST
```

Si CloudAMQP te da puerto TLS `5671`, puedes probar con ese puerto. Si hay problemas TLS, usa el puerto AMQP no TLS que indique tu dashboard, normalmente `5672`.

## 5. Crear PostgreSQL en Neon

Recomendacion simple: crea un proyecto Neon y dentro crea dos databases, una para cada microservicio:

```text
medibridge_iam
medibridge_profiles
```

Tambien puedes crear dos proyectos Neon separados, pero para esta etapa un solo proyecto con dos databases es suficiente.

### 5.1 Crear proyecto Neon

1. Entra a Neon.
2. Crea un proyecto PostgreSQL.
3. Crea o usa un rol/usuario para la conexion.
4. Crea las databases:

```text
medibridge_iam
medibridge_profiles
```

5. En `Connect`, copia el connection string.

Neon suele entregar una URL de este tipo:

```text
postgresql://USER:PASSWORD@HOST/medibridge_iam?sslmode=require
```

o:

```text
postgresql://USER:PASSWORD@HOST/medibridge_profiles?sslmode=require
```

### 5.2 Puerto en Neon

Si el connection string de Neon no muestra puerto, usa manualmente:

```text
5432
```

PostgreSQL usa `5432` por defecto.

## 6. Desplegar iam-service en Render

1. En Render, crea un nuevo `Web Service`.
2. Conecta tu repositorio GitHub.
3. Configura:

```text
Name: medibridge-iam-service
Environment: Docker
Root Directory: services/iam-service
Dockerfile Path: Dockerfile
```

4. Agrega variables de entorno.

### Variables de IAM

Si usas URL JDBC manual:

```text
IAM_DB_URL=jdbc:postgresql://<IAM_DB_HOST>:<IAM_DB_PORT>/<IAM_DB_NAME>
IAM_DB_USERNAME=<IAM_DB_USER>
IAM_DB_PASSWORD=<IAM_DB_PASSWORD>
```

Ejemplo:

```text
IAM_DB_URL=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/medibridge_iam
```

### 6.1.1 Como convertir la URL de Neon a variables Spring

Neon te muestra datos parecidos a estos:

```text
Connection string: postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_iam?sslmode=require
Username: neondb_owner
Password: PASSWORD
Database: medibridge_iam
Host: ep-example.us-east-2.aws.neon.tech
```

Para `iam-service`, NO pegues directamente el connection string de Neon en `IAM_DB_URL`, porque empieza con `postgresql://` y Spring Boot necesita `jdbc:postgresql://`.

Tampoco pongas usuario y password dentro del JDBC URL.

Incorrecto:

```text
IAM_DB_URL=postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_iam?sslmode=require
IAM_DB_URL=jdbc:postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_iam?sslmode=require
IAM_DB_URL=jdbc:postgresql://@ep-example.us-east-2.aws.neon.tech/medibridge_iam?sslmode=require
```

Correcto:

```text
IAM_DB_URL=jdbc:postgresql://ep-example.us-east-2.aws.neon.tech:5432/medibridge_iam?sslmode=require
IAM_DB_USERNAME=neondb_owner
IAM_DB_PASSWORD=PASSWORD
```

Regla:

```text
IAM_DB_URL=jdbc:postgresql://HOST:PORT/DATABASE
IAM_DB_USERNAME=Username de Neon
IAM_DB_PASSWORD=Password de Neon
```

Como Render se conecta a Neon desde fuera de Neon, conserva:

```text
?sslmode=require
```

RabbitMQ:

```text
RABBITMQ_HOST=<CLOUDAMQP_HOST>
RABBITMQ_PORT=5672
RABBITMQ_USER=<CLOUDAMQP_USER>
RABBITMQ_PASSWORD=<CLOUDAMQP_PASSWORD>
RABBITMQ_VHOST=<CLOUDAMQP_VHOST>
```

JWT:

```text
IAM_JWT_ISSUER=medibridge-iam
IAM_JWT_KEY_ID=medibridge-iam-rsa-1
IAM_JWT_EXPIRATION_DAYS=7
```

Puedes dejar sin configurar:

```text
IAM_JWT_PRIVATE_KEY
IAM_JWT_PUBLIC_KEY
```

En ese caso IAM generara claves efimeras. Para demo funciona, pero si Render reinicia el servicio, los tokens anteriores dejan de validar.

5. Despliega el servicio.

6. Cuando termine, copia la URL publica. Sera parecida a:

```text
https://medibridge-iam-service.onrender.com
```

Guarda esta URL. Profiles la necesitara.

## 7. Probar iam-service desplegado

Health:

```text
https://medibridge-iam-service.onrender.com/actuator/health
```

Swagger:

```text
https://medibridge-iam-service.onrender.com/swagger-ui.html
```

JWK:

```text
https://medibridge-iam-service.onrender.com/api/v1/jwks/.well-known/jwks.json
```

Prueba:

1. `POST /api/v1/authentication/sign-up`
2. `POST /api/v1/authentication/sign-in`
3. Copia el token.
4. Autoriza Swagger con:

```text
Bearer <token>
```

5. Ejecuta `GET /api/v1/users`.

## 8. Desplegar profiles-service en Render

1. Crea otro `Web Service`.
2. Conecta el mismo repo.
3. Configura:

```text
Name: medibridge-profiles-service
Environment: Docker
Root Directory: services/profiles-service
Dockerfile Path: Dockerfile
```

### Variables de Profiles

Base de datos:

```text
PROFILES_DB_URL=jdbc:postgresql://<PROFILES_DB_HOST>:<PROFILES_DB_PORT>/<PROFILES_DB_NAME>
PROFILES_DB_USERNAME=<PROFILES_DB_USER>
PROFILES_DB_PASSWORD=<PROFILES_DB_PASSWORD>
```

Para `profiles-service` aplica la misma regla que IAM.

Si Neon te da:

```text
Connection string: postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_profiles?sslmode=require
Username: neondb_owner
Password: PASSWORD
Database: medibridge_profiles
Host: ep-example.us-east-2.aws.neon.tech
```

Debes configurar:

```text
PROFILES_DB_URL=jdbc:postgresql://ep-example.us-east-2.aws.neon.tech:5432/medibridge_profiles?sslmode=require
PROFILES_DB_USERNAME=neondb_owner
PROFILES_DB_PASSWORD=PASSWORD
```

No configures:

```text
PROFILES_DB_URL=postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_profiles?sslmode=require
PROFILES_DB_URL=jdbc:postgresql://neondb_owner:PASSWORD@ep-example.us-east-2.aws.neon.tech/medibridge_profiles?sslmode=require
```

RabbitMQ:

```text
RABBITMQ_HOST=<CLOUDAMQP_HOST>
RABBITMQ_PORT=5672
RABBITMQ_USER=<CLOUDAMQP_USER>
RABBITMQ_PASSWORD=<CLOUDAMQP_PASSWORD>
RABBITMQ_VHOST=<CLOUDAMQP_VHOST>
```

Conexion con IAM:

```text
IAM_SERVICE_URL=https://medibridge-iam-service.onrender.com
IAM_JWK_SET_URI=https://medibridge-iam-service.onrender.com/api/v1/jwks/.well-known/jwks.json
```

Importante: usa la URL real que Render te dio para `iam-service`.

4. Despliega.

## 9. Probar profiles-service desplegado

Health:

```text
https://medibridge-profiles-service.onrender.com/actuator/health
```

Swagger:

```text
https://medibridge-profiles-service.onrender.com/swagger-ui.html
```

Flujo de prueba:

1. En IAM, registra usuario.
2. En IAM, haz login.
3. Copia el token.
4. En Swagger de Profiles, clic en `Authorize`.
5. Pega:

```text
Bearer <token>
```

6. Ejecuta un endpoint de Profiles que requiera usuario existente.

Profiles validara el JWT usando:

```text
IAM_JWK_SET_URI
```

Y consultara IAM usando:

```text
IAM_SERVICE_URL
```

## 10. Verificar eventos en CloudAMQP

CloudAMQP te da una consola RabbitMQ Management.

1. Entra a CloudAMQP.
2. Abre tu instancia.
3. Entra al panel de RabbitMQ Management.
4. Ve a `Exchanges`.
5. Busca:

```text
medibridge.events
```

Recuerda: un exchange no guarda historial. Para ver mensajes debes crear una queue de debug antes de publicar.

Queue de debug para IAM:

```text
Queue: debug.user-registered
Exchange: medibridge.events
Routing key: user.registered
```

Luego haz un `sign-up` nuevo en IAM. Despues entra a la queue y usa `Get messages`.

## 11. Orden correcto de despliegue

Usa este orden:

```text
1. CloudAMQP
2. Neon PostgreSQL con databases `medibridge_iam` y `medibridge_profiles`
3. iam-service
4. profiles-service
```

Profiles depende de IAM para:

- JWK.
- Validacion/consulta de usuarios.

Por eso IAM debe estar desplegado antes que Profiles.

## 12. Problemas comunes

### Render dice que el servicio no escucha el puerto correcto

Verifica que `application.yml` use:

```yaml
server:
  port: ${PORT:${SERVER_PORT:8081}}
```

IAM y Profiles ya estan configurados asi.

### Profiles no valida JWT

Revisa:

```text
IAM_JWK_SET_URI=https://<iam-url>/api/v1/jwks/.well-known/jwks.json
```

Abre esa URL en navegador. Debe devolver JSON con `keys`.

### Profiles no puede llamar a IAM

Revisa:

```text
IAM_SERVICE_URL=https://<iam-url>
```

Sin slash final.

### Error de PostgreSQL

Verifica que la URL sea JDBC:

```text
jdbc:postgresql://host:port/database
```

No pegues directamente una URL `postgres://...` en `IAM_DB_URL` o `PROFILES_DB_URL`, porque Spring JDBC espera formato `jdbc:postgresql://...`.

Tampoco incluyas usuario y password dentro de `IAM_DB_URL` o `PROFILES_DB_URL`.

Si ves este error:

```text
JDBC URL invalid port number
Driver org.postgresql.Driver claims to not accept jdbcUrl
```

probablemente configuraste algo como:

```text
jdbc:postgresql://USER:PASSWORD@HOST/DATABASE
```

La forma correcta es:

```text
SERVICE_DB_URL=jdbc:postgresql://HOST:5432/DATABASE
SERVICE_DB_USERNAME=USER
SERVICE_DB_PASSWORD=PASSWORD
```

Con Neon, normalmente agrega `sslmode=require`:

```text
jdbc:postgresql://HOST:5432/DATABASE?sslmode=require
```

### Error de RabbitMQ

Verifica:

```text
RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USER
RABBITMQ_PASSWORD
RABBITMQ_VHOST
```

Si usas CloudAMQP, el vhost puede no ser `/`. Copialo exactamente desde el dashboard.

## 13. Fuentes oficiales

- Render Docker: https://render.com/docs/docker
- Render Web Services: https://render.com/docs/web-services
- Neon conexion general: https://neon.com/docs/get-started-with-neon/connect-neon
- Neon Java/JDBC: https://neon.com/docs/guides/java
- CloudAMQP Docs: https://www.cloudamqp.com/docs/
