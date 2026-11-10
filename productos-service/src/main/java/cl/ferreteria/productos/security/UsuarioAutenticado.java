package cl.ferreteria.productos.security;

// Datos del usuario que hizo la petición, sacados del token JWT (el rol llega como texto: ADMINISTRADOR, VENDEDOR o CLIENTE)
public record UsuarioAutenticado(Long id, String correo, String rol) {
}
