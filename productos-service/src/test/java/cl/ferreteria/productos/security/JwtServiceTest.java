package cl.ferreteria.productos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "secreto-de-pruebas-con-mas-de-32-caracteres-123456";

    private final JwtService jwt = new JwtService(SECRETO);

    // Arma un token con el mismo formato que emite usuarios-service
    private String token(String secreto, String rol, String tipo, long duracionMs) {
        SecretKey clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        Date ahora = new Date();
        return Jwts.builder()
            .subject("admin@gmail.com")
            .claim("uid", 1L)
            .claim("rol", rol)
            .claim("tipo", tipo)
            .issuedAt(ahora)
            .expiration(new Date(ahora.getTime() + duracionMs))
            .signWith(clave)
            .compact();
    }

    @Test
    void leeUnTokenValidoEmitidoConElMismoSecreto() {
        Claims c = jwt.leer(token(SECRETO, "ADMINISTRADOR", "access", 60_000));

        assertEquals("admin@gmail.com", c.getSubject());
        assertEquals("ADMINISTRADOR", c.get("rol", String.class));
        assertEquals(JwtService.TIPO_ACCESS, c.get("tipo", String.class));
    }

    @Test
    void rechazaUnTokenFirmadoConOtroSecreto() {
        String ajeno = token("otro-secreto-completamente-distinto-de-32-o-mas-caracteres", "ADMINISTRADOR", "access", 60_000);

        assertThrows(JwtException.class, () -> jwt.leer(ajeno));
    }

    @Test
    void rechazaUnTokenVencido() {
        String vencido = token(SECRETO, "ADMINISTRADOR", "access", -60_000);

        assertThrows(JwtException.class, () -> jwt.leer(vencido));
    }
}
