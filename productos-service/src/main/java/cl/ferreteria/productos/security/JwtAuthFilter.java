package cl.ferreteria.productos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Se ejecuta en cada petición: si trae "Authorization: Bearer <token>" válido, deja al usuario autenticado con su rol.
// Si el token falta o es inválido, no autentica; luego SecurityConfig decide si la ruta lo exige (401) o es pública.
// No es un @Component a propósito: se crea dentro de SecurityConfig para que solo corra dentro de la cadena de seguridad.
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String cabecera = request.getHeader("Authorization");

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                Claims claims = jwt.leer(cabecera.substring(7));

                // Un refresh token NO sirve para acceder a recursos
                if (JwtService.TIPO_ACCESS.equals(claims.get("tipo", String.class))) {
                    String rol = claims.get("rol", String.class);
                    Long id = ((Number) claims.get("uid")).longValue();
                    UsuarioAutenticado principal = new UsuarioAutenticado(id, claims.getSubject(), rol);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException | NullPointerException e) {
                // Token inválido, alterado o vencido: se sigue sin autenticar
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
