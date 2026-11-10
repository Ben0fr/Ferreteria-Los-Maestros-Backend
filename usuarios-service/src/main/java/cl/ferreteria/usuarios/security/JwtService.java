package cl.ferreteria.usuarios.security;

import cl.ferreteria.usuarios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Genera y verifica tokens JWT firmados con HMAC-SHA256.
// Hay dos tipos: "access" (corto, se manda en cada petición) y "refresh" (largo, solo sirve para pedir un access nuevo).
@Service
public class JwtService {

    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    private final SecretKey clave;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.access-minutos}") long accessMinutos,
                      @Value("${app.jwt.refresh-dias}") long refreshDias) {
        // Exige al menos 256 bits (32 caracteres); si el secreto es más corto, falla al iniciar
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.accessMs = accessMinutos * 60_000L;
        this.refreshMs = refreshDias * 24L * 60L * 60_000L;
    }

    public String generarAccessToken(Usuario u) {
        return construir(u, TIPO_ACCESS, accessMs);
    }

    public String generarRefreshToken(Usuario u) {
        return construir(u, TIPO_REFRESH, refreshMs);
    }

    public long getAccessSegundos() {
        return accessMs / 1000;
    }

    // Verifica la firma y la expiración. Lanza JwtException si el token es inválido, está alterado o venció
    public Claims leer(String token) {
        return Jwts.parser()
            .verifyWith(clave)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private String construir(Usuario u, String tipo, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
            .subject(u.getCorreo())
            .claim("uid", u.getId())
            .claim("rol", u.getRol().name())
            .claim("tipo", tipo)
            .issuedAt(ahora)
            .expiration(new Date(ahora.getTime() + duracionMs))
            .signWith(clave)
            .compact();
    }
}
