package cl.ferreteria.productos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

// Versión de SOLO LECTURA del JwtService de usuarios-service: este microservicio no emite tokens, solo los verifica.
// Funciona porque usa el mismo secreto (app.jwt.secret) con el que usuarios-service firma los tokens.
@Service
public class JwtService {

    public static final String TIPO_ACCESS = "access";

    private final SecretKey clave;

    public JwtService(@Value("${app.jwt.secret}") String secreto) {
        // Exige al menos 256 bits (32 caracteres); si el secreto es más corto, falla al iniciar
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    // Verifica la firma y la expiración. Lanza JwtException si el token es inválido, está alterado o venció
    public Claims leer(String token) {
        return Jwts.parser()
            .verifyWith(clave)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
