package cl.ferreteria.usuarios.service;

import cl.ferreteria.usuarios.dto.AuthResponse;
import cl.ferreteria.usuarios.dto.LoginRequest;
import cl.ferreteria.usuarios.dto.RefreshRequest;
import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import cl.ferreteria.usuarios.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authManager;
    @Mock
    private UsuarioRepository repo;
    @Mock
    private UsuarioService usuarios;

    private JwtService jwt;
    private AuthService servicio;
    private Usuario maria;

    @BeforeEach
    void preparar() {
        jwt = new JwtService("secreto-de-pruebas-con-mas-de-32-caracteres-123456", 30, 7);
        servicio = new AuthService(authManager, repo, usuarios, jwt);

        maria = new Usuario();
        maria.setId(2L);
        maria.setNombre("María");
        maria.setCorreo("maria@duoc.cl");
        maria.setRol(Rol.VENDEDOR);
        maria.setActivo(true);
    }

    @Test
    void login_correcto_entregaTokensYUsuario() {
        when(repo.findByCorreo("maria@duoc.cl")).thenReturn(Optional.of(maria));

        AuthResponse r = servicio.login(new LoginRequest(" Maria@duoc.cl ", "vend123"));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("Bearer", r.tipoToken());
        assertEquals(1800, r.expiraEnSegundos());
        assertEquals(Rol.VENDEDOR, r.usuario().rol());
        assertEquals(JwtService.TIPO_ACCESS, jwt.leer(r.accessToken()).get("tipo", String.class));
        assertEquals(JwtService.TIPO_REFRESH, jwt.leer(r.refreshToken()).get("tipo", String.class));
    }

    @Test
    void login_conCredencialesInvalidas_lanza401() {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("mala"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.login(new LoginRequest("maria@duoc.cl", "incorrecta")));

        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void refrescar_conRefreshValido_entregaTokensNuevos() {
        when(repo.findByCorreo("maria@duoc.cl")).thenReturn(Optional.of(maria));

        AuthResponse r = servicio.refrescar(new RefreshRequest(jwt.generarRefreshToken(maria)));

        assertNotNull(r.accessToken());
        assertEquals("maria@duoc.cl", r.usuario().correo());
    }

    @Test
    void refrescar_conAccessToken_lanza401() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.refrescar(new RefreshRequest(jwt.generarAccessToken(maria))));

        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void refrescar_conTokenBasura_lanza401() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.refrescar(new RefreshRequest("esto.no.es.un.jwt")));

        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void refrescar_conUsuarioDesactivado_lanza401() {
        maria.setActivo(false);
        when(repo.findByCorreo("maria@duoc.cl")).thenReturn(Optional.of(maria));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.refrescar(new RefreshRequest(jwt.generarRefreshToken(maria))));

        assertEquals(401, ex.getStatusCode().value());
    }
}
