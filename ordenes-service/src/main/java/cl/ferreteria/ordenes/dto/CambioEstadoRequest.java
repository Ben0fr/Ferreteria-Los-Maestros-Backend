package cl.ferreteria.ordenes.dto;

import cl.ferreteria.ordenes.model.EstadoOrden;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(

    @NotNull(message = "El estado es obligatorio")
    EstadoOrden estado

) {}
