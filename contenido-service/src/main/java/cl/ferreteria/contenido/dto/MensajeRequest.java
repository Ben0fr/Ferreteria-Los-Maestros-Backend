package cl.ferreteria.contenido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Mismas reglas que el formulario de contacto de la tienda
public record MensajeRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String nombre,

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
             message = "El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com")
    String correo,

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    String comentario

) {}
