package cl.ferreteria.usuarios.service;

import cl.ferreteria.usuarios.dto.RegistroRequest;
import cl.ferreteria.usuarios.dto.UsuarioActualizarRequest;
import cl.ferreteria.usuarios.dto.UsuarioResponse;
import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import cl.ferreteria.usuarios.security.UsuarioAutenticado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(repo, encoder);
    }

    @Test
    void registrarCliente_creaClienteConClaveEncriptada() {
        when(repo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponse r = servicio.registrarCliente(
            new RegistroRequest("Pedro", "Soto", "  Pedro@Gmail.com ", "1234"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals(Rol.CLIENTE, r.rol());
        assertEquals("pedro@gmail.com", guardado.getCorreo());      // normalizado
        assertNotEquals("1234", guardado.getClave());               // no se guarda en texto plano
        assertTrue(encoder.matches("1234", guardado.getClave()));   // pero coincide con el hash
    }

    @Test
    void registrarCliente_conCorreoRepetido_lanza409() {
        when(repo.existsByCorreo("pedro@gmail.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            servicio.registrarCliente(new RegistroRequest("Pedro", null, "pedro@gmail.com", "1234")));

        assertEquals(409, ex.getStatusCode().value());
        verify(repo, never()).save(any());
    }

    @Test
    void eliminar_aSiMismo_lanza409() {
        UsuarioAutenticado yo = new UsuarioAutenticado(1L, "admin@gmail.com", Rol.ADMINISTRADOR);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.eliminar(1L, yo));

        assertEquals(409, ex.getStatusCode().value());
        verify(repo, never()).deleteById(any());
    }

    @Test
    void eliminar_usuarioInexistente_lanza404() {
        UsuarioAutenticado yo = new UsuarioAutenticado(1L, "admin@gmail.com", Rol.ADMINISTRADOR);
        when(repo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.eliminar(99L, yo));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void eliminar_otroUsuario_loBorra() {
        UsuarioAutenticado yo = new UsuarioAutenticado(1L, "admin@gmail.com", Rol.ADMINISTRADOR);
        Usuario otro = new Usuario();
        otro.setId(2L);
        when(repo.findById(2L)).thenReturn(Optional.of(otro));

        servicio.eliminar(2L, yo);

        verify(repo).deleteById(2L);
    }

    @Test
    void actualizar_propioRol_lanza409() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setRol(Rol.ADMINISTRADOR);
        when(repo.findById(1L)).thenReturn(Optional.of(admin));
        UsuarioAutenticado yo = new UsuarioAutenticado(1L, "admin@gmail.com", Rol.ADMINISTRADOR);

        UsuarioActualizarRequest cambio = new UsuarioActualizarRequest(
            null, "Admin", null, "admin@gmail.com", null, Rol.VENDEDOR, true, null, null, null, null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> servicio.actualizar(1L, cambio, yo));

        assertEquals(409, ex.getStatusCode().value());
        verify(repo, never()).save(any());
    }

    @Test
    void actualizar_sinClave_mantieneLaClaveActual() {
        Usuario u = new Usuario();
        u.setId(2L);
        u.setRol(Rol.VENDEDOR);
        u.setClave("hash-original");
        when(repo.findById(2L)).thenReturn(Optional.of(u));
        when(repo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        UsuarioAutenticado admin = new UsuarioAutenticado(1L, "admin@gmail.com", Rol.ADMINISTRADOR);

        UsuarioActualizarRequest cambio = new UsuarioActualizarRequest(
            null, "María", "González", "maria@duoc.cl", null, Rol.VENDEDOR, true, null, null, null, null);

        servicio.actualizar(2L, cambio, admin);

        assertEquals("hash-original", u.getClave());
        assertEquals("María", u.getNombre());
    }
}
