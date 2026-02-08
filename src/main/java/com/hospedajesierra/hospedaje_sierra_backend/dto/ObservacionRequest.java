package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para solicitud de observación
public record ObservacionRequest(
        // ID de la habitación
        Integer idHabitacion,
        // Comentario de la observación
        String comentario
) {}
