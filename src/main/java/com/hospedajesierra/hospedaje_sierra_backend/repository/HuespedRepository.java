package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Extiende JpaRepository para operaciones CRUD en Huesped
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    // Busca huésped por cédula
    Optional<Huesped> findByCedula(String cedula);
}
