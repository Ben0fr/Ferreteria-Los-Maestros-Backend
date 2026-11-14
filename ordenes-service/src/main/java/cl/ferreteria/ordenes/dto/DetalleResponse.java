package cl.ferreteria.ordenes.dto;

import cl.ferreteria.ordenes.model.DetalleOrden;

public record DetalleResponse(
    Long id,
    Long productoId,
    String productoNombre,
    int precioUnitario,
    int cantidad,
    int subtotal
) {

    public static DetalleResponse desde(DetalleOrden d) {
        return new DetalleResponse(
            d.getId(), d.getProductoId(), d.getProductoNombre(),
            d.getPrecioUnitario(), d.getCantidad(), d.getSubtotal()
        );
    }
}
