package cl.ferreteria.productos.service;

import cl.ferreteria.productos.dto.ProductoRequest;
import cl.ferreteria.productos.dto.ProductoResponse;
import cl.ferreteria.productos.model.Productos;
import cl.ferreteria.productos.repository.ProductosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service 
public class ProductoService {
    
    private static final String ICONO_POR_DEFECTO = "caja-herramientas.svg";

    private final ProductosRepository repo;
    private final CategoriaService categorias;

    public ProductoService(ProductosRepository repo, CategoriaService categorias) {
        this.repo = repo;
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String categoriaSlug) {
        List<Productos> lista = (categoriaSlug == null || categoriaSlug.isBlank())
            ? repo.findAll()
            : repo.findByCategoriaSlug(categoriaSlug);
        return lista.stream().map(p -> ProductoResponse.desde(p)).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarStockCritico() {
        return repo.findAll().stream()
        .filter(p -> p.getStockCritico() != null && p.getStock() <= p.getStockCritico())
        .map(ProductoResponse::desde)
        .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.desde(buscar(id));
    }

    @Transactional 
    public ProductoResponse crear(ProductoRequest r) {
        String codigo = r.codigo().trim();
        if (repo.existsByCodigo(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese codigo");
        }
        Productos p = new Productos();
        p.setCodigo(codigo);
        p.setIcono(ICONO_POR_DEFECTO);
        aplicar(p, r);
        return ProductoResponse.desde(repo.save(p));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest r) {
        Productos p = buscar(id);
        aplicar(p, r);
        return ProductoResponse.desde(repo.save(p));
    }
    
    @Transactional 
    public void eliminar(Long id) {
        buscar(id);
        repo.deleteById(id);
    }

    @Transactional 
    public ProductoResponse descontarStock(Long id, int cantidad) {
        Productos p = buscar(id);
        if (p.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No hay stock suficiente de " + p.getNombre() + "(disponible: " + p.getStock() + ")"
             );
        }
        p.setStock(p.getStock() - cantidad);
        return ProductoResponse.desde(repo.save(p));
    }

    private Productos buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private void aplicar(Productos p, ProductoRequest r) {
        p.setNombre(r.nombre().trim());
        p.setDescripcion(r.descripcion());
        p.setPrecio(r.precio());
        p.setStock(r.stock());
        p.setStockCritico(r.stockCritico());
        p.setCategoria(categorias.obtener(r.categoriaId()));
    }
}
