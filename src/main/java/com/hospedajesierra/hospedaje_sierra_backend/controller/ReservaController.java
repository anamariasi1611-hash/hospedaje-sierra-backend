package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.RegistroReservaRequest;
import com.hospedajesierra.hospedaje_sierra_backend.entity.*;
import com.hospedajesierra.hospedaje_sierra_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Define el controlador REST para reservas
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FacturaReservaRepository facturaReservaRepository;

    // Registra nueva reserva
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> registrarReserva(@RequestBody RegistroReservaRequest request) {
        try {
            // Busca o crea huésped
            Huesped huesped = huespedRepository.findByCedula(request.cedula())
                    .orElseGet(() -> {
                        Huesped nuevo = new Huesped(
                                request.nombres().trim(),
                                request.apellidos().trim(),
                                request.cedula().trim()
                        );
                        return huespedRepository.save(nuevo);
                    });

            // Busca habitación
            Habitacion habitacion = habitacionRepository.findById(request.idHabitacion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

            // Verifica disponibilidad
            if (habitacion.getEstado() != Habitacion.EstadoHabitacion.DISPONIBLE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La habitación no está disponible");
            }

            // Parsea fechas
            LocalDate fechaEntrada = LocalDate.parse(request.fechaEntrada());
            LocalDate fechaSalida = LocalDate.parse(request.fechaSalida());

            // Calcula noches
            long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
            if (noches <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de salida debe ser posterior a la fecha de entrada");
            }

            // Calcula precio total
            double precioTotal = noches * habitacion.getPrecio();

            // Obtiene empleado actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Empleado empleado = empleadoRepository.findByNombreUsuario(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empleado no encontrado"));

            // Crea reserva
            Reserva reserva = new Reserva();
            reserva.setHuesped(huesped);
            reserva.setHabitacion(habitacion);
            reserva.setFechaEntrada(fechaEntrada);
            reserva.setFechaSalida(fechaSalida);
            reserva.setCantidadAcompanantes(request.cantidadAcompanantes() != null ? request.cantidadAcompanantes() : 0);
            reserva.setEmpleado(empleado);
            reserva.setPrecioTotalHabitacion(precioTotal);

            // Guarda reserva
            Reserva guardada = reservaRepository.save(reserva);

            // Actualiza estado habitación
            habitacion.setEstado(Habitacion.EstadoHabitacion.OCUPADA);
            habitacionRepository.save(habitacion);

            // Crea factura asociada
            FacturaReserva factura = new FacturaReserva();
            factura.setReserva(guardada);
            factura.setTotalServicios(0.0);
            factura.setCostoHabitacion(precioTotal);
            factura.setTotalFinal(precioTotal);
            factura.setEmpleado(empleado);
            facturaReservaRepository.save(factura);

            // Prepara respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reserva registrada correctamente");
            response.put("idReserva", guardada.getIdReserva());
            response.put("idFactura", factura.getIdFactura());
            response.put("noches", noches);
            response.put("precioTotal", precioTotal);

            return ResponseEntity.ok(response);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido (use yyyy-MM-dd)");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la reserva: " + e.getMessage());
        }
    }

    // Lista todas las reservas
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    // Cancela reserva y libera habitación
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Integer id) {
        Optional<Reserva> optional = reservaRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = optional.get();
        Habitacion habitacion = reserva.getHabitacion();

        // Libera habitación
        habitacion.setEstado(Habitacion.EstadoHabitacion.DISPONIBLE);
        habitacionRepository.save(habitacion);

        // Elimina reserva
        reservaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}