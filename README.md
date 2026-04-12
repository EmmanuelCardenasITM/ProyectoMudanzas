<<<<<<< HEAD
# Sistema de Mudanzas — API REST (Java)

API REST para gestión de servicios de mudanza. Arquitectura por capas (Controller → Service → DAO), MySQL con JDBC puro, autenticación JWT y documentación Swagger/OpenAPI.

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Presentación | JAX-RS (Jersey 3) |
| Lógica de negocio | Java 11 (Services) |
| Persistencia | JDBC puro — MySQL Connector/J |
| Autenticación | JJWT (JWT) + jBCrypt |
| Documentación | Swagger / OpenAPI 3 |
| Build | Maven |
| Servidor | Tomcat 10+ |

## Estructura del proyecto

```
sistema-mudanzas/
├── src/main/java/com/mudanzas/
│   ├── config/
│   │   ├── DatabaseConnection.java    Conexión JDBC manual
│   │   ├── JwtUtil.java               Generación y validación JWT
│   │   └── SwaggerConfig.java         Configuración JAX-RS + Swagger
│   ├── filter/
│   │   └── AuthFilter.java            Filtro de autenticación JWT
│   ├── model/                         Entidades (Cliente, Servicio, Pago, Tarifa, Usuario)
│   ├── dao/                           Capa de persistencia — SQL puro con JDBC
│   ├── service/                       Capa de lógica de negocio
│   └── controller/                    Capa de presentación — endpoints REST
├── src/main/resources/
│   └── config.properties              Configuración de BD y JWT
├── src/main/webapp/WEB-INF/
│   └── web.xml                        Configuración del servlet
├── database/
│   ├── schema.sql                     DDL — crear tablas
│   └── seed.sql                       Datos iniciales
└── pom.xml
```

## Instalación y configuración

### 1. Requisitos previos

- Java 11+
- Maven 3.6+
- MySQL 8+
- Tomcat 10+

### 2. Crear la base de datos

```bash
mysql -u root -p < database/schema.sql
```

### 3. Configurar la conexión

Edita `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/sistema_mudanzas?useSSL=false&serverTimezone=UTC
db.user=root
db.password=tu_password
jwt.secret=un_secreto_muy_seguro_de_al_menos_32_caracteres
jwt.expiration.hours=8
```

### 4. Generar hashes de contraseñas para el seed

Crea y ejecuta esta clase temporal:

```java
import org.mindrot.jbcrypt.BCrypt;
public class GenHash {
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        System.out.println(BCrypt.hashpw("empleado123", BCrypt.gensalt()));
    }
}
```

Reemplaza los placeholders en `database/seed.sql` y ejecuta:

```bash
mysql -u root -p sistema_mudanzas < database/seed.sql
```

### 5. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

### 6. Acceder a la API

- API base: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Endpoints

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| POST | `/api/auth/login` | Iniciar sesión | Público |
| GET | `/api/clientes` | Listar clientes | Admin, Empleado |
| GET | `/api/clientes/{id}` | Obtener cliente | Admin, Empleado |
| POST | `/api/clientes` | Crear cliente | Admin |
| PUT | `/api/clientes/{id}` | Actualizar cliente | Admin |
| DELETE | `/api/clientes/{id}` | Eliminar cliente | Admin |
| GET | `/api/servicios` | Listar servicios | Admin, Empleado |
| GET | `/api/servicios/{id}` | Obtener servicio | Admin, Empleado |
| POST | `/api/servicios` | Crear servicio | Admin |
| PUT | `/api/servicios/{id}` | Actualizar servicio | Admin |
| PUT | `/api/servicios/{id}/estado` | Cambiar estado | Admin, Empleado |
| DELETE | `/api/servicios/{id}` | Eliminar servicio | Admin |
| GET | `/api/servicios/{id}/pagos` | Ver pagos | Admin, Empleado |
| POST | `/api/servicios/{id}/pagos` | Registrar pago | Admin |
| GET | `/api/tarifas` | Ver tarifas | Admin, Empleado |
| PUT | `/api/tarifas` | Actualizar tarifas | Admin |
| GET | `/api/clientes/{id}/historial` | Historial del cliente | Admin, Empleado |

## Autenticación

Todos los endpoints (excepto `/api/auth/login`) requieren:

```
Authorization: Bearer <token_jwt>
```

## Ramas de trabajo (Git)

```bash
git checkout -b feature/modulo-clientes
git checkout -b feature/modulo-servicios
git checkout -b feature/modulo-pagos
git checkout -b feature/modulo-tarifas
```
=======
# ProyectoMudanzas
Repositorio del proyecto para el sistema de gestión de mudanzas.
>>>>>>> ca75512b2f0787230656c698d30c509bad042d48
