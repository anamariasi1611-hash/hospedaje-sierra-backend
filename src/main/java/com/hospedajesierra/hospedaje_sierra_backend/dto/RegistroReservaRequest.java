package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para registro de reserva
public record RegistroReservaRequest(
        // Nombres del huésped
        String nombres,
        // Apellidos del huésped
        String apellidos,
        // Cédula del huésped
        String cedula,
        // Fecha de entrada como string
        String fechaEntrada,
        // Fecha de salida como string
        String fechaSalida,
        // Cantidad de acompañantes
        Integer cantidadAcompanantes,
        // ID de la habitación
        Integer idHabitacion
) {}
