package cl.ferreteria.productos.dto;

import jakarta.validation.constraints.Min;

public record StockRequest(
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    int cantidad
) {}
