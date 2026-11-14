package cl.ferreteria.ordenes.config;

import cl.ferreteria.ordenes.model.DetalleOrden;
import cl.ferreteria.ordenes.model.EstadoOrden;
import cl.ferreteria.ordenes.model.Orden;
import cl.ferreteria.ordenes.repository.OrdenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    // Carga unas ordenes de ejemplo la primera vez, para que el vendedor tenga algo que ver
    @Bean
    CommandLineRunner cargarOrdenesIniciales(OrdenRepository ordenes) {
        return args -> {
            if (ordenes.count() > 0) {
                return;
            }

            Orden entregada = crear("Juan Pérez", "juan@gmail.com", "Av. Francisco de Aguirre 123, La Serena",
                EstadoOrden.ENTREGADA, LocalDateTime.now().minusDays(5));
            agregar(entregada, 1L, "Martillo Carpintero", 6990, 1);
            agregar(entregada, 5L, "Guantes de Seguridad", 3990, 2);
            ordenes.save(entregada);

            Orden pendiente = crear("María González", "maria@duoc.cl", "Los Carrera 456, Coquimbo",
                EstadoOrden.PENDIENTE, LocalDateTime.now().minusHours(3));
            agregar(pendiente, 2L, "Taladro Percutor 650W", 39990, 1);
            ordenes.save(pendiente);

            Orden enviada = crear("Juan Pérez", "juan@gmail.com", "Av. Francisco de Aguirre 123, La Serena",
                EstadoOrden.ENVIADA, LocalDateTime.now().minusDays(1));
            agregar(enviada, 3L, "Saco de Cemento 25kg", 5490, 4);
            agregar(enviada, 4L, "Pintura Látex 1 Galón", 12990, 2);
            ordenes.save(enviada);
        };
    }

    private Orden crear(String nombre, String correo, String direccion, EstadoOrden estado, LocalDateTime fecha) {
        Orden o = new Orden();
        o.setClienteNombre(nombre);
        o.setClienteCorreo(correo);
        o.setDireccionEntrega(direccion);
        o.setEstado(estado);
        o.setFecha(fecha);
        return o;
    }

    private void agregar(Orden orden, Long productoId, String nombre, int precio, int cantidad) {
        DetalleOrden d = new DetalleOrden();
        d.setProductoId(productoId);
        d.setProductoNombre(nombre);
        d.setPrecioUnitario(precio);
        d.setCantidad(cantidad);
        d.setSubtotal(precio * cantidad);
        orden.agregarDetalle(d);
        orden.setTotal(orden.getTotal() + d.getSubtotal());
    }
}
