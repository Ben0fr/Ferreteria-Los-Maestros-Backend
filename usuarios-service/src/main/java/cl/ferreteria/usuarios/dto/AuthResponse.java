package cl.ferreteria.usuarios.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tipoToken,
    long expiraEnSegundos,
    UsuarioResponse usuario
) {}
