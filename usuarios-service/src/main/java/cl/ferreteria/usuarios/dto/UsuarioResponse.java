package cl.ferreteria.usuarios.dto;

import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;

import java.time.LocalDate;

// Lo que el API devuelve de un usuario: nunca incluye la clave
public record UsuarioResponse(
    Long id,
    String run,
    String nombre,
    String apellidos,
    String correo,
    Rol rol,
    String tipo,
    boolean activo,
    LocalDate fechaNacimiento,
    String region,
    String comuna,
    String direccion
) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
            u.getId(), u.getRun(), u.getNombre(), u.getApellidos(), u.getCorreo(),
            u.getRol(), u.getRol().getEtiqueta(), u.isActivo(),
            u.getFechaNacimiento(), u.getRegion(), u.getComuna(), u.getDireccion()
        );
    }
}
