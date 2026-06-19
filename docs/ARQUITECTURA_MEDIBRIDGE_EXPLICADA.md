# Arquitectura de MediBridge explicada como clase de arquitectura de software

## 1. Idea general del proyecto

MediBridge esta evolucionando desde un backend monolitico modular hacia una arquitectura de microservicios. Eso significa que el sistema no se partio al azar, sino tomando como base los limites de negocio que ya existian dentro del monolito.

La idea principal es esta:

> Cada parte importante del negocio vive en su propio servicio, con su propia base de datos, sus propias reglas y sus propios endpoints.

En la vida real, es parecido a una clinica. Una clinica no funciona con una sola persona haciendo todo. Hay admision, doctores, farmacia, caja, laboratorio, archivo clinico y administracion. Todos trabajan para el mismo paciente, pero cada area tiene responsabilidades distintas.

En MediBridge ocurre lo mismo:

- `iam-service` gestiona usuarios, login, JWT, roles y permisos.
- `profiles-service` gestiona perfiles de pacientes, doctores y familiares.
- `payments-service` gestiona planes, suscripciones, facturas y Stripe.
- `appointments-service` gestiona citas medicas y visitas familiares.
- `healthmonitoring-service` gestiona observaciones clinicas, signos vitales y alertas.
- `medication-service` gestiona medicamentos, horarios, dosis y stock.
- `reports-analytics-service` consolida informacion para reportes y dashboards.

## 2. Por que no dejar todo en un monolito

Un monolito no es malo por defecto. De hecho, para empezar un proyecto suele ser una buena decision porque es simple de construir, probar y desplegar. El problema aparece cuando el sistema crece.

En un monolito grande:

- Un cambio pequeno en pagos puede obligar a redeplegar todo el backend.
- Un error en reportes puede afectar login o citas.
- Todos los modulos comparten la misma base de datos y se vuelven dependientes.
- Es mas dificil escalar solo la parte que recibe mas carga.
- El equipo empieza a pisarse porque todo vive en el mismo proyecto.

Ejemplo de vida real:

Imagina un hospital donde farmacia, caja, admision y laboratorio usan un unico cuaderno fisico para registrar todo. Al inicio funciona. Pero cuando hay muchos pacientes, todos hacen cola para escribir en el mismo cuaderno. Si el cuaderno se pierde, se detiene todo el hospital.

Separar en microservicios es como darle a cada area su propio sistema interno, pero manteniendo mecanismos formales para comunicarse.

## 3. Por que la migracion es hibrida

Tu proyecto no intenta pasar de monolito a microservicios de golpe. Eso es importante.

La arquitectura actual es hibrida porque combina:

- REST/OpenFeign para operaciones que necesitan respuesta inmediata.
- RabbitMQ para eventos de negocio entre servicios.
- PostgreSQL separado por base de datos para cada servicio.
- JWT emitido por IAM para autenticar peticiones.
- Docker para infraestructura local.

Esto sigue el patron Strangler Fig. La idea es ir extrayendo partes del monolito poco a poco, hasta que el monolito deja de ser el centro.

Ejemplo de vida real:

Si tienes una casa vieja y quieres renovarla, no necesariamente la destruyes de un dia para otro. Primero cambias la cocina, luego el sistema electrico, luego el bano. La casa sigue funcionando mientras migras partes. Eso es lo que estas haciendo con MediBridge.

## 4. Estructura del proyecto

El proyecto tiene esta organizacion principal:

```text
medibridge/
├── docker/
│   ├── docker-compose.yml
│   └── postgres-init.sql
├── docs/
│   ├── GUIA_EJECUCION_PRUEBA_*.md
│   └── GUIA_DESPLIEGUE_RENDER_CLOUDAMQP_IAM_PROFILES.md
├── services/
│   ├── iam-service/
│   ├── profiles-service/
│   ├── payments-service/
│   ├── appointments-service/
│   ├── healthmonitoring-service/
│   ├── medication-service/
│   └── reports-analytics-service/
└── src/
    └── codigo base del backend original
```

`services/` representa la nueva arquitectura de microservicios.

`src/` representa el punto de partida monolitico o codigo base que todavia existe en el repositorio.

