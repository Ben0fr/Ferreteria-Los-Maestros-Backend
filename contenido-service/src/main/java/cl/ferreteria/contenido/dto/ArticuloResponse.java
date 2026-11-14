package cl.ferreteria.contenido.dto;

import cl.ferreteria.contenido.model.Articulo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public record ArticuloResponse(
    Long id,
    String titulo,
    String resumen,
    String contenido,
    List<String> parrafos,
    String imagen,
    String imagenAlt,
    String autor,
    LocalDate fechaPublicacion
) {

    // "parrafos" es el contenido separado por lineas en blanco, listo para mostrar en el detalle del blog
    public static ArticuloResponse desde(Articulo a) {
        List<String> parrafos = Arrays.stream(a.getContenido().split("\\R{2,}"))
            .map(String::trim)
            .filter(p -> !p.isEmpty())
            .toList();
        return new ArticuloResponse(
            a.getId(), a.getTitulo(), a.getResumen(), a.getContenido(), parrafos,
            a.getImagen(), a.getImagenAlt(), a.getAutor(), a.getFechaPublicacion()
        );
    }
}
