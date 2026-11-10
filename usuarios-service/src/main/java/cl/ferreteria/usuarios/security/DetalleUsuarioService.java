package cl.ferreteria.usuarios.security;

import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Le enseña a Spring Security a buscar usuarios en nuestra base de datos (el "username" es el correo).
// Spring compara la contraseña recibida contra el hash BCrypt y rechaza las cuentas desactivadas.
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;

    public DetalleUsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = repo.findByCorreo(correo.trim().toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(u.getCorreo())
            .password(u.getClave())
            .roles(u.getRol().name())
            .disabled(!u.isActivo())
            .build();
    }
}
