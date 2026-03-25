# Prestamo Libros - Azure Functions

API REST para la gestión de préstamos de libros, desarrollada con Azure Functions y Java 21, conectada a una base de datos Oracle Cloud.

## Tecnologías

- Java 21
- Azure Functions (v4)
- Oracle Database (JDBC + Wallet)
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
    "ORACLE_USERNAME": "tu_usuario",
    "ORACLE_PASSWORD": "tu_contraseña",
    "ORACLE_TNS_NAME": "tu_tns_name"
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

## Endpoints

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
  "email": "albaniamusabeli@gmail.com"
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

### Préstamos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/prestamos` | Listar todos los préstamos |
| GET | `/api/prestamos/{id}` | Obtener préstamo por ID |
| POST | `/api/prestamos` | Crear préstamo |
| PUT | `/api/prestamos/{id}` | Actualizar préstamo |
| DELETE | `/api/prestamos/{id}` | Eliminar préstamo |

#### Body - POST Préstamo

```json
{
  "idUsuario": 1,
  "idLibro": 1,
  "fechaInicio": "2026-03-25",
  "fechaFin": "2026-04-08",
  "estado": "ACTIVO"
}
```

#### Body - PUT Préstamo

```json
{
  "idUsuario": 1,
  "idLibro": 1,
  "fechaInicio": "2026-03-25",
  "fechaFin": "2026-04-08",
  "estado": "DEVUELTO"
}
```

> Los valores para `estado` son: `ACTIVO`, `DEVUELTO`.

---

## Estructura del proyecto

```
src/
├── main/java/com/musabeli/
│   ├── config/
│   │   ├── DatabaseConfig.java      # Conexión Oracle con Wallet
│   │   └── GsonConfig.java          # Configuración de Gson
│   ├── entities/
│   │   ├── Usuario.java
│   │   ├── Libro.java
│   │   └── Prestamo.java
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   ├── LibroRepository.java
│   │   └── PrestamoRepository.java
│   └── functions/
│       ├── UsuariosFunction.java
│       └── PrestamosLibrosFunction.java
└── test/java/com/musabeli/
    └── FunctionTest.java
```
