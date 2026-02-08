package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// Define la entidad para reserva
@Entity
@Table(name = "reserva")
@Data
public class Reserva {
    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    // Relación con huésped, no nula
    @ManyToOne
    @JoinColumn(name = "id_huesped", nullable = false)
    private Huesped huesped;

    // Relación con habitación, no nula
    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    // Fecha de entrada, no nula
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    // Fecha de salida, no nula
    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    // Cantidad de acompañantes, por defecto 0
    @Column(name = "cantidad_acompanantes")
    private Integer cantidadAcompanantes = 0;

    // Relación con empleado, no nula
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    // Precio total de habitación, no nulo
    @Column(name = "precio_total_habitacion", nullable = false)
    private Double precioTotalHabitacion;
}