`docker/` contiene infraestructura local compartida para desarrollo: PostgreSQL y RabbitMQ.

## 5. Mapa de microservicios

| Servicio | Puerto | Base de datos | Responsabilidad principal |
|---|---:|---|---|
| `iam-service` | `8081` | `medibridge_iam` | Usuarios, autenticacion, roles, JWT y JWKS |
| `profiles-service` | `8082` | `medibridge_profiles` | Pacientes, doctores, familiares y relaciones de cuidado |
| `payments-service` | `8083` | `medibridge_payments` | Suscripciones, planes, facturas y Stripe |
| `appointments-service` | `8084` | `medibridge_appointments` | Citas medicas y visitas familiares |
| `healthmonitoring-service` | `8085` | `medibridge_healthmonitoring` | Observaciones clinicas, signos vitales y alertas |
| `medication-service` | `8086` | `medibridge_medication` | Medicamentos, horarios, dosis y stock |
| `reports-analytics-service` | `8087` | `medibridge_reports` | Reportes clinicos, PDFs, dashboards y snapshots |

Cada servicio es una aplicacion Spring Boot independiente. Eso significa que cada uno puede compilarse, ejecutarse y desplegarse por separado.

## 6. Bounded Context: el concepto central

La decision mas importante de esta arquitectura es separar por bounded context.

Un bounded context es un limite de negocio. Dentro de ese limite, las palabras tienen un significado claro.

Ejemplo:

La palabra "usuario" significa algo distinto segun el contexto:

- En IAM, un usuario es alguien que puede iniciar sesion.
- En Profiles, un usuario puede tener un perfil de paciente, doctor o familiar.
- En Payments, un usuario puede tener una suscripcion activa.

Si todo usa una unica entidad `User` gigante, el sistema se vuelve confuso. Cada modulo empieza a agregar campos que solo le importan a el.

La arquitectura actual evita eso:

- IAM no necesita saber signos vitales.
- Payments no necesita saber horarios de medicacion.
- Appointments no necesita saber datos de Stripe.
- Reports no deberia modificar directamente citas o medicamentos; solo leer o construir snapshots.

Ejemplo de vida real:

En una universidad, una persona puede ser "alumno" para secretaria academica, "cliente" para caja, "usuario" para sistemas y "paciente" para topico. Es la misma persona, pero cada area la modela de forma distinta porque tiene necesidades distintas.

## 7. IAM como primer servicio

La guia empezo por IAM porque es el servicio que habilita seguridad para todos los demas.

`iam-service` hace tres cosas criticas:

1. Registra usuarios.
2. Valida credenciales.
3. Emite JWT firmados.

Ademas, expone un endpoint JWKS:

```text
GET /api/v1/jwks/.well-known/jwks.json
```

Ese endpoint publica la llave publica usada para validar tokens.

### Por que JWT

JWT permite que un usuario haga login una vez y luego use un token para llamar a otros servicios.

Sin JWT, cada servicio tendria que preguntarle a IAM en cada request:

```text
Appointments -> IAM: este usuario es valido?
Health -> IAM: este usuario es valido?
Medication -> IAM: este usuario es valido?
```

Eso volveria a IAM un cuello de botella.

Con JWT:

```text
Usuario hace login en IAM
IAM devuelve token
Usuario llama a otros servicios con Bearer Token
Cada servicio valida la firma con la llave publica de IAM
```

Ejemplo de vida real:

Es como entrar a un edificio y recibir una credencial. No tienes que volver a entregar tu DNI en cada oficina. Cada oficina revisa que la credencial sea autentica y vigente.

### Por que BCrypt

Las contrasenas no se guardan en texto plano. `iam-service` usa BCrypt para guardar un hash.

Eso significa que si alguien mira la tabla de usuarios, no ve:

```text
password = 12345678
```

Ve algo parecido a:

```text
$2a$10$...
```

BCrypt ademas incluye salt y costo computacional, lo que dificulta ataques por fuerza bruta.

Ejemplo de vida real:

Guardar contrasenas en texto plano es como dejar llaves reales pegadas en una pizarra. Guardar hashes es como guardar una huella irreversible: sirve para comparar, pero no para reconstruir la llave original.

## 8. Seguridad distribuida

