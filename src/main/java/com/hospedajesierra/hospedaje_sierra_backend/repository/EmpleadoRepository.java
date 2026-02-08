package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Extiende JpaRepository para operaciones CRUD en Empleado
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    // Busca por nombre de usuario
    Optional<Empleado> findByNombreUsuario(String nombreUsuario);

    // Busca por email
    Optional<Object> findByEmail(String email);

    // Busca por cédula
    Optional<Object> findByCedula(String cedula);
}

