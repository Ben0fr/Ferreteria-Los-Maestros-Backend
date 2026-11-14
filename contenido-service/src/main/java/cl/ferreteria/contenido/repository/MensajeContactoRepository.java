package cl.ferreteria.contenido.repository;

import cl.ferreteria.contenido.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {

    List<MensajeContacto> findAllByOrderByFechaDescIdDesc();

    List<MensajeContacto> findByLeidoOrderByFechaDescIdDesc(boolean leido);
}
