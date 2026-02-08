package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.ObservacionRequest;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Empleado;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Habitacion;
import com.hospedajesierra.hospedaje_sierra_backend.entity.ObservacionHabitacion;
import com.hospedajesierra.hospedaje_sierra_backend.repository.EmpleadoRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.HabitacionRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.ObservacionHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

// Define el controlador REST para observaciones
@RestController
@RequestMapping("/api/observaciones")
@RequiredArgsConstructor
public class ObservacionController {

    private final ObservacionHabitacionRepository observacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final EmpleadoRepository empleadoRepository;

    // Crea nueva observación
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ObservacionHabitacion> crearObservacion(@RequestBody ObservacionRequest request) {
        // Obtiene empleado actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Empleado empleado = empleadoRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no autorizado"));

        // Busca habitación
        Habitacion habitacion = habitacionRepository.findById(request.idHabitacion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        // Crea observación
        ObservacionHabitacion obs = new ObservacionHabitacion();
        obs.setHabitacion(habitacion);
        obs.setIdHabitacion(habitacion.getIdHabitacion());
        obs.setFecha(LocalDateTime.now());
        obs.setComentario(request.comentario().trim());
        obs.setEmpleado(empleado);

        // Guarda observación
        observacionRepository.save(obs);

        return ResponseEntity.ok(obs);
    }

    // Lista observaciones por habitación
    @GetMapping("/habitacion/{idHabitacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<ObservacionHabitacion>> listarPorHabitacion(@PathVariable Integer idHabitacion) {
        List<ObservacionHabitacion> observaciones = observacionRepository.findByHabitacionIdHabitacion(idHabitacion);
        return ResponseEntity.ok(observaciones);
    }

    // Elimina observación, solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarObservacion(@PathVariable Integer id) {
        if (!observacionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Elimina observación
        observacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
