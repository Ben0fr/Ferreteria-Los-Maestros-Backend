package cl.ferreteria.productos.controller;

import cl.ferreteria.productos.dto.ProductoRequest;
import cl.ferreteria.productos.dto.ProductoResponse;
import cl.ferreteria.productos.dto.StockRequest;
import cl.ferreteria.productos.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductosController {
    
    private final ProductoService servicio;

    public ProductosController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ProductoResponse> listar(@RequestParam(required = false) String categoria) {
        return servicio.listar(categoria);
    }

    @GetMapping("/stock-critico")
    public List<ProductoResponse> stockCritico() {
        return servicio.listarStockCritico();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return servicio.crear(request);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return servicio.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @PatchMapping(("/{id}/stock/descontar"))
    public ProductoResponse descontarStock(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        return servicio.descontarStock(id, request.cantidad());
    }
}
