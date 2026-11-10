package cl.ferreteria.usuarios.dto;

// Expresiones regulares compartidas por los DTOs (las mismas reglas que valida el frontend)
public final class Reglas {

    private Reglas() {
    }

    // Correo @duoc.cl, @profesor.duoc.cl o @gmail.com
    public static final String CORREO = "(?i)^[\\w.+-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$";

    // RUN sin puntos ni guion: 6 a 8 dígitos y un dígito verificador (0-9 o K)
    public static final String RUN = "^[0-9]{6,8}[0-9kK]$";

    public static final String MSG_CORREO = "El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com";
    public static final String MSG_RUN = "El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion";
    public static final String MSG_CLAVE = "La contraseña debe tener entre 4 y 10 caracteres";
}
