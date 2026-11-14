package cl.ferreteria.contenido.service;

import cl.ferreteria.contenido.dto.MensajeRequest;
import cl.ferreteria.contenido.dto.MensajeResponse;
import cl.ferreteria.contenido.model.MensajeContacto;
import cl.ferreteria.contenido.repository.MensajeContactoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repo;

    public MensajeContactoService(MensajeContactoRepository repo) {
        this.repo = repo;
    }

    // Guarda el mensaje que envia un visitante desde el formulario de contacto
    @Transactional
    public MensajeResponse crear(MensajeRequest r) {
        MensajeContacto m = new MensajeContacto();
        m.setNombre(r.nombre().trim());
        m.setCorreo(r.correo().trim());
        m.setComentario(r.comentario().trim());
        m.setFecha(LocalDateTime.now());
        m.setLeido(false);
        return MensajeResponse.desde(repo.save(m));
    }

    // Lista los mensajes (mas nuevos primero). Con "leido" se filtran los leidos o los pendientes
    @Transactional(readOnly = true)
    public List<MensajeResponse> listar(Boolean leido) {
        List<MensajeContacto> lista = (leido == null)
            ? repo.findAllByOrderByFechaDescIdDesc()
            : repo.findByLeidoOrderByFechaDescIdDesc(leido);
        return lista.stream().map(MensajeResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public MensajeResponse obtener(Long id) {
        return MensajeResponse.desde(buscar(id));
    }

    // Marca un mensaje como leido o como pendiente
    @Transactional
    public MensajeResponse marcarLeido(Long id, boolean leido) {
        MensajeContacto m = buscar(id);
        m.setLeido(leido);
        return MensajeResponse.desde(repo.save(m));
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        repo.deleteById(id);
    }

    private MensajeContacto buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensaje no encontrado"));
    }
}
