package cl.ferreteria.ordenes.model;

public enum EstadoOrden {
    PENDIENTE,
    CONFIRMADA,
    EN_PREPARACION,
    ENVIADA,
    ENTREGADA,
    CANCELADA;

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
