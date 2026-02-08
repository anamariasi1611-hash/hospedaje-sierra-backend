package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para informe diario
public record InformeDiaDto(
        // ID de la reserva
        Integer idReserva,
        // Nombre del huésped
        String huesped,
        // Cédula del huésped
        String cedula,
        // Número de habitación
        String habitacion,
        // Fecha de entrada como string
        String fechaEntrada,
        // Fecha de salida como string
        String fechaSalida,
        // Total de servicios
        Double totalServicios,
        // Costo de la habitación
        Double costoHabitacion,
        // Total final
        Double totalFinal,
        // ID de la compra asociada
        Integer idCompra
) {}
