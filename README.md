# Ferretería Los Maestros - Backend (microservicios)

Backend de la tienda en 5 microservicios Spring Boot (Java 17, MySQL, Spring Security + JWT, Swagger).
El frontend (React, puerto 5173) los consume por REST con axios.

| Servicio | Puerto | Base de datos | Responsabilidad | Swagger |
|---|---|---|---|---|
| `usuarios-service` | 8081 | `ferreteria_usuarios` | Registro, login, JWT (access + refresh), roles y CRUD de usuarios | http://localhost:8081/swagger-ui.html |
| `productos-service` | 8082 | `ferreteria_productos` | Productos, categorías y stock | http://localhost:8082/swagger-ui.html |
| `ordenes-service` | 8083 | `ferreteria_ordenes` | Órdenes de compra (boleta) y su detalle, estados | http://localhost:8083/swagger-ui.html |
| `contenido-service` | 8084 | `ferreteria_contenido` | Artículos del blog y mensajes de contacto | http://localhost:8084/swagger-ui.html |
| `inventario-service` | 8085 | `ferreteria_inventario` | Movimientos de stock y alertas de stock crítico | http://localhost:8085/swagger-ui.html |

> En el modelo del enunciado (Figura 1) `BOLETA` y `DETALLE_BOLETA` corresponden a las tablas `orden` y `detalle_orden` de `ordenes-service`.

## Cómo se comunican
```
Frontend :5173 ──axios + JWT──▶ usuarios (8081)   login, registro, usuarios
                             ├─▶ productos (8082)  catálogo y stock
                             ├─▶ ordenes (8083) ──(mismo JWT)──▶ productos   precios y stock
                             ├─▶ contenido (8084)  blog y contacto
                             └─▶ inventario (8085) ──(mismo JWT)──▶ productos   movimientos y alertas
```
- **Solo `usuarios-service` emite tokens.** Los demás servicios los **validan localmente** con el mismo secreto (`app.jwt.secret` / variable `JWT_SECRET`), sin llamarlo.
- Cuando `ordenes` o `inventario` llaman a `productos`, **reenvían el token** de quien hizo la petición, así `productos` aplica sus reglas por rol.
- Cada servicio tiene su propia base de datos.

## Requisitos
- Java 17 (JDK) y MySQL 8
- No necesitas instalar Maven: cada servicio trae `mvnw`

## Puesta en marcha
1. En MySQL Workbench (usuario `root`) ejecuta `db/crear-bases.sql` (crea las 5 bases y el usuario `ferreteria`).
2. Abre una terminal por servicio y ejecuta (Windows PowerShell):
   ```
   cd usuarios-service
   .\mvnw spring-boot:run
   ```
   Repite en `productos-service`, `ordenes-service`, `contenido-service` e `inventario-service`.
   Orden recomendado: usuarios → productos → ordenes → contenido → inventario (ordenes e inventario necesitan productos encendido).
3. Cada servicio crea sus tablas y datos de ejemplo la primera vez.
4. Variables opcionales: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `PRODUCTOS_URL`.

## Usuarios de prueba
| Rol | Correo | Contraseña |
|---|---|---|
| ADMINISTRADOR | admin@gmail.com | admin123 |
| VENDEDOR | maria@duoc.cl | vend123 |
| CLIENTE | juan@gmail.com | cli123 |

## Permisos por rol
| Servicio | Acción | Administrador | Vendedor | Cliente | Sin sesión |
|---|---|:-:|:-:|:-:|:-:|
| usuarios | Registrarse / iniciar sesión | ✔ | ✔ | ✔ | ✔ |
| usuarios | CRUD de usuarios | ✔ | ✘ | ✘ | ✘ |
| productos | Ver productos y categorías | ✔ | ✔ | ✔ | ✔ |
| productos | Stock crítico | ✔ | ✔ | ✘ | ✘ |
| productos | Crear / editar / eliminar | ✔ | ✘ | ✘ | ✘ |
| productos | Descontar / aumentar stock | ✔ | ✔ | ✔ | ✘ |
| ordenes | Ver órdenes y detalle | todas | todas | solo las suyas | ✘ |
| ordenes | Crear orden | ✔ | ✔ | ✔ | ✘ |
| ordenes | Cambiar estado | ✔ | ✘ | ✘ | ✘ |
| inventario | Ver historial y alertas | ✔ | ✔ | ✘ | ✘ |
| inventario | Registrar movimientos | ✔ | ✘ | ✘ | ✘ |
| contenido | Ver artículos / enviar mensaje de contacto | ✔ | ✔ | ✔ | ✔ |
| contenido | Gestionar artículos y leer mensajes | ✔ | ✘ | ✘ | ✘ |

Sin token válido la respuesta es `401`; con token pero sin el rol necesario, `403`.

## Flujo de una compra
1. El cliente inicia sesión y recibe su token.
2. El front hace `POST /api/v1/ordenes` con la dirección y los productos (sin precios).
3. `ordenes-service` toma el **correo del token** (no del cuerpo), consulta nombre y precio de cada producto en `productos-service`, valida el stock, lo descuenta y guarda la orden en estado `PENDIENTE`. Si algo falla a medio camino, devuelve el stock ya descontado.
4. El administrador cambia el estado: `PENDIENTE → CONFIRMADA → EN_PREPARACION → ENVIADA → ENTREGADA` (o `CANCELADA`, que devuelve el stock).
5. El vendedor puede ver las órdenes; el cliente, las suyas.

## Pruebas
- Unitarias (no necesitan MySQL, salvo `contextLoads`): en cada servicio, `.\mvnw test`.
- Manuales: `pruebas-integracion.http` (extensión *REST Client* de VS Code) recorre el flujo completo; cada servicio tiene además su propio `pruebas.http`.

## Limitaciones conocidas
- Un token ya emitido sigue siendo válido hasta que vence (30 min) aunque se desactive al usuario.
- `descontar` / `aumentar` stock están abiertos a cualquier usuario con sesión porque el flujo de compra los necesita con el token del cliente. En un sistema real se usaría una credencial entre servicios.
