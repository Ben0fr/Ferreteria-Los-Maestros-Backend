package cl.ferreteria.usuarios.service;

import cl.ferreteria.usuarios.dto.RegistroRequest;
import cl.ferreteria.usuarios.dto.UsuarioActualizarRequest;
import cl.ferreteria.usuarios.dto.UsuarioCrearRequest;
import cl.ferreteria.usuarios.dto.UsuarioResponse;
import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import cl.ferreteria.usuarios.security.UsuarioAutenticado;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Rol rol) {
        List<Usuario> lista = (rol == null) ? repo.findAll() : repo.findByRol(rol);
        return lista.stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    // Registro público: SIEMPRE crea un cliente, así nadie puede "autoasignarse" el rol de administrador
    @Transactional
    public UsuarioResponse registrarCliente(RegistroRequest r) {
        String correo = normalizarCorreo(r.correo());
        validarCorreoLibre(correo, null);

        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setApellidos(r.apellidos() == null ? null : r.apellidos().trim());
        u.setCorreo(correo);
        u.setClave(encoder.encode(r.clave()));
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        return UsuarioResponse.desde(repo.save(u));
    }

    // Creación por el administrador (puede asignar cualquier rol)
    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest r) {
        String correo = normalizarCorreo(r.correo());
        String run = normalizarRun(r.run());
        validarCorreoLibre(correo, null);
        validarRunLibre(run, null);

        Usuario u = new Usuario();
        aplicarDatos(u, run, r.nombre(), r.apellidos(), correo, r.rol(),
            r.fechaNacimiento(), r.region(), r.comuna(), r.direccion());
        u.setClave(encoder.encode(r.clave()));
        u.setActivo(true);
        return UsuarioResponse.desde(repo.save(u));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest r, UsuarioAutenticado actual) {
        Usuario u = buscar(id);

        // Regla de negocio: un administrador no puede quitarse su propio rol ni desactivarse
        // (así el sistema nunca se queda sin administradores)
        boolean esUnoMismo = u.getId().equals(actual.id());
        if (esUnoMismo && (r.rol() != u.getRol() || Boolean.FALSE.equals(r.activo()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No puedes cambiar tu propio rol ni desactivar tu propia cuenta");
        }

        String correo = normalizarCorreo(r.correo());
        String run = normalizarRun(r.run());
        validarCorreoLibre(correo, id);
        validarRunLibre(run, id);

        aplicarDatos(u, run, r.nombre(), r.apellidos(), correo, r.rol(),
            r.fechaNacimiento(), r.region(), r.comuna(), r.direccion());
        u.setActivo(r.activo());

        // La clave solo se cambia si viene en la petición
        if (r.clave() != null && !r.clave().isBlank()) {
            u.setClave(encoder.encode(r.clave()));
        }
        return UsuarioResponse.desde(repo.save(u));
    }

    @Transactional
    public void eliminar(Long id, UsuarioAutenticado actual) {
        if (id.equals(actual.id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes eliminar tu propia cuenta");
        }
        buscar(id);
        repo.deleteById(id);
    }

    // ---------------- auxiliares ----------------

    private Usuario buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void aplicarDatos(Usuario u, String run, String nombre, String apellidos, String correo, Rol rol,
                              LocalDate fechaNacimiento, String region, String comuna, String direccion) {
        u.setRun(run);
        u.setNombre(nombre.trim());
        u.setApellidos(apellidos == null ? null : apellidos.trim());
        u.setCorreo(correo);
        u.setRol(rol);
        u.setFechaNacimiento(fechaNacimiento);
        u.setRegion(region);
        u.setComuna(comuna);
        u.setDireccion(direccion);
    }

    private static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    private static String normalizarRun(String run) {
        return (run == null || run.isBlank()) ? null : run.trim().toUpperCase();
    }

    // idExcluido = el usuario que se está editando (para que no choque consigo mismo); null al crear
    private void validarCorreoLibre(String correo, Long idExcluido) {
        boolean repetido = (idExcluido == null)
            ? repo.existsByCorreo(correo)
            : repo.existsByCorreoAndIdNot(correo, idExcluido);
        if (repetido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
    }

    private void validarRunLibre(String run, Long idExcluido) {
        if (run == null) {
            return;
        }
        boolean repetido = (idExcluido == null)
            ? repo.existsByRun(run)
            : repo.existsByRunAndIdNot(run, idExcluido);
        if (repetido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese RUN");
        }
    }
}
