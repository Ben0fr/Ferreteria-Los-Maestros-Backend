package cl.ferreteria.contenido.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articulo")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 300)
    private String resumen;

    // Texto completo del articulo. Los parrafos se separan con una linea en blanco
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(length = 255)
    private String imagen;

    @Column(length = 150)
    private String imagenAlt;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false)
    private LocalDate fechaPublicacion;
}
