package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

// Define la entidad para factura de reserva
@Entity
@Table(name = "factura_reserva")
@Data
public class FacturaReserva {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    // Relación con reserva, no nula
    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    // Total de servicios, no nulo, inicializado en 0
    @Column(name = "total_servicios", nullable = false)
    private Double totalServicios = 0.0;

    // Costo de habitación, no nulo, inicializado en 0
    @Column(name = "costo_habitacion", nullable = false)
    private Double costoHabitacion = 0.0;

    // Total final, no nulo, inicializado en 0
    @Column(name = "total_final", nullable = false)
    private Double totalFinal = 0.0;

    // Relación con empleado, no nula
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;
}
