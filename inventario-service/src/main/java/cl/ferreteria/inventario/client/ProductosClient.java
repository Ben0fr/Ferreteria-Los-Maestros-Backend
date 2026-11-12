package cl.ferreteria.inventario.client;


import cl.ferreteria.inventario.dto.ProductoRemoto;
import cl.ferreteria.inventario.dto.StockRemoto;
import cl.ferreteria.inventario.dto.AlertaStock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component 
public class ProductosClient {
    
    private final RestClient http;

    public ProductosClient(RestClient productosRestClient) {
        this.http = productosRestClient;
    }

    public ProductoRemoto aumentarStock(Long id, int cantidad) {
        return llamar(() -> http.patch()
        .uri("/api/v1/productos/{id}/stock/aumentar", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new StockRemoto(cantidad))
        .retrieve()
        .body(ProductoRemoto.class));
    }

    public ProductoRemoto descontarStock(Long id, int cantidad) {
        return llamar(() -> http.patch()
        .uri("/api/v1/productos/{id}/stock/descontar", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new StockRemoto(cantidad))
        .retrieve()
        .body(ProductoRemoto.class));
    }

    public List<ProductoRemoto> stockCritico() {
        return llamar(() -> http.get()
        .uri("/api/v1/productos/stock-critico")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductoRemoto>>() {} ));
    }

    private <T> T llamar(Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch(HttpClientErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), extraerMensaje(ex));
        } catch(ResourceAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "productos-service no esta disponible, intenta mas tarde"
            );
        }
    }

    private String extraerMensaje(HttpClientErrorException ex) {
        try {
            Map<?,?> cuerpo = ex.getResponseBodyAs(Map.class);
            if (cuerpo != null && cuerpo.get("message") != null) {
                return cuerpo.get("message").toString();
            }
        } catch (Exception ignorado) {
            //Si el cuerpo no se lee envia un mensaje x defecto.
        }
        return  "Error al consultar productos-service";
    }
}
