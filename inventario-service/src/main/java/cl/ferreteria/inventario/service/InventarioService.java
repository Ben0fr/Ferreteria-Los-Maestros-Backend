package cl.ferreteria.inventario.service;

import cl.ferreteria.inventario.client.ProductosClient;
import cl.ferreteria.inventario.dto.AlertaStock;
import cl.ferreteria.inventario.dto.MovimientoRequest;
import cl.ferreteria.inventario.dto.MovimientoResponse;
import cl.ferreteria.inventario.dto.ProductoRemoto;
import cl.ferreteria.inventario.model.MovimientoStock;
import cl.ferreteria.inventario.model.TipoMovimiento;
import cl.ferreteria.inventario.repository.MovimientoStockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service 
public class InventarioService {
    
    private final MovimientoStockRepository repo;
    private final ProductosClient productos;

    public InventarioService(MovimientoStockRepository repo, ProductosClient productos) {
        this.repo = repo;
        this.productos = productos;
    }

    public MovimientoResponse registrar(MovimientoRequest r) {
        ProductoRemoto actualizado = (r.tipo() == TipoMovimiento.ENTRADA)
                ? productos.aumentarStock(r.productoId(), r.cantidad())
                : productos.descontarStock(r.productoId(), r.cantidad());
        MovimientoStock m = new MovimientoStock();
        m.setProductoId(actualizado.id());
        m.setProductoNombre(actualizado.nombre());
        m.setTipo(r.tipo());
        m.setCantidad(r.cantidad());
        m.setStockResultante(actualizado.stock());
        m.setMotivo(r.motivo());
        m.setFecha(LocalDateTime.now());
        return MovimientoResponse.desde(repo.save(m));
    }

    public List<MovimientoResponse> historial(Long productoId) {
        List<MovimientoStock> lista = (productoId == null)
                ? repo.findAllByOrderByFechaDesc()
                : repo.findByProductoIdOrderByFechaDesc(productoId);
        return lista.stream().map(MovimientoResponse::desde).toList();
    }

    public List<AlertaStock> alertas() {
        return productos.stockCritico().stream()
                .map(p -> new AlertaStock(
                    p.id(), p.codigo(), p.nombre(), p.stock(), p.stockCritico(), p.stock() == 0 ? "SIN_STOCK" : "CRITICO"
                )).toList();
    }
    
}
