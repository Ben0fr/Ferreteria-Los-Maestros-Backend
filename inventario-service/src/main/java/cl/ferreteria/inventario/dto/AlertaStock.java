package cl.ferreteria.inventario.dto;

public record AlertaStock(
    Long productoId,
    String codigo,
    String nombre,
    int stock,
    Integer stockCritico,
    String nivel    
) {
    
}
