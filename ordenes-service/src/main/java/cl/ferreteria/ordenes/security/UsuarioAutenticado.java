package cl.ferreteria.ordenes.security;

public record UsuarioAutenticado(Long id, String correo, String rol) {
}
