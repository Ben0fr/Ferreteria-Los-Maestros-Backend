package cl.ferreteria.contenido.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensajeRequestTest {

    private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void aceptaUnMensajeValido() {
        MensajeRequest r = new MensajeRequest("Juan Pérez", "juan@gmail.com", "Hola, tengo una consulta");
        assertTrue(validador.validate(r).isEmpty());
    }

    @Test
    void aceptaLosDominiosPermitidos() {
        for (String correo : new String[] {"a@duoc.cl", "a@profesor.duoc.cl", "a@gmail.com"}) {
            assertTrue(validador.validate(new MensajeRequest("Ana", correo, "Hola")).isEmpty(), correo);
        }
    }

    @Test
    void rechazaDominiosNoPermitidos() {
        assertEquals(1, validador.validate(new MensajeRequest("Ana", "ana@hotmail.com", "Hola")).size());
        assertEquals(1, validador.validate(new MensajeRequest("Ana", "ana@duoc.com", "Hola")).size());
    }

    @Test
    void exigeNombreCorreoYComentario() {
        assertEquals(3, validador.validate(new MensajeRequest("", "", "")).stream()
            .map(v -> v.getPropertyPath().toString()).distinct().count());
    }

    @Test
    void limitaElLargoDelComentario() {
        String largo = "a".repeat(501);
        assertEquals(1, validador.validate(new MensajeRequest("Ana", "ana@gmail.com", largo)).size());
    }
}
