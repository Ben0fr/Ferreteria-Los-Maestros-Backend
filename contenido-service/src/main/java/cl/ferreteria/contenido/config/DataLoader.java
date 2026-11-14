package cl.ferreteria.contenido.config;

import cl.ferreteria.contenido.model.Articulo;
import cl.ferreteria.contenido.model.MensajeContacto;
import cl.ferreteria.contenido.repository.ArticuloRepository;
import cl.ferreteria.contenido.repository.MensajeContactoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    private static final String AUTOR = "Ferretería Los Maestros";

    // Carga los articulos del blog de la tienda y unos mensajes de ejemplo la primera vez
    @Bean
    CommandLineRunner cargarContenidoInicial(ArticuloRepository articulos, MensajeContactoRepository mensajes) {
        return args -> {
            if (articulos.count() == 0) {
                crearArticulo(articulos,
                    "5 elementos de seguridad para trabajar en casa",
                    "Conoce los elementos básicos que debes utilizar antes de comenzar cualquier trabajo de construcción, reparación o mantenimiento.",
                    String.join("\n\n",
                        "Antes de comenzar cualquier reparación, construcción o trabajo con herramientas es importante contar con elementos de protección personal adecuados.",
                        "Uno de los elementos más importantes son los guantes de seguridad. Estos ayudan a proteger las manos frente a cortes, golpes y materiales ásperos.",
                        "Los lentes de seguridad también son fundamentales, especialmente al utilizar taladros, sierras o herramientas que puedan generar pequeñas partículas.",
                        "Otro elemento importante es el calzado de seguridad. Dependiendo del trabajo, puede ser recomendable utilizar zapatos resistentes que protejan los pies frente a golpes.",
                        "Cuando se realizan trabajos que generan mucho ruido, también es recomendable utilizar protección auditiva.",
                        "Finalmente, siempre es importante revisar el estado de las herramientas antes de utilizarlas y mantener el área de trabajo ordenada."),
                    "/img/images.jpg", "Elementos de seguridad", LocalDate.now().minusDays(14));

                crearArticulo(articulos,
                    "Herramientas básicas que deberías tener en tu hogar",
                    "Un martillo, destornilladores y algunas herramientas simples pueden ayudarte a solucionar muchas reparaciones domésticas.",
                    String.join("\n\n",
                        "Tener algunas herramientas básicas en casa puede ayudarte a solucionar pequeños problemas sin necesidad de realizar una compra cada vez que ocurre una reparación.",
                        "Una de las herramientas más conocidas es el martillo. Puede utilizarse para instalar clavos, realizar pequeñas reparaciones y trabajar con distintos materiales.",
                        "También es recomendable tener un juego de destornilladores. Existen distintos tamaños y tipos de punta, por lo que contar con varias opciones permite realizar diferentes tareas.",
                        "Una cinta métrica resulta útil para medir muebles, paredes, materiales y espacios antes de realizar una instalación.",
                        "Los alicates permiten sujetar, doblar o cortar algunos materiales y son útiles en numerosas reparaciones.",
                        "Finalmente, una caja de herramientas permite mantener todos estos elementos organizados y encontrarlos fácilmente cuando sean necesarios."),
                    "/img/blog2.jpg", "Herramientas básicas", LocalDate.now().minusDays(7));
            }

            if (mensajes.count() == 0) {
                crearMensaje(mensajes, "Juan Pérez", "juan@gmail.com",
                    "Hola, quisiera saber si tienen disponible cemento en cantidad para una obra pequeña.",
                    LocalDateTime.now().minusDays(2), true);
                crearMensaje(mensajes, "María González", "maria@duoc.cl",
                    "Buenas tardes, ¿hasta qué hora atienden los sábados?",
                    LocalDateTime.now().minusHours(5), false);
            }
        };
    }

    private void crearArticulo(ArticuloRepository repo, String titulo, String resumen, String contenido,
                               String imagen, String imagenAlt, LocalDate fecha) {
        Articulo a = new Articulo();
        a.setTitulo(titulo);
        a.setResumen(resumen);
        a.setContenido(contenido);
        a.setImagen(imagen);
        a.setImagenAlt(imagenAlt);
        a.setAutor(AUTOR);
        a.setFechaPublicacion(fecha);
        repo.save(a);
    }

    private void crearMensaje(MensajeContactoRepository repo, String nombre, String correo, String comentario,
                              LocalDateTime fecha, boolean leido) {
        MensajeContacto m = new MensajeContacto();
        m.setNombre(nombre);
        m.setCorreo(correo);
        m.setComentario(comentario);
        m.setFecha(fecha);
        m.setLeido(leido);
        repo.save(m);
    }
}
