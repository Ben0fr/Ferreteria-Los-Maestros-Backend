# contenido-service

Microservicio de **contenido** de Ferretería Los Maestros: los artículos del blog (CRUD) y los mensajes del formulario de contacto.

- Puerto: **8084**
- Base de datos MySQL: `ferreteria_contenido` (tablas `articulo` y `mensaje_contacto`)
- No depende de otros microservicios.

## Blog (`/api/v1/articulos`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Lista los artículos (más nuevos primero) |
| GET | `/{id}` | Detalle de un artículo |
| POST | `/` | Crea un artículo (la fecha de publicación es la de hoy) |
| PUT | `/{id}` | Edita un artículo |
| DELETE | `/{id}` | Elimina un artículo |

```json
{
  "titulo": "Como elegir un taladro",
  "resumen": "Que mirar antes de comprar un taladro para el hogar.",
  "contenido": "Primer parrafo.\n\nSegundo parrafo.",
  "imagen": "/img/taladro.jpg",
  "imagenAlt": "Taladro percutor",
  "autor": "Ferretería Los Maestros"
}
```

El `contenido` se guarda como un solo texto con los párrafos separados por una línea en blanco. La respuesta incluye además `parrafos` (la lista ya separada), pensada para la página de detalle del blog, que muestra un párrafo por línea. `autor`, `imagen` e `imagenAlt` son opcionales.

## Contacto (`/api/v1/contacto`)

| Método | Ruta | Quién lo usa | Descripción |
|---|---|---|---|
| POST | `/` | Visitante (formulario de la tienda) | Envía un mensaje |
| GET | `/` | Administrador | Lista los mensajes. Filtro opcional `?leido=true` o `?leido=false` |
| GET | `/{id}` | Administrador | Ver un mensaje |
| PATCH | `/{id}/leido` | Administrador | Marca un mensaje como leído o pendiente (`{"leido": true}`) |
| DELETE | `/{id}` | Administrador | Elimina un mensaje |

El mensaje sigue las mismas reglas que el formulario de la tienda: nombre obligatorio (máx. 100), correo obligatorio con dominio `@duoc.cl`, `@profesor.duoc.cl` o `@gmail.com` (máx. 100) y comentario obligatorio (máx. 500).

## Errores

Mismo formato que los otros microservicios:

```json
{ "status": 400, "message": "El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com", "timestamp": "..." }
```

## Cómo ejecutarlo

1. Tener MySQL en marcha (opcional: ejecutar `db/contenido-schema.sql` en MySQL Workbench; si no, las tablas se crean solas).
2. Desde esta carpeta: `mvn spring-boot:run` (o ejecutar `ContenidoServiceApplication` desde el IDE).

Variables de entorno opcionales: `DB_USER` y `DB_PASSWORD`.

La primera vez se cargan los 2 artículos del blog de la tienda y 2 mensajes de ejemplo. Las pruebas manuales están en `pruebas.http`. Las pruebas automáticas (`mvn test`) usan H2 en memoria y no necesitan MySQL.
