package cl.ferreteria.usuarios.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Swagger UI: http://localhost:8081/swagger-ui.html
// El botón "Authorize" permite pegar el accessToken para probar los endpoints protegidos
@Configuration
@OpenAPIDefinition(info = @Info(
    title = "API de Usuarios - Ferretería Los Maestros",
    version = "v1",
    description = "Registro, login con JWT, renovación de tokens y administración de usuarios por rol."))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class OpenApiConfig {
}