Los servicios que no son IAM validan JWT usando:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8081/api/v1/jwks/.well-known/jwks.json
```

Eso significa:

- IAM emite tokens.
- Los otros servicios no emiten tokens.
- Los otros servicios verifican si el token fue firmado por IAM.
- Los endpoints publicos son pocos: health checks, Swagger, endpoints internos puntuales y login/register.

Este diseno evita duplicar logica de autenticacion.

## 9. Base de datos por servicio

Cada microservicio tiene su propia base de datos:

```text
medibridge_iam
medibridge_profiles
medibridge_payments
medibridge_appointments
medibridge_healthmonitoring
medibridge_medication
medibridge_reports
```

En desarrollo todas viven dentro del mismo contenedor PostgreSQL, pero logicamente estan separadas.

Esto es importante porque un microservicio debe ser dueno de sus datos.

Regla arquitectonica:

> Un servicio no debe modificar directamente las tablas de otro servicio.

Ejemplo:

`appointments-service` no debe hacer:

```sql
SELECT * FROM profiles.patient_profiles;
```

En lugar de eso, puede:

- Consumir un evento `patient.registered` y guardar una referencia local.
- Consultar un endpoint interno de Profiles si necesita respuesta inmediata.

Ejemplo de vida real:

Un doctor no entra al sistema contable para modificar una factura. Si necesita saber si el paciente tiene cobertura, consulta a caja o recibe una confirmacion. Cada area protege sus propios registros.

## 10. Por que existen tablas de referencia local

Varios servicios tienen tablas como:

```text
patient_references
user_references
doctor_patient_relations
family_patient_relations
```

Estas tablas existen para evitar acoplamiento fuerte.

Ejemplo:

`appointments-service` necesita saber si un paciente existe y si un doctor puede atenderlo. Pero no deberia depender de consultar Profiles en cada request, porque:

- Profiles podria estar temporalmente caido.
- La cita no deberia requerir leer tablas externas.
- Se genera dependencia excesiva entre servicios.

Entonces Profiles publica eventos:

```text
patient.registered
doctor.assigned.patient
family.assigned.patient
patient.deactivated
```

Appointments consume esos eventos y mantiene su propia copia minima:

```text
patientId
doctorId
familyMemberId
active
```

No copia todo el perfil. Solo copia lo necesario para sus reglas.

Ejemplo de vida real:

Una aerolinea no necesita toda tu historia clinica para validar tu boleto. Solo necesita una copia minima: nombre, documento y estado de reserva.

## 11. REST vs RabbitMQ

Tu arquitectura usa dos formas de comunicacion:

- REST/OpenFeign para preguntas inmediatas.
- RabbitMQ para avisos de negocio.

### REST: cuando necesito respuesta ahora

Ejemplo:

`profiles-service` valida si un usuario de IAM existe antes de crear un perfil.

Eso necesita respuesta inmediata:

```text
Profiles -> IAM: existe este userId?
IAM -> Profiles: si/no
```

En la vida real:

Es como llamar a recepcion y preguntar: "esta persona esta registrada?". Necesitas la respuesta antes de continuar.

### RabbitMQ: cuando quiero avisar que algo paso

Ejemplo:

Cuando se registra un paciente, Profiles publica:

```text
patient.registered
```

No necesita esperar a que Appointments, Health, Medication y Reports terminen de procesarlo. Solo anuncia el evento.

En la vida real:

Es como poner un aviso en una pizarra interna: "Nuevo paciente registrado". Cada area que necesita esa informacion la toma y actualiza sus propios registros.

## 12. Exchange y queues en RabbitMQ

Todos los servicios usan el exchange:

```text
medibridge.events
```

El exchange funciona como una central de distribucion. Los servicios publican eventos con routing keys, y RabbitMQ los entrega a las queues interesadas.

Ejemplo:

```text
profiles-service publica patient.registered
RabbitMQ lo enruta a:
- appointments.patient-registered
- healthmonitoring.patient-registered
- medication.patient-registered
```

Cada servicio declara sus propias queues consumidoras.

Esto es correcto porque la queue representa el buzon del consumidor, no del productor.

Ejemplo de vida real:

Si administracion envia un comunicado, cada area tiene su propio buzon. El buzon de farmacia no lo crea admision; lo crea farmacia porque farmacia decide que quiere recibir esos avisos.

## 13. Mapa principal de eventos

| Productor | Evento | Consumidores o efecto |
|---|---|---|
| `iam-service` | `user.registered` | `payments-service` crea referencia local de usuario |
| `profiles-service` | `patient.registered` | Appointments, Health Monitoring, Medication, Reports |
| `profiles-service` | `patient.deactivated` | Appointments, Medication, Reports |
| `profiles-service` | `doctor.assigned.patient` | Appointments, Health Monitoring |
| `profiles-service` | `family.assigned.patient` | Appointments |
| `payments-service` | `subscription.activated` | IAM puede reaccionar ante activacion |
| `appointments-service` | `appointment.scheduled` | Reports puede generar snapshot |
| `healthmonitoring-service` | `observation.recorded` | Reports alimenta dashboard clinico |
| `healthmonitoring-service` | `alert.critical.triggered` | Reports y futuro Communication |
| `medication-service` | `medication.registered` | Reports alimenta historial de medicacion |
| `medication-service` | `dose.administered` | Reports y futuro Communication |
| `medication-service` | `dose.skipped` | Reports y futuro Communication |
| `medication-service` | `stock.low` | Reports y futuro Communication |
| `reports-analytics-service` | `clinical-report.generated` | Posibles notificaciones futuras |

## 14. Flujo de registro de usuario y paciente

### Paso 1: IAM registra usuario

```text
POST /api/v1/authentication/sign-up
```

IAM:

- Valida datos.
- Hashea password con BCrypt.
- Guarda usuario.
- Asocia roles.
- Publica `user.registered`.

### Paso 2: Payments escucha usuario registrado

`payments-service` consume:

```text
payments.user-registered
```

Y crea una referencia local del usuario. Asi despues puede crear suscripciones sin consultar directamente la tabla de IAM.

### Paso 3: Profiles crea perfil de paciente

```text
POST /api/v1/profiles/patients
```

Profiles valida que el `userId` exista en IAM y crea el perfil de paciente.

Luego publica:

```text
patient.registered
```

### Paso 4: Otros servicios sincronizan paciente

Appointments, Health Monitoring y Medication consumen el evento y crean `PatientReference`.

En terminos simples:

```text
Profiles dice: "existe un nuevo paciente"
Los demas servicios anotan: "este patientId es valido"
```

## 15. Flujo de cita medica

`appointments-service` gestiona dos tipos de cita:

- Cita medica: paciente + doctor.
- Visita familiar: paciente + familiar.

Endpoints:

```text
POST /api/v1/appointments/medical
POST /api/v1/appointments/family-visits
GET  /api/v1/appointments/{appointmentId}
GET  /api/v1/appointments/patient/{patientId}
```

Por que Appointments guarda relaciones locales:

- Para validar que el paciente existe.
- Para validar que el doctor esta asignado al paciente.
- Para validar que el familiar puede visitar al paciente.

Ejemplo de vida real:

Antes de agendar una consulta, recepcion verifica si el paciente existe y si ese doctor puede atenderlo. No necesita abrir todo el expediente clinico; solo necesita validar la relacion.

Cuando se agenda una cita, el servicio publica:

```text
appointment.scheduled
```

Reports puede usarlo para construir resumenes y reportes.

## 16. Flujo de monitoreo de salud

`healthmonitoring-service` registra observaciones clinicas y alertas.

Endpoints principales:

```text
POST /api/v1/health-monitoring/patients/{patientId}/observations
GET  /api/v1/health-monitoring/patients/{patientId}/observations
GET  /api/v1/health-monitoring/patients/{patientId}/alerts/active
GET  /api/v1/health-monitoring/patients/{patientId}/summary
```

Cuando se registra una observacion, publica:

```text
observation.recorded
```

Si la observacion genera una alerta critica, publica:

```text
alert.critical.triggered
```

Ejemplo de vida real:

Si una enfermera toma la presion y todo esta normal, solo queda registrado. Si la presion esta peligrosamente alta, ademas se dispara una alerta para que otros sistemas reaccionen.

## 17. Flujo de medicacion

`medication-service` gestiona:

- Registro de medicamentos.
- Horarios de administracion.
- Dosis administradas.
- Dosis omitidas.
- Stock bajo.

Endpoints principales:

```text
POST  /api/v1/medications
GET   /api/v1/medications/{medicationId}
GET   /api/v1/medications/patients/{patientId}
PATCH /api/v1/medications/{medicationId}/stock
GET   /api/v1/medications/patients/{patientId}/low-stock

