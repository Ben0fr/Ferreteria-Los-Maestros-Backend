package cl.ferreteria.ordenes.dto;

import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.model.Orden;

import java.time.LocalDateTime;
import java.util.List;

// Orden completa, con el detalle de cada producto (para la vista de detalle / boleta)
public record OrdenResponse(
    Long id,
    String clienteNombre,
    String clienteCorreo,
    String direccionEntrega,
    LocalDateTime fecha,
    EstadoOrden estado,
    int total,
    List<DetalleResponse> detalles
) {

    public static OrdenResponse desde(Orden o) {
        List<DetalleResponse> detalles = o.getDetalles().stream()
            .map(DetalleResponse::desde)
            .toList();
        return new OrdenResponse(
            o.getId(), o.getClienteNombre(), o.getClienteCorreo(), o.getDireccionEntrega(),
            o.getFecha(), o.getEstado(), o.getTotal(), detalles
        );
    }
}
