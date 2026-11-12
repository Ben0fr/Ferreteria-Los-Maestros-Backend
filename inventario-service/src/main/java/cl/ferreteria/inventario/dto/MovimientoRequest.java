package cl.ferreteria.inventario.dto;

import cl.ferreteria.inventario.model.TipoMovimiento;
import jakarta.validation.constraints.*;

public record MovimientoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "El tipo de movimiento es obligatorio (Entrada o salida)")
        TipoMovimiento tipo,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad,

        @Size(max = 200, message = "El motivo no puede superar los 200 caracteres")
        String motivo

) {} 
