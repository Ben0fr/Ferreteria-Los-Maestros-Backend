package cl.ferreteria.usuarios.security;

import cl.ferreteria.usuarios.model.Rol;

public record UsuarioAutenticado(Long id, String correo, Rol rol) {
}
