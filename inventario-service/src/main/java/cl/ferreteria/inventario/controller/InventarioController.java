package cl.ferreteria.inventario.controller;

import cl.ferreteria.inventario.dto.AlertaStock;
import cl.ferreteria.inventario.dto.MovimientoRequest;
import cl.ferreteria.inventario.dto.MovimientoResponse;
import cl.ferreteria.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController 
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    private final InventarioService servicio;

    public InventarioController(InventarioService service) {
        this.servicio = service;
    }

    @PostMapping("/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse registrar(@Valid @RequestBody MovimientoRequest request) {
        return servicio.registrar(request);
    }

    @GetMapping("/movimientos")
    public List<MovimientoResponse> historial(@RequestParam(required = false) Long productoId) {
        return servicio.historial(productoId);
    }

    @GetMapping("/alertas")
    public List<AlertaStock> alertas() {
        return servicio.alertas();
    }
    
}
