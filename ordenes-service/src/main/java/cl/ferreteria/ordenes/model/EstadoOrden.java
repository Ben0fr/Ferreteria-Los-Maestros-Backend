package cl.ferreteria.ordenes.model;

/**
 * Estados por los que pasa una orden.
 * Flujo normal: PENDIENTE -> CONFIRMADA -> EN_PREPARACION -> ENVIADA -> ENTREGADA.
 * Una orden se puede CANCELAR mientras no haya sido enviada.
 */
public enum EstadoOrden {
    PENDIENTE,
    CONFIRMADA,
    EN_PREPARACION,
    ENVIADA,
    ENTREGADA,
    CANCELADA;

    // Indica si una orden que esta en este estado puede pasar al estado nuevo
    public boolean puedePasarA(EstadoOrden nuevo) {
        return switch (this) {
            case PENDIENTE -> nuevo == CONFIRMADA || nuevo == CANCELADA;
            case CONFIRMADA -> nuevo == EN_PREPARACION || nuevo == CANCELADA;
            case EN_PREPARACION -> nuevo == ENVIADA || nuevo == CANCELADA;
            case ENVIADA -> nuevo == ENTREGADA;
            case ENTREGADA, CANCELADA -> false;
        };
    }
}
