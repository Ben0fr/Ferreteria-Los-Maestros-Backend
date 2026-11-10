package cl.ferreteria.usuarios.model;

// Roles del sistema según el enunciado: Administrador (acceso total), Vendedor (solo productos y órdenes) y Cliente (solo tienda)
public enum Rol {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // Texto para mostrar en pantalla (el frontend actual usa "Administrador", "Vendedor", "Cliente")
    public String getEtiqueta() {
        return etiqueta;
    }
}
