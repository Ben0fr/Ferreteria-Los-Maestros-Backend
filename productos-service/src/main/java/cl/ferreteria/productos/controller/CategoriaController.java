package cl.ferreteria.productos.controller;

import cl.ferreteria.productos.model.Categoria;
import cl.ferreteria.productos.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Categoria> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Categoria obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crear (@Valid @RequestBody Categoria c) {
        return servicio.crear(c);
    }

    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id, @Valid @RequestBody Categoria c) {
        return servicio.actualizar(id, c);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
    
}
