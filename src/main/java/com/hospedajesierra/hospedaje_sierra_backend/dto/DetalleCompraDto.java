package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para detalle de compra
public record DetalleCompraDto(
        // ID del detalle
        Integer idDetalle,
        // Nombre del producto
        String nombreProducto,
        // Cantidad comprada
        Integer cantidad,
        // Subtotal del detalle
        Double subtotal
) {}
