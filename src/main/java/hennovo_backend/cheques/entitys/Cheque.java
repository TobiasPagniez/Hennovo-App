package hennovo_backend.cheques.entitys;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.clientes.entitys.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cheques")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 150)
    private String titular;

    @Column(nullable = false, length = 20)
    private String codigoBanco;

    @Column(nullable = false, length = 100)
    private String nombreBanco;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Column(nullable = false)
    private Boolean endosado = false;

    @Column(nullable = false)
    private Boolean firmaTitular = false;

    @Column(nullable = false)
    private Boolean activo = true;
}
