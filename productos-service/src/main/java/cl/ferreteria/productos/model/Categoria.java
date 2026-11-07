package cl.ferreteria.productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor 
@Entity 
@Table(name = "Categoria")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank 
    @Column (nullable = false, unique = true)
    private String nombre;

    @NotBlank 
    @Column (nullable = false, unique = true)
    private String slug;

    private String icono;

    public Categoria(String nombre, String slug, String icono) {
        this.nombre = nombre;
        this.slug = slug;
        this.icono = icono;
    }
}
