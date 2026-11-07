package cl.ferreteria.productos.config;

import cl.ferreteria.productos.model.Categoria;
import cl.ferreteria.productos.model.Productos;
import cl.ferreteria.productos.repository.CategoriaRepository;
import cl.ferreteria.productos.repository.ProductosRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class DataLoader {
    

    @Bean
    CommandLineRunner cargarDatosIniciales(CategoriaRepository categorias, ProductosRepository productos) {
        return args -> {
            if (categorias.count() > 0) {
                return;
            }

            Categoria manuales = categorias.save(new Categoria("Herramientas manueales", "manuales", "martillo.svg"));
            Categoria electricas = categorias.save(new Categoria("Herramientas electricas", "Electricas", "taladro.svg"));
            Categoria construccion = categorias.save(new Categoria("Materiales de construccion", "construccion", "ladrillos.svg"));
            Categoria pinturas = categorias.save(new Categoria("Pinturas", "pinturas", "pintura.svg"));
            Categoria seguridad = categorias.save(new Categoria("Seguridad y EPP", "seguridad", "guante.svg"));
            Categoria general = categorias.save(new Categoria("Ferreteria general", "general", "tornillo.svg"));

            crear(productos, "MAR001", "Martillo Carpintero", manuales, 6990, 25, "martillo.svg");
            crear(productos, "TAL002", "Taladro Percutor 650W", electricas, 39990, 12, "taladro.svg");
            crear(productos, "CEM003", "Saco de Cemento 25kg", construccion, 5490, 40, "ladrillos.svg");
            crear(productos, "PIN004", "Pintura Látex 1 Galón", pinturas, 12990, 18, "pintura.svg");
            crear(productos, "GUA005", "Guantes de Seguridad", seguridad, 3990, 30, "guante.svg");
            crear(productos, "TOR006", "Caja de Tornillos (100 un.)", general, 2490, 50, "tornillo.svg");
        };
    }

    private void crear(ProductosRepository repo, String codigo, String nombre, Categoria categoria, int precio, int stock, String icono) {
        Productos p = new Productos();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setStockCritico(5);
        p.setIcono(icono);
        repo.save(p);
    }

}
