package cl.ferreteria.inventario.dto;

import java.time.LocalDateTime;

import cl.ferreteria.inventario.model.MovimientoStock;
import cl.ferreteria.inventario.model.TipoMovimiento;

public record MovimientoResponse(
    Long id,
    Long productoId,
    String productoNombre,
    TipoMovimiento tipo,
    int cantidad,
    int stockResultante,
    String motivo,
    LocalDateTime fecha
) {
    public static MovimientoResponse desde(MovimientoStock m) {
        return new MovimientoResponse(m.getId(), m.getProductoId(), m.getProductoNombre(), m.getTipo(), 
        m.getCantidad(), m.getStockResultante(), m.getMotivo(), m.getFecha());
    }
}
