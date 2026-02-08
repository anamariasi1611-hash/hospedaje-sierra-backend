package com.hospedajesierra.hospedaje_sierra_backend.dto;

// Define DTO para registro de empleado
public record RegistroEmpleadoRequest(
        // Nombre de usuario
        String nombreUsuario,
        // Nombre completo
        String nombreCompleto,
        // Cédula
        String cedula,
        // Email
        String email,
        // Contraseña
        String password,
        // Rol
        String rol
) {}