POST  /api/v1/medication-schedules
GET   /api/v1/medication-schedules/patients/{patientId}/active

POST  /api/v1/dose-administrations
POST  /api/v1/dose-administrations/skip
GET   /api/v1/dose-administrations/medications/{medicationId}
```

Reglas de negocio importantes:

- No se permite stock negativo.
- Al administrar una dosis, el stock baja.
- Si el stock llega al umbral, se publica `stock.low`.
- Si se omite una dosis, se publica `dose.skipped`.

Ejemplo de vida real:

Farmacia tiene su inventario. Enfermeria administra una dosis. Cuando se administra, farmacia debe tener menos stock. Si quedan pocas unidades, el sistema avisa para reponer.

## 18. Flujo de pagos

`payments-service` gestiona:

- Planes.
- Suscripciones.
- Facturas.
- Metodos de pago.
- Stripe/webhooks.

Endpoints principales:

```text
POST /api/v1/subscriptions
POST /api/v1/subscriptions/{subscriptionId}/cancel
POST /api/v1/subscriptions/{subscriptionId}/renew
GET  /api/v1/subscriptions/users/{userId}
GET  /api/v1/subscriptions/users/{userId}/active
GET  /api/v1/invoices/users/{userId}
POST /api/v1/stripe-webhooks
```

Payments consume:

```text
user.registered
```

Y publica:

```text
subscription.activated
```

Ejemplo de vida real:

Caja no necesita conocer todo el historial medico del paciente. Solo necesita saber que existe un usuario y si tiene una suscripcion activa.

## 19. Flujo de reportes y analytics

`reports-analytics-service` es un servicio integrador. Su responsabilidad es construir reportes clinicos y dashboards usando informacion de varios contextos.

Endpoints principales:

```text
POST /api/v1/clinical-reports
POST /api/v1/clinical-reports/{reportId}/pdf
GET  /api/v1/clinical-reports/{reportId}/pdf
GET  /api/v1/clinical-reports/{reportId}
GET  /api/v1/clinical-reports/patients/{patientId}
GET  /api/v1/analytics-dashboards/patients/{patientId}
```

Reports consume eventos como:

```text
appointment.scheduled
observation.recorded
medication.registered
dose.administered
dose.skipped
stock.low
```

Tambien usa Feign hacia otros servicios para construir reportes bajo demanda.

Esto es normal en una migracion hibrida. Al inicio, Reports puede consultar otros servicios directamente. Con el tiempo, puede depender mas de snapshots locales alimentados por eventos.

Ejemplo de vida real:

El area de reportes de una clinica no atiende pacientes ni administra medicamentos. Solo recopila informacion de admision, doctores, farmacia y monitoreo para generar un informe.

## 20. Capas internas de cada servicio

La mayoria de servicios siguen una estructura parecida:

```text
domain/
  model/
    aggregates/
    entities/
    commands/
    queries/
    events/
    valueobjects/
  services/

