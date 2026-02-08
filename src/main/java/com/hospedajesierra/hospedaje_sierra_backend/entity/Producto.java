package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

// Define la entidad para producto
@Entity
@Table(name = "producto")
@Data
public class Producto {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    // Nombre, no nulo
    @Column(nullable = false, length = 100)
    private String nombre;

    // Precio, no nulo
    @Column(nullable = false)
    private Double precio;

    // Activo, por defecto true
    private Boolean activo = true;
}
