package cl.ferreteria.ordenes.dto;

import cl.ferreteria.ordenes.model.DetalleOrden;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.model.Orden;

import java.time.LocalDateTime;

// Version corta de la orden, para mostrar en listados
public record OrdenResumen(
    Long id,
    String clienteNombre,
    String clienteCorreo,
    LocalDateTime fecha,
    EstadoOrden estado,
    int total,
    int cantidadUnidades
) {

    public static OrdenResumen desde(Orden o) {
        int unidades = o.getDetalles().stream().mapToInt(DetalleOrden::getCantidad).sum();
        return new OrdenResumen(
            o.getId(), o.getClienteNombre(), o.getClienteCorreo(),
            o.getFecha(), o.getEstado(), o.getTotal(), unidades
        );
    }
}
