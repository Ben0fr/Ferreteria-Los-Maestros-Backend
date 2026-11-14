package cl.ferreteria.contenido.controller;

import cl.ferreteria.contenido.dto.LeidoRequest;
import cl.ferreteria.contenido.dto.MensajeRequest;
import cl.ferreteria.contenido.dto.MensajeResponse;
import cl.ferreteria.contenido.service.MensajeContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacto")
public class ContactoController {

    private final MensajeContactoService servicio;

    public ContactoController(MensajeContactoService servicio) {
        this.servicio = servicio;
    }

    // Lo usa el formulario de contacto de la tienda (cualquier visitante)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeResponse enviar(@Valid @RequestBody MensajeRequest request) {
        return servicio.crear(request);
    }

    // Lo usa el administrador para revisar los mensajes
    @GetMapping
    public List<MensajeResponse> listar(@RequestParam(required = false) Boolean leido) {
        return servicio.listar(leido);
    }

    @GetMapping("/{id}")
    public MensajeResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PatchMapping("/{id}/leido")
    public MensajeResponse marcarLeido(@PathVariable Long id, @Valid @RequestBody LeidoRequest request) {
        return servicio.marcarLeido(id, request.leido());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
