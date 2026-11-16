package cl.ferreteria.usuarios.service;

import cl.ferreteria.usuarios.dto.AuthResponse;
import cl.ferreteria.usuarios.dto.LoginRequest;
import cl.ferreteria.usuarios.dto.RefreshRequest;
import cl.ferreteria.usuarios.dto.RegistroRequest;
import cl.ferreteria.usuarios.dto.UsuarioResponse;
import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import cl.ferreteria.usuarios.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioRepository repo;
    private final UsuarioService usuarios;
    private final JwtService jwt;

    public AuthService(AuthenticationManager authManager, UsuarioRepository repo,
                       UsuarioService usuarios, JwtService jwt) {
        this.authManager = authManager;
        this.repo = repo;
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        return usuarios.registrarCliente(request);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String correo = request.correo().trim().toLowerCase();

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(correo, request.clave()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }

        Usuario u = repo.findByCorreo(correo).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        return construirRespuesta(u);
    }
    @Transactional(readOnly = true)
    public AuthResponse refrescar(RefreshRequest request) {
        Claims claims;
        try {
            claims = jwt.leer(request.refreshToken());
        } catch (JwtException | IllegalArgumentException e) {
            throw refreshInvalido();
        }
        if (!JwtService.TIPO_REFRESH.equals(claims.get("tipo", String.class))) {
            throw refreshInvalido();
        }

        Usuario u = repo.findByCorreo(claims.getSubject())
            .filter(Usuario::isActivo)
            .orElseThrow(this::refreshInvalido);
        return construirRespuesta(u);
    }

    private AuthResponse construirRespuesta(Usuario u) {
        return new AuthResponse(
            jwt.generarAccessToken(u),
            jwt.generarRefreshToken(u),
            "Bearer",
            jwt.getAccessSegundos(),
            UsuarioResponse.desde(u)
        );
    }

    private ResponseStatusException refreshInvalido() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido o expirado");
    }
}
