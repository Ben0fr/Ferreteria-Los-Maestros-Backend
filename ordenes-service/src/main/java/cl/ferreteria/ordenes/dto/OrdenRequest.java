package cl.ferreteria.ordenes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrdenRequest(

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String clienteNombre,

    @NotBlank(message = "El correo del cliente es obligatorio")
    @Email(message = "El correo no es valido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    String clienteCorreo,

    @NotBlank(message = "La direccion de entrega es obligatoria")
    @Size(max = 300, message = "La direccion no puede superar los 300 caracteres")
    String direccionEntrega,

    @NotEmpty(message = "La orden debe tener al menos un producto")
    @Valid
    List<ItemRequest> items

) {}
