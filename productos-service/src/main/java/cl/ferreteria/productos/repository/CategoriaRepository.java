package cl.ferreteria.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.ferreteria.productos.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsBySlug(String slug);
    boolean existsByNombre(String nombre);
} 
    

