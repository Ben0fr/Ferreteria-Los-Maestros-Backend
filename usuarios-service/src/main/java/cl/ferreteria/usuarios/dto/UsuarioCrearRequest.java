package cl.ferreteria.usuarios.dto;

import cl.ferreteria.usuarios.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Creación de usuarios por un administrador (puede crear cualquier rol)
public record UsuarioCrearRequest(

    @Pattern(regexp = Reglas.RUN, message = Reglas.MSG_RUN)
    String run,

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
    String clave,

    @NotNull(message = "El rol es obligatorio")
    Rol rol,

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    LocalDate fechaNacimiento,

    @Size(max = 100, message = "La región no puede superar los 100 caracteres")
    String region,

    @Size(max = 100, message = "La comuna no puede superar los 100 caracteres")
    String comuna,

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    String direccion

) {}
