package cl.ferreteria.productos.service;

import cl.ferreteria.productos.model.Categoria;
import cl.ferreteria.productos.repository.CategoriaRepository;
import cl.ferreteria.productos.repository.ProductosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service 
public class CategoriaService {
    
    private final CategoriaRepository repo;
    private final ProductosRepository productoRepo;

    public CategoriaService(CategoriaRepository repo, ProductosRepository productoRepo){
        this.repo = repo;
        this.productoRepo = productoRepo;
    }

    public List<Categoria> listar() {
        return repo.findAll();
    }

    public Categoria obtener(Long id){
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
    
    }

    public Categoria crear(Categoria c) {
        if (repo.existsBySlug(c.getSlug()) || repo.existsByNombre(c.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe una categoria con ese nombre o slug"
            );
        }
        c.setId(null);
        return repo.save(c);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria c = obtener(id);
        c.setNombre(datos.getNombre());
        c.setSlug(datos.getSlug());
        c.setIcono(datos.getIcono());
        return repo.save(c);
    }

    public void eliminar(long id){
        obtener(id);
        if (productoRepo.existsByCategoriaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se puede eliminar: Hay productos en esta categoria"
            );
        }
        repo.deleteById(id);
    }

}
