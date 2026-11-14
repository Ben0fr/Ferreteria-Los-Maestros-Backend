package cl.ferreteria.contenido.dto;

import cl.ferreteria.contenido.model.Articulo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticuloResponseTest {

    private Articulo articulo(String contenido) {
        Articulo a = new Articulo();
        a.setId(1L);
        a.setTitulo("Titulo");
        a.setResumen("Resumen");
        a.setContenido(contenido);
        a.setAutor("Autor");
        a.setFechaPublicacion(LocalDate.of(2026, 11, 14));
        return a;
    }

    @Test
    void separaElContenidoEnParrafos() {
        ArticuloResponse r = ArticuloResponse.desde(articulo("Uno\n\nDos\n\n\nTres"));
        assertEquals(List.of("Uno", "Dos", "Tres"), r.parrafos());
    }

    @Test
    void sinLineasEnBlancoEsUnSoloParrafo() {
        ArticuloResponse r = ArticuloResponse.desde(articulo("Solo un parrafo"));
        assertEquals(List.of("Solo un parrafo"), r.parrafos());
    }
}
