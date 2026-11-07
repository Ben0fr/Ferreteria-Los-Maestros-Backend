package cl.ferreteria.productos.dto;

import cl.ferreteria.productos.model.Productos;

public record ProductoResponse(
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
) {
    
    public static ProductoResponse desde(Productos p) {
        boolean critico = p.getStockCritico() != null && p.getStock() <= p.getStockCritico();
        return new ProductoResponse(
            p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
            p.getCategoria().getNombre(), p.getCategoria().getSlug(), p.getCategoria().getId(),
            p.getPrecio(), p.getStock(), p.getStockCritico(), critico, p.getIcono()
        );

    }
}
