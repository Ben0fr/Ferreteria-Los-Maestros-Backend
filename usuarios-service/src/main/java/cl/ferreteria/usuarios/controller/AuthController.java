package cl.ferreteria.usuarios.controller;

import cl.ferreteria.usuarios.dto.AuthResponse;
import cl.ferreteria.usuarios.dto.LoginRequest;
import cl.ferreteria.usuarios.dto.RefreshRequest;
import cl.ferreteria.usuarios.dto.RegistroRequest;
import cl.ferreteria.usuarios.dto.UsuarioResponse;
import cl.ferreteria.usuarios.security.UsuarioAutenticado;
import cl.ferreteria.usuarios.service.AuthService;
import cl.ferreteria.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, login, renovación de token y datos de la sesión actual")
public class AuthController {

    private final AuthService auth;
    private final UsuarioService usuarios;

    public AuthController(AuthService auth, UsuarioService usuarios) {
        this.auth = auth;
        this.usuarios = usuarios;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cliente", description = "Registro público. Siempre crea un usuario con rol CLIENTE.")
    @ApiResponse(responseCode = "201", description = "Cliente creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest request) {
        return auth.registrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Entrega un access token (30 min) y un refresh token (7 días).")
    @ApiResponse(responseCode = "200", description = "Login correcto")
    @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Recibe un refresh token válido y entrega un par de tokens nuevo.")
    @ApiResponse(responseCode = "200", description = "Tokens renovados")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return auth.refrescar(request);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Datos del usuario con la sesión actual",
        description = "Útil para que el frontend recupere el usuario al recargar la página.")
    @ApiResponse(responseCode = "200", description = "Usuario actual")
    @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido")
    public UsuarioResponse me(@AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarios.obtener(actual.id());
    }
}