application/
  internal/
    commandservices/
    queryservices/
    eventhandlers/

infrastructure/
  persistence/
  messaging/
  security/
  acl/

interfaces/
  rest/
    controllers/
    resources/
    transform/
```

Esta separacion ayuda a mantener orden.

### Domain

Contiene las reglas del negocio.

Ejemplo:

- Un medicamento no puede tener stock negativo.
- Una dosis no debe registrarse dos veces para el mismo horario en el mismo dia.
- Una cita medica requiere paciente y doctor.

### Application

Orquesta casos de uso.

Ejemplo:

Cuando se registra una dosis:

1. Busca medicamento.
2. Verifica stock.
3. Decrementa stock.
4. Guarda dosis.
5. Publica evento.

### Infrastructure

Contiene detalles tecnicos:

- JPA.
- RabbitMQ.
- Seguridad.
- Feign.
- Stripe.
- JWT.

### Interfaces REST

Expone el servicio al exterior:

- Controllers.
- Request/response resources.
- Assemblers o transformers.

## 21. Por que usar Commands, Queries y Resources

Tu proyecto usa un estilo CQRS ligero:

- Commands representan intenciones de cambio.
- Queries representan intenciones de lectura.
- Resources representan datos HTTP de entrada o salida.

Ejemplo:

Un request HTTP puede ser:

```json
{
  "patientId": 1,
  "name": "Paracetamol",
  "stockQuantity": 10
}
```

Pero internamente se convierte a:

```text
RegisterMedicationCommand
```

Eso evita que el dominio dependa directamente del formato HTTP.

Ejemplo de vida real:

Un formulario fisico no es el tramite. El formulario inicia el tramite. Internamente, la oficina procesa una solicitud con reglas propias.

## 22. Por que usar eventos de integracion

Un evento de integracion representa algo que ya paso y que puede interesar a otros servicios.

Ejemplos:

```text
patient.registered
appointment.scheduled
dose.administered
stock.low
```

No son ordenes. Son hechos.

Correcto:

```text
"Paciente registrado"
```

Menos correcto:

```text
"Appointments, crea una referencia de paciente"
```

La diferencia es importante. El productor no debe saber demasiado sobre los consumidores.

Ejemplo de vida real:

Cuando una tienda emite una boleta, no le dice al almacen exactamente que hacer. Solo registra que hubo una venta. Almacen, contabilidad y reportes reaccionan segun sus propias reglas.

## 23. Resiliencia y desacoplamiento

RabbitMQ permite que un servicio publique eventos aunque otro servicio este momentaneamente caido.

Ejemplo:

Si `profiles-service` publica `patient.registered` y `medication-service` esta apagado, la queue `medication.patient-registered` puede conservar el mensaje hasta que Medication vuelva a estar disponible.

Esto da resiliencia.

Pero tambien introduce una idea importante:

> La consistencia entre servicios es eventual.

Eso significa que puede haber unos segundos donde Profiles ya creo el paciente, pero Medication aun no proceso el evento.

Ejemplo de vida real:

Cuando pagas una compra online, a veces el banco confirma primero y la tienda actualiza el pedido segundos despues. No todo ocurre exactamente al mismo tiempo, pero el sistema termina sincronizandose.

## 24. Consistencia eventual

En un monolito con una sola base de datos, muchas operaciones pueden hacerse en una unica transaccion.

En microservicios, cada servicio tiene su base. No conviene abrir una transaccion distribuida entre siete bases de datos.

Por eso se usan eventos.

Ejemplo:

1. Profiles crea paciente en `medibridge_profiles`.
2. Profiles publica `patient.registered`.
3. Appointments crea `PatientReference` en `medibridge_appointments`.
4. Medication crea `PatientReference` en `medibridge_medication`.

No es una unica transaccion global. Es una propagacion por eventos.

La ventaja:

- Menos acoplamiento.
- Mejor escalabilidad.
- Servicios mas autonomos.

La desventaja:

- Hay que manejar casos donde el evento todavia no llego.
- Hay que pensar en reintentos, duplicados e idempotencia.

## 25. Idempotencia: una regla que debes reforzar

En sistemas con eventos, un mensaje podria llegar mas de una vez. Por eso los handlers deben ser idempotentes.

Idempotente significa:

> Si proceso el mismo evento dos veces, el resultado final debe ser el mismo que procesarlo una vez.

Ejemplo:

Si llega dos veces `patient.registered`, no deberia crear dos pacientes locales duplicados. Deberia actualizar o ignorar si ya existe.

En varios servicios ya se ve esta intencion con referencias locales buscadas por `patientId` o `userId`.

Ejemplo de vida real:

Si recibes dos copias del mismo correo de confirmacion, no deberias reservar dos citas. Debes reconocer que es la misma confirmacion.

## 26. Flyway y control de esquema

Cada servicio tiene migraciones Flyway:

```text
V1__iam_schema.sql
V1__profiles_schema.sql
V1__payments_schema.sql
V2__user_references.sql
V1__appointments_schema.sql
V2__local_references.sql
V1__health_monitoring_schema.sql
V2__local_references.sql
V1__medication_schema.sql
V2__patient_references.sql
V1__reports_analytics_schema.sql
```

Flyway permite versionar la base de datos como parte del codigo.

Sin Flyway, cada desarrollador podria tener tablas distintas en su maquina.

Ejemplo de vida real:

Flyway es como un historial oficial de remodelaciones de un edificio. No basta con decir "agrega una puerta"; queda registrado en que version se agrego, con que script y en que orden.

## 27. Docker local

El archivo `docker/docker-compose.yml` levanta:

```text
PostgreSQL 16
RabbitMQ con Management UI
```

PostgreSQL expone:

```text
localhost:5433
```

RabbitMQ expone:

```text
localhost:5672
http://localhost:15672
```

En desarrollo, esto simplifica mucho:

- No instalas PostgreSQL manualmente para cada base.
- No instalas RabbitMQ manualmente.
- Puedes reiniciar infraestructura de forma controlada.

Ejemplo de vida real:

Docker es como tener una cocina prefabricada para pruebas. No importa en que local la pongas: viene con horno, gas y mesa en las mismas condiciones.

## 28. Despliegue en nube

Tu proyecto ya tiene una guia para Render, CloudAMQP y Neon.

La idea general es:

- Render despliega los servicios Spring Boot como contenedores Docker.
- Neon aloja PostgreSQL.
- CloudAMQP aloja RabbitMQ.
- Las credenciales se pasan como variables de entorno.

En local usas:

```text
localhost
```

En nube usas:

```text
URLs externas o internas del proveedor
```

Por eso los `application.yml` usan variables como:

```text
IAM_DB_URL
RABBITMQ_HOST
IAM_JWK_SET_URI
```

Esto evita quemar credenciales dentro del codigo.

## 29. Ventajas de la arquitectura actual

### Separacion de responsabilidades

Cada servicio tiene un proposito claro.

Ejemplo:

Si hay un bug en stock de medicamentos, lo buscas en `medication-service`, no en todo el backend.

### Escalabilidad selectiva

Si reportes consume muchos recursos generando PDFs, puedes escalar `reports-analytics-service` sin escalar IAM.

### Seguridad consistente

IAM centraliza autenticacion y los demas servicios validan tokens.

### Menor acoplamiento por eventos

Profiles no necesita saber exactamente que hacen Appointments, Health o Medication cuando se registra un paciente.

### Migracion progresiva

No se reescribe todo de golpe. Cada contexto se extrae por fases.

## 30. Costos y riesgos de esta arquitectura

Microservicios no son gratis. Traen complejidad adicional.

Riesgos principales:

- Mas servicios para ejecutar.
- Mas variables de entorno.
- Mas logs que revisar.
- Posibles fallos de red entre servicios.
- Consistencia eventual.
- Necesidad de monitorear RabbitMQ.
- Duplicacion controlada de datos por referencias locales.

Ejemplo de vida real:

Una clinica con muchas areas especializadas puede atender mejor, pero necesita coordinacion. Si nadie gestiona comunicacion interna, aparecen retrasos y errores.

## 31. Por que esta arquitectura tiene sentido para MediBridge

MediBridge tiene dominios naturalmente separados:

- Identidad.
- Perfiles clinicos.
- Citas.
- Monitoreo de salud.
- Medicacion.
- Pagos.
- Reportes.

Estos dominios no cambian al mismo ritmo.

Payments puede cambiar por integracion con Stripe.

Health Monitoring puede crecer con nuevas reglas clinicas.

Medication puede crecer con alertas de stock y horarios complejos.

Reports puede crecer con dashboards y PDFs.

IAM debe mantenerse estable y seguro.

Separarlos permite evolucionar cada parte sin arrastrar todo el sistema.

## 32. Diagrama textual de alto nivel

```text
                    Usuario / Frontend
                          |
                          v
                    iam-service
             login, JWT, roles, JWKS
                          |
             JWT validado por JWK publico
                          |
    -------------------------------------------------
    |          |           |          |             |
    v          v           v          v             v
