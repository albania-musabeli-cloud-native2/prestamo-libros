# Prestamo Libros - Azure Functions

API para la gestión de préstamos de libros, desarrollada con Azure Functions y Java 21, conectada a una base de datos Oracle Cloud.

- **Usuarios y Libros** exponen una API REST tradicional.
- **Préstamos** exponen una API GraphQL a través de un único endpoint.

## Tecnologías

- Java 21
- Azure Functions (v4)
- Oracle Database (JDBC + Wallet)
- GraphQL Java 25.0
- Azure Event Grid (azure-messaging-eventgrid 4.20.0)
- Gson 2.11
- Lombok
- JUnit 5 + Mockito

## Requisitos previos

- Java 21
- Maven 3.x
- Azure Functions Core Tools v4
- Wallet de Oracle en `src/main/resources/wallet/`

## Configuración local

Editar `local.settings.json` con las credenciales de Oracle:

```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "ORACLE_USERNAME": "mi_usuario",
    "ORACLE_PASSWORD": "mi_contraseña",
    "ORACLE_TNS_NAME": "mi_tns_name",
    "EVENT_GRID_TOPIC_ENDPOINT": "https://<topic>.eventgrid.azure.net/api/events",
    "EVENT_GRID_TOPIC_KEY": "mi_access_key"
  }
}
```

## Ejecutar localmente

```bash
mvn clean package
mvn azure-functions:run
```

La API estará disponible en `http://localhost:7071/api/`

---

## Endpoints REST

### Usuarios

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

#### Body - POST / PUT Usuario

```json
{
  "nombre": "Albania Musabeli",
  "email": "albaniamusabeli@correo.com"
}
```

---

### Libros

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/libros` | Listar todos los libros |
| GET | `/api/libros/{id}` | Obtener libro por ID |
| POST | `/api/libros` | Crear libro |
| PUT | `/api/libros/{id}` | Actualizar libro |
| DELETE | `/api/libros/{id}` | Eliminar libro |

#### Body - POST / PUT Libro

```json
{
  "titulo": "Frankenstein",
  "autor": "Mary Shelley",
  "isbn": "978-84-376-0494-8",
  "stock": 4
}
```

---

## Integración con Azure Event Grid

Al crear un préstamo mediante la mutación GraphQL `crearPrestamo`, la función publica automáticamente un evento en un topic de Azure Event Grid.

### Evento publicado: `prestamo-libros.PrestamoCreado`

| Campo | Valor |
|-------|-------|
| **Tipo** | `prestamo-libros.PrestamoCreado` |
| **Subject** | `prestamos/{id}` |
| **Versión de datos** | `1.0` |

#### Payload del evento

```json
{
  "idPrestamo": 1,
  "idUsuario": 1,
  "idLibro": 2,
  "accion": "CREAR_PRESTAMO"
}
```

### Variables de entorno requeridas

| Variable | Descripción |
|----------|-------------|
| `EVENT_GRID_TOPIC_ENDPOINT` | URL del topic de Event Grid (ej. `https://<topic>.eventgrid.azure.net/api/events`) |
| `EVENT_GRID_TOPIC_KEY` | Access key del topic de Event Grid |

> El evento se publica de forma síncrona al momento de confirmar la creación del préstamo en la base de datos.

---

## Endpoint GraphQL — Préstamos

Todos los préstamos se gestionan a través de un único endpoint GraphQL:

```
POST /api/graphql
Content-Type: application/json
```

El body siempre tiene la forma:

```json
{
  "query": "..."
}
```

### Schema

```graphql
type Prestamo {
    id: ID
    idUsuario: ID
    idLibro: ID
    fechaInicio: String
    fechaFin: String
    estado: String
    createdAt: String
}

type Query {
    prestamos: [Prestamo]
    prestamo(id: ID!): Prestamo
}

type Mutation {
    crearPrestamo(idUsuario: ID!, idLibro: ID!, fechaInicio: String!, fechaFin: String!, estado: String!): Prestamo
    actualizarPrestamo(id: ID!, idUsuario: ID, idLibro: ID, fechaInicio: String, fechaFin: String, estado: String): Boolean
    eliminarPrestamo(id: ID!): Boolean
}
```

### Operaciones

#### Listar todos los préstamos

```json
{
  "query": "{ prestamos { id idUsuario idLibro fechaInicio fechaFin estado createdAt } }"
}
```

#### Obtener préstamo por ID

```json
{
  "query": "{ prestamo(id: 1) { id idUsuario idLibro fechaInicio fechaFin estado createdAt } }"
}
```

#### Crear préstamo

```json
{
  "query": "mutation { crearPrestamo(idUsuario: 1, idLibro: 1, fechaInicio: \"2026-04-07\", fechaFin: \"2026-04-21\", estado: \"ACTIVO\") { id idUsuario idLibro fechaInicio fechaFin estado } }"
}
```

#### Actualizar préstamo

Solo se envían los campos que se desean modificar:

```json
{
  "query": "mutation { actualizarPrestamo(id: 1, estado: \"DEVUELTO\") }"
}
```

#### Eliminar préstamo

```json
{
  "query": "mutation { eliminarPrestamo(id: 1) }"
}
```

### Respuesta exitosa

```json
{
  "data": {
    "prestamos": [
      {
        "id": "1",
        "idUsuario": "1",
        "idLibro": "2",
        "fechaInicio": "2026-04-07",
        "fechaFin": "2026-04-21",
        "estado": "ACTIVO",
        "createdAt": "2026-04-07T10:30:00"
      }
    ]
  }
}
```

### Respuesta con error

```json
{
  "errors": [
    { "message": "Descripción del error" }
  ]
}
```

> Los valores válidos para `estado` son: `ACTIVO`, `DEVUELTO`.

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/musabeli/
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java          # Conexión Oracle con Wallet
│   │   │   └── GsonConfig.java              # Configuración de Gson
│   │   ├── entities/
│   │   │   ├── Usuario.java
│   │   │   ├── Libro.java
│   │   │   └── Prestamo.java
│   │   ├── repository/
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── LibroRepository.java
│   │   │   └── PrestamoRepository.java
│   │   ├── events/
│   │   │   └── EventGridPublisher.java      # Publicación de eventos a Event Grid
│   │   ├── graphql/
│   │   │   ├── GraphQLConfig.java           # Construcción del motor GraphQL
│   │   │   └── PrestamoDataFetchers.java    # Resolvers de préstamos
│   │   └── functions/
│   │       ├── UsuariosFunction.java        # REST: usuarios
│   │       ├── PrestamosLibrosFunction.java # REST: libros
│   │       └── GraphQLFunction.java         # GraphQL: préstamos
│   └── resources/
│       ├── prestamo.graphqls                # Schema GraphQL
│       └── wallet/                          # Wallet Oracle Cloud
└── test/java/com/musabeli/
    └── FunctionTest.java
```
