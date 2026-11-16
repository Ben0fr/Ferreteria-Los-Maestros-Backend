package cl.ferreteria.ordenes.service;

import cl.ferreteria.ordenes.client.ProductosClient;
import cl.ferreteria.ordenes.dto.ItemRequest;
import cl.ferreteria.ordenes.dto.OrdenRequest;
import cl.ferreteria.ordenes.dto.OrdenResponse;
import cl.ferreteria.ordenes.dto.OrdenResumen;
import cl.ferreteria.ordenes.dto.ProductoRemoto;
import cl.ferreteria.ordenes.model.DetalleOrden;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.model.Orden;
import cl.ferreteria.ordenes.repository.OrdenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdenService {

    private final OrdenRepository repo;
    private final ProductosClient productos;

    public OrdenService(OrdenRepository repo, ProductosClient productos) {
        this.repo = repo;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<OrdenResumen> listar(EstadoOrden estado, String correo) {
        boolean hayCorreo = correo != null && !correo.isBlank();
        List<Orden> lista;

        if (estado != null && hayCorreo) {
            lista = repo.findByEstadoAndClienteCorreoIgnoreCaseOrderByFechaDescIdDesc(estado, correo.trim());
        } else if (estado != null) {
            lista = repo.findByEstadoOrderByFechaDescIdDesc(estado);
        } else if (hayCorreo) {
            lista = repo.findByClienteCorreoIgnoreCaseOrderByFechaDescIdDesc(correo.trim());
        } else {
            lista = repo.findAllByOrderByFechaDescIdDesc();
        }

        return lista.stream().map(OrdenResumen::desde).toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtener(Long id) {
        return OrdenResponse.desde(buscar(id));
    }

    public OrdenResponse crear(OrdenRequest r) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemRequest item : r.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Orden orden = new Orden();
        orden.setClienteNombre(r.clienteNombre().trim());
        orden.setClienteCorreo(r.clienteCorreo().trim());
        orden.setDireccionEntrega(r.direccionEntrega().trim());
        orden.setFecha(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PENDIENTE);

        int total = 0;
        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
            ProductoRemoto producto = productos.obtener(entrada.getKey());
            int cantidad = entrada.getValue();

            if (producto.stock() < cantidad) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay stock suficiente de " + producto.nombre() + " (disponible: " + producto.stock() + ")");
            }

            DetalleOrden detalle = new DetalleOrden();
            detalle.setProductoId(entrada.getKey());
            detalle.setProductoNombre(producto.nombre());
            detalle.setPrecioUnitario(producto.precio());
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(producto.precio() * cantidad);
            total += detalle.getSubtotal();
            orden.agregarDetalle(detalle);
        }
        orden.setTotal(total);
        List<DetalleOrden> descontados = new ArrayList<>();
        try {
            for (DetalleOrden detalle : orden.getDetalles()) {
                productos.descontarStock(detalle.getProductoId(), detalle.getCantidad());
                descontados.add(detalle);
            }
            return OrdenResponse.desde(repo.save(orden));
        } catch (RuntimeException ex) {
            devolverStock(descontados);
            throw ex;
        }
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long id, EstadoOrden nuevo) {
        Orden orden = buscar(id);
        EstadoOrden actual = orden.getEstado();

        if (actual == nuevo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden ya esta en estado " + actual);
        }
        if (!actual.puedePasarA(nuevo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se puede cambiar una orden de " + actual + " a " + nuevo);
        }

        if (nuevo == EstadoOrden.CANCELADA) {
            for (DetalleOrden detalle : orden.getDetalles()) {
                productos.aumentarStock(detalle.getProductoId(), detalle.getCantidad());
            }
        }

        orden.setEstado(nuevo);
        return OrdenResponse.desde(repo.save(orden));
    }

    private void devolverStock(List<DetalleOrden> detalles) {
        for (DetalleOrden detalle : detalles) {
            try {
                productos.aumentarStock(detalle.getProductoId(), detalle.getCantidad());
            } catch (RuntimeException ignorado) {
                
            }
        }
    }

    private Orden buscar(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }
}
