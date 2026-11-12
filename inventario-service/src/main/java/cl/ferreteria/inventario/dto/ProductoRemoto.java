package cl.ferreteria.inventario.dto;

public record ProductoRemoto(
    Long id,
    String codigo,
    String nombre,
    String descripcion,
    String categoria,
    String categoriaSlug,
    Long categoriaId,
    int precio,
    int stock,
    Integer stockCritico,
    boolean stockEnNivelCritico,
    String icono
    
) {}
