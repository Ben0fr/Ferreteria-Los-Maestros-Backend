package cl.ferreteria.productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice 
public class GlobalExceptionHandler {
    

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarEstado(ResponseStatusException ex) {
        return construir(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        StringBuilder detalle = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if(detalle.length() > 0) {
                detalle.append("; ");
            }
            detalle.append(error.getDefaultMessage());
        }
        return construir(HttpStatus.BAD_REQUEST, detalle.toString());
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus estado, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", estado.value());
        cuerpo.put("message", mensaje);
        cuerpo.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
