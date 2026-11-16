package cl.ferreteria.inventario.security;

public record UsuarioAutenticado(Long id, String correo, String rol) {
}
