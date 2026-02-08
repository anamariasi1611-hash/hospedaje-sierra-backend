package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// Define la entidad para observación de habitación
@Entity
@Table(name = "observacion_habitacion")
@Data
public class ObservacionHabitacion {
    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObservacion;

    // Relación con habitación, no nula
    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    // Fecha de observación
    private LocalDateTime fecha;

    // Comentario como texto
    @Column(columnDefinition = "TEXT")
    private String comentario;

    // Relación con empleado, no nula
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    public void setIdHabitacion(Integer idHabitacion) {

    }
}