profiles   payments   appointments  health     medication
service    service    service       monitoring service
    |          |           |          |             |
    |          |           |          |             |
    ---------------- RabbitMQ -----------------------
                         |
                         v
              reports-analytics-service
              reportes, PDFs, dashboards
```

## 33. Diagrama de eventos simplificado

```text
iam-service
  └── user.registered ───────────────> payments-service

profiles-service
  ├── patient.registered ────────────> appointments-service
  │                                    healthmonitoring-service
  │                                    medication-service
  │                                    reports-analytics-service
  ├── doctor.assigned.patient ───────> appointments-service
  │                                    healthmonitoring-service
  └── family.assigned.patient ───────> appointments-service

appointments-service
  └── appointment.scheduled ─────────> reports-analytics-service

healthmonitoring-service
  ├── observation.recorded ──────────> reports-analytics-service
  └── alert.critical.triggered ──────> reports-analytics-service

medication-service
  ├── medication.registered ─────────> reports-analytics-service
  ├── dose.administered ─────────────> reports-analytics-service
  ├── dose.skipped ──────────────────> reports-analytics-service
  └── stock.low ─────────────────────> reports-analytics-service

payments-service
  └── subscription.activated ────────> iam-service
```

## 34. Lectura recomendada del proyecto

Para entender el proyecto como arquitecto, revisa en este orden:

1. `services/iam-service`: autenticacion, JWT, BCrypt y JWKS.
2. `services/profiles-service`: perfiles y eventos base para pacientes.
3. `services/appointments-service`: ejemplo claro de referencias locales y reglas por relacion.
4. `services/healthmonitoring-service`: eventos clinicos y alertas.
5. `services/medication-service`: stock, dosis y eventos operativos.
6. `services/payments-service`: integracion externa con Stripe y eventos hacia IAM.
7. `services/reports-analytics-service`: servicio integrador que consume muchos eventos.
8. `docker/docker-compose.yml`: infraestructura local.
9. `docs/GUIA_EJECUCION_PRUEBA_*.md`: flujo practico por servicio.

## 35. Recomendaciones de mejora arquitectonica

### 1. Estandarizar nombres de eventos y queues

Ya hay una convencion clara, pero conviene mantenerla estricta:

```text
evento: recurso.accion
queue: servicio.evento-en-kebab-case
```

Ejemplo:

```text
patient.registered
appointments.patient-registered
```

### 2. Agregar correlationId a eventos

Un `correlationId` permite rastrear un flujo completo.

Ejemplo:

```text
sign-up -> user.registered -> payment reference created
```

Sin correlationId, rastrear logs entre servicios es mas dificil.

### 3. Centralizar manejo de errores REST

Cada servicio deberia responder errores con un formato consistente:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "..."
}
```

