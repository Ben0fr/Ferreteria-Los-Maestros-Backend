package cl.ferreteria.ordenes.controller;

import cl.ferreteria.ordenes.dto.CambioEstadoRequest;
import cl.ferreteria.ordenes.dto.OrdenRequest;
import cl.ferreteria.ordenes.dto.OrdenResponse;
import cl.ferreteria.ordenes.dto.OrdenResumen;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenesController {

    private final OrdenService servicio;

    public OrdenesController(OrdenService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<OrdenResumen> listar(@RequestParam(required = false) EstadoOrden estado,
                                     @RequestParam(required = false) String correo) {
        return servicio.listar(estado, correo);
    }

    // Estados posibles, para armar un selector en la pantalla del vendedor
    @GetMapping("/estados")
    public List<EstadoOrden> estados() {
        return Arrays.asList(EstadoOrden.values());
    }

    @GetMapping("/{id}")
    public OrdenResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest request) {
        return servicio.crear(request);
    }

    @PatchMapping("/{id}/estado")
    public OrdenResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return servicio.cambiarEstado(id, request.estado());
    }
}
