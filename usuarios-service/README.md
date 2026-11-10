# usuarios-service

Microservicio de **usuarios, autenticación y roles** de Ferretería Los Maestros (Spring Boot + MySQL + Spring Security + JWT).

- Puerto: `8081`
- Swagger UI: http://localhost:8081/swagger-ui.html
- Base de datos: `ferreteria_usuarios`

## Requisitos
- Java 17
- MySQL 8 corriendo en `localhost:3306`

## Puesta en marcha
1. Crear la base de datos y el usuario (una sola vez):
   ```sql
   CREATE DATABASE ferreteria_usuarios CHARACTER SET utf8mb4;
   CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
   GRANT ALL PRIVILEGES ON ferreteria_usuarios.* TO 'ferreteria'@'localhost';
   ```
2. Ejecutar:
   ```
   ./mvnw spring-boot:run
   ```
   (en Windows: `mvnw.cmd spring-boot:run`)
3. Tests (no necesitan MySQL, usan H2 en memoria):
   ```
   ./mvnw test
   ```

Variables de entorno opcionales: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

## Usuarios de prueba (se crean solos la primera vez)
| Rol | Correo | Contraseña |
|---|---|---|
| ADMINISTRADOR | admin@gmail.com | admin123 |
| VENDEDOR | maria@duoc.cl | vend123 |
| CLIENTE | juan@gmail.com | cli123 |

## Endpoints (`/api/v1`)
| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/registro` | Público | Registra un **cliente** |
| POST | `/auth/login` | Público | Devuelve access token (30 min) + refresh token (7 días) |
| POST | `/auth/refresh` | Público (con refresh token) | Renueva el par de tokens |
| GET | `/auth/me` | Cualquier usuario autenticado | Datos de la sesión actual |
| GET | `/usuarios` (`?rol=`) | ADMINISTRADOR | Lista usuarios |
| GET | `/usuarios/{id}` | ADMINISTRADOR | Detalle |
| POST | `/usuarios` | ADMINISTRADOR | Crea usuario de cualquier rol |
| PUT | `/usuarios/{id}` | ADMINISTRADOR | Actualiza (clave opcional) |
| DELETE | `/usuarios/{id}` | ADMINISTRADOR | Elimina |

Archivo `pruebas.http` con todas las peticiones (extensión *REST Client* de VS Code).

## Cómo funciona la seguridad
1. **Login**: `AuthService` delega en `AuthenticationManager`, que usa `DetalleUsuarioService` (busca por correo en la BD) y compara la clave contra el hash **BCrypt**.
2. Si es correcto, `JwtService` firma (HMAC-SHA256) dos tokens con `sub` = correo y los claims `uid`, `rol` y `tipo` (`access` o `refresh`).
3. En cada petición, `JwtAuthFilter` lee `Authorization: Bearer <token>`, verifica firma y expiración, exige `tipo=access` y carga el usuario con la autoridad `ROLE_<ROL>`.
4. `SecurityConfig` define qué rol puede entrar a cada ruta. Sin token válido → **401**; con token pero sin el rol → **403** (ambos con el mismo JSON de error).
5. La sesión es **stateless**: el servidor no guarda nada; por eso se desactiva CSRF.
6. **Refresh**: un refresh token válido entrega tokens nuevos, y se vuelve a consultar la BD (si el usuario fue desactivado, ya no se renueva). Un access token no sirve como refresh y viceversa.

## Reglas de negocio
- El registro público siempre crea `CLIENTE`: nadie puede asignarse otro rol.
- Correo y RUN únicos (409 si se repiten). Correo normalizado a minúsculas.
- Un administrador no puede eliminarse, quitarse su rol ni desactivarse.
- Mensaje de login genérico ("Correo o contraseña incorrectos") para no revelar si el correo existe.

## Para que otros microservicios validen el token
`productos-service` y `ordenes-service` NO llaman a este servicio: validan el JWT localmente con el **mismo `app.jwt.secret`** (misma clase `JwtService`/`JwtAuthFilter`, solo lectura) y aplican sus propias reglas por rol.
