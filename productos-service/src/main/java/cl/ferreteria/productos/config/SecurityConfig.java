package cl.ferreteria.productos.config;

import cl.ferreteria.productos.security.JwtAuthFilter;
import cl.ferreteria.productos.security.JwtService;
import cl.ferreteria.productos.security.ManejadorErroresSeguridad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Reglas de acceso de productos-service. Los tokens los emite usuarios-service; aquí solo se validan.
//
//  Ruta                                          Quién puede
//  GET  /productos, /productos/{id}, /categorias  Público (la tienda la ve cualquiera)
//  GET  /productos/stock-critico                  ADMINISTRADOR y VENDEDOR
//  PATCH /productos/{id}/stock/descontar          Cualquier usuario con sesión (al comprar)
//  POST/PUT/DELETE de productos y categorías      Solo ADMINISTRADOR
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, ManejadorErroresSeguridad errores)
            throws Exception {

        http
            .cors(Customizer.withDefaults())
            // CSRF se desactiva porque no usamos cookies de sesión: el token viaja en el header Authorization
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                .authenticationEntryPoint(errores)
                .accessDeniedHandler(errores))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Documentación Swagger
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // Esta ruta va ANTES de la pública, si no "/productos/*" la dejaría abierta
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/stock-critico")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                // Catálogo de la tienda: lectura pública
                .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*",
                    "/api/v1/categorias", "/api/v1/categorias/*").permitAll()
                // Descontar stock lo hace cualquier usuario autenticado al confirmar una compra
                .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/*/stock/descontar").authenticated()
                // Crear, modificar y eliminar productos/categorías: solo el administrador
                .requestMatchers("/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS en Spring Security (debe ir aquí porque el filtro de seguridad corre antes que Spring MVC)
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") List<String> origenes) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(origenes);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", cors);
        return fuente;
    }
}
