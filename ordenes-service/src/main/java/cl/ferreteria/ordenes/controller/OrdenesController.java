package cl.ferreteria.ordenes.controller;

import cl.ferreteria.ordenes.dto.CambioEstadoRequest;
import cl.ferreteria.ordenes.dto.OrdenRequest;
import cl.ferreteria.ordenes.dto.OrdenResponse;
import cl.ferreteria.ordenes.dto.OrdenResumen;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.security.UsuarioAutenticado;
import cl.ferreteria.ordenes.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                                     @RequestParam(required = false) String correo,
                                     @AuthenticationPrincipal UsuarioAutenticado actual) {
        String filtroCorreo = esCliente(actual) ? actual.correo() : correo;
        return servicio.listar(estado, filtroCorreo);
    }

    @GetMapping("/estados")
    public List<EstadoOrden> estados() {
        return Arrays.asList(EstadoOrden.values());
    }

    @GetMapping("/{id}")
    public OrdenResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        OrdenResponse orden = servicio.obtener(id);
        if (esCliente(actual) && !orden.clienteCorreo().equalsIgnoreCase(actual.correo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver órdenes de otro cliente");
        }
        return orden;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest request,
                               @AuthenticationPrincipal UsuarioAutenticado actual) {
        OrdenRequest propia = new OrdenRequest(
            request.clienteNombre(), actual.correo(), request.direccionEntrega(), request.items());
        return servicio.crear(propia);
    }

    @PatchMapping("/{id}/estado")
    public OrdenResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return servicio.cambiarEstado(id, request.estado());
    }

    private boolean esCliente(UsuarioAutenticado actual) {
        return "CLIENTE".equals(actual.rol());
    }
}
