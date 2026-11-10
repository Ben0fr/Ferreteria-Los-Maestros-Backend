package cl.ferreteria.usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Usa el perfil "test" (H2 en memoria), así no necesita MySQL para correr
@SpringBootTest
@ActiveProfiles("test")
class UsuariosServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
