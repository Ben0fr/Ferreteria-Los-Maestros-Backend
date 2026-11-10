package cl.ferreteria.usuarios.security;

import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "secreto-de-pruebas-con-mas-de-32-caracteres-123456";

    private JwtService jwt;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        jwt = new JwtService(SECRETO, 30, 7);
        usuario = new Usuario();
        usuario.setId(5L);
        usuario.setCorreo("maria@duoc.cl");
        usuario.setRol(Rol.VENDEDOR);
    }

    @Test
    void accessToken_contieneCorreoRolYTipo() {
        Claims c = jwt.leer(jwt.generarAccessToken(usuario));

        assertEquals("maria@duoc.cl", c.getSubject());
        assertEquals("VENDEDOR", c.get("rol", String.class));
        assertEquals(JwtService.TIPO_ACCESS, c.get("tipo", String.class));
        assertEquals(5L, ((Number) c.get("uid")).longValue());
    }

    @Test
    void refreshToken_tieneTipoRefresh() {
        Claims c = jwt.leer(jwt.generarRefreshToken(usuario));
        assertEquals(JwtService.TIPO_REFRESH, c.get("tipo", String.class));
    }

    @Test
    void tokenVencido_esRechazado() {
        JwtService vencido = new JwtService(SECRETO, -1, 7); // expira un minuto antes de crearse
        String token = vencido.generarAccessToken(usuario);

        assertThrows(JwtException.class, () -> jwt.leer(token));
    }

    @Test
    void tokenAlterado_esRechazado() {
        String token = jwt.generarAccessToken(usuario);
        String alterado = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwt.leer(alterado));
    }

    @Test
    void tokenFirmadoConOtraClave_esRechazado() {
        JwtService otro = new JwtService("otra-clave-completamente-distinta-de-32-o-mas-caracteres", 30, 7);
        String token = otro.generarAccessToken(usuario);

        assertThrows(JwtException.class, () -> jwt.leer(token));
    }

    @Test
    void secretoCorto_noSePermite() {
        assertThrows(RuntimeException.class, () -> new JwtService("corto", 30, 7));
    }
}
