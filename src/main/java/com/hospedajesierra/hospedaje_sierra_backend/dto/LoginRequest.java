package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para credenciales de login
public record LoginRequest(
        // Nombre de usuario
        String username,
        // Contraseña
        String password
) {}
