package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para producto
public record ProductoDto(
        // ID del producto (null en creación)
        Integer idProducto,
        // Nombre del producto
        String nombre,
        // Precio del producto
        Double precio
) {}