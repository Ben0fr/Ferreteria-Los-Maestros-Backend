package cl.ferreteria.productos.repository;

import cl.ferreteria.productos.model.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByCategoriaId(Long categoriaId);
    List<Productos> findByCategoriaSlug(String slug);
        
}

