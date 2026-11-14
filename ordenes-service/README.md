# ordenes-service

Microservicio de **órdenes** de Ferretería Los Maestros. Permite crear órdenes de compra, listarlas, ver su detalle y cambiar su estado.

- Puerto: **8083**
- Base de datos MySQL: `ferreteria_ordenes` (tablas `orden` y `detalle_orden`)
- Depende de `productos-service` (puerto 8082) para consultar precios y mover el stock.

## Endpoints (`/api/v1/ordenes`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Lista las órdenes (más nuevas primero). Filtros opcionales: `?estado=PENDIENTE`, `?correo=cliente@gmail.com` |
| GET | `/estados` | Estados posibles de una orden |
| GET | `/{id}` | Detalle de una orden con sus productos |
| POST | `/` | Crea una orden (estado inicial `PENDIENTE`) |
| PATCH | `/{id}/estado` | Cambia el estado de la orden |

### Crear una orden

```json
{
  "clienteNombre": "Pedro Soto",
  "clienteCorreo": "pedro@gmail.com",
  "direccionEntrega": "Calle Prat 789, La Serena",
  "items": [ { "productoId": 1, "cantidad": 2 } ]
}
```

El servicio toma nombre y precio de cada producto desde `productos-service` (el cliente no envía precios), verifica el stock, lo descuenta y calcula el total. Si algo falla a mitad de camino, devuelve el stock ya descontado.

### Estados

`PENDIENTE → CONFIRMADA → EN_PREPARACION → ENVIADA → ENTREGADA`

Se puede pasar a `CANCELADA` desde `PENDIENTE`, `CONFIRMADA` o `EN_PREPARACION`; al cancelar se devuelve el stock a `productos-service`. `ENTREGADA` y `CANCELADA` son estados finales. Un cambio no permitido responde `409`.

### Errores

Mismo formato que los otros microservicios:

```json
{ "status": 409, "message": "No hay stock suficiente de Martillo Carpintero (disponible: 3)", "timestamp": "..." }
```

## Cómo ejecutarlo

1. Tener MySQL en marcha (opcional: ejecutar `db/ordenes-schema.sql` en MySQL Workbench; si no, las tablas se crean solas).
2. Tener `productos-service` corriendo en el puerto 8082 (necesario solo para crear y cancelar órdenes).
3. Desde esta carpeta: `mvn spring-boot:run` (o ejecutar `OrdenesServiceApplication` desde el IDE).

Variables de entorno opcionales: `DB_USER`, `DB_PASSWORD`, `PRODUCTOS_URL` (por defecto `http://localhost:8082`).

La primera vez se cargan 3 órdenes de ejemplo. Las pruebas manuales están en `pruebas.http`. Las pruebas automáticas (`mvn test`) usan H2 en memoria y no necesitan MySQL.
