package cl.ferreteria.ordenes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoOrdenTest {

    @Test
    void elFlujoNormalAvanzaPasoAPaso() {
        assertTrue(EstadoOrden.PENDIENTE.puedePasarA(EstadoOrden.CONFIRMADA));
        assertTrue(EstadoOrden.CONFIRMADA.puedePasarA(EstadoOrden.EN_PREPARACION));
        assertTrue(EstadoOrden.EN_PREPARACION.puedePasarA(EstadoOrden.ENVIADA));
        assertTrue(EstadoOrden.ENVIADA.puedePasarA(EstadoOrden.ENTREGADA));
    }

    @Test
    void sePuedeCancelarAntesDeEnviar() {
        assertTrue(EstadoOrden.PENDIENTE.puedePasarA(EstadoOrden.CANCELADA));
        assertTrue(EstadoOrden.CONFIRMADA.puedePasarA(EstadoOrden.CANCELADA));
        assertTrue(EstadoOrden.EN_PREPARACION.puedePasarA(EstadoOrden.CANCELADA));
    }

    @Test
    void noSePuedeCancelarUnaOrdenEnviadaOEntregada() {
        assertFalse(EstadoOrden.ENVIADA.puedePasarA(EstadoOrden.CANCELADA));
        assertFalse(EstadoOrden.ENTREGADA.puedePasarA(EstadoOrden.CANCELADA));
    }

    @Test
    void noSePuedeSaltarPasosNiRetroceder() {
        assertFalse(EstadoOrden.PENDIENTE.puedePasarA(EstadoOrden.ENVIADA));
        assertFalse(EstadoOrden.PENDIENTE.puedePasarA(EstadoOrden.ENTREGADA));
        assertFalse(EstadoOrden.ENVIADA.puedePasarA(EstadoOrden.PENDIENTE));
    }

    @Test
    void losEstadosFinalesNoCambian() {
        for (EstadoOrden nuevo : EstadoOrden.values()) {
            assertFalse(EstadoOrden.ENTREGADA.puedePasarA(nuevo));
            assertFalse(EstadoOrden.CANCELADA.puedePasarA(nuevo));
        }
    }
}