### 4. Reforzar idempotencia en consumidores RabbitMQ

Cada handler deberia poder procesar el mismo evento mas de una vez sin duplicar datos.

### 5. Agregar pruebas de contrato

Los eventos son contratos. Si cambia `PatientRegisteredIntegrationEvent`, puede romper consumidores.

Una prueba de contrato ayudaria a detectar eso antes de desplegar.

### 6. Agregar observabilidad

Cuando crezcan los servicios, necesitarias:

- Logs estructurados.
- Health checks.
- Metricas.
- Trazas distribuidas.

## 36. Conclusion

La arquitectura de MediBridge es una migracion progresiva y bien encaminada desde un monolito modular hacia microservicios.

La decision correcta no fue "crear muchos proyectos", sino separar por capacidades reales del negocio:

- IAM protege el sistema.
- Profiles define personas y relaciones.
- Appointments agenda interacciones.
- Health Monitoring observa estado clinico.
- Medication gestiona tratamientos y stock.
- Payments monetiza y controla suscripciones.
- Reports consolida informacion.

REST se usa cuando una respuesta inmediata es necesaria. RabbitMQ se usa cuando un servicio solo necesita anunciar que algo ocurrio. PostgreSQL separado por servicio protege la autonomia de datos. JWT permite seguridad distribuida sin consultar IAM en cada request.

En resumen:

> MediBridge esta dejando de ser una aplicacion grande donde todo depende de todo, para convertirse en un conjunto de servicios especializados que colaboran mediante contratos claros.

Esa es la esencia de una buena arquitectura de microservicios.
