package cl.ferreteria.contenido.repository;

import cl.ferreteria.contenido.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    List<Articulo> findAllByOrderByFechaPublicacionDescIdDesc();
}
