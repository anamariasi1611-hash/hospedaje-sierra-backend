package com.hospedajesierra.hospedaje_sierra_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Define la entidad para empleado
@Entity
@Table(name = "empleado")
@Data
public class Empleado {

    // ID primario autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    // Nombre de usuario único, no nulo
    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    // Contraseña ignorada en JSON, no nula
    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String contrasena;

    // Nombre completo
    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    // Email único
    @Column(name = "email", length = 100, unique = true)
    private String email;

    // Cédula única
    @Column(name = "cedula", length = 20, unique = true)
    private String cedula;

    // Rol, no nulo, por defecto EMPLEADO
    @Column(nullable = false)
    private String rol = "EMPLEADO";
}