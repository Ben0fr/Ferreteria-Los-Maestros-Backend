package cl.ferreteria.usuarios.repository;

import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoAndIdNot(String correo, Long id);
    boolean existsByRun(String run);
    boolean existsByRunAndIdNot(String run, Long id);
    List<Usuario> findByRol(Rol rol);
}
