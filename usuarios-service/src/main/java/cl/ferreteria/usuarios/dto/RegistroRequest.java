package cl.ferreteria.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Registro público desde la tienda: siempre crea un CLIENTE (el rol nunca viene del request)
public record RegistroRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String nombre,

    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    String apellidos,

    @NotBlank(message = "El correo es obligatorio")
    @Pattern(regexp = Reglas.CORREO, message = Reglas.MSG_CORREO)
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 10, message = Reglas.MSG_CLAVE)
    String clave

) {}
