package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

// Define la entidad para detalle de compra
@Entity
@Table(name = "detalle_compra")
@Data
public class DetalleCompra {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // Relación con compra, no nula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    // Relación con producto, no nula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Cantidad, no nula
    @Column(nullable = false)
    private Integer cantidad;

    // Subtotal del producto, no nulo
    @Column(name = "subtotal_producto", nullable = false)
    private Double subtotalProducto;
}
