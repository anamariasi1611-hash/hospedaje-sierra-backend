package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

// Define la entidad para huésped
@Entity
@Table(name = "huesped")
@Data
public class Huesped {
    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHuesped;

    // Nombres, no nulos
    @Column(nullable = false, length = 100)
    private String nombres;

    // Apellidos, no nulos
    @Column(nullable = false, length = 100)
    private String apellidos;

    // Cédula única, no nula
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    // Constructor vacío
    public Huesped() {}

    // Constructor con datos básicos
    public Huesped(String nombres, String apellidos, String cedula) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
    }
}