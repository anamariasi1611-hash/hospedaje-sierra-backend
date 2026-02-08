package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.FacturaReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Extiende JpaRepository para operaciones CRUD en FacturaReserva
public interface FacturaReservaRepository extends JpaRepository<FacturaReserva, Integer> {

    // Busca factura por ID de reserva
    Optional<FacturaReserva> findByReservaIdReserva(Integer idReserva);
}