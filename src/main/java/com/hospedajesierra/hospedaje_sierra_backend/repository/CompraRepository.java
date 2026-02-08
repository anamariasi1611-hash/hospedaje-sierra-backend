package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Extiende JpaRepository para operaciones CRUD en Compra
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    // Busca compra por ID de reserva
    Optional<Compra> findByReservaIdReserva(Integer idReserva);
}
