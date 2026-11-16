package cl.ferreteria.productos.security;

public record UsuarioAutenticado(Long id, String correo, String rol) {
}
