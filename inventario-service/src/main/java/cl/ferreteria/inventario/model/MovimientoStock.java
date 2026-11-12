package cl.ferreteria.inventario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter 
@Setter 
@NoArgsConstructor
@Entity
@Table (name = "movientos_stock")
public class MovimientoStock {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId;

    @Column(length = 100)
    private String productoNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private int stockResultante;

    @Column(length = 200)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;
}