package cl.ferreteria.inventario.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    OpenAPI openApiProductos() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Inventario - Ferretería Los Maestros")
                .version("v1")
                .description("Catálogo de productos y categorías, control de stock. "
                    + "Lectura pública; crear/modificar/eliminar solo ADMINISTRADOR; stock crítico ADMINISTRADOR y VENDEDOR."))
            .components(new Components().addSecuritySchemes(ESQUEMA,
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
