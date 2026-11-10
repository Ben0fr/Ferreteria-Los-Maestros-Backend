package cl.ferreteria.usuarios.controller;

import cl.ferreteria.usuarios.dto.UsuarioActualizarRequest;
import cl.ferreteria.usuarios.dto.UsuarioCrearRequest;
import cl.ferreteria.usuarios.dto.UsuarioResponse;
import cl.ferreteria.usuarios.model.Rol;
import cl.ferreteria.usuarios.security.UsuarioAutenticado;
import cl.ferreteria.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Todo este controlador exige rol ADMINISTRADOR (se configura en SecurityConfig)
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios (solo administrador)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Opcionalmente filtra por rol: ?rol=VENDEDOR")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    @ApiResponse(responseCode = "401", description = "Sin sesión")
    @ApiResponse(responseCode = "403", description = "No eres administrador")
    public List<UsuarioResponse> listar(@RequestParam(required = false) Rol rol) {
        return servicio.listar(rol);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un usuario", description = "El administrador puede crear usuarios de cualquier rol.")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Correo o RUN ya registrados")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioCrearRequest request) {
        return servicio.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario",
        description = "Si no se envía 'clave', se mantiene la actual. No puedes cambiar tu propio rol ni desactivarte.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "409", description = "Conflicto (correo/RUN repetido o cambio de rol propio)")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioActualizarRequest request,
                                      @AuthenticationPrincipal UsuarioAutenticado actual) {
        return servicio.actualizar(id, request, actual);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un usuario", description = "No puedes eliminar tu propia cuenta.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "409", description = "Intento de eliminar tu propia cuenta")
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        servicio.eliminar(id, actual);
    }
}
