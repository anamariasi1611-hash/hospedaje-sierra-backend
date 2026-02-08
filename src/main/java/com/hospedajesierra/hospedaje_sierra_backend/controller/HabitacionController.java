package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Habitacion;
import com.hospedajesierra.hospedaje_sierra_backend.repository.HabitacionRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.ObservacionHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Define el controlador REST para habitaciones
@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionRepository habitacionRepository;
    private final ObservacionHabitacionRepository observacionRepository;

    // Lista todas las habitaciones
    @GetMapping
    public ResponseEntity<List<Habitacion>> listarHabitaciones() {
        return ResponseEntity.ok(habitacionRepository.findAll());
    }

    // Obtiene habitación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> obtenerHabitacion(@PathVariable Integer id) {
        return habitacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crea nueva habitación
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Habitacion> crearHabitacion(@RequestBody Habitacion habitacion) {
        // Valida número
        if (habitacion.getNumero() == null || habitacion.getNumero().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(habitacionRepository.save(habitacion));
    }

    // Actualiza habitación parcialmente
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> actualizarHabitacion(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {

        // Obtiene usuario actual y verifica rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Optional<Habitacion> optional = habitacionRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Habitacion habitacion = optional.get();

        // Actualiza estado (permitido para todos)
        if (updates.containsKey("estado")) {
            String estadoStr = (String) updates.get("estado");
            try {
                habitacion.setEstado(Habitacion.EstadoHabitacion.valueOf(estadoStr));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Estado inválido: " + estadoStr);
            }
        }

        // Actualiza campos restringidos solo para ADMIN
        if (esAdmin) {
            if (updates.containsKey("personas")) {
                try {
                    habitacion.setPersonas(((Number) updates.get("personas")).intValue());
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Valor inválido para personas");
                }
            }

            if (updates.containsKey("precio")) {
                try {
                    habitacion.setPrecio(((Number) updates.get("precio")).doubleValue());
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Valor inválido para precio");
                }
            }

            if (updates.containsKey("imagenUrl")) {
                String nuevaUrl = (String) updates.get("imagenUrl");

                if (nuevaUrl == null || nuevaUrl.trim().isEmpty()) {
                    habitacion.setImagenUrl(null);
                } else {
                    nuevaUrl = nuevaUrl.trim();
                    // Valida URL de imagen
                    boolean esValida = (nuevaUrl.startsWith("http://") || nuevaUrl.startsWith("https://")) &&
                            nuevaUrl.length() > 10 &&
                            !nuevaUrl.contains(" ") &&
                            (nuevaUrl.toLowerCase().contains(".jpg") ||
                                    nuevaUrl.toLowerCase().contains(".jpeg") ||
                                    nuevaUrl.toLowerCase().contains(".png") ||
                                    nuevaUrl.toLowerCase().contains(".webp") ||
                                    nuevaUrl.toLowerCase().contains(".gif") ||
                                    nuevaUrl.toLowerCase().contains("unsplash.com") ||
                                    nuevaUrl.toLowerCase().contains("imgur.com") ||
                                    nuevaUrl.toLowerCase().contains("picsum.photos") ||
                                    nuevaUrl.toLowerCase().contains("source.unsplash.com") ||
                                    nuevaUrl.toLowerCase().contains("drive.google.com/uc") ||
                                    nuevaUrl.toLowerCase().contains("cloudinary.com"));

                    if (esValida) {
                        habitacion.setImagenUrl(nuevaUrl);
                    } else {
                        return ResponseEntity.badRequest().body(
                                "URL de imagen no válida. Debe ser pública http/https (Imgur, Unsplash, etc.)."
                        );
                    }
                }
            }
        }

        // Guarda cambios
        Habitacion guardada = habitacionRepository.save(habitacion);

        return ResponseEntity.ok(guardada);
    }

    // Elimina habitación
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Integer id) {
        if (habitacionRepository.existsById(id)) {
            habitacionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Lista habitaciones disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Habitacion>> listarHabitacionesDisponibles() {
        List<Habitacion> disponibles = habitacionRepository.findAll().stream()
                .filter(h -> h.getEstado() == Habitacion.EstadoHabitacion.DISPONIBLE)
                .collect(Collectors.toList());
        return ResponseEntity.ok(disponibles);
    }

    // Cuenta observaciones por habitación
    @GetMapping("/{id}/observaciones/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Long> contarObservaciones(@PathVariable Integer id) {
        long count = observacionRepository.countByHabitacionIdHabitacion(id);
        return ResponseEntity.ok(count);
    }
}