package cl.ferreteria.usuarios.security;

import cl.ferreteria.usuarios.model.Rol;

// Datos del usuario que hizo la petición, sacados del token JWT (disponible con @AuthenticationPrincipal)
public record UsuarioAutenticado(Long id, String correo, Rol rol) {
}
