package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para agregar detalle a compra
public record AgregarDetalleDto(
        // ID del producto a agregar
        Integer idProducto,
        // Cantidad del producto
        Integer cantidad
) {}