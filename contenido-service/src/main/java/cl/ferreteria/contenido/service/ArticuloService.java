package cl.ferreteria.contenido.service;

import cl.ferreteria.contenido.dto.ArticuloRequest;
import cl.ferreteria.contenido.dto.ArticuloResponse;
import cl.ferreteria.contenido.model.Articulo;
import cl.ferreteria.contenido.repository.ArticuloRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ArticuloService {

    private static final String AUTOR_POR_DEFECTO = "Ferretería Los Maestros";

    private final ArticuloRepository repo;

    public ArticuloService(ArticuloRepository repo) {
        this.repo = repo;
    }

    // Lista los articulos, los mas nuevos primero
    @Transactional(readOnly = true)
    public List<ArticuloResponse> listar() {
        return repo.findAllByOrderByFechaPublicacionDescIdDesc().stream()
            .map(ArticuloResponse::desde)
            .toList();
    }

    @Transactional(readOnly = true)
    public ArticuloResponse obtener(Long id) {
        return ArticuloResponse.desde(buscar(id));
    }

    @Transactional
    public ArticuloResponse crear(ArticuloRequest r) {
        Articulo a = new Articulo();
        a.setFechaPublicacion(LocalDate.now());
        aplicar(a, r);
        return ArticuloResponse.desde(repo.save(a));
    }

    @Transactional
    public ArticuloResponse actualizar(Long id, ArticuloRequest r) {
        Articulo a = buscar(id);
        aplicar(a, r);
        return ArticuloResponse.desde(repo.save(a));
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        repo.deleteById(id);
    }

    private Articulo buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Articulo no encontrado"));
    }

    private void aplicar(Articulo a, ArticuloRequest r) {
        a.setTitulo(r.titulo().trim());
        a.setResumen(r.resumen().trim());
        a.setContenido(r.contenido().trim());
        a.setImagen(r.imagen());
        a.setImagenAlt(r.imagenAlt());
        a.setAutor(r.autor() == null || r.autor().isBlank() ? AUTOR_POR_DEFECTO : r.autor().trim());
    }
}
