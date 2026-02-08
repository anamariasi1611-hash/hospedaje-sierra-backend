package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

// Extiende JpaRepository para operaciones CRUD en Habitacion
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
}