package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Define la entidad para compra en la base de datos
@Entity
@Table(name = "compra")
@Data
public class Compra {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    // Relación con reserva, no nula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    // Fecha de compra, no nula, inicializada actual
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    // Total de compra, no nulo, inicializado en 0
    @Column(name = "total_compra", nullable = false)
    private Double totalCompra = 0.0;

    // Relación con empleado, no nula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;
}
