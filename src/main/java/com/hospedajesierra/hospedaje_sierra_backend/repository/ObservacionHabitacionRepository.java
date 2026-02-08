package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.ObservacionHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Extiende JpaRepository para operaciones CRUD en ObservacionHabitacion
public interface ObservacionHabitacionRepository extends JpaRepository<ObservacionHabitacion, Integer> {
    // Lista observaciones por ID de habitación
    List<ObservacionHabitacion> findByHabitacionIdHabitacion(Integer idHabitacion);

    // Cuenta observaciones por ID de habitación
    long countByHabitacionIdHabitacion(Integer id);
}
