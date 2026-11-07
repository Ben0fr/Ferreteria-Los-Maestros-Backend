package cl.ferreteria.productos.dto;

import jakarta.validation.constraints.*;

public record ProductoRequest(
     
    @NotBlank(message = "El codigo es obligatorio")
    @Size(min = 3, max = 30, message = "El codigo debe tener entre 3 y 30 caracteres")
    String codigo,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar lo 100 caracteres")
    String nombre,

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
    String descripcion,

    @Min(value = 0, message = "El precio no puede ser negativo")
    int precio,

    @Min(value = 0, message = "El stock no puede ser negativo")
    int stock,

    @Min(value = 0, message = "El stock critico no puede ser negativo")
    Integer stockCritico,

    @NotNull(message = "La categoria es obligatoria")
    Long categoriaId

) {}
