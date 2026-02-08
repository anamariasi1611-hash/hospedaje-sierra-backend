package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para representar compra
public record CompraDto(
        // ID de la compra
        Integer idCompra,
        // ID de la reserva asociada
        Integer idReserva,
        // Fecha de la compra como string
        String fechaCompra,
        // Total de la compra
        Double totalCompra,
        // Nombre del empleado responsable
        String nombreEmpleado
) {}

