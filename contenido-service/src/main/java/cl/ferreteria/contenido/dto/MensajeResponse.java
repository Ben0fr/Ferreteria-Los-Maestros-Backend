package cl.ferreteria.contenido.dto;

import cl.ferreteria.contenido.model.MensajeContacto;

import java.time.LocalDateTime;

public record MensajeResponse(
    Long id,
    String nombre,
    String correo,
    String comentario,
    LocalDateTime fecha,
    boolean leido
) {

    public static MensajeResponse desde(MensajeContacto m) {
        return new MensajeResponse(
            m.getId(), m.getNombre(), m.getCorreo(), m.getComentario(), m.getFecha(), m.isLeido()
        );
    }
}
