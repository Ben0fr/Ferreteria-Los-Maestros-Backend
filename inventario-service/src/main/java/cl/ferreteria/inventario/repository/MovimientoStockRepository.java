package cl.ferreteria.inventario.repository;

import cl.ferreteria.inventario.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByProductoIdOrderByFechaDesc(Long productoId);
    List<MovimientoStock> findAllByOrderByFechaDesc();
}
