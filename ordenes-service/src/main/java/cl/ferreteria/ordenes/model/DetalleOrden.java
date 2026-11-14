package cl.ferreteria.ordenes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "detalle_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    private Orden orden;

    // Id del producto en productos-service (no es una llave foranea porque es otra base de datos)
    @Column(nullable = false)
    private Long productoId;

    // Se guardan el nombre y el precio del momento de la compra, por si el producto cambia despues
    @Column(nullable = false, length = 100)
    private String productoNombre;

    @Column(nullable = false)
    private int precioUnitario;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private int subtotal;
}
