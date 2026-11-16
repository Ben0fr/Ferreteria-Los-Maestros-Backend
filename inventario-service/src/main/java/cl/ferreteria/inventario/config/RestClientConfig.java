package cl.ferreteria.inventario.config;

import cl.ferreteria.inventario.security.ReenvioTokenInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    
    @Bean 
    RestClient productosRestClient(@Value("${productos.service.url}") String url) {
        return RestClient.builder()
            .baseUrl(url)
            .requestInterceptor(new ReenvioTokenInterceptor())
            .build();
    }
}
