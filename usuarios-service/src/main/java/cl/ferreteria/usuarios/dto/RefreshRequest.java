package cl.ferreteria.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

    @NotBlank(message = "El refresh token es obligatorio")
    String refreshToken

) {}
