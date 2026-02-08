package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

// Define la entidad para habitación
@Entity
@Table(name = "habitacion")
@Data
public class Habitacion {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    // Número único, no nulo
    @Column(nullable = false, length = 10, unique = true)
    private String numero;

    // Capacidad de personas, no nulo
    @Column(nullable = false)
    private Integer personas;

    // Precio, no nulo
    @Column(nullable = false)
    private Double precio;

    // Estado enumerado, no nulo, por defecto DISPONIBLE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado = EstadoHabitacion.DISPONIBLE;

    // URL de imagen
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    // Enumerado para estados de habitación
    public enum EstadoHabitacion {
        DISPONIBLE, EN_LIMPIEZA, OCUPADA
    }
}