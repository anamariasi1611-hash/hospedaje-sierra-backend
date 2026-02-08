package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Extiende JpaRepository para operaciones CRUD en Producto
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Lista productos activos
    List<Producto> findByActivoTrue();

    // Verifica existencia por nombre ignorando mayúsculas
    boolean existsByNombreIgnoreCase(String nombre);
}