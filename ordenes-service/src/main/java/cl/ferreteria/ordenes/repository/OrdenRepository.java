package cl.ferreteria.ordenes.repository;

import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findAllByOrderByFechaDescIdDesc();

    List<Orden> findByEstadoOrderByFechaDescIdDesc(EstadoOrden estado);

    List<Orden> findByClienteCorreoIgnoreCaseOrderByFechaDescIdDesc(String clienteCorreo);

    List<Orden> findByEstadoAndClienteCorreoIgnoreCaseOrderByFechaDescIdDesc(EstadoOrden estado, String clienteCorreo);
}
