package cl.ferreteria.contenido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArticuloRequest(

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150, message = "El titulo no puede superar los 150 caracteres")
    String titulo,

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 300, message = "El resumen no puede superar los 300 caracteres")
    String resumen,

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 10000, message = "El contenido no puede superar los 10000 caracteres")
    String contenido,

    @Size(max = 255, message = "La imagen no puede superar los 255 caracteres")
    String imagen,

    @Size(max = 150, message = "El texto alternativo no puede superar los 150 caracteres")
    String imagenAlt,

    @Size(max = 100, message = "El autor no puede superar los 100 caracteres")
    String autor

) {}
