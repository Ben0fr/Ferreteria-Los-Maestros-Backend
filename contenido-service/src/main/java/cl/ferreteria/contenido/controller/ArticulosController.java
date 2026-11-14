package cl.ferreteria.contenido.controller;

import cl.ferreteria.contenido.dto.ArticuloRequest;
import cl.ferreteria.contenido.dto.ArticuloResponse;
import cl.ferreteria.contenido.service.ArticuloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articulos")
public class ArticulosController {

    private final ArticuloService servicio;

    public ArticulosController(ArticuloService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ArticuloResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ArticuloResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticuloResponse crear(@Valid @RequestBody ArticuloRequest request) {
        return servicio.crear(request);
    }

    @PutMapping("/{id}")
    public ArticuloResponse actualizar(@PathVariable Long id, @Valid @RequestBody ArticuloRequest request) {
        return servicio.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
