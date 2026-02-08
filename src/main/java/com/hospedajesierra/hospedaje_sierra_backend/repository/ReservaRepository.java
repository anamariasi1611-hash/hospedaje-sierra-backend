package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

// Extiende JpaRepository para operaciones CRUD en Reserva
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Lista reservas vigentes en una fecha
    List<Reserva> findByFechaEntradaLessThanEqualAndFechaSalidaGreaterThanEqual(
            LocalDate fechaEntradaMax,
            LocalDate fechaSalidaMin
    );

    // Lista reservas por rango de entrada
    List<Reserva> findByFechaEntradaBetween(LocalDate start, LocalDate end);
}
