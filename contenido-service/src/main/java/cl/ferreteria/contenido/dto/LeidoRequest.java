package cl.ferreteria.contenido.dto;

import jakarta.validation.constraints.NotNull;

public record LeidoRequest(

    @NotNull(message = "Debes indicar si el mensaje fue leido")
    Boolean leido

) {}
