package cl.ferreteria.ordenes.controller;

import cl.ferreteria.ordenes.dto.ItemRequest;
import cl.ferreteria.ordenes.dto.OrdenRequest;
import cl.ferreteria.ordenes.dto.OrdenResponse;
import cl.ferreteria.ordenes.dto.OrdenResumen;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.security.UsuarioAutenticado;
import cl.ferreteria.ordenes.service.OrdenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdenesControllerTest {

    private static class ServicioFalso extends OrdenService {
        String correoRecibido;
        OrdenRequest requestRecibido;
        OrdenResponse ordenAEntregar;

        ServicioFalso() {
            super(null, null);
        }

        @Override
        public List<OrdenResumen> listar(EstadoOrden estado, String correo) {
            correoRecibido = correo;
            return List.of();
        }

        @Override
        public OrdenResponse obtener(Long id) {
            return ordenAEntregar;
        }

        @Override
        public OrdenResponse crear(OrdenRequest r) {
            requestRecibido = r;
            return ordenAEntregar;
        }
    }

    private ServicioFalso servicio;
    private OrdenesController controlador;

    private final UsuarioAutenticado cliente = new UsuarioAutenticado(3L, "juan@gmail.com", "CLIENTE");
    private final UsuarioAutenticado vendedor = new UsuarioAutenticado(2L, "maria@duoc.cl", "VENDEDOR");

    @BeforeEach
    void preparar() {
        servicio = new ServicioFalso();
        servicio.ordenAEntregar = new OrdenResponse(1L, "Pedro Soto", "pedro@gmail.com", "Calle 1",
            LocalDateTime.now(), EstadoOrden.PENDIENTE, 1000, List.of());
        controlador = new OrdenesController(servicio);
    }

    @Test
    void cliente_listaSoloSusOrdenes_ignorandoElFiltroEnviado() {
        controlador.listar(null, "otro@gmail.com", cliente);

        assertEquals("juan@gmail.com", servicio.correoRecibido);
    }

    @Test
    void vendedor_veTodasLasOrdenes_sinFiltroDeCorreo() {
        controlador.listar(null, null, vendedor);

        assertNull(servicio.correoRecibido);
    }

    @Test
    void cliente_noPuedeVerLaOrdenDeOtraPersona() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> controlador.obtener(1L, cliente));

        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void vendedor_puedeVerCualquierOrden() {
        assertEquals(1L, controlador.obtener(1L, vendedor).id());
    }

    @Test
    void crear_usaElCorreoDelToken_noElDelCuerpo() {
        OrdenRequest pedido = new OrdenRequest("Juan", "suplantado@gmail.com", "Calle 2",
            List.of(new ItemRequest(1L, 2)));

        controlador.crear(pedido, cliente);

        assertEquals("juan@gmail.com", servicio.requestRecibido.clienteCorreo());
        assertEquals("Calle 2", servicio.requestRecibido.direccionEntrega());
    }
}
