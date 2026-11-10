package cl.ferreteria.usuarios.config;

import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import cl.ferreteria.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Crea un usuario de cada rol la primera vez que se levanta el servicio (solo si la tabla está vacía)
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarUsuariosIniciales(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }

            crear(repo, encoder, "19011022K", "Administrador", "Sistema", "admin@gmail.com", "admin123", Rol.ADMINISTRADOR);
            crear(repo, encoder, "189876543", "María", "González", "maria@duoc.cl", "vend123", Rol.VENDEDOR);
            crear(repo, encoder, null, "Juan", "Pérez", "juan@gmail.com", "cli123", Rol.CLIENTE);
        };
    }

    private void crear(UsuarioRepository repo, PasswordEncoder encoder, String run, String nombre,
                       String apellidos, String correo, String clave, Rol rol) {
        Usuario u = new Usuario();
        u.setRun(run);
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setClave(encoder.encode(clave));
        u.setRol(rol);
        u.setActivo(true);
        repo.save(u);
    }
}
